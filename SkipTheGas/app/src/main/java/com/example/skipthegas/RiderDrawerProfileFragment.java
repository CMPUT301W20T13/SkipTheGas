package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
 * This fragment is accessed from within the Rider's drawer menu
 * It displays the rider's profile information fetched from firebase
 */
public class RiderDrawerProfileFragment extends Fragment {
    TextView riderNameEditText;
    TextView riderPhoneEditText;
    TextView riderEmailEditText;
    TextView riderQREditText;
    TextView riderProfileHeader;

    Button editButton;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String riderName;
    String riderPhone;
    String riderEmail;
    double QRBucks;
    String TAG = "RiderDrawerProfileFragment:";

    /**
     * onCreateView method for RiderDrawerProfileFragment fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_profile, container, false);
        return view;
    }

    /**
     * onActivityCreated method for DriverDrawerProfileFragment fragment
     * Get rider's information from firebase and display it on profile
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        riderNameEditText = getActivity().findViewById(R.id.rider_profile_user_name_textView);
        riderPhoneEditText = getActivity().findViewById(R.id.rider_profile_phone_TextView);
        riderEmailEditText = getActivity().findViewById(R.id.rider_profile_email_TextView);
        riderQREditText = getActivity().findViewById(R.id.editText6);
        riderProfileHeader = getActivity().findViewById(R.id.rider_profile_header);

        editButton = getActivity().findViewById(R.id.editButton);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null) {
            riderEmail = firebaseUser.getEmail();
        }
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(riderEmail))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    /**
                     * Method sets the edit text fields in the edit rider profile page
                     * @param documentSnapshot
                     * @param e
                     */
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e!=null){
                            Log.i(TAG,"Error occurred "+e.getMessage());
                            return;
                        }
                        if (documentSnapshot!=null && documentSnapshot.exists()) {
                            riderName = documentSnapshot.getString("username");
                            riderPhone = documentSnapshot.getString("phone");
                            String header = riderName + "'s Profile";
                            riderNameEditText.setText(riderName);
                            riderPhoneEditText.setText(riderPhone);
                            riderEmailEditText.setText(riderEmail);
                            riderProfileHeader.setText(header);
                            QRBucks = (double) documentSnapshot.getData().get("QR_bucks");
                            riderQREditText.setText(String.valueOf(QRBucks));
                        } else {
                            Log.d(TAG,"document does not exist");
                        }
                    }
                });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(getActivity(), RiderProfileEditable.class);
                startActivity(editProfileIntent);
            }
        });

    }
}
