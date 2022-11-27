package com.androidimpact.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidimpact.app.R;
import com.google.android.material.snackbar.Snackbar;

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



        // TODO: Remove this, its for convenience
        username.setText("Curtis Kan");
        password.setText("CMPUT 301");

    }

    /**
     * onClick method to start main activity on a successful login
     * @param view
     *    THe view that triggers this onClick
     */
    public void login(View view) {

        //TODO: firebase auth

        if (username.getText().toString().equals("Curtis Kan") && password.getText().toString().equals("CMPUT 301")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", username.getText().toString());
            // Clear fields
            username.setText("");
            password.setText("");
            startActivity(intent);
        }
        else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.login_layout), "Invalid login!", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            TextView snackbarTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snackbar.setAction("Ok", view1 -> {}).show();
        }
    }

    /**
     * onClick method to start main activity on a signup
     * @param view
     *    THe view that triggers this onClick
     */
    public void signup(View view) {
        //TODO: create new user
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username.getText().toString());
        // Clear fields
        username.setText("");
        password.setText("");
        startActivity(intent);
    }
}
