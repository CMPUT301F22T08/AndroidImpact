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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidimpact.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This is the activity for the login page
 *
 * Reference: https://firebase.google.com/docs/auth/android/start
 * @version 2.0
 * @author Curtis Kan
 */
public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    // Instantiate XML elements
    TextView title;
    TextView saying;
    EditText username;
    EditText password;
    Button signup;
    Button login;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

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
        username.setText("test@gmail.com");
        password.setText("qwerty");

    }

    /**
     * onClick method to start main activity on a successful login
     * @param view
     *    THe view that triggers this onClick
     */
    public void login(View view) {
        try {
            firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG + "Login", "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.login_layout), "Invalid login!", Snackbar.LENGTH_SHORT);
                            View snackbarView = snackbar.getView();
                            TextView snackbarTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            snackbar.setAction("Ok", view1 -> {
                            }).show();
                        }
                    });
        }
        catch(Exception e) {
            Toast.makeText(this, "Please enter correct credentials", Toast.LENGTH_SHORT);
        }
    }

    /**
     * onClick method to start sign up activity on a signup
     * @param view
     *    THe view that triggers this onClick
     */
    public void signup(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        // Clear fields
        username.setText("");
        password.setText("");
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null)
            updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", user.getDisplayName());
        intent.putExtra("uid", user.getUid());
        intent.putExtra("user-path-firebase", "userData" + user.getUid());
        // Clear fields
        username.setText("");
        password.setText("");
        startActivity(intent);
    }

}
