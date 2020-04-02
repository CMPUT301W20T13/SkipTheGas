package com.example.skipthegas;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import javax.annotation.Nullable;

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
    Button backButton;
    Button cancelButton;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String requestID;
    String riderName, riderEmail, riderPhone;

    /**
     * onCreate method for YourRideRequestActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_ride_request_layout);

//        final Button cancelVerify = findViewById(R.id.verificationButton);
//        cancelVerify.setOnClickListener((v)->{
//            new CancelFragment().show(getSupportFragmentManager(), "Cancel Request");
//        });

        Intent intent = getIntent();
        requestID = Objects.requireNonNull(intent.getExtras()).getString("request_id");

        start = findViewById(R.id.startTextView);
        end = findViewById(R.id.endTextView);
        fare = findViewById(R.id.fareTextView);
        driver = findViewById(R.id.driverTextView);
        status = findViewById(R.id.statusTextView);
        backButton = findViewById(R.id.back_button);
        cancelButton = findViewById(R.id.verificationButton);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("all_requests")
                .document(requestID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        boolean accepted = (boolean) documentSnapshot.getData().get("is_accepted");
                        String req_fare = (String) documentSnapshot.getData().get("est_fare");
                        String driverName = (String) documentSnapshot.getData().get("driver_name");
                        String destinationAddress = (String) documentSnapshot.getData().get("destination_address");
                        String originAddress = (String) documentSnapshot.getData().get("origin_address");
                        end.setText(destinationAddress);
                        start.setText(originAddress);
                        if (driverName != null) {
                            driver.setText(driverName);
                            driver.setOnClickListener((v)-> {
                                new DriverProfileFragment().show(getSupportFragmentManager(), "View Profile");
                            });
                        }
                        fare.setText(req_fare);
                        if(accepted){
                            status.setText("accepted");
                        }
                        else{
                            status.setText("Not Accepted");
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(YourRideRequestActivity.this)
                        .setTitle("Warning")
                        .setMessage("Are you sure to cancel your request?")
                        .setNegativeButton("Back", null)
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseFirestore.collection("all_requests").document(requestID).update("is_cancel", true);
                                Intent intent = new Intent(getApplicationContext(), RiderDrawerActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).create().show();
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
