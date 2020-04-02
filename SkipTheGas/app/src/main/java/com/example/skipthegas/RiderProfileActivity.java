package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

/**
 * This is a class that governs the rider profile screen
 * NEED TO DELETE
 */
public class RiderProfileActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    TextView riderName, riderEmail, riderPhone, riderQR, header;
//    Button editButton, requestButton, logoutButton;
    double QRBucks;

    /**
     * onCreate method for RiderProfileActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_layout);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        header = findViewById(R.id.rider_profile_title);

        riderName = findViewById(R.id.rider_name);
        riderEmail = findViewById(R.id.rider_email);
        riderPhone = findViewById(R.id.rider_phone_number);
        riderQR = findViewById(R.id.account_balance);

//        editButton = findViewById(R.id.editButton);
//        requestButton = findViewById(R.id.requestButton);
//        logoutButton = findViewById(R.id.logout_button);

        FirebaseUser rider = firebaseAuth.getCurrentUser();
        assert rider != null;
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(rider.getEmail()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            String userName = documentSnapshot.getString("username")+"'s Profile";
                            header.setText(userName);
                            riderName.setText(documentSnapshot.getString("username"));
                            riderEmail.setText(documentSnapshot.getString("email"));
                            riderPhone.setText(documentSnapshot.getString("phone"));

                            QRBucks = (double) documentSnapshot.getData().get("QR_bucks");
                            riderQR.setText(String.valueOf(QRBucks));
                        }
                    }
                });
    }

    /**
     * This opens the editable view of the rider profile upon a button click
     * @param view
     * Changes screens from the rider profile (read-only) to the rider profile (editable)
     */
    public void edit(View view) {
        Intent intent = new Intent(this, RiderProfileEditable.class);
        startActivity(intent);
    }

    /**
     * This opens the browse active requests screen upon a button click
     * @param view
     * Changes screens from the rider profile (read-only) to the rider profile (editable)
     */
    public void requestRides(View view) {
        Intent intent = new Intent(this, RequestRideActivity.class);
        startActivity(intent);
    }

    /**
     * This logs out a user upon a button click
     * @param view
     * Changes screens from the rider profile (read-only) to the login screen
     */
    public void logOut(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
