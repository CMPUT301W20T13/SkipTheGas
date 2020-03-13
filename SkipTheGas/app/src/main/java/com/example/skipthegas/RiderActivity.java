package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton postReqBtn,editProfBtn,logoutBtn;
    Button clearMapBtn,switchModeBtn;
    String TAG = "MESSAGE";
    ArrayList<LatLng> locPointsList;
    int LOCATION_REQUEST_CODE = 1;
    Polyline line;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userId;
    String username;
    String phone;
    String email;
    EditText mSearchTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        locPointsList = new ArrayList<>();

        // Cloud database initiation
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // UI initiation
        floatingActionMenu = findViewById(R.id.rider_float_action_menu);
        postReqBtn = findViewById(R.id.request_ride);
        editProfBtn = findViewById(R.id.edit_view_profile);
        logoutBtn = findViewById(R.id.logout_option);
        clearMapBtn = findViewById(R.id.clear_button);
        switchModeBtn = findViewById(R.id.switch_mode);

        // Retrieve user information from cloud store
        assert firebaseUser != null;
        email = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(email)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        assert documentSnapshot != null;
                        email = documentSnapshot.getString("email");
                        phone = documentSnapshot.getString("phone");
                        username = documentSnapshot.getString("username");
                    }
                });

        // Post a new request
        postReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG,"Request posted by user " + userId);

                String msg1 = "Estimated ride distance:";
                String msg2 = "Estimated ride time:";
                String msg3 = "Estimated ride fare: ";

                if (locPointsList.size() < 2){

                    Toast.makeText(RiderActivity.this, "At least 2 points needed", Toast.LENGTH_SHORT).show();

                } else if (locPointsList.size() > 2){

                    Toast.makeText(RiderActivity.this, "Too many points", Toast.LENGTH_SHORT).show();

                } else {
                    GeoPoint origin = new GeoPoint(locPointsList.get(0).latitude, locPointsList.get(0).longitude);
                    GeoPoint destination = new GeoPoint(locPointsList.get(1).latitude, locPointsList.get(1).longitude);

                    String originAddress = getGeoAddress(origin);
                    String destinationAddress = getGeoAddress(destination);

                    double ride_dist = distance(locPointsList.get(0).latitude, locPointsList.get(0).longitude,
                            locPointsList.get(1).latitude, locPointsList.get(1).longitude);
                    DecimalFormat twoDecPoints = new DecimalFormat("#.##");
                    String rounded_dist = twoDecPoints.format(ride_dist);

                    //estimated ride time is calculated using a speed of 50 km/h for the time being
                    double ride_time = (ride_dist/50)*60;
                    String rounded_time = twoDecPoints.format(ride_time);

                    //estimated ride fare is calculated using a rate of $0.81 per km on top of a minimum fare of $5.25
                    double ride_fare = 5.25 + (ride_dist*0.81);
                    String rounded_fare = twoDecPoints.format(ride_fare);
                    Ride ride = new Ride(username, phone, email, origin, destination, rounded_dist,
                            rounded_time, rounded_fare, null, null,
                            null, false, false, originAddress, destinationAddress);
                    new AlertDialog.Builder(RiderActivity.this)
                            .setTitle("Requested Ride Details")
                            .setMessage(msg1 + rounded_dist + " kilometers" + "\n"
                                    + msg2 + rounded_time + " minutes" + "\n"
                                    + msg3 + "$" + rounded_fare)
                            .setNegativeButton("Cancel",null)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    HashMap<String, Object> reqData = new HashMap<>();
                                    reqData.put("rider_name",username);
                                    reqData.put("rider_phone",phone);
                                    reqData.put("rider_email",email);
                                    reqData.put("ride_origin",origin);
                                    reqData.put("ride_destination",destination);
                                    reqData.put("est_distance",rounded_dist);
                                    reqData.put("est_time",rounded_time);
                                    reqData.put("est_fare",rounded_fare);
                                    reqData.put("driver_name", null);
                                    reqData.put("driver_phone", null);
                                    reqData.put("driver_email", null);
                                    reqData.put("is_compete",false);
                                    reqData.put("is_accepted",false);
                                    reqData.put("origin_address", originAddress);
                                    reqData.put("destination_address", destinationAddress);

                                    firebaseFirestore
                                            .collection("all_requests")
                                            .add(reqData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.i(TAG,"post success");
                                                    Toast.makeText(RiderActivity.this, "request posted", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i(TAG,"post failed "+e.getMessage());
                                                }
                                            });
                                    clearMap();
                                }
                            }).create().show();

                }
            }
        });

        // Edit profile
        editProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Editing Profile");
                Intent riderProfileIntent = new Intent(getApplicationContext(), RiderProfileActivity.class);
                startActivity(riderProfileIntent);
            }
        });

        // Log out of current account
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Log out");
                new AlertDialog.Builder(RiderActivity.this)
                        .setTitle("Logging out of rider account")
                        .setMessage("Are you sure?")
                        .setNegativeButton("NO",null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        }).create().show();
            }
        });

        // Switch to driver mode
        switchModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DriverDrawerActivity.class));
            }
        });

        // Clear the map
        clearMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMap();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (locPointsList.size()==2){
                    clearMap();
                }
                locPointsList.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                if (locPointsList.size()==1){
                    // add the first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else {
                    // add the second marker to the map and a line between them
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    if (locPointsList.size() == 2){
                        line = mMap.addPolyline(new PolylineOptions()
                                .add(locPointsList.get(0), locPointsList.get(1))
                                .width(10)
                                .color(Color.BLUE));
                    }
                }
                mMap.addMarker(markerOptions);
            }
        });
        this.initSearch();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    // Function calculates the distance between the origin and the destination using the latitude and longitude of the 2 locations
    private double distance(double origin_lat, double origin_lon, double dest_lat, double dest_lon) {
        double theta = origin_lon - dest_lon;
        double distCalc = Math.sin(DegToRad(origin_lat)) * Math.sin(DegToRad(dest_lat))
                + Math.cos(DegToRad(origin_lat)) * Math.cos(DegToRad(dest_lat)) * Math.cos(DegToRad(theta));
        distCalc = Math.acos(distCalc);
        distCalc = RadToDeg(distCalc);
        distCalc = distCalc * 60 * 1.1515 * 1.609344;
        return distCalc;
    }

    // Function converts decimal degrees to radians
    private double DegToRad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // Function converts radians to decimal degrees
    private double RadToDeg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public String getGeoAddress(GeoPoint geoPoint) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        double lat = geoPoint.getLatitude();
        double lng = geoPoint.getLongitude();
        String returnAddress = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            String add = addresses.get(0).getAddressLine(0);
            returnAddress = add;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnAddress;
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void clearMap(){
        locPointsList.clear();
        mMap.clear();
    }

    public void initSearch(){
        Log.d(TAG,"Initializing search ...");
        mSearchTxt = findViewById(R.id.search_input);
        mSearchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (
                        actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    geoLocate();
                    mSearchTxt.setText("");
                }
                return false;
            }
        });
    }

    public void geoLocate(){
        Log.i(TAG,"locating...");
        String searchString = mSearchTxt.getText().toString();
        Geocoder geocoder = new Geocoder(RiderActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {

            addressList = geocoder.getFromLocationName(searchString,1);

        } catch (Exception e) {

            Log.d(TAG,"Error occurred " + e.getMessage());
            e.printStackTrace();

        }

        if (addressList.size()>0) {

            Address address = addressList.get(0);
            Toast.makeText(this, "Found a location", Toast.LENGTH_SHORT).show();
            Log.d("Address","Found a location " + address.toString());

        } else {

            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Location not found");

        }
    }
}