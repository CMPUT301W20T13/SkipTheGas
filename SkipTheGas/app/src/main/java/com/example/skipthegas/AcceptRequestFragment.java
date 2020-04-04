package com.example.skipthegas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is a class which displays a fragment prompting a driver to accept a ride request
 */
public class AcceptRequestFragment extends DialogFragment implements OnMapReadyCallback {

    private static final String TAG = "AcceptRequestFragment";
    private GoogleMap mMap;
    private View mapView;

    private static View view;


    private String userName;
    private String driver_phone;
    private String driver_email;
    private String driver_name;
    private String request_ID;
    private boolean accepted;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;

    private MarkerOptions startLocation;
    private MarkerOptions endLocation;

    private GeoApiContext mGeoApiContext = null;

    private Ride rides;

    FirebaseFirestore firebaseFirestore;



    public interface OnFragmentInteractionListener {
        void onOkPressed();
    }

    /**
     * onCreateView method for AcceptRequestFragment fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
//        View view = layoutInflater.inflate(R.layout.accept_request_layout, container, false);

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }

        try {
            view = inflater.inflate(R.layout.accept_request_layout, container, false);
        } catch (InflateException e) {
            Toast.makeText(getActivity(), "Fragment Success", Toast.LENGTH_SHORT).show();
        }
//        View view =  inflater.inflate(R.layout.accept_request_layout, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            userName = bundle.getString("user_name");
            driver_email = bundle.getString("driver_email");
            driver_name = bundle.getString("driver_name");
            driver_phone = bundle.getString("driver_phone");
            request_ID = bundle.getString("request_ID");
            startLat = bundle.getDouble("start_lat");
            startLng = bundle.getDouble("start_lng");
            endLat = bundle.getDouble("end_lat");
            endLng = bundle.getDouble("end_lng");
            startLocation = new MarkerOptions().position(new LatLng(startLat, startLng)).title("Start Location");
            endLocation = new MarkerOptions().position(new LatLng(endLat, endLng)).title("End Location");

        }
        else {
            Toast.makeText(getActivity(), "Bundle is null", Toast.LENGTH_SHORT).show();
        }
        initMap();
        calculateDirections(startLocation, endLocation);

        return view;
    }


    /**
     * onCreateDialog method for AcceptRequestFragment fragment
     * Allows driver to accept ride request
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.accept_request_layout, null);
        } catch (InflateException e) {
            Toast.makeText(getActivity(), "Bundle is null", Toast.LENGTH_SHORT).show();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Accept Request For Ride?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMap.clear();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //accepts the chosen request by the driver, updates firebase
                        Toast.makeText(getActivity(), "Added New Request", Toast.LENGTH_SHORT).show();
                        accepted = true;
                        firebaseFirestore = FirebaseFirestore.getInstance();
                        firebaseFirestore.collection("all_requests").document(request_ID).update("driver_email", driver_email);
                        firebaseFirestore.collection("all_requests").document(request_ID).update("driver_name", driver_name);
                        firebaseFirestore.collection("all_requests").document(request_ID).update("driver_phone", driver_phone);
                        firebaseFirestore.collection("all_requests").document(request_ID).update("is_accepted", accepted);
                        mMap.clear();
                        Intent intent = new Intent(getActivity(), DriverTripProcessActivity.class);
                        intent.putExtra("request_id", request_ID);
                        startActivity(intent);
                    }
                }).create();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(startLocation);
        mMap.addMarker(endLocation);
        setCamera();
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.driver_map_accept_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    private void setCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder().include(new LatLng(startLat, startLng)).include(new LatLng(endLat, endLng));

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
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                }
            }
        });
    }

}
