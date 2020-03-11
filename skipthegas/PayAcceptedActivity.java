package com.example.skipthegas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the "pay accepted" screen, which launches upon a QR payment
 */

public class PayAcceptedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payaccepted_layout);
    }
}
