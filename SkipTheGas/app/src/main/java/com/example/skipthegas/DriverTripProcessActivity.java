package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

/**
 * This class displays the relevant information for the ride request that is currently being served by the driver
 * Driver is able to confirm and complete the currently open ride from this activity
 */
public class DriverTripProcessActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoApiContext mGeoApiContext = null;
    public String requestID;
    private static final int REQUEST_CALL = 1;
    private boolean count = true;

    Button completeButton;
    Button viewRequestButton;
    TextView riderPhoneTextView;
    TextView riderStartAddressTextView;
    TextView riderEndAddressTextView;
    TextView riderNameTextView;
    TextView riderEmailTextView;
    TextView riderConfirmTextView;
    ImageView phoneCallIcon;

    double currentBalance;
    String userEmail;

    private static final String TAG = "DriverProcessActivity";

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    Intent scannerIntent;

    /**
     * onCreate method for the DriverTripProcessActivity
     * Retrieves the associated layout file and displays it
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_process);
        scannerIntent = new Intent(getApplicationContext(),DriverPaymentScannerActivity.class);
        riderPhoneTextView = findViewById(R.id.driver_process_rider_phone_TextView);
        completeButton = findViewById(R.id.driver_complete_button);
        viewRequestButton = findViewById(R.id.driver_process_view_request_button);
        riderStartAddressTextView = findViewById(R.id.driver_process_start_location_TextView);
        riderEndAddressTextView = findViewById(R.id.driver_process_end_location_TextView);
        riderNameTextView = findViewById(R.id.driver_process_rider_name_TextView);
        riderEmailTextView = findViewById(R.id.driver_process_rider_email_TextView);
        riderConfirmTextView = findViewById(R.id.driver_process_rider_confirm_TextView);
        phoneCallIcon = findViewById(R.id.driver_phone_icon);

        Intent intent = getIntent();
        requestID = Objects.requireNonNull(intent.getExtras()).getString("request_id");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        initMap();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userEmail = firebaseUser.getEmail();
        }

        firebaseFirestore
                .collection("users")
                .document(userEmail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    /**
                     * onComplete method for facilitating the payment process
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                currentBalance = (double) documentSnapshot.get("QR_bucks");
                                scannerIntent.putExtra("current_balance", currentBalance);
                            } else {
                                Log.d(TAG, "Document does not exist.");
                            }
                        } else {
                            Log.d(TAG, "Error: " + task.getException());
                        }
                    }
                });

        firebaseFirestore
                .collection("all_requests")
                .document(requestID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method fetches the relevant ride information from firebase including rider info and start/end locations
                     * Also sets this data to the textviews in the layout file
                     * @param documentSnapshot
                     * @param e
                     */
                    @SuppressLint("SetTextI18n")
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
                            GeoPoint start = documentSnapshot.getGeoPoint("ride_origin");
                            GeoPoint end = documentSnapshot.getGeoPoint("ride_destination");
                            MarkerOptions startLocation = new MarkerOptions().position(new LatLng(start.getLatitude(), start.getLongitude())).title("Start Location");
                            MarkerOptions endLocation = new MarkerOptions().position(new LatLng(end.getLatitude(), end.getLongitude())).title("End location");
                            boolean riderConfirm = documentSnapshot.getBoolean("is_confirmed");
                            boolean cancel = documentSnapshot.getBoolean("is_cancel");
                            riderPhoneTextView.setText(riderPhone);
                            riderStartAddressTextView.setText(startAddress);
                            riderEndAddressTextView.setText(endAddress);
                            riderNameTextView.setText(riderName);
                            riderEmailTextView.setText(riderEmail);
                            calculateDirections(startLocation, endLocation);
                            if (!riderConfirm) {
                                riderConfirmTextView.setText("Waiting for Rider to confirm.");
                            } else {
                                riderConfirmTextView.setText("Rider confirmed your request.");
                                if (count) {
                                    notification();
                                    count = false;
                                }
                            }
                            if (cancel) {
                                Toast.makeText(getApplicationContext(), "Driver Canceled Request", Toast.LENGTH_SHORT).show();
                                Intent cancelIntent = new Intent(getApplicationContext(), DriverDrawerActivity.class);
                                startActivity(cancelIntent);
                                finish();
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

        phoneCallIcon.setOnClickListener(new View.OnClickListener() {
            /**
             * Method allows users to make a phone call when the phone call icon is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        viewRequestButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method allows drivers to view the request they just accepted
             * @param view
            */
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(),YourRideAcceptedActivity.class);
                viewIntent.putExtra("request_Id",requestID);
                startActivity(viewIntent);
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method is called when the complete button is clicked
             * Displays a dialog box asking for ride completion confirmation
             * @param v
             */
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DriverTripProcessActivity.this)
                        .setTitle("Complete")
                        .setNegativeButton("No",null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            /**
                             * Method is called when the "Yes" button is clicked in the dialog box confirming ride completion for the driver
                             * The firebase database is updated with the relevant information
                             * @param dialogInterface
                             * @param i
                             */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseFirestore
                                        .collection("all_requests")
                                        .document(requestID)
                                        .update("is_driver_completed",true);
                                startActivity(scannerIntent);
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    /**
     * Method allows the driver to call the rider's phone
     * Directs the user to the phone app
     */
    public void makePhoneCall(){
        String number = riderPhoneTextView.getText().toString();
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(DriverTripProcessActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { // Permission not granted

                ActivityCompat.requestPermissions(DriverTripProcessActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            } else {

                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

            }

        } else {
            Toast.makeText(this, "Please Enter Valid Number", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method determines what to do once the access permissions for the phone app is either granted or denied
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method is invoked once the map is ready for use by the application
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("all_requests")
                .document(requestID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Adds the start and end locations to the map along with the directions polyline
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e!=null){
                            Log.d(TAG,"Error occurred "+e.getMessage());
                            return;
                        }
                        if (documentSnapshot!=null&&documentSnapshot.exists()){
                            GeoPoint start = documentSnapshot.getGeoPoint("ride_origin");
                            GeoPoint end = documentSnapshot.getGeoPoint("ride_destination");
                            MarkerOptions startLocation = new MarkerOptions().position(new LatLng(start.getLatitude(), start.getLongitude())).title("Start Location");
                            MarkerOptions endLocation = new MarkerOptions().position(new LatLng(end.getLatitude(), end.getLongitude())).title("End location");
                            mMap.addMarker(startLocation);
                            mMap.addMarker(endLocation);
                            setCamera(start, end);
                        } else {
                            Log.i(TAG,"Document does not exist");
                        }

                    }
                });

    }

    /**
     * Initializes the map and displays it
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_trip_process_map);
        mapFragment.getMapAsync(this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    /**
     * Allows the camera to move to show the start and end locations
     * @param start
     * @param end
     */
    private void setCamera(GeoPoint start, GeoPoint end) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder().include(new LatLng(start.getLatitude(), start.getLongitude())).include(new LatLng(end.getLatitude(), end.getLongitude()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
    }

    /**
     * Calculate the direction between two points
     * Code taken from CodingWithMitch and modified by Jun
     * https://www.youtube.com/watch?v=xl0GwkLNpNI&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=20
     * @param startLocation
     * @param endLocation
     */
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
            /**
             * onResult method for directions calculations
             * @param result
             */
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolyline(result);
            }

            /**
             * onFailure method that throws an exception message
             * @param e
             */
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    /**
     * Method adds polyline between the start & end locations along the directions
     * @param result
     */
    private void addPolyline (final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            /**
             * Method runs a polyline along the route calculated by the directions function
             */
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


    /**
     * Sends a notification if users request is accepted
     * Code taken from https://stackoverflow.com/questions/16045722/android-notification-is-not-showing
     * Author / user = Md Imran Choudhury
     */
    private void notification() {
        NotificationManager mNotificationManager;
        String message = "Notification";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), DriverTripProcessActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(DriverTripProcessActivity.this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle("Rider has confirmed your request");
        bigText.setSummaryText("message for: Driver");

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
            String channelId = "driver_channel";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "driver_notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
}

