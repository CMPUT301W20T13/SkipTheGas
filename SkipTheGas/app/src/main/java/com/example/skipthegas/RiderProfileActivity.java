package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the rider profile screen
 */
public class RiderProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_layout);
    }

    /**
     * This opens the editable view of the rider profile upon a button click
     * @param view
     *      Changes screens from the rider profile (read-only) to the rider profile (editable)
     */
    public void edit(View view) {
        Intent intent = new Intent(this, RiderProfileEditable.class);
        startActivity(intent);
    }

    /**
     * This opens the browse active requests screen upon a button click
     * @param view
     *      Changes screens from the rider profile (read-only) to the rider profile (editable)
     */
    public void requestRides(View view) {
        Intent intent = new Intent(this, RequestRideActivity.class);
        startActivity(intent);
    }

    /**
     * This logs out a user upon a button click
     * @param view
     *      Changes screens from the rider profile (read-only) to the login screen
     */
    public void logOut(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
