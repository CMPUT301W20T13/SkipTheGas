package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RequestRideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_ride_layout);
    }
    public void submitRequest(View view) {
        Intent intent = new Intent(this, YourRideRequestActivity.class);
        startActivity(intent);
    }
}
