package com.example.rmapp;

import android.app.Activity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.util.Log;

/*
 * This class is used to get data from the servlet.
 * It is used by the three search activities
 * (CharactersSearchActivity, EpisodesSearchActivity, LocationsSearchActivity)
 * to get data from the servlet
 * and display it in the app.
 *
 * @Author: Yuefeng Ma; Andrew Id: yuefengm
 * @Date:   2023-11-19
 */
public class GetData {
    private String searchTerm;
    private String searchType;
    private Activity activity;

    private String result = null;

    /*
     * Constructor
     * @param searchType: the type of the search
     * @param activity: the activity that calls this class
     * @return: none
     */
    public GetData(String searchType, Activity activity){
        this.activity = activity;
        this.searchType = searchType;
    }

    /*
     * This method is used to search for the data from the servlet.
     * It is called by the search button in the search activities.
     * It calls the BackgroundTask class to execute the search in the background.
     * @param searchTerm: the term to search for
     * @return: none
     */
    public void search(String searchTerm){
        this.searchTerm = searchTerm;
        new BackgroundTask(activity).execute();
    }


    private class BackgroundTask{
        private Activity activity;

        /*
         * Constructor
         * @param activity: the activity that calls this class
         * @return: none
         */
        public BackgroundTask(Activity activity){
            this.activity = activity;
        }

        private void startBackground(){
            new Thread(new Runnable() {
                public void run() {

                    doInBackground();

                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute(result);
                        }
                    });
                }
            }).start();
        }

        private void doInBackground() { result = search(searchTerm);
        }

        protected void onPostExecute(String result) {
            if (activity instanceof CharactersSearchActivity) {
                ((CharactersSearchActivity) activity).dataReady(result);
            } else if (activity instanceof EpisodesSearchActivity) {
                ((EpisodesSearchActivity) activity).dataReady(result);
            } else if (activity instanceof LocationsSearchActivity) {
                ((LocationsSearchActivity) activity).dataReady(result);
            }
        }
        private void execute(){
            startBackground();
        }

        private String search(String searchTerm){
            try {
                String urlString = "https://shiny-palm-tree-wrv5jw57vpw395w7-8080.app.github.dev/searchfor";
                urlString += "?type=" + URLEncoder.encode(searchType, "UTF-8");
                urlString += "&keyword=" + URLEncoder.encode(searchTerm, "UTF-8");

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (IOException e) {
                handleNetworkException(e);
                return null;
            } catch (Exception e){
                handleGenericException(e);
                return null;
            }
        }

        private void handleNetworkException(IOException e) {
            // Log the network exception with details
            Log.e("NetworkException", "Failed to execute network request", e);


        }

        private void handleGenericException(Exception e) {
            // Log the generic exception with details
            Log.e("GenericException", "An error occurred", e);

        }

    }
}
