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
                        Toast.makeText(getActivity(), "Added New Request", Toast.LENGTH_SHORT).show();
                    }
                }).create();
    }
}
