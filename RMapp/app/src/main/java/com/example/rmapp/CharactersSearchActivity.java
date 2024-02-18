package com.example.rmapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class is the activity for searching for characters.
 * It is called by the character button in the main activity.
 * It contains a search button and a text field for the user to enter the search term.
 * It displays the search result in the app.
 *
 * @Author: Yuefeng Ma; Andrew Id: yuefengm
 * @Date:   2023-11-19
 */
public class CharactersSearchActivity extends AppCompatActivity {
    CharactersSearchActivity me = this;


    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.character_main);

        Button submitButton = (Button)findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchTerm = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                System.out.println("searchTerm = " + searchTerm);
                GetData gd = new GetData("character", me);
                gd.search(searchTerm); // Done asynchronously in another thread.
            }
        });
    }

    /*
     * This method is called when the data is ready.
     * It displays the data in the app.
     * @param result: the data to display
     * @return: none
     */
    public void dataReady(String result) {
        LinearLayout resultLayout = (LinearLayout) findViewById(R.id.resultLayout);
        resultLayout.removeAllViews();
        TextView feedbackText = (TextView) findViewById(R.id.feedback);
        String searchTerm = ((EditText) findViewById(R.id.searchTerm)).getText().toString();
        ImageView logo = (ImageView) findViewById(R.id.RMLogo);
        if (result != null) {
            logo.setVisibility(View.GONE);
            feedbackText.setText("Here is the information about character " + searchTerm);
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject character = jsonArray.getJSONObject(i);
                    String id = String.valueOf(character.getInt("id"));
                    String imageUrl = character.getString("image");
                    String name = character.getString("name");
                    String status = character.getString("status");
                    String species = character.getString("species");
                    String type = character.getString("type");
                    String gender = character.getString("gender");
                    String origin = character.getString("origin");
                    String location = character.getString("location");
                    String episode = character.getString("episode");
                    String url = character.getString("url");
                    String created = character.getString("created");
                    // Create an ImageView for the character's image
                    ImageView characterImage = new ImageView(this);
                    characterImage.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    characterImage.setAdjustViewBounds(true);
                    Picasso.get().load(imageUrl).into(characterImage);
                    resultLayout.addView(characterImage);

                    // Create a TextView for the character's information
                    TextView characterInfo = new TextView(this);
                    characterInfo.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    characterInfo.setText(String.format("ID: %s\nName: %s\nStatus: %s\nSpecies: %s\nType: %s\nGender: %s\nOrigin: %s\nLocation: %s\nEpisode: %s\nURL: %s\nCreated: %s",
                            id, name, status, species, type, gender, origin, location, episode, url, created));
                    resultLayout.addView(characterInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            feedbackText.setText("There is no information about character " + searchTerm + ". Please try another keyword!");
        }

    }
}
