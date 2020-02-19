package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.account_menu_layout, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.login){ // Login an existing account
            Intent logInIntent = new Intent(getApplicationContext(), LogInActivity.class);

            startActivity(logInIntent);

            return true;
        } else if (item.getItemId()==R.id.signup){ // Sign up for an account
            Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);

            startActivity(signUpIntent);
            return true;
        }

        return false;
    }
}
