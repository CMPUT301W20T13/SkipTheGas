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

    /**
     * getter method for Rider Name
     * @return
     */
    String getRiderName() {
        return this.riderName;
    }

    /**
     * getter method for Rider Phone
     * @return
     */
    String getRiderPhone() {
        return this.riderPhone;
    }

    /**
     * getter method for Rider Email
     * @return
     */
    String getRiderEmail() {
        return this.riderEmail;
    }

    /**
     * getter method for Driver Name
     * @return
     */
    String getDriverName() {
        return this.driverName;
    }

    /**
     * getter method for Driver Phone
     * @return
     */
    String getDriverPhone() {
        return this.driverPhone;
    }

    /**
     * getter method for Driver Email
     * @return
     */
    String getDriverEmail() {
        return this.driverEmail;
    }

    /**
     * getter method for Origin location
     * @return
     */
    GeoPoint getOrigin() {
        return this.origin;
    }

    /**
     * getter method for Destination location
     * @return
     */
    GeoPoint getDestination() {
        return this.destination;
    }

    /**
     * getter method for Distance between start & end locations
     * @return
     */
    String getDist() {
        return this.dist;
    }

    /**
     * getter method for TIme required for request completion
     * @return
     */
    String getTime() {
        return this.time;
    }

    /**
     * getter method for Request fare
     * @return
     */
    String getFare() {
        return this.fare;
    }

    /**
     * getter method for is_accepted boolean
     * @return
     */
    boolean getAccepted() {
        return this.accepted;
    }

    /**
     * getter method for is_driver_completed boolean
     * @return
     */
    boolean getIsDriverCompleted() {
        return this.isDriverCompleted;
    }

    /**
     * getter method for is_rider_completed boolean
     * @return
     */
    boolean getIsRiderCompleted() {
        return this.isRiderCompleted;
    }

    /**
     * getter method for Origin location address
     * @return
     */
    String getOriginAddress() {
        return this.originAddress;
    }

    /**
     * getter method for Destination location address
     * @return
     */
    String getDestinationAddress() {
        return this.destinationAddress;
    }

    /**
     * getter method for the Request ID
     * @return
     */
    String getRequestID() {
        return this.requestID;
    }

    /**
     * getter method for is_confirmed boolean
     * @return
     */
    boolean getIsConfirmed() {
        return this.isConfirmed;
    }

    /**
     * getter method for is_cancelled boolean
     * @return
     */
    boolean getIsCanceled() {
        return this.isCanceled;
    }
}