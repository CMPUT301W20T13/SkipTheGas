package com.example.skipthegas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
/**
 * This is a class that contains a list implementation of requests
 */
public class RequestCustomList extends ArrayAdapter<Request> {

    private ArrayList<Request> requests;
    private Context context;

    public RequestCustomList(Context context, ArrayList<Request> requests) {
        super(context, 0, requests);
        this.requests = requests;
        this.context = context;
    }

    /**
     * Method sets up the view for browsing requests
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.driver_request_content, parent, false);
        }

        Request request = requests.get(position);

        TextView riderName = view.findViewById(R.id.content_rider_name);
        TextView riderNumber = view.findViewById(R.id.rider_number);
        TextView riderCurrentLocation = view.findViewById(R.id.rider_current_location);
        TextView riderDestination = view.findViewById(R.id.rider_destination);

        riderName.setText(request.getRiderName());
        riderNumber.setText(request.getRiderPhone());

        //String locationString = "" + request.getOrigin().latitude +", " +request.getRiderCurrentLocation().longitude;

        //String destinationString = "" + request.getRiderDestination().latitude +", " +request.getRiderDestination().longitude;

        //riderCurrentLocation.setText(locationString);
        //riderDestination.setText(destinationString);

        return view;
    }
}
