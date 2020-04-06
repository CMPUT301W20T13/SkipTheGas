package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class YourRideAcceptedActivity extends AppCompatActivity {
    Button backButton;
    String requestID;
    String TAG = "YourRideAcceptedActivity";

    TextView startView;
    TextView endView;
    TextView fareView;
    TextView riderView;

    FirebaseFirestore fireDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ride_accepted);

        Intent intent = getIntent();
        requestID = intent.getStringExtra("request_Id");

        backButton = findViewById(R.id.back_button);

        startView = findViewById(R.id.startTextView);
        endView = findViewById(R.id.endTextView);
        fareView = findViewById(R.id.fareTextView);
        riderView = findViewById(R.id.riderTextView);

        fireDB = FirebaseFirestore.getInstance();
        fireDB
                .collection("all_requests")
                .document(requestID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method fetches the request-related information from the firebase database
                     * @param documentSnapshot reference
                     * @param e exception
                     */
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e!=null){
                            Log.d(TAG,"Error:"+e.getMessage());
                            return;
                        }

                        if (documentSnapshot!=null&&documentSnapshot.exists()){
                            String originAddress = (String) documentSnapshot.get("destination_address");
                            String destinationAddress = (String) documentSnapshot.get("origin_address");
                            String estFare = (String) documentSnapshot.get("est_fare");
                            String riderName = (String) documentSnapshot.get("rider_name");
                            startView.setText(originAddress);
                            endView.setText(destinationAddress);
                            fareView.setText(estFare);
                            riderView.setText(riderName);
                        } else {
                            Log.i(TAG,"no such document");
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * Method direct drivers back to the process screen
             * @param view view clicked on
             *
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
