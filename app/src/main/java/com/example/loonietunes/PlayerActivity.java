package com.example.loonietunes;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    TextView songTitleTxt, startTimeTxt, endTimeTxt;
    SeekBar seekBar;
    ImageView pauseplayBtn, nextBtn, previousBtn, musicIcon, favBtn;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;
    Set<String> favoritesSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songTitleTxt = findViewById(R.id.songTitleTxt);
        startTimeTxt = findViewById(R.id.startTimeTxt);
        endTimeTxt = findViewById(R.id.endTimeTxt);
        seekBar = findViewById(R.id.seekBar);
        pauseplayBtn = findViewById(R.id.pause_playBtn);
        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.prevBtn);
        musicIcon = findViewById(R.id.loonie);
        favBtn = findViewById(R.id.favBtn);

        songTitleTxt.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        favoritesSet = getFavoritesFromPreferences();

        setResourcesWithMusic();

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    startTimeTxt.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        musicIcon.setRotation(x++);
                    } else {
                        musicIcon.setRotation(0);
                    }

                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Automatically play the next song when the current song finishes
                playNextSong();
            }
        });
    }

    void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        songTitleTxt.setText(currentSong.getTitle());

        endTimeTxt.setText(convertToMMSS(currentSong.getDuration()));

        pauseplayBtn.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        favBtn.setOnClickListener(v -> toggleFavorite());

        playMusic();
        updateFavoriteButton();
    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }

        updatePlayPauseButtons();
        updateFavoriteButton();// Set initial play/pause button drawable
    }

    private void playNextSong() {
        if (MyMediaPlayer.currentIndex == songsList.size() - 1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();

        updatePlayPauseButtons(); // Update play/pause button drawable
    }

    private void toggleFavorite() {
        // Toggle the favorite status
        currentSong.setFavorite(!currentSong.isFavorite());

        // Update UI based on the new favorite status
        updateFavoriteStatus();

        // Add/remove from favorites list and update SharedPreferences
        if (currentSong.isFavorite()) {
            addToFavorites(currentSong);
        } else {
            removeFromFavorites(currentSong);
        }
    }

    private void updateFavoriteStatus() {
        updateFavoriteButton();
        // You may add additional logic here if needed
    }

    private void updatePlayPauseButtons() {
        int drawableResId;
        if (isDarkMode()) {
            drawableResId = mediaPlayer.isPlaying() ? R.drawable.pause_dark : R.drawable.play_dark;
        } else {
            drawableResId = mediaPlayer.isPlaying() ? R.drawable.pause_light : R.drawable.play_light;
        }

        pauseplayBtn.setImageResource(drawableResId);
    }

    private void updateFavoriteButton() {
        int drawableResId;
        if (isDarkMode()) {
            drawableResId = currentSong.isFavorite() ? R.drawable.heart_filled_dark : R.drawable.heart_dark;
        } else {
            drawableResId = currentSong.isFavorite() ? R.drawable.heart_filled_light : R.drawable.heart_light;
        }

        favBtn.setImageResource(drawableResId);
    }

    private void addToFavorites(AudioModel audioModel) {
        if (!favoritesSet.contains(audioModel.getPath())) {
            // Add the current song path to the set
            favoritesSet.add(audioModel.getPath());

            // Save the updated set back to SharedPreferences
            saveFavoritesToPreferences(favoritesSet);

            // Show a Toast indicating that the song has been added to favorites
            showToast("Added to Favorites: " + audioModel.getTitle());
        }
    }

    private void removeFromFavorites(AudioModel audioModel) {
        if (favoritesSet.contains(audioModel.getPath())) {
            // Remove the current song path from the set
            favoritesSet.remove(audioModel.getPath());

            // Save the updated set back to SharedPreferences
            saveFavoritesToPreferences(favoritesSet);

            // Show a Toast indicating that the song has been removed from favorites
            showToast("Removed from Favorites: " + audioModel.getTitle());
        }
    }

    private Set<String> getFavoritesFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        // Retrieve the set of favorites from SharedPreferences, defaulting to an empty set
        return preferences.getStringSet("favorites", new HashSet<>());
    }

    private void saveFavoritesToPreferences(Set<String> favoritesSet) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        // Save the set of favorites to SharedPreferences
        preferences.edit().putStringSet("favorites", favoritesSet).apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }




    private boolean isDarkMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}