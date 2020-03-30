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
 * This fragment displays the rider's cancelled requests in list view
 */
public class CancelledRequestsFragment extends Fragment {

    ListView cancelledReqList;
    ArrayAdapter<Ride> requestAdapter;
    ArrayList<Ride> requestList;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private String riderName;
    private String riderPhone;
    private String riderEmail;

    private int p;
    public Ride requests;

    /**
     * onCreateView method for CancelledRequestsFragment class
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ride_cancelled_layout, container, false);
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
        riderEmail = firebaseUser.getEmail();
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
                        riderName = documentSnapshot.getString("username");
                        riderPhone = documentSnapshot.getString("phone");
                    }
                });

        cancelledReqList = getActivity().findViewById(R.id.cancelled_requests_list);

        requestList = new ArrayList<>();

        requestAdapter = new CustomList(getActivity(), requestList);

        cancelledReqList.setAdapter(requestAdapter);

        firebaseFirestore
                .collection("all_requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    /**
                     * Method retrieves cancelled requests data from firebase database
                     * @param queryDocumentSnapshots
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        requestList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String requestID = doc.getId();
                            boolean cancelled = (boolean) doc.getData().get("is_cancel");
                            if (cancelled) {
                                String riderName = (String) doc.getData().get("rider_name");
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
                                boolean isDriverCompleted = (boolean) doc.getData().get("is_compete");
                                String originAddress = (String) doc.getData().get("origin_address");
                                String destinationAddress = (String) doc.getData().get("destination_address");

                                requestList.add(new Ride(riderName, riderPhone, riderEmail, origin, destination, dist, time, fare, driverName, driverPhone, driverEmail, false, isDriverCompleted, false, originAddress, destinationAddress, requestID, false, false));
                            }
                        }
                        requestAdapter.notifyDataSetChanged();
                    }
                });


        Log.v("Ride Info", requestList.toString());

        final ListView openFragment = getActivity().findViewById(R.id.cancelled_requests_list);
        openFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Method is invoked when cancelled request fragment is opened from rider requests page
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                p = position;
                requests = requestAdapter.getItem(position);
                String userName = requests.getRiderName();
                String start = requests.getOriginAddress();
                String end = requests.getDestinationAddress();
                String fare = requests.getFare();
                String request_ID = requests.getRequestID();
                GeoPoint startLocation = requests.getOrigin();
                GeoPoint endLocation = requests.getDestination();
                double startLat = startLocation.getLatitude();
                double startLog = startLocation.getLongitude();
                double endLat = endLocation.getLatitude();
                double endLog = endLocation.getLongitude();

                Bundle bundle = new Bundle();
                AcceptRequestFragment acceptRequestFragment = new AcceptRequestFragment();
                bundle.putString("user_name", userName);
                bundle.putString("rider_email", riderEmail);
                bundle.putString("rider_phone", riderPhone);
                bundle.putString("rider_name", riderName);
                bundle.putString("request_ID", request_ID);
                bundle.putDouble("start_lat", startLat);
                bundle.putDouble("start_lng", startLog);
                bundle.putDouble("end_lat", endLat);
                bundle.putDouble("end_lng", endLog);
            }
        });
    }
}
