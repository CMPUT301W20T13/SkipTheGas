package com.example.skipthegas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This is a class which implements a fragment displaying a driver's profile
 */
public class DriverProfileFragment extends DialogFragment {
    public interface OnFragmentInteractionListener {
        void onOkPressed();
    }

    /**
     * onCreateDialog method for DriverProfileFragment fragment
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.driver_profile_fragment_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Fitzhugh Phillifent's Profile")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    /**
                     * onClick method that opens driver profile when OK button is clicked
                     * @param dialogInterface
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Opening Driver Profile", Toast.LENGTH_SHORT).show();
                    }
                }).create();
    }
}
