package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class YourRideRequestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_ride_request_layout);

        setContentView(R.layout.your_ride_request_layout);
        final Button cancelVerify = findViewById(R.id.verificationButton);
        cancelVerify.setOnClickListener((v)->{
            new CancelFragment().show(getSupportFragmentManager(), "Cancel Request");
        });
        final TextView openProfile = findViewById(R.id.Driver);
        openProfile.setOnClickListener((v)->{
            new DriverProfileFragment().show(getSupportFragmentManager(), "View Profile");
        });
    }

    public void onOkPressed(){
        Toast.makeText(this, "Ride Canceled", Toast.LENGTH_SHORT).show();
    }
}
