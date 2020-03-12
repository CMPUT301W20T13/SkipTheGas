package com.example.skipthegas;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_requests_layout);
        final ListView openFragment = findViewById(R.id.rides_list);
        openFragment.setOnItemClickListener((adapterView, v, i, L)->{
            new AcceptRequestFragment().show(getSupportFragmentManager(), "Accept Request");
        });

        ridesList = findViewById(R.id.rides_list);

        String []riders ={"Grersch"};
        String []drivers ={""};
        String []startLocs ={"University of Alberta"};
        String []endLocs ={"Dairy Queen"};
        Date []dates ={new Date(2020, 3, 7)};
        Integer []prices ={131};

        rideDataList = new ArrayList<>();

        rideAdapter = new ArrayAdapter<>(this, R.layout.content, rideDataList);


//        for (int i=0;i<riders.length;i++){
//            rideDataList.add(new Ride(riders[i], drivers[i], startLocs[i], endLocs[i], dates[i], prices[i]));
//        }

        rideAdapter = new CustomList(this, rideDataList);
        ridesList.setAdapter(rideAdapter);
        Log.v("Ride Info", ridesList.toString());

    }

    /**
     * This returns the user back to their profile on a button click
     * @param view
     *      Changes view from the requests screen to the profile screen
     */
    public void returnToProfile(View view) {
        Intent intent = new Intent(this, DriverProfileActivity.class);
        startActivity(intent);
    }

}
