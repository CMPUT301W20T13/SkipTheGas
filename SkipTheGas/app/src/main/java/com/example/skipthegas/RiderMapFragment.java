package com.example.skipthegas;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Rider's map activity fragment
 * Rider can view map from this fragment
 */
public class RiderMapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap mMap;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private View mapView;

    private static final String TAG = "RiderMapFragment";
    private GeoApiContext mGeoApiContext = null;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 9001;
    private static final float DEFAULT_ZOOM = 15f;

    // widgets
    Button postRequestButton, clearMapButton;
    ArrayList<LatLng> locationMarkerList;
    private EditText searchEditText;

    // vars
    public String estDistance;
    public String estTime;
    public String estFare;
    private MarkerOptions startLocation;
    private MarkerOptions endLocation;
    private String userEmail;
    private String userName;
    private String userPhone;

    double currentBalance;
    AutocompleteSupportFragment autocompleteFragment;

    /**
     * onCreateView method for the Rider map fragment
     * Retrieves and displays the map in fragment view, along with other rider layout functionality
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_rider_map, null, false);
        locationMarkerList = new ArrayList<>();
        initMap();

        // Cloud database initiation
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            userEmail = firebaseUser.getEmail();
        }

        // UI initiation
        postRequestButton = view.findViewById(R.id.post_request_button);
        clearMapButton = view.findViewById(R.id.clear_map_button);
        searchEditText = view.findViewById(R.id.rider_map_search_edit_text);

        firebaseFirestore
                .collection("users")
                .document(userEmail)
                .addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    /**
                     * Method retrieves rider information from firebase
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Message: " + e.getMessage());
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()){
                            userPhone = documentSnapshot.getString("phone");
                            userName = documentSnapshot.getString("username");
                            currentBalance = (double) documentSnapshot.get("QR_bucks");
                        } else {
                            Log.d(TAG, "Document does not exist.");
                        }
                    }
                });


        postRequestButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method invoked when the post request button is clicked
             * Shows relevant information about ride request in a pop-up dialog box
             * @param v
             */
            @Override
            public void onClick(View v) {
                String msg1 = "Estimated ride distance: ";
                String msg2 = "Estimated ride time: ";
                String msg3 = "Estimated ride fare: ";

                if (locationMarkerList.size() < 2) {
                    Toast.makeText(getContext(), "At least 2 points needed.", Toast.LENGTH_SHORT).show();
                } else {
                    GeoPoint origin = new GeoPoint(locationMarkerList.get(0).latitude, locationMarkerList.get(0).longitude);
                    GeoPoint destination = new GeoPoint(locationMarkerList.get(1).latitude, locationMarkerList.get(1).longitude);

                    DecimalFormat twoDecPoints = new DecimalFormat("#.##");
                    String originAddress = getGeoAddress(origin);
                    String destinationAddress = getGeoAddress(destination);
                    String tempDist = estDistance.replaceAll("[^0-9.]", "");
                    double dist = Double.parseDouble(tempDist);
                    double fare = 5.25 + (dist * 0.81);
                    estFare = twoDecPoints.format(fare);
                    if (fare > currentBalance) {

                        Toast.makeText(getActivity(), "Cannot post request, Your balance is not enough", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "Can post request", Toast.LENGTH_SHORT).show();
                        Log.i(TAG,"Can post request");
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Requested Ride Details")
                                .setMessage(msg1 + estDistance + "\n" + msg2 + estTime + "\n" + msg3 + estFare + "QR bucks")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    /**
                                     * Method invoked when the "Confirm" button in the dialog box is clicked
                                     * Confirms ride requests and posts it to the database
                                     * Makes the request visible to drivers through the Active Requests page
                                     * @param dialog
                                     * @param which
                                     */
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        HashMap<String, Object> requestData = new HashMap<>();
                                        requestData.put("rider_name", userName);
                                        requestData.put("rider_phone", userPhone);
                                        requestData.put("rider_email", userEmail);
                                        requestData.put("ride_origin", origin);
                                        requestData.put("ride_destination",destination);
                                        requestData.put("est_distance",estDistance);
                                        requestData.put("est_time",estTime);
                                        requestData.put("est_fare",estFare);
                                        requestData.put("driver_name", null);
                                        requestData.put("driver_phone", null);
                                        requestData.put("driver_email", null);
                                        requestData.put("is_confirmed",false);
                                        requestData.put("is_accepted",false);
                                        requestData.put("origin_address", originAddress);
                                        requestData.put("destination_address", destinationAddress);
                                        requestData.put("is_driver_completed", false);
                                        requestData.put("is_rider_completed", false);
                                        requestData.put("is_cancel", false);

                                        firebaseFirestore
                                                .collection("all_requests")
                                                .add(requestData)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    /**
                                                     * Method is invoked if the ride request was posted successfully
                                                     * @param documentReference
                                                     */
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.i(TAG, "post success");
                                                        Toast.makeText(getContext(), "Request posted.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    /**
                                                     * Method is invoked if the ride request post was unsuccessful
                                                     * Throws an exception
                                                     * @param e
                                                     */
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i(TAG, "pst failed"+e.getMessage());
                                                    }
                                                });
                                        clearMap();
                                        postRequestButton.setEnabled(false);
                                        Intent intent = new Intent(getActivity(), RiderTripProcessActivity.class);
                                        startActivity(intent);
                                    }
                                }).create().show();
                    }
                }
            }
        });

        clearMapButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method is invoked when the clear map button is clicked
             * Clears the map of any pins or polyline
             * @param v
             */
            @Override
            public void onClick(View v) {
                clearMap();
            }
        });
        return view;
    }

    /**
     * This method initializes the map in the rider map fragment
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.rider_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    /**
     * onMapReady method to set start/end locations in rider map fragment
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            initSearch();
        }

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            /**
             * Method is invoked when any point on the map is clicked
             * A origin/destination pin is placed at the clicked location
             * When the map is clicked for a third time, the previous are pins are cleared from the map
             * @param latLng
             */
            @Override
            public void onMapClick(LatLng latLng) {
                if (locationMarkerList.size() >= 2) {
                    clearMap();
                }
                locationMarkerList.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                if (locationMarkerList.size() == 1) {
                    // add the first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else {
                    // add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    startLocation = new MarkerOptions().position(locationMarkerList.get(0));
                    endLocation = new MarkerOptions().position(locationMarkerList.get(1));
                    if (mMap != null) {
                        calculateDirections(startLocation, endLocation);
                    }
                }
                mMap.addMarker(markerOptions);
            }
        });

    }

    /**
     * Get devices current location for rider map fragment
     * Sets current location using 'my current location' button to the bottom right corner
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices location");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                /**
                 * onComplete method for finding current location
                 * @param task
                 */
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();

                        assert currentLocation != null;
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * This method moves the camera to the marked points on the map
     * @param latLng
     * @param zoom
     */
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Method gets device permission to get current location
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
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

        // Did not enable more than one routes
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
                estDistance = result.routes[0].legs[0].distance.toString();
                estTime = result.routes[0].legs[0].duration.toString();
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
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                }
            }
        });
    }

    /**
     * Method clears the map of any pins or lines
     */
    public void clearMap(){
        locationMarkerList.clear();
        mMap.clear();
    }

    /**
     * Method gets the location address from GeoPoint
     * @param geoPoint
     * @return returnAddress
     */
    public String getGeoAddress(GeoPoint geoPoint) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
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
     * Method initializes the search bar
     */
    private void initSearch() {
        Log.d(TAG, "init: initializing");

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /**
             * Method executes the method for searching the location entered in search bar
             * @param v
             * @param actionId
             * @param keyEvent
             * @return
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == keyEvent.ACTION_DOWN || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                    geoLocate();
                    searchEditText.getText().clear();
                }
                return false;
            }
        });
    }

    /**
     * Method searches the map using the geo location
     */
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchEditText.getText().toString();
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "gepLocate: IOException: " + e.getMessage());
        }

        if (list.size() != 0) {
            if (locationMarkerList.size() >= 2) {
                clearMap();
            }
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

//            Toast.makeText(getActivity(), address.toString(), Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            locationMarkerList.add(latLng);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            if (locationMarkerList.size() == 1) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                startLocation = new MarkerOptions().position(locationMarkerList.get(0));
                endLocation = new MarkerOptions().position(locationMarkerList.get(1));
                if (locationMarkerList.size() == 2) {
                    calculateDirections(startLocation, endLocation);
                }
            }
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

}
