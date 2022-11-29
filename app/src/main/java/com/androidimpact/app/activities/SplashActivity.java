package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androidimpact.app.R;

import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // hide support action bar
        this.getSupportActionBar().hide();

        // Get elements
        imageView = findViewById(R.id.imageView_splash);
        progressBar = findViewById(R.id.progressBar);

        // Fade in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView.startAnimation(fadeIn);
        progressBar.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 5000);
    }
}