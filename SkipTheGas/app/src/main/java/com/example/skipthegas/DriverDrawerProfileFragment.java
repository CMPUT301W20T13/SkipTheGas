package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

/**
 * This is a fragment that is nested within the Driver's drawer menu
 * It displays the driver's profile and relevant contact information
 */
public class DriverDrawerProfileFragment extends Fragment {
    TextView driverNameEditText;
    TextView driverPhoneEditText;
    TextView driverEmailEditText;
    TextView driverProfileHeader;

    Button editButton;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String driverName;
    String driverPhone;
    String driverEmail;

    /**
     * onCreateView method for DriverDrawerProfileFragment fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        return view;
    }

    /**
     * onActivityCreated method for DriverDrawerProfileFragment fragment
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        driverNameEditText = getActivity().findViewById(R.id.driver_profile_user_name_textView);
        driverPhoneEditText = getActivity().findViewById(R.id.driver_profile_phone_TextView);
        driverEmailEditText = getActivity().findViewById(R.id.driver_profile_email_TextView);
        driverProfileHeader = getActivity().findViewById(R.id.driver_profile_header);

        editButton = getActivity().findViewById(R.id.editButton);

        // get current user info from firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        driverEmail = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(driverEmail))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method sets the edit text fields in the edit driver profile page
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        driverName = documentSnapshot.getString("username");
                        driverPhone = documentSnapshot.getString("phone");
                        String header = driverName + "'s Profile";
                        driverNameEditText.setText(driverName);
                        driverPhoneEditText.setText(driverPhone);
                        driverEmailEditText.setText(driverEmail);
                        driverProfileHeader.setText(header);
                    }
                });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(getActivity(), DriverProfileEditable.class);
                startActivity(editProfileIntent);
            }
        });


//        driverNameEditText.setText(driverName);
//        driverPhoneEditText.setText(driverPhone);
//        driverEmailEditText.setText(driverEmail);
    }
}
