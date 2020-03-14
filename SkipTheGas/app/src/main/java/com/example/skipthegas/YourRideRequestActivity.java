package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class which governs the "your ride request" screen, where riders can view details
 * regarding the ride request they have submitted
 */
public class YourRideRequestActivity extends AppCompatActivity {

    /**
     * onCreate method for YourRideRequestActivity class
     * @param savedInstanceState
     */
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

    /**
     * This displays to the screen that a ride has been cancelled if the user selects "ok" on the
     * cancel ride fragment
     */
    public void onOkPressed(){
        Toast.makeText(this, "Ride Canceled", Toast.LENGTH_SHORT).show();
    }
}
