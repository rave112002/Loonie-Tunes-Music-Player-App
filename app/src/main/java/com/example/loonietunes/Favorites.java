package com.example.loonietunes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Favorites extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> favoritesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.favRecyclerView);
        noMusicTextView = findViewById(R.id.no_songs_text);

        // Retrieve the list of favorite songs
        favoritesList = getFavoritesList();

        if (favoritesList.size() == 0) {
            noMusicTextView.setVisibility(android.view.View.VISIBLE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(favoritesList, getApplicationContext()));
        }

        loadFavoritesFromPreferences();

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        favoritesList = getFavoritesList();
    }


    private ArrayList<AudioModel> getFavoritesList() {
        // Get the current favorites from SharedPreferences
        Set<String> favoritesPaths = getFavoritesFromPreferences();

        // Create a list of AudioModel objects for the favorite songs
        ArrayList<AudioModel> favoritesList = new ArrayList<>();
        for (AudioModel audioModel : AudioUtils.loadAudioFiles(this)) {
            if (audioModel.getPath() != null && favoritesPaths.contains(audioModel.getPath())) {
                audioModel.setFavorite(true);  // Set favorite status
                favoritesList.add(audioModel);
            }
        }


        return favoritesList;
    }

    private Set<String> getFavoritesFromPreferences() {
        // Retrieve the set of favorites from SharedPreferences, defaulting to an empty set
        return getSharedPreferences("MyPreferences", MODE_PRIVATE)
                .getStringSet("favorites", new HashSet<>());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Favorites", "onPause called");
        saveFavoritesToPreferences();
    }

    private void saveFavoritesToPreferences() {
        Set<String> favoritesSet = new HashSet<>();
        for (AudioModel audioModel : favoritesList) {
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
        for (AudioModel audioModel : favoritesList) {
            audioModel.setFavorite(favoritesPaths.contains(audioModel.getPath()));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {

    }

    @Override
    protected void onDestroy() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Favorites", "onStop called");
        saveFavoritesToPreferences();
    }

}