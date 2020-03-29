package com.example.skipthegas;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
/**
 * This is a class that stores and contains getter methods for all attributes of a request
 */
public class Ride {
    private GeoPoint origin;
    private GeoPoint destination;
    private String originAddress;
    private String destinationAddress;
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
    private boolean isDriverCompleted;
    private boolean isRiderCompleted;
    private String requestID;
    private boolean isConfirmed;
    private boolean isCanceled;

    Ride(String riderName, String riderPhone, String riderEmail, GeoPoint origin,
         GeoPoint destination, String dist, String time, String fare,
         String driverName, String driverPhone, String driverEmail, boolean accepted, boolean isDriverCompleted, boolean isRiderCompleted,
         String originAddress, String destinationAddress, String requestID, boolean isConfirmed, boolean isCanceled) {
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
        this.isDriverCompleted = isDriverCompleted;
        this.isRiderCompleted = isRiderCompleted;
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
        this.requestID = requestID;
        this.isConfirmed = isConfirmed;
        this.isCanceled = isCanceled;
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

    boolean getAccepted() {
        return this.accepted;
    }

    boolean getIsDriverCompleted() {
        return this.isDriverCompleted;
    }

    boolean getIsRiderCompleted() {
        return this.isRiderCompleted;
    }

    String getOriginAddress() {
        return this.originAddress;
    }

    String getDestinationAddress() {
        return this.destinationAddress;
    }

    String getRequestID() {
        return this.requestID;
    }

    boolean getIsConfirmed() {
        return this.isConfirmed;
    }

    boolean getIsCanceled() {
        return this.isCanceled;
    }
}