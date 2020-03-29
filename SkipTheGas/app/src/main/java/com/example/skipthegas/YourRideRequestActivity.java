package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * This is a class which governs the "your ride request" screen, where riders can view details
 * regarding the ride request they have submitted
 */
public class YourRideRequestActivity extends AppCompatActivity {
    TextView start;
    TextView end;
    TextView fare;
    TextView status;
    TextView driver;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String riderName, riderEmail, riderPhone;

    /**
     * onCreate method for YourRideRequestActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_ride_request_layout);

        setContentView(R.layout.your_ride_request_layout);
        final Button cancelVerify = findViewById(R.id.verificationButton);
        cancelVerify.setOnClickListener((v)->{
            new CancelFragment().show(getSupportFragmentManager(), "Cancel Request");
        });
        //final TextView openProfile = findViewById(R.id.Driver);
        //openProfile.setOnClickListener((v)->{
          //  new DriverProfileFragment().show(getSupportFragmentManager(), "View Profile");
        //});

        start = findViewById(R.id.startTextView);
        end = findViewById(R.id.endTextView);
        fare = findViewById(R.id.fareTextView);
        driver = findViewById(R.id.driverTextView);
        status = findViewById(R.id.statusTextView);
        driver.setOnClickListener((v)-> {
            new DriverProfileFragment().show(getSupportFragmentManager(), "View Profile");
        });

        //driver.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
              //  Intent editProfileIntent = new Intent(YourRideRequestActivity.this, DriverProfileFragment.class);
                //YourRideRequestActivity.this.startActivity(editProfileIntent);
            //}
        //});

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        riderEmail = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(riderEmail))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method retrieves driver name and phone from firebase database
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        riderName = documentSnapshot.getString("username");
                        riderPhone = documentSnapshot.getString("phone");
                    }
                });
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
                        //rideDataList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String requestID = doc.getId();
                            boolean accepted = (boolean) doc.getData().get("is_accepted");
                            String req_riderName = (String) doc.getData().get("rider_name");
                            boolean completed = (boolean) doc.getData().get("is_compete");
                            if (riderName.equals(req_riderName) && !completed){
                                String req_fare = (String) doc.getData().get("est_fare");
                                String driverName = (String) doc.getData().get("driver_name");
                                String destinationAddress = (String) doc.getData().get("destination_address");
                                String originAddress = (String) doc.getData().get("origin_address");
                                end.setText(destinationAddress);
                                start.setText(originAddress);
                                driver.setText(driverName);
                                fare.setText(req_fare);
                                if(accepted){
                                    status.setText("accepted");
                                }
                                else{
                                    status.setText("not accepted");
                                }


                                //rideDataList.add(new Ride(riderName, riderPhone, riderEmail, origin, destination, dist, time, fare, driverName, driverPhone, driverEmail, false, completed, originAddress, destinationAddress, requestID, false));
                            }
                        }
                        //rideAdapter.notifyDataSetChanged();
                    }
                });

    }



    /**
     * This displays to the screen that a ride has been cancelled if the user selects "ok" on the
     * cancel ride fragment
     */
    public void onOkPressed(){
        Toast.makeText(this, "Ride Canceled", Toast.LENGTH_SHORT).show();
    }
}
