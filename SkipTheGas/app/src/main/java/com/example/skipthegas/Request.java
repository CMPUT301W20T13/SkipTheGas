package com.example.skipthegas;

import com.google.android.gms.maps.model.LatLng;

public class Request {
    private String riderName;
    private String riderNumber;
    private LatLng riderCurrentLocation;
    private LatLng riderDestination;

    Request(String riderName, String riderNumber, LatLng riderCurrentLocation, LatLng riderDestination) {
        this.riderName = riderName;
        this.riderNumber = riderNumber;
        this.riderCurrentLocation = riderCurrentLocation;
        this.riderDestination = riderDestination;
    }

    String getRiderName() {
        return this.riderName;
    }

    String getRiderNumber() {
        return this.riderNumber;
    }

    LatLng getRiderCurrentLocation() {
        return this.riderCurrentLocation;
    }

    LatLng getRiderDestination() {
        return this.riderDestination;
    }
}

