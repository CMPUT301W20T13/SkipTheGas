package com.example.skipthegas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class DriverDrawerProfileFragment extends Fragment {
    TextView driverNameEditText;
    TextView driverPhoneEditText;
    TextView driverEmailEditText;
    TextView driverProfileHeader;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String driverName;
    String driverPhone;
    String driverEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        driverNameEditText = getActivity().findViewById(R.id.driver_profile_user_name_textView);
        driverPhoneEditText = getActivity().findViewById(R.id.driver_profile_phone_TextView);
        driverEmailEditText = getActivity().findViewById(R.id.driver_profile_email_TextView);
        driverProfileHeader = getActivity().findViewById(R.id.driver_profile_header);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        driverEmail = firebaseUser.getEmail();
        firebaseFirestore
                .collection("users")
                .document(Objects.requireNonNull(driverEmail))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
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



//        driverNameEditText.setText(driverName);
//        driverPhoneEditText.setText(driverPhone);
//        driverEmailEditText.setText(driverEmail);
    }
}
