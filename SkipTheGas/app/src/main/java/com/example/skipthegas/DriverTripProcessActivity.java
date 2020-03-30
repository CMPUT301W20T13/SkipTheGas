package com.example.skipthegas;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class DriverTripProcessActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoApiContext mGeoApiContext = null;
    public String requestID;
    private GeoPoint start;
    private GeoPoint end;

    Button completeButton;
    TextView riderPhoneTextView;
    TextView riderStartAddressTextView;
    TextView riderEndAddressTextView;
    TextView riderNameTextView;
    TextView riderEmailTextView;

    private MarkerOptions startLocation;
    private MarkerOptions endLocation;

    private static final String TAG = "DriverProcessActivity";

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_process);

        riderPhoneTextView = findViewById(R.id.driver_process_rider_phone_TextView);
        completeButton = findViewById(R.id.driver_complete_button);
        riderStartAddressTextView = findViewById(R.id.driver_process_start_location_TextView);
        riderEndAddressTextView = findViewById(R.id.driver_process_end_location_TextView);
        riderNameTextView = findViewById(R.id.driver_process_rider_name_TextView);
        riderEmailTextView = findViewById(R.id.driver_process_rider_email_TextView);

        Intent intent = getIntent();
        requestID = intent.getExtras().getString("request_id");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initMap();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore
                .collection("all_requests")
                .document(requestID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG,"Error message: " + e.getMessage());
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Log.d(TAG, "Current Data: " + documentSnapshot.getData());
                            String riderPhone = (String) documentSnapshot.getData().get("rider_phone");
                            String startAddress = documentSnapshot.getString("origin_address");
                            String endAddress = documentSnapshot.getString("destination_address");
                            String riderName = documentSnapshot.getString("rider_name");
                            String riderEmail = documentSnapshot.getString("rider_email");
                            riderPhoneTextView.setText(riderPhone);
                            riderStartAddressTextView.setText(startAddress);
                            riderEndAddressTextView.setText(endAddress);
                            riderNameTextView.setText(riderName);
                            riderEmailTextView.setText(riderEmail);
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });






//        calculateDirections(startLocation, endLocation);


        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.addMarker(startLocation);
//        mMap.addMarker(endLocation);
//        setCamera(start, end);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_trip_process_map);
        mapFragment.getMapAsync(this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    private void setCamera(GeoPoint start, GeoPoint end) {
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(start.getLatitude() - 0.1, start.getLongitude() - 0.1),
                new LatLng(end.getLatitude() + 0.1, end.getLongitude() + 0.1)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
    }

    // calculate the direction between two points
    // Code taken from CodingWithMitch and modified by Jun
    // https://www.youtube.com/watch?v=xl0GwkLNpNI&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=20
    private void calculateDirections(MarkerOptions startLocation, MarkerOptions endLocation){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                endLocation.getPosition().latitude,
                endLocation.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(false);

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                startLocation.getPosition().latitude,
                startLocation.getPosition().longitude
        );

        directions.origin(origin);

        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolyline(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    private void addPolyline (final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for (DirectionsRoute route: result.routes) {
                    Log.d(TAG, "run: leg:" + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng: decodedPath) {

                        Log.d(TAG, "run: latLng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(DriverTripProcessActivity.this, R.color.colorPrimary));
                }
            }
        });
    }
}

