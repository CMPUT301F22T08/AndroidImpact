package com.androidimpact.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidimpact.app.R;

/**
 * This is the activity for the login page
 * @version 2.0
 * @author Curtis Kan
 */
public class LoginActivity extends AppCompatActivity {

    // Instantiate XML elements
    TextView title;
    TextView saying;
    EditText username;
    EditText password;
    Button signup;
    Button login;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Link XML elements
        title = findViewById(R.id.login_title);
        saying = findViewById(R.id.login_phrase);
        username = findViewById(R.id.username);
        password= findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        // Fade in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        title.startAnimation(fadeIn);
        saying.startAnimation(fadeIn);
        username.startAnimation(fadeIn);
        password.startAnimation(fadeIn);
        signup.startAnimation(fadeIn);
        login.startAnimation(fadeIn);

        //TODO: firebase auth

    }

    // Go to MainActivity
    public void login(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username.getText().toString());
        startActivity(intent);
    }
}
