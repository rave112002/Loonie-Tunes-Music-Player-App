package com.example.loonietunes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private final ArrayList<AudioModel> songsList;
    private final Context context;

    public MusicListAdapter(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioModel audioModel = songsList.get(position);
        holder.titleTextView.setText(audioModel.getTitle());

        // Check if the current song is a favorite and update UI
        if (audioModel.isFavorite()) {
            holder.favBtn.setImageResource(R.drawable.heart_filled);
        } else {
            holder.favBtn.setImageResource(R.drawable.heart);
        }

        if (MyMediaPlayer.currentIndex == position) {
            holder.titleTextView.setTextAppearance(R.style.medium);
        } else {
            holder.titleTextView.setTextAppearance(R.style.normal);
        }

        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the favorite status
                audioModel.setFavorite(!audioModel.isFavorite());

                // Update UI based on the new favorite status
                if (audioModel.isFavorite()) {
                    holder.favBtn.setImageResource(R.drawable.heart_filled);
                    addToFavorites(audioModel);
                } else {
                    holder.favBtn.setImageResource(R.drawable.heart);
                    removeFromFavorites(audioModel);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = position;
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("LIST", songsList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        // Check if the song is in favorites and update UI
        if (isSongInFavorites(audioModel)) {
            holder.favBtn.setImageResource(R.drawable.heart_filled);
        } else {
            holder.favBtn.setImageResource(R.drawable.heart);
        }
    }

    // Add this method to check if a song is in favorites
    private boolean isSongInFavorites(AudioModel audioModel) {
        Set<String> favoritesSet = getFavoritesFromPreferences();
        return favoritesSet.contains(audioModel.getPath());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView titleTextView;
        public final ImageButton favBtn;  // Add this line

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView); // Replace with your actual TextView ID
            favBtn = view.findViewById(R.id.favBtn);  // Add this line with the correct ID

        }
    }

    private void addToFavorites(AudioModel audioModel) {
        // Get the current favorites from SharedPreferences
        Set<String> favoritesSet = getFavoritesFromPreferences();

        // Check if the song is already in favorites
        if (!favoritesSet.contains(audioModel.getPath())) {
            // Add the current song path to the set
            favoritesSet.add(audioModel.getPath());

            // Save the updated set back to SharedPreferences
            saveFavoritesToPreferences(favoritesSet);

            // Update UI based on the new favorite status
            notifyDataSetChanged();

            // Show a Toast indicating that the song has been added to favorites
            showToast("Added to Favorites: " + audioModel.getTitle());
        } else {
            // If the song is already in favorites, you might want to handle it differently
            showToast("Already in Favorites: " + audioModel.getTitle());
        }
    }

    // Add this method for showing a Toast
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorites(AudioModel audioModel) {
        // Get the current favorites from SharedPreferences
        Set<String> favoritesSet = getFavoritesFromPreferences();

        // Check if the song is in favorites
        if (favoritesSet.contains(audioModel.getPath())) {
            // Remove the current song path from the set
            favoritesSet.remove(audioModel.getPath());

            // Save the updated set back to SharedPreferences
            saveFavoritesToPreferences(favoritesSet);

            // Update UI based on the new favorite status
            notifyDataSetChanged();

            // Show a Toast indicating that the song has been removed from favorites
            showToast("Removed from Favorites: " + audioModel.getTitle());
        }
    }

    private Set<String> getFavoritesFromPreferences() {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        // Retrieve the set of favorites from SharedPreferences, defaulting to an empty set
        return preferences.getStringSet("favorites", new HashSet<>());
    }

    private void saveFavoritesToPreferences(Set<String> favoritesSet) {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        // Save the set of favorites to SharedPreferences
        preferences.edit().putStringSet("favorites", favoritesSet).apply();
    }
}


