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

/**
 * This is a class which allows the user to log into the app
 */
public class LogInActivity extends AppCompatActivity {

    EditText loginEmailField;
    EditText passwordField;
    Button loginButton;
    TextView passwordReset;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    /**
     * onCreate method for LogInActivity class
     * @param savedInstanceState
     */
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
            /**
             * This checks for valid login in formation and allows the user to enter if validated
             */
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

                progressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    /**
                     * This brings the user to the corresponding profile screen if validation was successful
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
                            finish();
                            loginButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(LogInActivity.this, "Your email or password is incorrect "+task.getException(), Toast.LENGTH_SHORT).show();
                            loginButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            /**
             * Method is invoked when password reset is clicked on
             * @param view
             */
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
                            /**
                             * Confirms password reset request
                             * Sends an email to user to reset password
                             * @param dialogInterface
                             * @param i
                             */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Extract email address
                                String mail = emailField.getText().toString();
                                if (mail.length()==0){
                                    Toast.makeText(LogInActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    /**
                                     * Displays toast when password reset email is successfully sent
                                     * @param aVoid
                                     */
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(LogInActivity.this, "Email is send", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    /**
                                     * Displays email when password reset email fails to send
                                     * @param e
                                     */
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
    }
}
