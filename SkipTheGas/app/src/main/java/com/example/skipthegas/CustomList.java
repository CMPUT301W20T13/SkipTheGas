package com.example.skipthegas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This is a class which implements a custom list used with the Ride object to display relevant ride data
 */
public class CustomList extends ArrayAdapter<Ride> {
    private ArrayList<Ride> rides;
    private Context context;
    String username;
    String email;
    String phone;

    CustomList(Context context, ArrayList<Ride> rides){
        super(context, 0, rides);
        this.rides = rides;
        this.context = context;
    }

    /**
     * getView method for CustomList class
     * Inflates the associated content layout file and sets the data to the appropriate fields
     * @param position index
     * @param convertView view
     * @param parent parent
     * @return view
     */
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }
        Ride ride = rides.get(position);
        TextView riderName = view.findViewById(R.id.rider_text);
        TextView startLoc = view.findViewById(R.id.start_loc_text);
        TextView endLoc = view.findViewById(R.id.end_loc_text);
        TextView ridePrice = view.findViewById(R.id.price_text);

        username = ride.getRiderName();
        email = ride.getRiderEmail();
        phone = ride.getRiderPhone();

        riderName.setText(ride.getRiderName());
        startLoc.setText(ride.getOriginAddress());
        endLoc.setText(ride.getDestinationAddress());
        ridePrice.setText(ride.getFare());

        return view;
    }

}
