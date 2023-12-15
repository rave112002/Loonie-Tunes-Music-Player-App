package com.example.loonietunes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.loonietunes.R;

public class SplashView extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_view);

        // Check for EdgeToEdge class or utility and handle accordingly
        // Assuming EdgeToEdge is a utility class for handling UI layout, adjust as needed

        ImageView imageView = findViewById(R.id.loonie);

        // Create a RotateAnimation
        RotateAnimation rotateAnimation = new RotateAnimation(
                0f, 360f, // degrees to rotate from/to
                Animation.RELATIVE_TO_SELF, 0.5f, // pivot point x-axis
                Animation.RELATIVE_TO_SELF, 0.5f // pivot point y-axis
        );

        rotateAnimation.setDuration(2000); // duration in milliseconds

        // Set an animation listener to start the next activity after the rotation animation completes
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, navigate to the main activity or next screen
                navigateToMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated, if needed
            }
        });

        // Start the animation
        imageView.startAnimation(rotateAnimation);

        // Request the READ_EXTERNAL_STORAGE permission
        requestStoragePermission();
    }

    private void requestStoragePermission() {
        // Check if the permission has not been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission already granted, proceed with your logic
            navigateToMainActivity();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Add this line

        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                navigateToMainActivity();
            } else {
                // Permission denied, handle accordingly (show a message, exit the app, etc.)
                // For simplicity, we finish the activity here
                finish();
            }
        }
    }

    private void navigateToMainActivity() {
        // Start the main activity or next screen after a delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashView.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 2000); // Adjust the delay as needed
    }
}