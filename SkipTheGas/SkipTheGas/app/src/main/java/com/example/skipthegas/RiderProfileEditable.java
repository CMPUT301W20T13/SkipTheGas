package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the editable view of the rider profile screen
 */

public class RiderProfileEditable extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_edit_layout);
    }
    /**
     * This cancels edit mode and does not save changes to the profile, returning the user back
     * to the read-only rider profile screen upon a button click
     * @param view
     *      Changes screens from the rider profile (editable) to the rider profile (read-only)
     */
    public void cancel(View view) {
        Intent intent = new Intent(this, RiderProfileActivity.class);
        startActivity(intent);
    }
    /**
     * This saves the edits made to the profile screen and updates the rider profile, returning
     * the user to the read-only rider profile screen with the new edited changes, upon a button
     * click
     * @param view
     *      Changes screens from the rider profile (editable) to the rider profile (read-only)
     */
    public void submitEdit(View view) {
        Intent intent = new Intent(this, RiderProfileActivity.class);
        startActivity(intent);
        //passes new info back to view-only profile
    }
}
