package com.example.skipthegas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class UserContactInfoFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        YourRideRequestActivity activity = (YourRideRequestActivity) getActivity();
        String driverName = activity.getDriverName();
        String driverEmail = activity.getDriverEmail();
        String driverPhone = activity.getDriverPhone();

        String msg1 = "Driver's name: ";
        String msg2 = "Driver's email: ";
        String msg3 = "Driver's phone: ";

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setTitle(driverName + "'s Contact Information")
                .setMessage(msg1 + driverName + "\n" + msg2 + driverEmail + "\n" + msg3 + driverPhone)
                .setPositiveButton("View Driver's Profile", new DialogInterface.OnClickListener() {
                    /**
                     * onClick method that opens driver profile when button is clicked
                     * @param dialogInterface
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Opening Driver Profile", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getActivity(), DriverProfileFragment.class);
//                        startActivity(intent);
                    }
                }).create();
    }
}
