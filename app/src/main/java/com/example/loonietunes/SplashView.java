package com.example.loonietunes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class SplashView extends ComponentActivity {

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_view);

        // Animation setup
        setupRotationAnimation();

        // Check storage permission
        requestStoragePermission();
    }

    private void setupRotationAnimation() {
        ImageView imageView = findViewById(R.id.loonie);

        RotateAnimation rotateAnimation = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotateAnimation.setDuration(3000);

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("SplashView", "Animation started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("SplashView", "Animation ended");
                // Start scanning for songs in the background
                new SongLoaderTask().execute();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("SplashView", "Animation repeated");
            }
        });

        imageView.startAnimation(rotateAnimation);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            // Use the new permission request API for better readability
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            // Permission already granted, delayed navigation to MainActivity
            new Handler().postDelayed(() -> navigateToMainActivity(), 3000);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // Permission granted, delayed navigation to MainActivity
                    new Handler().postDelayed(() -> navigateToMainActivity(), 3000);
                } else {
                    // Permission denied, finish the activity
                    finish();
                }
            }
    );

    private void navigateToMainActivity() {
        Intent intent = new Intent(SplashView.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    // AsyncTask to load songs in the background
    private static class SongLoaderTask extends AsyncTask<Void, Void, ArrayList<AudioModel>> {

        @Override
        protected ArrayList<AudioModel> doInBackground(Void... voids) {
            // Replace the following line with your actual implementation to load audio files
            return AudioUtils.loadAudioFiles();
        }

        @Override
        protected void onPostExecute(ArrayList<AudioModel> result) {
            // Songs loaded, you can handle the result if needed
        }
    }
}
