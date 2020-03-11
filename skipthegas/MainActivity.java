package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This is the main class which is launched when initially starting the app.
 * It contains the welcome screen, which prompts the user to either Log In or Sign Up.
 */
public class MainActivity extends AppCompatActivity {

    Button logInBtn;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInBtn = findViewById(R.id.login_page_button);
        signUpBtn = findViewById(R.id.signup_page_button);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This will bring the user to the login screen
             */
            public void onClick(View view) {
                Intent logInIntent = new Intent(getApplicationContext(), LogInActivity.class);

                startActivity(logInIntent);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This will bring the user to the sign in screen
             */
            public void onClick(View view) {
                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);

                startActivity(signUpIntent);
            }
        });
    }


}
