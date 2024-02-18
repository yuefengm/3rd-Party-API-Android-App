package com.example.rmapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class is the activity for searching for episodes.
 * It is called by the episode button in the main activity.
 * It contains a search button and a text field for the user to enter the search term.
 * It displays the search result in the app.
 *
 * @Author: Yuefeng Ma; Andrew Id: yuefengm
 * @Date:   2023-11-19
 */
public class EpisodesSearchActivity extends AppCompatActivity {
    EpisodesSearchActivity me = this;


    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.episode_main);

        Button submitButton = (Button)findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchTerm = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                System.out.println("searchTerm = " + searchTerm);
                GetData gd = new GetData("episode", me);
                gd.search(searchTerm);
            }
        });
    }


    /*
     * This method is called when the data is ready.
     * It displays the data in the app.
     * @param result: the data to display
     * @return: none
     */
    public void dataReady(String result){
        LinearLayout resultLayout = (LinearLayout) findViewById(R.id.resultLayout);
        resultLayout.removeAllViews();
        TextView feedbackText = (TextView) findViewById(R.id.feedback);
        String searchTerm = ((EditText) findViewById(R.id.searchTerm)).getText().toString();
        ImageView logo = (ImageView) findViewById(R.id.RMLogo);

        if (result != null){
            logo.setVisibility(View.GONE);
            feedbackText.setText("Here is the information about episode " + searchTerm);
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject episode = jsonArray.getJSONObject(i);
                    String id = String.valueOf(episode.getInt("id"));
                    String name = episode.getString("name");
                    String airDate = episode.getString("air_date");
                    String episodeString = episode.getString("episode");
                    String characters = episode.getString("characters");
                    String url = episode.getString("url");
                    String created = episode.getString("created");

                    TextView characterInfo = new TextView(this);
                    characterInfo.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    characterInfo.setText(String.format("ID: %s\nName: %s\nAir Date: %s\nEpisode: %s\nCharacters: %s\nURL: %s\nCreated: %s",
                            id, name, airDate, episodeString, characters, url, created));
                    resultLayout.addView(characterInfo);
                }
            } catch (JSONException e){
                e.printStackTrace();

            }

        } else {
            feedbackText.setText("There is no information about episode " + searchTerm + ". Please try another keyword!");

        }
    }
}
