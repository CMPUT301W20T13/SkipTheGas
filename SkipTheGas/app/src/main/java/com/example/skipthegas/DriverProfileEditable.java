package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

/**
 * This is a class that governs the editable view of the driver profile screen
 */
public class DriverProfileEditable extends AppCompatActivity {
    private String driverEmailString;
    private String driverNameString;
    private String driverPhoneString;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    EditText driverName;
    EditText driverEmail;
    EditText driverPhone;
    TextView driverHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_edit_layout);

        driverName = findViewById(R.id.driver_edit_user_name);
        driverEmail = findViewById(R.id.driver_edit_email);
        driverPhone = findViewById(R.id.driver_edit_phone);
        driverHeader = findViewById(R.id.driver_edit_header);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        driverEmailString = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(driverEmailString))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        driverNameString = documentSnapshot.getString("username");
                        driverPhoneString = documentSnapshot.getString("phone");
                        String header = driverNameString + "'s Profile";
                        driverName.setText(driverNameString);
                        driverPhone.setText(driverPhoneString);
                        driverEmail.setText(driverEmailString);
                        driverHeader.setText(header);

                    }
                });



    }

    /**
     * This cancels edit mode and does not save changes to the profile, returning the user back
     * to the read-only driver profile screen upon a button click
     * @param view
     *      Changes screens from the driver profile (editable) to the driver profile (read-only)
     */
    public void cancel(View view) {
        Intent intent = new Intent(this, DriverDrawerActivity.class);
        startActivity(intent);
    }

    /**
     * This saves the edits made to the profile screen and updates the driver profile, returning
     * the user to the read-only driver profile screen with the new edited changes, upon a button
     * click
     * @param view
     *      Changes screens from the driver profile (editable) to the driver profile (read-only)
     */
    public void submitEdit(View view) {
        Intent intent = new Intent(this, DriverDrawerActivity.class);
        startActivity(intent);
    }
}
