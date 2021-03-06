package com.example.skipthegas;

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

import javax.annotation.Nullable;

/**
 * This is a class that governs the editable view of the rider profile screen
 */
public class RiderProfileEditable extends AppCompatActivity {

    TextView usernameDisplay, emailDisplay, qrDisplay;
    EditText phoneEdit;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String username;
    String phone;
    String email;
    double qr_bucks;
    Button submit_button;

    String TAG = "RiderProfileEditable";

    /**
     * onCreate method for RiderProfileEditable
     * Populates the rider info into the edit text boxes for the user to edit
     * @param savedInstanceState saved Instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_edit_layout);

        // UI initiation
        usernameDisplay = findViewById(R.id.editText3);
        emailDisplay = findViewById(R.id.editText4);
        phoneEdit = findViewById(R.id.editText5);
        qrDisplay = findViewById(R.id.editText6);
        submit_button = findViewById(R.id.submitButton);

        // Cloud database initiation
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        email = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(email)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    /**
                     * Method fetches the editable rider data from the firebase database
                     * @param documentSnapshot documentSnapshot is the reference
                     * @param e exception
                     */
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e!=null){
                            Log.i(TAG,"Error:"+e.getMessage());
                            return;
                        }
                        if (documentSnapshot!=null && documentSnapshot.exists()) {
                            email = documentSnapshot.getString("email");
                            phone = documentSnapshot.getString("phone");
                            username = documentSnapshot.getString("username");
                            qr_bucks = (double) documentSnapshot.get("QR_bucks");
                            usernameDisplay.setText(username);
                            emailDisplay.setText(email);
                            phoneEdit.setText(phone);
                            qrDisplay.setText(String.valueOf(qr_bucks));
                        } else {
                            Log.d(TAG,"no such document");
                        }
                    }
                });

        submit_button.setOnClickListener(new View.OnClickListener() {
            /**
             * Method saves the changed data in the firebase database
             * Returns the user back to the driver profile screen
             * @param v view that is clicked
             */
            @Override
            public void onClick(View v) {
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("users").document(email).update("phone", phoneEdit.getText().toString());
                firebaseFirestore.collection("users").document(email).update("username", usernameDisplay.getText().toString());
                submitEdit(v);
            }
        });
    }


    /**
     * This cancels edit mode and does not save changes to the profile, returning the user back
     * to the read-only rider profile screen upon a button click
     * @param view view that is clicked
     */
    public void cancel(View view) {
        finish();
    }

    /**
     * This saves the edits made to the profile screen and updates the rider profile, returning
     * the user to the read-only rider profile screen with the new edited changes, upon a button
     * click
     * @param view view that is clicked
     */
    public void submitEdit(View view) {
        Toast.makeText(this, "Edit Saved Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

}
