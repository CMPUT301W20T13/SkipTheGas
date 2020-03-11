package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the ride confirmed screen
 */
public class RideConfirmedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_confirmed_layout);
    }

    /**
     * This returns the user back to their profile upon a button press
     * @param view
     *      Brings the user from the ride confirmed screen to the driver profile screen
     */
    public void returnToProfile(View view) {
        Intent intent = new Intent(this, DriverProfileActivity.class);
        startActivity(intent);
    }
}
