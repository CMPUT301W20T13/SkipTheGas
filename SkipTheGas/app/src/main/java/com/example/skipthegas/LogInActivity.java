package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    EditText loginEmailField;
    EditText passwordField;
    Button loginButton;
<<<<<<< HEAD
    TextView passwordReset;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
=======
    Button goToProfile;
    TextView passwordReset;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    String userType = "Driver";
>>>>>>> brighton


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Toast.makeText(this, "Login to your account", Toast.LENGTH_SHORT).show();

        // Input for login information
        loginEmailField = findViewById(R.id.email_login_field);
        passwordField = findViewById(R.id.password_login_field);
        progressBar = findViewById(R.id.progress_bar2);
        // Click on the button to log in
        loginButton = findViewById(R.id.login_button);

        // Click on the text to reset password
        passwordReset = findViewById(R.id.password_reset);

        // Initiate Fire base
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmailField.getText().toString();
                String password = passwordField.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LogInActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LogInActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()<8) {
                    Toast.makeText(LogInActivity.this,"Password is too short", Toast.LENGTH_SHORT).show();
                    return;
                }
<<<<<<< HEAD
=======
                else{
                    if (userType == "Rider") {
                        Intent intent = new Intent(view.getContext(), RiderProfileActivity.class);
                        startActivity(intent);
                    }
                    else if (userType == "Driver"){
                        Intent intent = new Intent(view.getContext(), DriverProfileActivity.class);
                        startActivity(intent);
                    }
                }
>>>>>>> brighton

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Login success", Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
                            startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
=======
//                            startActivity(new Intent(getApplicationContext(),RidersActivity.class));
>>>>>>> brighton
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(LogInActivity.this, "Your email or password is incorrect", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText emailField = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setIcon(android.R.drawable.ic_dialog_email)
                        .setTitle("Reset password")
                        .setMessage("Enter your email to receive the reset link")
                        .setView(emailField)
                        .setNegativeButton("NO",null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Extract email address
                                String mail = emailField.getText().toString();
                                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
<<<<<<< HEAD
                                        Toast.makeText(LogInActivity.this, "Email is send", Toast.LENGTH_SHORT).show();
=======
                                        Toast.makeText(LogInActivity.this, "Email is sent", Toast.LENGTH_SHORT).show();
>>>>>>> brighton
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogInActivity.this, "Unable to send the email"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                passwordResetDialog.create().show();
            }
        });
<<<<<<< HEAD
=======

>>>>>>> brighton
    }
}
