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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class RidersActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton postReqBtn,editProfBtn,logoutBtn;
    Button clearMapBtn,switchModeBtn;
    String TAG = "MESSAGE";
    ArrayList<LatLng> locPointsList;
    int LOCATION_REQUEST_CODE = 1;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userId;
    String username;
    String phone;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riders);
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
        userId = firebaseUser.getUid();
        firebaseFirestore
                .collection("users")
                .document(userId)
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

                if (locPointsList.size() < 2){

                    Toast.makeText(RidersActivity.this, "At least 2 points needed", Toast.LENGTH_SHORT).show();

                } else {

                    GeoPoint origin = new GeoPoint(locPointsList.get(0).latitude, locPointsList.get(0).longitude);
                    GeoPoint destination = new GeoPoint(locPointsList.get(1).latitude, locPointsList.get(1).longitude);
                    HashMap<String, Object> reqData = new HashMap<>();
                    reqData.put("origin",origin);
                    reqData.put("destination",destination);

                    reqData.put("rider",userId);
                    reqData.put("rider_phone",phone);
                    reqData.put("rider_email",email);
                    reqData.put("rider_name",username);

                    firebaseFirestore
                            .collection("users")
                            .document(userId)
                            .collection("requests")
                            .add(reqData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG,"Addition success");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"Addition failed");
                                }
                            });
                    clearMap();
                }
            }
        });

        // Edit profile
        editProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Editing Profile");
            }
        });

        // Log out of current account
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Log out");
                new AlertDialog.Builder(RidersActivity.this)
                        .setTitle("Log out?")
                        .setMessage("Are you sure")
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
                startActivity(new Intent(getApplicationContext(),DriversActivity.class));
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
                    // add the second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                mMap.addMarker(markerOptions);
            }
        });

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

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void clearMap(){
        locPointsList.clear();
        mMap.clear();
    }
}