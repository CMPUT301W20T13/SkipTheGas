package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RiderProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_layout);
    }

    public void edit(View view) {
        Intent intent = new Intent(this, RiderProfileEditable.class);
        startActivity(intent);
    }

    public void requestRides(View view) {
        Intent intent = new Intent(this, RequestRideActivity.class);
        startActivity(intent);
    }

    public void logOut(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
