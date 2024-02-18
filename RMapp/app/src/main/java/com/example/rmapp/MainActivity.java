package com.example.rmapp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rmapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import android.content.Intent;

/*
 * This class is the main activity of the app.
 * It is the entry point of the app.
 * It contains three buttons to navigate to the three search activities.
 *
 * @Author: Yuefeng Ma; Andrew Id: yuefengm
 * @Date:   2023-11-19
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button characterButton = findViewById(R.id.characterButton);
        Button episodeButton = findViewById(R.id.episodesButton);
        Button locationButton = findViewById(R.id.locationButton);


        characterButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CharactersSearchActivity.class);
            intent.putExtra("type", "char");
            startActivity(intent);
        });

        episodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EpisodesSearchActivity.class);
            intent.putExtra("type", "epi");
            startActivity(intent);
        });

        locationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LocationsSearchActivity.class);
            intent.putExtra("type", "loc");
            startActivity(intent);
        });

    }

}