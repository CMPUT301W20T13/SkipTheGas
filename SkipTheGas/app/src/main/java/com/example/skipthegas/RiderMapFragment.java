package com.example.skipthegas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Rider's map activity fragement
 * Rider can view map from this fragment
 */
public class RiderMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private View mapView;

    private static final String TAG = "RiderMapFragment";
    private GeoApiContext mGeoApiContext = null;

    private boolean mLocationPermissionGranted = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 9001;
    private static final float DEFAULT_ZOOM = 15f;

    Button postRequestButton, clearMapButton;
    ArrayList<LatLng> locationMarkerList;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public String estDistance;
    public String estTime;
    public String estFare;
    private MarkerOptions startLocation;
    private MarkerOptions endLocation;
    private String userEmail;
    private String userID;
    private String userName;
    private String userPhone;


    /**
     * onCreateView method for the Rider map fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
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

        // UI initiation
        postRequestButton = view.findViewById(R.id.post_request_button);
        clearMapButton = view.findViewById(R.id.clear_map_button);

        // Retrieve user information from firebase
        userEmail = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(userEmail)
                .addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        userPhone = documentSnapshot.getString("phone");
                        userName = documentSnapshot.getString("username");
                    }
                });

        postRequestButton.setOnClickListener(new View.OnClickListener() {
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
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Requested Ride Details")
                            .setMessage(msg1 + estDistance + "\n" + msg2 + estTime + "\n" + msg3 + estFare + "QR bucks")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                                    requestData.put("is_compete",false);
                                    requestData.put("is_accepted",false);
                                    requestData.put("origin_address", originAddress);
                                    requestData.put("destination_address", destinationAddress);
                                    requestData.put("is_driver_confirmed", false);
                                    requestData.put("is_rider_confirmed", false);
                                    requestData.put("is_cancel", false);

                                    firebaseFirestore
                                            .collection("all_requests")
                                            .add(requestData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.i(TAG, "post success");
                                                    Toast.makeText(getContext(), "Request posted.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i(TAG, "pst failed"+e.getMessage());
                                                }
                                            });
                                    clearMap();
                                }
                            }).create().show();
                }
            }
        });

        clearMapButton.setOnClickListener(new View.OnClickListener() {
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
     * onMapReady method to set current location in rider mao fragment
     * Sets 'my current location' button to the bottom right corner
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
        }

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices location");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
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
     * Get device permission to get current location
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
                estDistance = result.routes[0].legs[0].distance.toString();
                estTime = result.routes[0].legs[0].duration.toString();
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
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                }
            }
        });
    }

    /**
     * Method clears the map
     */
    public void clearMap(){
        locationMarkerList.clear();
        mMap.clear();
    }

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
}
