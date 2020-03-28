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
 * This is a class that displays the active requests of the driver
 */
public class DriverRequestFragment extends Fragment {

    ListView ridesList;
    ArrayAdapter<Ride> rideAdapter;
    ArrayList<Ride> rideDataList;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private String driverName;
    private String driverPhone;
    private String driverEmail;

    private int p;
    public Ride rides;


    /**
     * onCreateView method for DriverRequestFragment fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_request, container, false);

        return view;
    }

    /**
     * Method connects to firebase and retrieves driver info
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        driverEmail = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(driverEmail))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method retrieves driver name and phone from firebase database
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        driverName = documentSnapshot.getString("username");
                        driverPhone = documentSnapshot.getString("phone");
                    }
                });

        ridesList = getActivity().findViewById(R.id.request_list);

        rideDataList = new ArrayList<>();

        rideAdapter = new CustomList(getActivity(), rideDataList);

        ridesList.setAdapter(rideAdapter);

        firebaseFirestore
                .collection("all_requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    /**
                     * Method retrieves posted ride request data from firebase database
                     * @param queryDocumentSnapshots
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        rideDataList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String requestID = doc.getId();
                            boolean accepted = (boolean) doc.getData().get("is_accepted");
                            if (!accepted) {
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
//                                boolean accepted = (boolean) doc.getData().get("is_accepted");
                                boolean completed = (boolean) doc.getData().get("is_compete");
                                String originAddress = (String) doc.getData().get("origin_address");
                                String destinationAddress = (String) doc.getData().get("destination_address");

                                rideDataList.add(new Ride(riderName, riderPhone, riderEmail, origin, destination, dist, time, fare, driverName, driverPhone, driverEmail, false, completed, originAddress, destinationAddress, requestID, false));
                            }
                        }
                        rideAdapter.notifyDataSetChanged();
                    }
                });


//        final ListView openFragment = getActivity().findViewById(R.id.request_list);
//        openFragment.setOnItemClickListener((adapterView, v, i, L)->{
//            new AcceptRequestFragment().show(getFragmentManager(), "Accept Request");
//        });



        Log.v("Ride Info", ridesList.toString());

        final ListView openFragment = getActivity().findViewById(R.id.request_list);
        openFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Method is invoked when request option is selected in drawer menu
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                p = position;
                rides = rideAdapter.getItem(position);
                String userName = rides.getRiderName();
                String start = rides.getOriginAddress();
                String end = rides.getDestinationAddress();
                String fare = rides.getFare();
                String request_ID = rides.getRequestID();
                GeoPoint startLocation = rides.getOrigin();
                GeoPoint endLocation = rides.getDestination();
                double startLat = startLocation.getLatitude();
                double startLog = startLocation.getLongitude();
                double endLat = endLocation.getLatitude();
                double endLog = endLocation.getLongitude();
                //Toast.makeText(getActivity(), "user name is"+userName, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                AcceptRequestFragment acceptRequestFragment = new AcceptRequestFragment();
                bundle.putString("user_name", userName);
                bundle.putString("driver_email", driverEmail);
                bundle.putString("driver_phone", driverPhone);
                bundle.putString("driver_name", driverName);
                bundle.putString("request_ID", request_ID);
                bundle.putDouble("start_lat", startLat);
                bundle.putDouble("start_lng", startLog);
                bundle.putDouble("end_lat", endLat);
                bundle.putDouble("end_lng", endLog);


                acceptRequestFragment.setArguments(bundle);
                acceptRequestFragment.show(getFragmentManager(), "Accept Request");
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Request Information")
//                        .setMessage("Uername: " + userName + "\n\n" + "Start Location:" + start + "\n\nEnd Location: " + end + "\n\nEstimated Fare: " + fare )
//                        .setNegativeButton("Cancel",null)
//                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(getContext(), "Try" + driverName + "!!!", Toast.LENGTH_SHORT).show();
//
//
//                            }
//                        }).create().show();

            }
        });
    }
}
