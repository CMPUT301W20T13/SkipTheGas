package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This is a class that allows the login user to choose whether to be a rider or driver
 */
public class SelectionActivity extends AppCompatActivity {

    Button riderSelectButton;
    Button driverSelectButton;

    /**
     * onCreate method for SelectionActivity
     * Retrieves and displays the associated layout file
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        riderSelectButton = findViewById(R.id.rider_select_button);
        driverSelectButton = findViewById(R.id.driver_select_button);

        riderSelectButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method invoked when the rider button is clicked
             * onCLick method for entering Rider account
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent riderIntent = new Intent(getApplicationContext(), RiderDrawerActivity.class);
                startActivity(riderIntent);
                finish();
            }
        });

        driverSelectButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method invoked when the driver button is clicked
             * onCLick method for entering Driver account
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent driverIntent = new Intent(getApplicationContext(), DriverDrawerActivity.class);
                startActivity(driverIntent);
                finish();
            }
        });
    }
}
