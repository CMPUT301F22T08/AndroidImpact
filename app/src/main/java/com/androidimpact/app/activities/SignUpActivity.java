package com.androidimpact.app.activities;

import android.app.Activity;
import android.content.Context;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the activity for the login page
 * @version 2.0
 * @author Aneeljyot Alagh
 */
public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "SignUpActivity";

    // Instantiate XML elements
    EditText email, password, name;
    Button signup;
    Button cancel;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        // Link XML elements
        name = findViewById(R.id.person_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        cancel = findViewById(R.id.cancel_button);
        this.context = SignUpActivity.this;

        cancel.setOnClickListener(view -> finish());

        signup.setOnClickListener(view -> firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name.getText().toString())
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");

                                        db = FirebaseFirestore.getInstance();
                                        Map<String, Object> data = new HashMap<>();
                                        db.collection("userData").document(user.getUid()).set(data);

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        intent.putExtra("username", user.getDisplayName());
                                        intent.putExtra("uid", user.getUid());
                                        intent.putExtra("user-path-firebase", "userData" + user.getUid());
                                        // Clear fields
                                        name.setText("");
                                        email.setText("");
                                        password.setText("");
                                        startActivity(intent);
                                        finish();
                                    }
                                });


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}
