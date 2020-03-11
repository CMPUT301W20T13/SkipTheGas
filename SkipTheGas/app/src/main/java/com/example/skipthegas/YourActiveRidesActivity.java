package com.example.skipthegas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the "your active rides" screen, where a driver can view details
 * about a ride they have accepted
 */
public class YourActiveRidesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_active_rides_layout);
    }
}
