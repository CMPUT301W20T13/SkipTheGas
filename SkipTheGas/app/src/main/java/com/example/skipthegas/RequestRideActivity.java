package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the request ride screen
 */
public class RequestRideActivity extends AppCompatActivity {

    /**
     * onCreate method for RequestRideActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_ride_layout);
    }

    /**
     * This allows the user to submit a request to the database, where a driver might accept it
     * while browsing in the "browse active requests" screen while logged into a driver account
     * @param view
     *      Switches view from the request ride screen to the your ride request screen, where a user
     *      can view summarized details about their request
     */
    public void submitRequest(View view) {
        Intent intent = new Intent(this, YourRideRequestActivity.class);
        startActivity(intent);
    }
}
