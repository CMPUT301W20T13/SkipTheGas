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
 * This fragment displays the rider's completed requests in list view
 */
public class CompletedRequestsFragment extends Fragment implements View.OnClickListener {

    ListView completedReqList;
    ArrayAdapter<Ride> requestAdapter;
    ArrayList<Ride> requestList;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private String riderName;
    private String riderPhone;
    private String riderEmail;

    public Ride requests;

    Button back_button;

    String TAG = "CompletedRequestsFragment";

    /**
     * onCreateView method for CompletedRequestsFragment
     * Inflates the layout view associated with it and enables the back button
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ride_completed_layout, container, false);

        back_button = view.findViewById(R.id.button3);
        back_button.setOnClickListener(this);

        return view;
    }

    /**
     * Method connects to firebase and retrieves rider info
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null) {
            riderEmail = firebaseUser.getEmail();
        }
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(riderEmail))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method retrieves rider name and phone from firebase database
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e!=null){
                            Log.w(TAG,"Error:"+e.getMessage());
                            return;
                        }
                        if (documentSnapshot!=null&&documentSnapshot.exists()) {
                            riderName = documentSnapshot.getString("username");
                            riderPhone = documentSnapshot.getString("phone");
                        } else {
                            Log.i(TAG,"no such document");
                        }

                    }
                });

        completedReqList = getActivity().findViewById(R.id.completed_requests_list);

        requestList = new ArrayList<>();

        requestAdapter = new CustomList(getActivity(), requestList);

        completedReqList.setAdapter(requestAdapter);

        firebaseFirestore
                .collection("all_requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    /**
                     * Method retrieves completed requests data from firebase database
                     * @param queryDocumentSnapshots
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        requestList.clear();
                        if (e!=null) {
                            Log.d(TAG,"Error:"+e.getMessage());
                            return;
                        }
                        if (queryDocumentSnapshots!=null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                String requestID = doc.getId();
                                String req_riderName = (String) doc.getData().get("rider_name");
                                boolean isDriverCompleted = (boolean) doc.getData().get("is_driver_completed");
                                boolean isRiderCompleted = (boolean) doc.getData().get("is_rider_completed");
                                if (req_riderName.equals(riderName) && isDriverCompleted && isRiderCompleted) {
                                    //String riderName = (String) doc.getData().get("rider_name");
                                    String riderPhone = (String) doc.getData().get("rider_phone");
                                    String riderEmail = (String) doc.getData().get("rider_email");
                                    GeoPoint origin = (GeoPoint) doc.getData().get("ride_origin");
                                    GeoPoint destination = (GeoPoint) doc.getData().get("ride_destination");
                                    String dist = (String) doc.getData().get("est_distance");
                                    String time = (String) doc.getData().get("est_time");
                                    String fare = (String) doc.getData().get("est_fare");
                                    String driverName = (String) doc.getData().get("driver_name");
                                    String driverPhone = (String) doc.getData().get("driver_phone");
                                    String driverEmail = (String) doc.getData().get("driver_email");
                                    //boolean isDriverCompleted = (boolean) doc.getData().get("is_driver_completed");
                                    String originAddress = (String) doc.getData().get("origin_address");
                                    String destinationAddress = (String) doc.getData().get("destination_address");

                                    requestList.add(new Ride(riderName, riderPhone, riderEmail, origin, destination, dist, time, fare, driverName, driverPhone, driverEmail, false, true, true, originAddress, destinationAddress, requestID, false, false));
                                }
                            }
                        }
                        requestAdapter.notifyDataSetChanged();
                    }
                });


        Log.v("Ride Info", requestList.toString());

    }

    /**
     * Method initializes the back button
     * Sets it to go back to the previous page (RiderRequestFragment)
     * @param v
     */
    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if (v.getId() == R.id.button3) {
            fragment = new RiderRequestFragment();
            replaceFragment(fragment);
        }
    }

    /**
     * Method replaces the Cancelled Requests fragment with the Ride Requests fragment
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.rider_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}