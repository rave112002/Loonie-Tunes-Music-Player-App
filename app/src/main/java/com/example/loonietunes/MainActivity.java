package com.example.loonietunes;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private ListView songListView;
    private ArrayList<String> songs;
    private MediaPlayer mediaPlayer;
    private int currentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songListView = findViewById(R.id.songsList);
        mediaPlayer = new MediaPlayer();

        if (checkPermission()) {
            loadSongs();
        } else {
            requestPermission();
        }

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPlayerActivity(position);
            }
        });
    }

    private void openPlayerActivity(int position) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("song_position", position);
        startActivity(intent);
    }

    private void loadSongs() {
        songs = findSongs(Environment.getExternalStorageDirectory());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        songListView.setAdapter(adapter);
    }

    private ArrayList<String> findSongs(File directory) {
        ArrayList<String> songList = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !file.isHidden()) {
                    songList.addAll(findSongs(file));
                } else if (file.getName().endsWith(".mp3")) {
                    songList.add(file.getName());
                }
            }
        }
        return songList;
    }

    private boolean checkPermission() {
        int readExternalPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return readExternalPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

}