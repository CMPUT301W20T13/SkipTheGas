package com.example.skipthegas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

/**
 * This is a class which displays a fragment prompting a driver to accept a ride request
 */
public class AcceptRequestFragment extends DialogFragment implements OnMapReadyCallback {

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

    private Polyline currentPolyline;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;



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
                .setNegativeButton("Cancel", null)
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

                    }
                }).create();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.driver_map_accept_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
    }

}
