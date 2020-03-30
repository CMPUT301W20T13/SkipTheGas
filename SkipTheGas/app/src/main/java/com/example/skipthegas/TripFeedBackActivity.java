package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a class that governs the trip feedback screen, which launches upon the completion of a
 * trip and gives the rider an opportunity to rate the driver
 */
public class TripFeedBackActivity extends AppCompatActivity {
    Button returnButton;
    ToggleButton goodRating;
    ToggleButton badRating;

    TextView goodRatingCnt;
    TextView badRatingCnt;

    /**
     * onCreate method for TripFeedBackActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_feedback_layout);
        goodRating = findViewById(R.id.imageView);
        badRating = findViewById(R.id.imageView2);

        goodRatingCnt = findViewById(R.id.textView33);
        badRatingCnt = findViewById(R.id.textView34);

        returnButton = findViewById(R.id.rating_button);

        goodRating.setOnClickListener(view -> {
            badRating.setChecked(false);
            goodRating.setChecked(true);
        });
        
        badRating.setOnClickListener(view -> {
            goodRating.setChecked(false);
            badRating.setChecked(true);
        });

        returnButton.setOnClickListener(view -> {
            if (!badRating.isChecked() && !goodRating.isChecked()) {
                Toast.makeText(this, "Please rate your driver", Toast.LENGTH_SHORT).show();
            } else {

                // TODO: ********** ********** Update the driver ratings in Firebase ********** **********
                /* TODO */
                // TODO: ********** ********** Update the driver ratings in Firebase ********** **********
                Intent riderIntent = new Intent(getApplicationContext(),RiderDrawerActivity.class);
                startActivity(riderIntent);
                finish();
            }
        });
    }
}