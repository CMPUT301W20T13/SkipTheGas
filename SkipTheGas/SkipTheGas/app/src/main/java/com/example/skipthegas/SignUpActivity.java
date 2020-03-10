package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * This is a class that governs the sign up screen of the app
 */
public class SignUpActivity extends AppCompatActivity {

    EditText usernameField;
    EditText passwordField;
    EditText registerEmailField;
    EditText phoneNumField;

    Button registerButton;

    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireDatabase;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toast.makeText(this, "Sign up for your account", Toast.LENGTH_SHORT).show();

        // Input for sign up information
        usernameField = findViewById(R.id.username_register_field);
        passwordField = findViewById(R.id.password_register_field);
        registerEmailField = findViewById(R.id.email_register_field);
        phoneNumField = findViewById(R.id.phone_register_field);

        // Click the button to register account
        registerButton = findViewById(R.id.register_button);

        // Shows up while register in progress
        progressBar = findViewById(R.id.progress_bar);

        // Initiate Fire base
        firebaseAuth = FirebaseAuth.getInstance();
        fireDatabase = FirebaseFirestore.getInstance();

//        if (firebaseAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), RidersActivity.class));
//            finish();
//        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This performs validation checks on a user's entered sign up information
             * @param username
             *      This is the user's entered email address
             * @param password
             *      This is the user's entered password
             * @param email
             *      This is this user's entered email
             * @param phone
             *      This is the user's entered phone number
             * @retur
             *      Either an error is returned, or the user is brought back to the login page if
             *      validation was successful
             */
            public void onClick(View view) {
                final String username = usernameField.getText().toString();
                final String password = passwordField.getText().toString();
                final String email = registerEmailField.getText().toString();
                final String phone = phoneNumField.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUpActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()<8) {
                    Toast.makeText(SignUpActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignUpActivity.this, "Phone number is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Register the user
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    /**
                     * This registers the user in the firebase as a new user for the app
                     */
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) { // Create user success
                            // Send verification link
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // If verification email is successfully sent
                                    Toast.makeText(SignUpActivity.this, "Verification Email sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // If verification email is not sent
                                    Toast.makeText(SignUpActivity.this, "Unable to send verification email"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("","Error!, unable to send verification email");
                                }
                            });

                            Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();

                            // Get the unique userID
                            userID = firebaseAuth.getCurrentUser().getUid();

                            DocumentReference documentReference = fireDatabase.collection("users").document(userID);

                            HashMap<String,Object> users = new HashMap<>();
                            users.put("username",username);
                            users.put("email",email);
                            users.put("phone",phone);

                            /* MORE USER INFO IS NEEDED */

                            // Adding set of information to collection users
                            documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Message","Data addition success: information created for" + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Message", "Data addition failure: unable to create info" + e.getMessage());
                                }
                            });

                            // Direct user to Rider's screen
//                            startActivity(new Intent(getApplicationContext(), RidersActivity.class));
                            progressBar.setVisibility(View.GONE);
                        } else { // Fail to create user
                            Toast.makeText(SignUpActivity.this, "Unable to create user.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

    }

}
