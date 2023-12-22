package com.example.loonietunes;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

public class AudioUtils {

    public static ArrayList<AudioModel> loadAudioFiles(Context context) {
        ArrayList<AudioModel> audioList = new ArrayList<>();

        // Define the projection to get audio file details
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        // Specify the selection criteria (only audio files)
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        // Use a cursor to query the external content URI for audio files
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
        )) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // Extract audio file details from the cursor
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                    // Create an AudioModel object and add it to the list
                    AudioModel audioModel = new AudioModel(path, title, duration);
                    audioList.add(audioModel);
                }
            }
        }


        return audioList;
    }

    // Additional method to load audio files without requiring a Context
    public static ArrayList<AudioModel> loadAudioFiles() {
        // Create a dummy context or handle this case based on your requirements
        // For example, you may choose to load audio files from a specific directory
        // or from some default location if no context is provided.

        // For demonstration purposes, returning an empty list here.
        return new ArrayList<>();
    }
}
