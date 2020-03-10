package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the driver profile screen
 */

public class DriverProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_layout);
    }

    /**
     * This opens the editable view of the driver profile upon a button click
     * @param view
     *      Changes screens from the driver profile (read-only) to the driver profile (editable)
     */
    public void edit(View view) {
        Intent intent = new Intent(this, DriverProfileEditable.class);
        startActivity(intent);
    }

    /**
     * This opens the browse active requests screen upon a button click
     * @param view
     *      Changes screens from the driver profile (read-only) to the driver profile (editable)
     */
    public void browseRequests(View view) {
        Intent intent = new Intent(this, ActiveRequestsActivity.class);
        startActivity(intent);
    }

    /**
     * This logs out a user upon a button click
     * @param view
     *      Changes screens from the driver profile (read-only) to the login screen
     */
    public void logOut(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
