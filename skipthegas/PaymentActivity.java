package com.example.skipthegas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This a class that governs the payment screen, where a QR code is generated for payment transfer
 */

public class PaymentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);
    }
}
