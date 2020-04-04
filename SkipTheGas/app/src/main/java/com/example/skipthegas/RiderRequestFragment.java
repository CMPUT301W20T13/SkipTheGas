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
 * This fragment displays the rider's requests
 * Allows for selecton between completed and cancelled requests
 */
public class RiderRequestFragment extends Fragment implements View.OnClickListener {

    Button rider_completed_requests;
    Button rider_cancelled_requests;

    /**
     * onCreateView method for the RiderRequestFragment
     * Retrieves and displays the associated layout file
     * Initializes the buttons in the rider request fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_request, container, false);

        rider_completed_requests = view.findViewById(R.id.completedRequests);
        rider_completed_requests.setOnClickListener(this);

        rider_cancelled_requests = view.findViewById(R.id.cancelledRequests);
        rider_cancelled_requests.setOnClickListener(this);

        return view;
    }

    /**
     * Method initializes the completed requests and the cancelled requests button
     * On click will direct to respective requests page
     * @param v
     */
    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.cancelledRequests:
                fragment = new CancelledRequestsFragment();
                replaceFragment(fragment);
                break;
            case R.id.completedRequests:
                fragment = new CompletedRequestsFragment();
                replaceFragment(fragment);
        }
    }

    /**
     * Method replaces the Rider Request fragment with either the Completed or the Cancelled Requests page
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.rider_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}