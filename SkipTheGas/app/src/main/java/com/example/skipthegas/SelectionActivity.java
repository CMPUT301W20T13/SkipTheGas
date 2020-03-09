package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectionActivity extends AppCompatActivity {

    Button riderSelectButton;
    Button driverSelectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        riderSelectButton = findViewById(R.id.rider_select_button);
        driverSelectButton = findViewById(R.id.driver_select_button);

        driverSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent driverIntent = new Intent(getApplicationContext(), DriverDrawerActivity.class);
                startActivity(driverIntent);
            }
        });
    }
}
