package rmserver.rmserver;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This servlet is responsible for handling the dashboard requests.
 * It will fetch the analytics data from MongoDB and pass it to the JSP.
 * It will also log the request in MongoDB.
 *
 * @Author: Yuefeng Ma; Andrew ID: yuefengm
 * @Date: 2023-11-19
 */
@WebServlet(name = "Dashboard", value = "/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final String CONNECTION_STRING = "mongodb+srv://yuefengm:123myf@95702ds.bbijune.mongodb.net/?retryWrites=true&w=majority";
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

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
     * It will fetch the analytics data from MongoDB and pass it to the JSP.
     * It will also log the request in MongoDB.
     *
     * @param request the request from the client
     * @param response the response to the client
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Fetch analytics data
        FindIterable<Document> logs = collection.find().sort(Sorts.descending("timestamp")).limit(100);

        List<Document> topSearchTerms = collection.aggregate(
                Arrays.asList(
                        Aggregates.group("$keyword", Accumulators.sum("count", 1)),
                        Aggregates.sort(Sorts.descending("count")),
                        Aggregates.limit(10)
                )
        ).into(new ArrayList<>());

        // count the number of requests
        long requestCount = collection.countDocuments();

        // Top 5 mobile device models
        List<Document> topDeviceModels = collection.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.exists("userAgent")), // Filter documents where userAgent exists
                        Aggregates.group("$userAgent", Accumulators.sum("count", 1)),
                        Aggregates.sort(Sorts.descending("count")),
                        Aggregates.limit(5)
                )
        ).into(new ArrayList<>());
        // Pass the data to the JSP
        request.setAttribute("topSearchTerms", topSearchTerms);
        request.setAttribute("requestCount", requestCount);
        request.setAttribute("topDeviceModels", topDeviceModels);
        request.setAttribute("logs", logs.into(new ArrayList<>()));

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/dashboard.jsp");
        dispatcher.forward(request, response);

    }

}
