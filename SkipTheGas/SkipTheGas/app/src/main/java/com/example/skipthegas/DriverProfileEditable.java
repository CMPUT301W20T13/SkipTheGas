package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the editable view of the driver profile screen
 */

public class DriverProfileEditable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_edit_layout);
    }

    /**
     * This cancels edit mode and does not save changes to the profile, returning the user back
     * to the read-only driver profile screen upon a button click
     * @param view
     *      Changes screens from the driver profile (editable) to the driver profile (read-only)
     */
    public void cancel(View view) {
        Intent intent = new Intent(this, DriverProfileActivity.class);
        startActivity(intent);
    }

    /**
     * This saves the edits made to the profile screen and updates the driver profile, returning
     * the user to the read-only driver profile screen with the new edited changes, upon a button
     * click
     * @param view
     *      Changes screens from the driver profile (editable) to the driver profile (read-only)
     */
    public void submitEdit(View view) {
        Intent intent = new Intent(this, DriverProfileActivity.class);
        startActivity(intent);
    }
}
