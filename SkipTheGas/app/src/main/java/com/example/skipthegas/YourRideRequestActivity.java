package com.example.skipthegas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

import javax.annotation.Nullable;

/**
 * This is a class which governs the rider's current ride request
 * Allows riders to view details regarding the most recent ride request they submitted
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
    String requestID;
    String driver_name, driver_email, driver_phone;
    String TAG = "YourRideRequestActivity";
    /**
     * onCreate method for YourRideRequestActivity
     * Retrieves and displays the associated layout file
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_ride_request_layout);

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
                    /**
                     * Method fetches the request-related information from the firebase database
                     * @param documentSnapshot
                     * @param e
                     */
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e!=null) {
                            Log.i(TAG,"Error:"+e.getMessage());
                            return;
                        }
                        if (documentSnapshot!=null&&documentSnapshot.exists()){
                            boolean accepted = (boolean) documentSnapshot.getData().get("is_accepted");
                            String req_fare = (String) documentSnapshot.getData().get("est_fare");
                            String driverName = (String) documentSnapshot.getData().get("driver_name");
                            String driverPhone = (String) documentSnapshot.getData().get("driver_phone");
                            String driverEmail = (String) documentSnapshot.getData().get("driver_email");
                            String destinationAddress = (String) documentSnapshot.getData().get("destination_address");
                            String originAddress = (String) documentSnapshot.getData().get("origin_address");
                            end.setText(destinationAddress);
                            start.setText(originAddress);
                            if (driverName != null) {
                                driver_name = driverName;
                                driver_email = driverEmail;
                                driver_phone = driverPhone;
                                driver.setText(driverName);
                                driver.setOnClickListener((v)-> {
                                    new DriverContactInfoFragment().show(getSupportFragmentManager(), "View Contact Info");
                                });
                            }
                            fare.setText(req_fare);
                            if(accepted){
                                status.setText("accepted");
                            }
                            else{
                                status.setText("Not Accepted");
                            }
                        } else {
                            Log.d(TAG,"no such document");
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method invoked when the back button is clicked
             * Takes the user back to the previous page
             * @param v
             */
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method invoked when the cancel button is clicked
             * Allows the user to cancel the current request
             * On click displays a dialog box asking for ride cancellation confirmation
             * @param v
             */
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(YourRideRequestActivity.this)
                        .setTitle("Warning")
                        .setMessage("Are you sure to cancel your request?")
                        .setNegativeButton("Back", null)
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            /**
                             * Method invoked when the "Cancel" button in the dialog box is clicked
                             * Updates the firebase by setting the "is_cancel" field for the ride request
                             * @param dialog
                             * @param which
                             */
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseFirestore.collection("all_requests").document(requestID).update("is_cancel", true);
                                onCancelPressed();
                                Intent intent = new Intent(getApplicationContext(), RiderDrawerActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).create().show();
            }
        });

    }

    /**
     * getter method which passes the driver's name to DriverContactInfoFragment
     * Allows the driver info to be displayed in a dialog box when the driver's name is clicked
     * @return driver_name
     */
    public String getDriverName() {
        return driver_name;
    }

    /**
     * getter method which passes the driver's email to DriverContactInfoFragment
     * Allows the driver info to be displayed in a dialog box when the driver's name is clicked
     * @return driver_email
     */
    public String getDriverEmail() {
        return driver_email;
    }

    /**
     * getter method which passes the driver's email to DriverContactInfoFragment
     * Allows the driver info to be displayed in a dialog box when the driver's name is clicked
     * @return driver_phone
     */
    public String getDriverPhone() {
        return driver_phone;
    }

    /**
     * This displays to the screen that a ride has been cancelled
     * if the user selects "Cancel" on the cancel ride dialog box
     */
    public void onCancelPressed(){
        Toast.makeText(this, "Ride Canceled", Toast.LENGTH_SHORT).show();
    }

}