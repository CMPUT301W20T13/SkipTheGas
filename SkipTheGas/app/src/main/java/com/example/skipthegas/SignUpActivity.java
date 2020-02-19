package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button riderSignUpButton;
    Button driverSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toast.makeText(this, "Sign up for your account", Toast.LENGTH_SHORT).show();

        // Input for sign up information
        username = findViewById(R.id.username_signup_field);
        password = findViewById(R.id.password_signup_field);

        // Click the button to register account
        riderSignUpButton = findViewById(R.id.rider_button);
        driverSignUpButton = findViewById(R.id.driver_button);

    }

}
