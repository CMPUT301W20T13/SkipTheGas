package com.example.skipthegas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * This is a class that governs the "browse active requests" screen
 */
public class ActiveRequestsActivity extends AppCompatActivity {
    ListView ridesList;
    ArrayAdapter<Ride> rideAdapter;
    ArrayList<Ride> rideDataList;

    CustomList customList;

    /**
     * onCreate method for ActiveRequestsActivity class
     * @param savedInstanceState
     */
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_requests_layout);
        final ListView openFragment = findViewById(R.id.rides_list);
        openFragment.setOnItemClickListener((adapterView, v, i, L)->{
            new AcceptRequestFragment().show(getSupportFragmentManager(), "Accept Request");
        });

        ridesList = findViewById(R.id.rides_list);

        rideDataList = new ArrayList<>();

        rideAdapter = new ArrayAdapter<>(this, R.layout.content, rideDataList);



        rideAdapter = new CustomList(this, rideDataList);
        ridesList.setAdapter(rideAdapter);
        Log.v("Ride Info", ridesList.toString());

    }

}
