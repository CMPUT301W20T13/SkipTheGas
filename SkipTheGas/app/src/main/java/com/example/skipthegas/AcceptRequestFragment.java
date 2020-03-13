package com.example.skipthegas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


/**
 * This is a class which displays a fragment prompting a driver to accept a ride request
 */
public class AcceptRequestFragment extends DialogFragment {
    private TextView userNameTextView;
    private TextView startLocationTextView;
    private TextView endLocationTextView;
    private TextView estimatePriceTextView;

    private String userName;
    private String driver_phone;
    private String driver_email;
    private String driver_name;
    private String request_ID;
    private boolean accepted;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;



    public interface OnFragmentInteractionListener {
        void onOkPressed();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.accept_request_layout, container, false);
        userNameTextView = (TextView) view.findViewById(R.id.accept_fragment_rider_name);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String tempUserName = bundle.getString("user_name");
            driver_email = bundle.getString("driver_email");
            driver_name = bundle.getString("driver_name");
            driver_phone = bundle.getString("driver_phone");
            request_ID = bundle.getString("request_ID");
            userName = tempUserName;
            Toast.makeText(getContext(), "Fragment Message: "+ tempUserName, Toast.LENGTH_SHORT).show();
            userNameTextView.setText(tempUserName);
        }
        else {
            Toast.makeText(getActivity(), "Bundle is null", Toast.LENGTH_SHORT).show();
        }
//        userNameTextView.setText();

//        DriverRequestFragment driverRequestFragment = (DriverRequestFragment) getActivity();
//        String userName = driverRequestFragment.getUserName();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.accept_request_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Accept Request For Ride?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //accepts the chosen request by the driver, updates firebase
                        Toast.makeText(getActivity(), "Added New Request", Toast.LENGTH_SHORT).show();
                        accepted = true;
                        firebaseFirestore = FirebaseFirestore.getInstance();
                        firebaseFirestore.collection("all_requests").document(request_ID).update("driver_email", driver_email);
                        firebaseFirestore.collection("all_requests").document(request_ID).update("driver_name", driver_name);
                        firebaseFirestore.collection("all_requests").document(request_ID).update("driver_phone", driver_phone);
                        firebaseFirestore.collection("all_requests").document(request_ID).update("is_accepted", accepted);
                        
                    }
                }).create();
    }
}
