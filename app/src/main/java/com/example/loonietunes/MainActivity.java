package com.example.loonietunes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    Button favoritesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        noMusicTextView = findViewById(R.id.no_songs_text);
        favoritesBtn = findViewById(R.id.favoritesBtn);

        songsList = AudioUtils.loadAudioFiles(this);

        if (songsList.size() == 0) {
            noMusicTextView.setVisibility(android.view.View.VISIBLE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }

        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Favorites activity
                Intent intent = new Intent(MainActivity.this, Favorites.class);
                startActivity(intent);
            }
        });

        loadFavoritesFromPreferences();

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("favorites")) {
            saveFavoritesToPreferences();
        }
    }

    // ... existing code ...

    @Override
    protected void onDestroy() {
        // Unregister the listener to avoid memory leaks
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }
        loadFavoritesFromPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause called");
        saveFavoritesToPreferences();
    }

    private void saveFavoritesToPreferences() {
        Set<String> favoritesSet = new HashSet<>();
        for (AudioModel audioModel : songsList) {
            if (audioModel.isFavorite()) {
                favoritesSet.add(audioModel.getPath());
            }
        }
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        preferences.edit().putStringSet("favorites", favoritesSet).apply();
    }

    private void loadFavoritesFromPreferences() {
        Set<String> favoritesPaths = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                .getStringSet("favorites", new HashSet<>());

        // Mark songs as favorites based on the loaded paths
        for (AudioModel audioModel : songsList) {
            audioModel.setFavorite(favoritesPaths.contains(audioModel.getPath()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop called");
        saveFavoritesToPreferences();
    }



}
