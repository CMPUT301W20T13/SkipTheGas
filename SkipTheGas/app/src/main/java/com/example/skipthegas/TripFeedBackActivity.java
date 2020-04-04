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
    TextView rideFareView;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String requestID;
    String yourDriverEmail;
    String yourDriverName;
    String TAG = "TripFeedBackActivity:";
    double fare;
    long goodCount,badCount;

    /**
     * onCreate method for TripFeedBackActivity
     * Retrieves and displays the associated layout file
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_feedback_layout);

        Intent intent = getIntent();
        requestID = intent.getStringExtra("request_Id");
        yourDriverEmail = intent.getStringExtra("your_driver_email");
        fare = intent.getDoubleExtra("fare",25.0);
        Log.i(TAG, yourDriverEmail);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        goodRating = findViewById(R.id.imageView); // Good rating ToggleButton
        badRating = findViewById(R.id.imageView2); // Bad rating ToggleButton
        goodRatingCntView = findViewById(R.id.textView33);
        badRatingCntView = findViewById(R.id.textView34);
        rideFareView = findViewById(R.id.textView29);
        rideFareView.setText(fare + " QR");

        firebaseFirestore
                .collection("users")
                .document(yourDriverEmail)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method fetches the driver's name from the firebase database
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot!=null){
                            yourDriverName = (String) documentSnapshot.get("username");
                        } else {
                            Log.d(TAG,"no such document");
                        }
                    }
                });

        firebaseFirestore
                .collection("users")
                .document(yourDriverEmail)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method fetches the driver's rating information from the firebase database
                     * Also sets the rating information to the text view fields
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                       if (e != null) {
                           Log.d(TAG, "Error occurred " + e.getMessage());
                           return;
                       }
                       if (documentSnapshot!=null && documentSnapshot.exists()) {
                           goodCount = (long) documentSnapshot.get("good_rating");
                           badCount = (long) documentSnapshot.get("bad_ratings");
                           goodRatingCntView.setText(Long.toString(goodCount));
                           badRatingCntView.setText(Long.toString(badCount));
                       } else {
                           Log.i(TAG,"document does not exist");
                       }
                    }
                });

    }

    // Good rating ToggleButton
    @SuppressLint("SetTextI18n")
    public void onClickGoodRating(View view) {
        goodRating.setEnabled(false);
        badRating.setEnabled(true);

        goodRating.setChecked(true);
        goodCount+=1;
        goodRatingCntView.setText(Long.toString(goodCount));
        if (badRating.isChecked()) {
            badRating.setChecked(false);
            badCount-=1;
            badRatingCntView.setText(Long.toString(badCount));
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
        badRatingCntView.setText(Long.toString(badCount));
        if (goodRating.isChecked()) {
            goodRating.setChecked(false);
            goodCount-=1;
            goodRatingCntView.setText(Long.toString(goodCount));
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