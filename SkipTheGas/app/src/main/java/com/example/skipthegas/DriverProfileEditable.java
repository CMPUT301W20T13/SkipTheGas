package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView driverEmail;
    EditText driverPhone;
    TextView driverHeader;
    Button submit_button;

    String TAG = "DriverProfileEditable";

    /**
     * onCreate method for DriverProfileEditable
     * Populates the driver info into the edit text boxes for the user to edit
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_edit_layout);

        driverName = findViewById(R.id.driver_edit_user_name);
        driverEmail = findViewById(R.id.driver_edit_email);
        driverPhone = findViewById(R.id.driver_edit_phone);
        driverHeader = findViewById(R.id.driver_edit_header);
        submit_button = findViewById(R.id.submitButton);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            driverEmailString = firebaseUser.getEmail();
        }
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(driverEmailString))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method fetches the editable driver data from the firebase database
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e!=null) {
                            Log.d(TAG,"Error:"+e.getMessage());
                            return;
                        }
                        if (documentSnapshot!=null&&documentSnapshot.exists()){
                            driverNameString = documentSnapshot.getString("username");
                            driverPhoneString = documentSnapshot.getString("phone");
                            String header = driverNameString + "'s Profile";
                            driverName.setText(driverNameString);
                            driverPhone.setText(driverPhoneString);
                            driverEmail.setText(driverEmailString);
                            driverHeader.setText(header);
                        } else {
                            Log.i(TAG,"no such document");
                        }
                    }
                });

        submit_button.setOnClickListener(new View.OnClickListener() {
            /**
             * Method saves the changed data in the firebase database
             * Returns the user back to the driver profile screen
             * @param v
             */
            @Override
            public void onClick(View v) {
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("users").document(driverEmailString).update("phone", driverPhone.getText().toString());
                firebaseFirestore.collection("users").document(driverEmailString).update("username", driverName.getText().toString());
                submitEdit(v);
            }
        });
    }

    /**
     * This cancels edit mode and does not save changes to the profile, returning the user back
     * to the read-only driver profile screen upon a button click
     * @param view
     */
    public void cancel(View view) {
        finish();
    }

    /**
     * This saves the edits made to the profile screen and updates the driver profile, returning
     * the user to the read-only driver profile screen with the new edited changes, upon a button
     * click
     * @param view
     */
    public void submitEdit(View view) {
        Toast.makeText(this, "Edit Saved Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
