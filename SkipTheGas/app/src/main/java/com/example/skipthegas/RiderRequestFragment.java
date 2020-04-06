package com.example.skipthegas;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;





/**
 * This fragment displays the rider's requests
 * Allows for selecton between completed and cancelled requests
 */
public class RiderRequestFragment extends Fragment implements View.OnClickListener {

    Button rider_completed_requests;
    Button rider_cancelled_requests;

    /**
     * onCreateView method for the RiderRequestFragment
     * Retrieves and displays the associated layout file
     * Initializes the buttons in the rider request fragment
     * @param inflater inflate
     * @param container menu container
     * @param savedInstanceState saved Instance
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_request, container, false);

        rider_completed_requests = view.findViewById(R.id.completedRequests);
        rider_completed_requests.setOnClickListener(this);

        rider_cancelled_requests = view.findViewById(R.id.cancelledRequests);
        rider_cancelled_requests.setOnClickListener(this);

        return view;
    }

    /**
     * Method initializes the completed requests and the cancelled requests button
     * On click will direct to respective requests page
     * @param v
     */
    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.cancelledRequests:
                fragment = new CancelledRequestsFragment();
                replaceFragment(fragment);
                break;
            case R.id.completedRequests:
                fragment = new CompletedRequestsFragment();
                replaceFragment(fragment);
        }
    }

    /**
     * Method replaces the Rider Request fragment with either the Completed or the Cancelled Requests page
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.rider_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}