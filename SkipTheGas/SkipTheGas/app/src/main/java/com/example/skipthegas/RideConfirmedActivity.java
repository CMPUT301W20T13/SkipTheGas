package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RideConfirmedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_confirmed_layout);
    }
    public void returnToProfile(View view) {
        Intent intent = new Intent(this, DriverProfileActivity.class);
        startActivity(intent);
    }
}
