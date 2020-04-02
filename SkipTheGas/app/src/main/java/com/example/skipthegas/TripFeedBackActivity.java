package com.example.skipthegas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

/**
 * This is a class that governs the trip feedback screen, which launches upon the completion of a
 * trip and gives the rider an opportunity to rate the driver
 */
public class TripFeedBackActivity extends AppCompatActivity {
    ToggleButton goodRating;
    ToggleButton badRating;

    TextView goodRatingCntView;
    TextView badRatingCntView;
    TextView driverNameView;
    TextView rideFareView;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String requestID;
    String yourDriverEmail;
    String TAG = "TripFeedBackActivity:";
    double fare;
    int goodCount,badCount;
    /**
     * onCreate method for TripFeedBackActivity class
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_feedback_layout);

        Intent intent = getIntent();
        requestID = intent.getStringExtra("request_Id");
        fare = intent.getDoubleExtra("fare",25.0);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        goodRating = findViewById(R.id.imageView); // Good rating ToggleButton
        badRating = findViewById(R.id.imageView2); // Bad rating ToggleButton
        goodRatingCntView = findViewById(R.id.textView33);
        badRatingCntView = findViewById(R.id.textView34);
        driverNameView = findViewById(R.id.textView28);
        rideFareView = findViewById(R.id.textView29);
        rideFareView.setText(fare + " QR");
        firebaseFirestore
                .collection("all_requests")
                .document(requestID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG,"Error occurred " + e.getMessage());
                            return;
                        }
                        if (documentSnapshot!=null && documentSnapshot.exists()) {
                            // Get drivers Email from request Info
                            yourDriverEmail = (String) documentSnapshot.get("driver_name");
                        } else {
                            Log.i(TAG,"document does not exist");
                        }
                    }
                });

        firebaseFirestore
                .collection("users")
                .document(yourDriverEmail)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                       if (e != null) {
                           Log.d(TAG, "Error occurred " + e.getMessage());
                           return;
                       }
                       if (documentSnapshot!=null && documentSnapshot.exists()) {
                           goodCount = (int) documentSnapshot.get("good_rating");
                           badCount = (int) documentSnapshot.get("bad_rating");
                       } else {
                           Log.i(TAG,"document does not exist");
                       }
                    }
                });
        goodRatingCntView.setText(Integer.toString(goodCount));
        badRatingCntView.setText(Integer.toString(badCount));
    }

    // Good rating ToggleButton
    @SuppressLint("SetTextI18n")
    public void onClickGoodRating(View view) {
        goodRating.setEnabled(false);
        badRating.setEnabled(true);

        goodRating.setChecked(true);
        goodCount+=1;
        goodRatingCntView.setText(Integer.toString(goodCount));
        if (badRating.isChecked()) {
            badRating.setChecked(false);
            badCount-=1;
            badRatingCntView.setText(Integer.toString(badCount));
        }
        Toast.makeText(this, "Liked", Toast.LENGTH_SHORT).show();
    }

    // Bad rating ToggleButton
    @SuppressLint("SetTextI18n")
    public void onClickBadRating(View view) {
        badRating.setEnabled(false);
        goodRating.setEnabled(true);

        badRating.setChecked(true);
        badCount+=1;
        badRatingCntView.setText(Integer.toString(badCount));
        if (goodRating.isChecked()) {
            goodRating.setChecked(false);
            goodCount-=1;
            goodRatingCntView.setText(Integer.toString(goodCount));
        }
        Toast.makeText(this, "Disliked", Toast.LENGTH_SHORT).show();
    }

    public void onClickReturn(View view) {
        if (!badRating.isChecked() && !goodRating.isChecked()) {
            Toast.makeText(this, "Please rate your driver", Toast.LENGTH_SHORT).show();
        } else {

            // TODO: ********** ********** Update your driver ratings in Firebase (You are the rider) ********** **********
            /* TODO */
            // Get the document field of the driver
            DocumentReference docRef = firebaseFirestore.collection("users").document(yourDriverEmail);
            if (goodRating.isChecked()) {
                // Update Good Rating
                docRef.update("good_rating", goodCount);
            } else if (badRating.isChecked()) {
                // Update Bad Rating
                docRef.update("bad_ratings", badCount);
            }
            // TODO: ********** ********** Update your driver ratings in Firebase (You are the rider) ********** **********
            Intent riderIntent = new Intent(getApplicationContext(),RiderDrawerActivity.class);
            startActivity(riderIntent);
            finish();
        }
    }
}