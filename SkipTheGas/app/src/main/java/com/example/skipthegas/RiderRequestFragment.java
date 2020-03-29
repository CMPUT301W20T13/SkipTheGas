package com.example.skipthegas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * This fragment displays the rider's past and active requests
 * STILL NEED TO COMPLETE IMPLEMENTATION
 */
public class RiderRequestFragment extends Fragment {

    Button rider_current_request;
    Button rider_completed_requests;
    Button rider_cancelled_requests;

    /**
     * Initializes the buttons in the rider request fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_request, container, false);
        rider_current_request = view.findViewById(R.id.currentRequest);
        rider_current_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent currentRequestIntent = new Intent(getActivity(), YourRideRequestActivity.class);
                startActivity(currentRequestIntent);
            }
        });

        rider_completed_requests = view.findViewById(R.id.completedRequests);
        rider_completed_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completedReqIntent = new Intent(getActivity(), CompletedRequests.class);
                startActivity(completedReqIntent);
            }
        });

        rider_cancelled_requests = view.findViewById(R.id.cancelledRequests);
        rider_cancelled_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelledReqIntent = new Intent(getActivity(), CancelledRequests.class);
                startActivity(cancelledReqIntent);
            }
        });

        return view;

    }
}