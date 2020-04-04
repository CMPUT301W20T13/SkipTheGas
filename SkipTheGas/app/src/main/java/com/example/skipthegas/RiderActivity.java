package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
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
import com.google.android.gms.maps.CameraUpdateFactory;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * This is a class that implements the opening screen of the Rider account
 * Rider is able to send requests from this screen
 * Rider can access their profile from this screen
 * Rider can logout from this screen
 */
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

    EditText mSearchView;

    /**
     * onCreate method for RiderActivity class
     * @param savedInstanceState
     */
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
        mSearchView = findViewById(R.id.search_input);

        // Retrieve user information from cloud store
        assert firebaseUser != null;
        email = firebaseUser.getEmail();
        firebaseFirestore
                .collection("all_requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    /**
                     * This is the method for notifying the rider that the driver accepted their ride request
                     * @param queryDocumentSnapshots
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String requestID = doc.getId();
                            boolean accepted = (boolean) doc.getData().get("is_accepted");
                            boolean completed = (boolean) doc.getData().get("is_compete");

                            String riderEmail = (String) doc.getData().get("rider_email");
                            if (accepted && !completed && riderEmail.equals(email)) {
                                //sends a notification if users request is accepted
                                //Code taken from https://stackoverflow.com/questions/16045722/android-notification-is-not-showing
                                //Author / user = Md Imran Choudhury
                                NotificationManager mNotificationManager;
                                String message = "Click here to go to the main menu";
                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getApplicationContext(), "notify_001");
                                Intent ii = new Intent(getApplicationContext(), RiderActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(RiderActivity.this, 0, ii, 0);

                                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                                bigText.bigText(message);
                                bigText.setBigContentTitle("Driver has accepted your request");
                                bigText.setSummaryText("message for: rider");

                                mBuilder.setContentIntent(pendingIntent);
                                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                                mBuilder.setContentTitle("Your Title");
                                mBuilder.setContentText("Your text");
                                mBuilder.setPriority(Notification.PRIORITY_MAX);
                                mBuilder.setStyle(bigText);

                                mNotificationManager =
                                        (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    String channelId = "rider_channel";
                                    NotificationChannel channel = new NotificationChannel(
                                            channelId,
                                            "rider_notifications",
                                            NotificationManager.IMPORTANCE_HIGH);
                                    mNotificationManager.createNotificationChannel(channel);
                                    mBuilder.setChannelId(channelId);
                                }

                                mNotificationManager.notify(0, mBuilder.build());
                            }
                        }
                    }
                });

        firebaseFirestore
                .collection("users")
                .document(email)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    /**
                     * This method fetches user information from firebase database
                     * @param documentSnapshot
                     * @param e
                     */
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
            /**
             * This method is invoked when the Post Request button within the Floating Action Menu is clicked
             * Opens a dialog box with information regarding the ride request
             * @param view
             */
            @Override
            public void onClick(View view) {

                Log.i(TAG,"Request posted by user " + userId);

                String msg1 = "Estimated ride distance: ";
                String msg2 = "Estimated ride time: ";
                String msg3 = "Estimated ride fare: ";

                if (locPointsList.size() < 2){

                    Toast.makeText(RiderActivity.this, "At least 2 points needed", Toast.LENGTH_SHORT).show();

                } else {
                    GeoPoint origin = new GeoPoint(locPointsList.get(0).latitude, locPointsList.get(0).longitude);
                    GeoPoint destination = new GeoPoint(locPointsList.get(1).latitude, locPointsList.get(1).longitude);

                    String originAddress = getGeoAddress(origin);
                    String destinationAddress = getGeoAddress(destination);

                    // Estimates distance between the start and end points
                    double ride_dist = distance(locPointsList.get(0).latitude, locPointsList.get(0).longitude,
                            locPointsList.get(1).latitude, locPointsList.get(1).longitude);
                    DecimalFormat twoDecPoints = new DecimalFormat("#.##");
                    String rounded_dist = twoDecPoints.format(ride_dist);

                    // Estimated ride time is calculated using a speed of 50 km/h for the time being
                    double ride_time = (ride_dist/50)*60;
                    String rounded_time = twoDecPoints.format(ride_time);

                    // Estimated ride fare is calculated using a rate of $0.81 per km on top of a minimum fare of $5.25
                    double ride_fare = 5.25 + (ride_dist*0.81);
                    String rounded_fare = twoDecPoints.format(ride_fare);
//                    Ride ride = new Ride(username, phone, email, origin, destination, rounded_dist,
//                            rounded_time, rounded_fare, null, null,
//                            null, false, false, originAddress, destinationAddress, null, false);
                    new AlertDialog.Builder(RiderActivity.this)
                            .setTitle("Requested Ride Details")
                            .setMessage(msg1 + rounded_dist + " km" + "\n"
                                    + msg2 + rounded_time + " min" + "\n"
                                    + msg3 + rounded_fare + " QR bucks")
                            .setNegativeButton("Cancel",null)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                /**
                                 * This method passes the ride request information to the firebase database
                                 * @param dialogInterface
                                 * @param i
                                 */
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
                                                /**
                                                 * This method is invoked if the ride request post to the database fails
                                                 * @param e
                                                 */
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
            /**
             * This method is invoked when the User Profile button within the Floating Action Menu is clicked
             * Opens the User's Profile
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Editing Profile");
                Intent riderProfileIntent = new Intent(getApplicationContext(), RiderProfileActivity.class);
                startActivity(riderProfileIntent);
            }
        });

        // Log out of current account
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is invoked when the Log out button within the Floating Action Menu is clicked
             * Opens a dialog box that prompts for confirmation
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Log out");
                new AlertDialog.Builder(RiderActivity.this)
                        .setTitle("Logging out of rider account")
                        .setMessage("Are you sure?")
                        .setNegativeButton("NO",null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            /**
                             * Method logs out the user when YES is clicked in the dialog box
                             * @param dialogInterface
                             * @param i
                             */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        }).create().show();
            }
        });

        // Switch to driver mode
        switchModeBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * Method redirects to Driver account when DRIVER MODE button is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DriverDrawerActivity.class));
            }
        });

        // Clear the map
        clearMapBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * Method clears the map of any remaining markers when CLEAR MAP button is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                clearMap();
            }
        });
    }

    /**
     * Method initializes the google map
     * @param googleMap
     */
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
            /**
             * Method draws polyline between start and end locations when 2 markers are placed on the map
             * Map will be cleared when user tries to place more than 2 markers on the map
             * @param latLng
             */
            @Override
            public void onMapClick(LatLng latLng) {
                if (locPointsList.size()>=2){
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
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
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

    /**
     * Method asks user permission for enabling location on their device
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * Function calculates the distance between the origin and the destination
     * using the latitude and longitude of the 2 locations
     */
    private double distance(double origin_lat, double origin_lon, double dest_lat, double dest_lon) {
        double theta = origin_lon - dest_lon;
        double distCalc = Math.sin(DegToRad(origin_lat)) * Math.sin(DegToRad(dest_lat))
                + Math.cos(DegToRad(origin_lat)) * Math.cos(DegToRad(dest_lat)) * Math.cos(DegToRad(theta));
        distCalc = Math.acos(distCalc);
        distCalc = RadToDeg(distCalc);
        distCalc = distCalc * 60 * 1.1515 * 1.609344;
        return distCalc;
    }

    /**
     * Function converts decimal degrees to radians
     */
    private double DegToRad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Function converts radians to decimal degrees
     * @param rad
     * @return
     */
    private double RadToDeg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * convert GeoPoint which is LatLng to address in real life
     * @param geoPoint
     * @return
     * the address
     */
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

    /**
     * Method logs the user out of the app
     */
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    /**
     * Method clears the map
     */
    public void clearMap(){
        locPointsList.clear();
        mMap.clear();
    }

    /**
     * Method initializes the search from the input in the search bar
     */
    public void initSearch(){
        Log.i(TAG,"Initializing search ...");
        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) { // KEYCODE_ENTER is the event when you press enter key
                     geoLocate();
                     mSearchView.setText("");
                }
                return true;
            }
        });
    }

    /**
     * Method geo-locates the address in the search bar
     */
    public void geoLocate(){
        Log.d(TAG,"Geo-locating ...");
        String searchStr = mSearchView.getText().toString();
        Geocoder geocoder = new Geocoder(RiderActivity.this);
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocationName(searchStr,1);
        } catch (Exception e) {
            Log.d(TAG,"Error in locating");
            e.printStackTrace();
        }

        if (addresses.size()!=0) {
            Toast.makeText(this, "found a location", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Location found");

            if (locPointsList.size()>=2) {
                clearMap();
            }
            Address address = addresses.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            locPointsList.add(latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            if (locPointsList.size()==1) {
                // add the first marker to the map
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                // add the second marker to the map
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                if (locPointsList.size() == 2) {
                    line = mMap.addPolyline(new PolylineOptions()
                            .add(locPointsList.get(0), locPointsList.get(1))
                            .width(10)
                            .color(Color.BLUE));
                }
            }
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        } else {
            Toast.makeText(this, "location not found", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Location not found");
        }
    }
}