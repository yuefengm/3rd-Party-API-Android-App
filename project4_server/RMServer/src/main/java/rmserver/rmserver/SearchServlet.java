package rmserver.rmserver;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.rickandmortyapi.ApiException;
import com.rickandmortyapi.ApiRequest;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;
import org.bson.types.ObjectId;


/*
 * This servlet is responsible for handling the search requests.
 * It will call the Rick and Morty API and return the results to the client.
 * It will also log the request in MongoDB.
 * @Author: Yuefeng Ma; Andrew ID: yuefengm
 * @Date: 2023-11-19
 */
@WebServlet(name = "SearchFor", value = "/searchfor")
public class SearchServlet extends HttpServlet {
    private static final String CONNECTION_STRING = "mongodb+srv://yuefengm:123myf@95702ds.bbijune.mongodb.net/?retryWrites=true&w=majority";

    private MongoCollection<Document> collection;
    private MongoDatabase database;
    private MongoClient mongoClient;

    private static final Logger LOGGER = Logger.getLogger(SearchServlet.class.getName());

    /**
     * Initialize the MongoDB connection
     */
    @Override
    public void init() {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .serverApi(serverApi)
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("RMDatabase");
        collection = database.getCollection("RMCollection");
    }


    /**
     * Handle the GET request from the client
     * It will call the Rick and Morty API and return the results to the client.
     * It will also log the request in MongoDB.
     *
     * @param request the request from the client
     * @param response the response to the client
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String userAgent = request.getHeader("User-Agent"); // Log the model of the phone


        String type = request.getParameter("type");
        String keyword = request.getParameter("keyword");

        if (type == null || keyword == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\":\"Missing type or keyword parameter\"}");
            return;
        }
        String path = null;
        Map<String, Object> params = new HashMap<>();
        switch (type){
            case "character":
                path = "/character";
                params.put("name", keyword);
                break;
            case "location":
                path = "/location";
                params.put("name", keyword);
                break;
            case "episode":
                path = "/episode";
                params.put("episode", keyword);
                break;
        }

        long apiRequestTime = System.currentTimeMillis();
        try {
            // Create the API request for getting characters
            ApiRequest rq = new ApiRequest("GET", path);
            rq.setParameters(params);

            // Execute the API request
            JsonElement responseElement = rq.execute();
            long apiResponseTime = System.currentTimeMillis();

            // Store the log in MongoDB
            Document logDocument = new Document("timestamp", System.currentTimeMillis())
                    .append("type", type)
                    .append("keyword", keyword)
                    .append("userAgent", userAgent)
                    .append("apiRequestDuration", apiResponseTime - apiRequestTime)
                    .append("responseStatus", response.getStatus());
            collection.insertOne(logDocument);
            ObjectId id = logDocument.getObjectId("_id");

            GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();

            // Check if the response contains an array of characters
            if (responseElement.isJsonObject() && responseElement.getAsJsonObject().has("results")) {
                JsonArray results = responseElement.getAsJsonObject().getAsJsonArray("results");

                // Deserialize each JsonElement to a Character object
                String prettyJsonString = gsonBuilder.create().toJson(results);
                System.out.println(prettyJsonString);
                out.println(prettyJsonString);
            }
            long endTime = System.currentTimeMillis();
            Document update = new Document("$set", new Document("totalResponseTime", endTime - startTime));
            collection.updateOne(new Document("_id", id), update);
            params.clear();
        } catch (ApiException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            collection.insertOne(new Document("error", e.getMessage()).append("responseStatus", response.getStatus()));
            out.println("{\"error\":\"Internal server error occurred.\"}");
        }   catch (IllegalArgumentException e) {
            handleInvalidInputException(e, response);
        } catch (Exception e) {
            handleGenericException(e, response);
        }

    }

    private void handleInvalidInputException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        logError(e);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println("{\"error\":\"Invalid server-side input.\"}");
    }

    private void handleGenericException(Exception e, HttpServletResponse response) throws IOException {
        logError(e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().println("{\"error\":\"An unexpected error occurred.\"}");
    }

    private void logError(Exception e) {
        LOGGER.log(Level.SEVERE, "An error occurred during the operation", e);

        Document errorLog = new Document("timestamp", System.currentTimeMillis())
                .append("errorType", e.getClass().getName())
                .append("errorMessage", e.getMessage());

        collection.insertOne(errorLog);
    }

    public void destroy() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}