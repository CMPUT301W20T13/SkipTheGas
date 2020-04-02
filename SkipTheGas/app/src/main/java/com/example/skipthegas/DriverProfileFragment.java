package com.example.skipthegas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

/**
 * This is a class which implements a fragment displaying a driver's profile
 */
public class DriverProfileFragment extends DialogFragment {

    TextView driverNameTextView;
    TextView driverPhoneTextView;
    TextView driverEmailTextView;
    TextView driverProfileHeader;
    TextView driverGoodRating;
    TextView driverBadRating;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String driverName;
    String driverPhone;
    String driverEmail;
    long goodRating;
    long badRating;

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

        //YourRideRequestActivity activity = (YourRideRequestActivity) getActivity();
        //String driverName = activity.getDriverName();

        driverNameTextView = getActivity().findViewById(R.id.username_textView);
        driverPhoneTextView = getActivity().findViewById(R.id.phone_textView);
        driverEmailTextView = getActivity().findViewById(R.id.email_textView);
        driverProfileHeader = getActivity().findViewById(R.id.textView8);
        driverGoodRating = getActivity().findViewById(R.id.textView10);
        driverBadRating = getActivity().findViewById(R.id.textView11);

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
                        goodRating = (long) documentSnapshot.getData().get("good_rating");
                        badRating = (long) documentSnapshot.getData().get("bad_ratings");
                        String good = String.valueOf(goodRating);
                        String bad = String.valueOf(badRating);

                        String header = driverName + "'s Profile";
                        driverNameTextView.setText(driverName);
                        driverPhoneTextView.setText(driverPhone);
                        driverEmailTextView.setText(driverEmail);
                        driverProfileHeader.setText(header);
                        driverGoodRating.setText(good);
                        driverBadRating.setText(bad);
                    }
                });

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
