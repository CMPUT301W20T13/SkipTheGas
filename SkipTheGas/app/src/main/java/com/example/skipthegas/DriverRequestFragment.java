package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

public class DriverRequestFragment extends Fragment {
    //
//    ListView requestList;
//    ArrayAdapter<Request> requestAdapter;
//    ArrayList<Request> requestDataList;
//
//    RequestCustomList requestCustomList;
    ListView ridesList;
    ArrayAdapter<Ride> rideAdapter;
    ArrayList<Ride> rideDataList;

    private int p;
    public Ride rides;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_request, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        final ListView openFragment = getActivity().findViewById(R.id.request_list);
//        openFragment.setOnItemClickListener((adapterView, v, i, L)->{
//            new AcceptRequestFragment().show(getFragmentManager(), "Accept Request");
//        });

        ridesList = getActivity().findViewById(R.id.request_list);

        String []riders ={"Grersch", "Test2"};
        String []drivers ={"", ""};
        String []startLocs ={"University of Alberta", "SUB"};
        String []endLocs ={"Dairy Queen", "SouthGate"};
        Date[]dates ={new Date(2020, 3, 7), new Date(2020, 3, 10)};
        Integer []prices ={131, 99};

        rideDataList = new ArrayList<>();

        rideAdapter = new CustomList(getActivity(), rideDataList);


        for (int i=0;i<riders.length;i++){
            rideDataList.add(new Ride(riders[i], drivers[i], startLocs[i], endLocs[i], dates[i], prices[i]));
        }

        rideAdapter = new CustomList(getActivity(), rideDataList);
        ridesList.setAdapter(rideAdapter);
        Log.v("Ride Info", ridesList.toString());

        final ListView openFragment = getActivity().findViewById(R.id.request_list);
        openFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                rides = rideAdapter.getItem(position);
                String userName = rides.getRider();
                Toast.makeText(getActivity(), "user name is"+userName, Toast.LENGTH_SHORT).show();
                bundle.putString("user_name", userName);
                AcceptRequestFragment acceptRequestFragment = new AcceptRequestFragment();
                acceptRequestFragment.setArguments(bundle);
                new AcceptRequestFragment().show(getFragmentManager(), "Accept Request");
            }
        });

//        ridesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                p = position;
//                Bundle acceptBundle = new Bundle();
//
//            }
//        });



//        requestList = getActivity().findViewById(R.id.request_list);
//
//        String riderName = "Jun";
//        String riderNumber = "2507971287";
//        LatLng currentLocation = new LatLng(53.5253, -113.5272);
//        LatLng riderDestination = new LatLng(53.4849, -113.5137);
//
//        requestDataList = new ArrayList<>();
//
//
//        requestDataList.add((new Request(riderName, riderNumber, currentLocation, riderDestination)));
//
//        requestAdapter = new RequestCustomList(getActivity(), requestDataList);
//
//        requestList.setAdapter(requestAdapter);


    }

//    public String getUserName() {
//        rides = rideAdapter.getItem(p);
//        return rides.getRider();
//    }

}
