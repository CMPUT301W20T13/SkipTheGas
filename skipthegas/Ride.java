package com.example.skipthegas;

import java.util.Date;

/**
 * This is a class which describes the Ride object
 */

public class Ride {
    private String rider;
    private String driver;
    private String startLocation;
    private String endLocation;
    private Date date;
    private Integer price;

    /**
     * This is a constructor for the Ride object
     * @param rider
     *      The rider associated with a ride; the person who submitted the ride request
     * @param driver
     *      The driver associated with a ride; the person who accepted the ride request
     * @param startLocation
     *      The starting location of the scheduled ride
     * @param endLocation
     *      The end location of the scheduled ride
     * @param date
     *      The time of day at which the ride is to commence
     * @param price
     *      The estimated price (in QR bucks) for the rider to pay the driver for the ride
     */
    Ride(String rider, String driver, String startLocation, String endLocation, Date date, Integer price){
        this.rider = rider;
        this.driver = driver;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.date = date;
        this.price = price;
    }

    String getRider() {return this.rider;}
    String getDriver() {return this.driver;}
    String getStartLocation() {return this.startLocation;}
    String getEndLocation() {return this.endLocation;}
    Date getDate() {return this.date;}
    Integer getPrice() {return this.price;}
}
