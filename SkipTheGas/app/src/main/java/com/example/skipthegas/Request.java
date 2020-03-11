package com.example.skipthegas;

//import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Request {
    private GeoPoint origin;
    private GeoPoint destination;
    private String riderName;
    private String riderPhone;
    private String riderEmail;
    private String driverName;
    private String driverPhone;
    private String driverEmail;
    private String dist;
    private String time;
    private String fare;
    private boolean accepted;
    private boolean completed;

    Request(String riderName, String riderPhone, String riderEmail, GeoPoint origin,
            GeoPoint destination, String dist, String time, String fare,
            String driverName, String driverPhone, String driverEmail, boolean accepted, boolean completed) {
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.riderEmail = riderEmail;
        this.origin = origin;
        this.destination = destination;
        this.dist = dist;
        this.time = time;
        this.fare = fare;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverEmail = driverEmail;
        this.accepted = accepted;
        this.completed = completed;
    }

    String getRiderName() {
        return this.riderName;
    }

    String getRiderPhone() {
        return this.riderPhone;
    }

    String getRiderEmail() {
        return this.riderEmail;
    }

    String getDriverName() {
        return this.driverName;
    }

    String getDriverPhone() {
        return this.driverPhone;
    }

    String getDriverEmail() {
        return this.driverEmail;
    }

    GeoPoint getOrigin() {
        return this.origin;
    }

    GeoPoint getDestination() {
        return this.destination;
    }

    String getDist() {
        return this.dist;
    }

    String getTime() {
        return this.time;
    }

    String getFare() {
        return this.fare;
    }

    Boolean getAccepted() {
        return this.accepted;
    }

    Boolean getCompleted() {
        return this.completed;
    }
}

