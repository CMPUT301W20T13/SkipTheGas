package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Toast.makeText(this, "Login to your account", Toast.LENGTH_SHORT).show();

        // Input for login information
        username = findViewById(R.id.username_login_field);
        password = findViewById(R.id.password_login_field);
        logInButton = findViewById(R.id.login_button);

    }
}
