package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import javax.annotation.Nullable;

public class RiderTripProcessActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoApiContext mGeoApiContext = null;

    private static final String TAG = "RiderProcessActivity";
    private static final int REQUEST_CALL = 1;

    Button viewRequestButton;
    Button confirmButton;
    Button completeButton;
    TextView driverNameTextView;
    TextView driverPhoneTextView;
    TextView driverEmailTextView;
    TextView driverAcceptedTextView;
    ImageView phoneCallIcon;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public String userEmail;
    String requestID;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_trip_process);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initMap();

        viewRequestButton = findViewById(R.id.rider_process_view_request_button);
        confirmButton = findViewById(R.id.rider_process_confirm_button);
        completeButton = findViewById(R.id.rider_complete_button);
        driverNameTextView = findViewById(R.id.rider_process_driver_name_TextView);
        driverPhoneTextView = findViewById(R.id.rider_process_driver_phone_TextView);
        driverEmailTextView = findViewById(R.id.rider_process_driver_email_TextView);
        driverAcceptedTextView = findViewById(R.id.rider_process_driver_accept_TextView);
        phoneCallIcon = findViewById(R.id.driver_phone_icon);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        userEmail = firebaseUser.getEmail();
        firebaseFirestore
                .collection("all_requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            boolean isDriverCompleted = (boolean) doc.getData().get("is_driver_completed");
                            boolean isRiderCompleted = (boolean) doc.getData().get("is_rider_completed");
                            boolean canceled = (boolean) doc.getData().get("is_cancel");
                            String riderEmail = (String) doc.getData().get("rider_email");
                            if (!isDriverCompleted && !isRiderCompleted && !canceled && userEmail.equals(riderEmail)) {
                                requestID = doc.getId();
                                boolean accepted = doc.getBoolean("is_accepted");
                                boolean confirmed = doc.getBoolean("is_confirmed");
                                GeoPoint start = doc.getGeoPoint("ride_origin");
                                GeoPoint end = doc.getGeoPoint("ride_destination");
                                MarkerOptions startLocation = new MarkerOptions().position(new LatLng(start.getLatitude(), start.getLongitude())).title("Start Location");
                                MarkerOptions endLocation = new MarkerOptions().position(new LatLng(end.getLatitude(), end.getLongitude())).title("End location");
                                if (accepted && !confirmed) {
                                    viewRequestButton.setEnabled(true);
                                    confirmButton.setEnabled(true);
                                    String driverAcceptedText = "Driver accepted your request. Please confirm.";
                                    driverAcceptedTextView.setText(driverAcceptedText);
                                    String driverName = doc.getString("driver_name");
                                    String driverEmail = doc.getString("driver_email");
                                    String driverPhone = doc.getString("driver_phone");
                                    driverNameTextView.setText(driverName);
                                    driverEmailTextView.setText(driverEmail);
                                    driverPhoneTextView.setText(driverPhone);

                                    //  Notification
                                    notification();
                                }
                                if (accepted && confirmed) {
                                    String driverAcceptedText = "Driver accepted your request.";
                                    driverAcceptedTextView.setText(driverAcceptedText);
                                    confirmButton.setEnabled(false);
                                    completeButton.setEnabled(true);
                                }
                                calculateDirections(startLocation, endLocation);
                            }
                        }
                    }
                });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("all_requests").document(requestID).update("is_confirmed", true);
            }
        });

        viewRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), YourRideRequestActivity.class);
                intent.putExtra("request_id", requestID);
                startActivity(intent);
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(RiderTripProcessActivity.this)
                        .setTitle("Complete")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseFirestore
                                        .collection("all_requests")
                                        .document(requestID)
                                        .update("is_rider_completed",true);
                                Intent paymentIntent = new Intent(getApplicationContext(),PaymentActivity.class);
                                paymentIntent.putExtra("request_Id",requestID);
                                startActivity(paymentIntent);
                            }
                        });
            }
        });

        phoneCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

    }

    public void makePhoneCall(){
        String number = driverPhoneTextView.getText().toString();
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(RiderTripProcessActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { // Permission not granted

                ActivityCompat.requestPermissions(RiderTripProcessActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            } else {

                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

            }

        } else {
            Toast.makeText(this, "Please Enter Valid Number", Toast.LENGTH_SHORT).show();
        }
    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        String mapUserEmail = firebaseUser.getEmail();

        firebaseFirestore
                .collection("all_requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            boolean isDriverCompleted = (boolean) doc.getData().get("is_driver_completed");
                            boolean isRiderCompleted = (boolean) doc.getData().get("is_rider_completed");
                            boolean canceled = (boolean) doc.getData().get("is_cancel");
                            String riderEmail = (String) doc.getData().get("rider_email");
                            if (!isDriverCompleted && !isRiderCompleted && !canceled && mapUserEmail.equals(riderEmail)) {
                                GeoPoint start = doc.getGeoPoint("ride_origin");
                                GeoPoint end = doc.getGeoPoint("ride_destination");
                                MarkerOptions startLocation = new MarkerOptions().position(new LatLng(start.getLatitude(), start.getLongitude())).title("Start Location");
                                MarkerOptions endLocation = new MarkerOptions().position(new LatLng(end.getLatitude(), end.getLongitude())).title("End location");
                                mMap.addMarker(startLocation);
                                mMap.addMarker(endLocation);
                                setCamera(start, end);
                            }
                        }
                    }
                });

    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.rider_process_map);
        mapFragment.getMapAsync(this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    private void setCamera(GeoPoint start, GeoPoint end) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder().include(new LatLng(start.getLatitude(), start.getLongitude())).include(new LatLng(end.getLatitude(), end.getLongitude()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
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
                    polyline.setColor(ContextCompat.getColor(RiderTripProcessActivity.this, R.color.colorPrimary));
                }
            }
        });
    }

    //sends a notification if users request is accepted
    //Code taken from https://stackoverflow.com/questions/16045722/android-notification-is-not-showing
    //Author / user = Md Imran Choudhury
    private void notification() {
        NotificationManager mNotificationManager;
        String message = "Notification";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), RiderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(RiderTripProcessActivity.this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle("Driver has accepted your request");
        bigText.setSummaryText("message for: Rider");

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
