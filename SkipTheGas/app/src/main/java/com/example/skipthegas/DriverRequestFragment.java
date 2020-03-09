package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DriverRequestFragment extends Fragment {

    private View view;

    ListView requestList;
    ArrayAdapter<Request> requestAdapter;
    ArrayList<Request> requestDataList;

    RequestCustomList requestCustomList;
    Button showAllRequestsButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_request, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestList = getActivity().findViewById(R.id.request_list);

        String riderName = "Jun";
        String riderNumber = "2507971287";
        LatLng currentLocation = new LatLng(53.5253, -113.5272);
        LatLng riderDestination = new LatLng(53.4849, -113.5137);

        requestDataList = new ArrayList<>();


        requestDataList.add((new Request(riderName, riderNumber, currentLocation, riderDestination)));

        requestAdapter = new RequestCustomList(getActivity(), requestDataList);

        requestList.setAdapter(requestAdapter);

        showAllRequestsButton = getActivity().findViewById(R.id.show_all_requests_button);

        showAllRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activeRequestIntent = new Intent(getActivity(), ActiveRequestsActivity.class);
                startActivity(activeRequestIntent);
            }
        });

    }

}
