package com.example.skipthegas;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReqListAdapter extends ArrayAdapter {

    private final Activity context;
    private final String[] usernameArray;
    private final String[] startLocArray;
    private final String[] endLocArray;
    private final String[] qr_bucksArray;

    public ReqListAdapter(Activity context, String[] usernameParam, String[] startParam, String[] endParam, String[] qr_bucksParam) {
        super(context, R.layout.content , usernameParam);

        this.context=context;
        this.usernameArray = usernameParam;
        this.startLocArray = startParam;
        this.endLocArray = endParam;
        this.qr_bucksArray = qr_bucksParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.content, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView usernameText = (TextView) rowView.findViewById(R.id.rider_text);
        TextView startLocText = (TextView) rowView.findViewById(R.id.start_loc_text);
        TextView endLocText = (TextView) rowView.findViewById(R.id.end_loc_text);
        TextView qrText = (TextView) rowView.findViewById(R.id.price_text);

        //this code sets the values of the objects to values from the arrays
        usernameText.setText(usernameArray[position]);
        startLocText.setText(startLocArray[position]);
        endLocText.setText(endLocArray[position]);
        qrText.setText(qr_bucksArray[position]);

        return rowView;
    }

}
