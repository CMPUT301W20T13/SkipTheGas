package com.example.skipthegas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This class implements the dialog fragment that displays driver's contact info
 * Invoked when the driver's username is clicked on in the YourRideRequestActivity class
 */
public class DriverContactInfoFragment extends DialogFragment {

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
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    /**
                     * onClick method that opens driver profile when button is clicked
                     * @param dialogInterface
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Closing Contact Info", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }).create();
    }
}
