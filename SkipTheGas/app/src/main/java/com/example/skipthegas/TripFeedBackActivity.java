package com.example.skipthegas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the trip feedback screen, which launches upon the completion of a
 * trip and gives the rider an opportunity to rate the driver
 */
public class TripFeedBackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_feedback_layout);
    }
}
