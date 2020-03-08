package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class SelectModeActivity extends AppCompatActivity {
    Button driverBtn,riderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        driverBtn = findViewById(R.id.driver_mode_button);
        riderBtn = findViewById(R.id.rider_mode_button);
    }

    public void toDriver(View view){
        Log.i("Message","Log in drivers page");
        startActivity(new Intent(getApplicationContext(),DriversActivity.class));
        finish();
    }

    public void toRider(View view){
        Log.i("Message","Log in riders page");
        startActivity(new Intent(getApplicationContext(),RidersActivity.class));
        finish();
    }

}
