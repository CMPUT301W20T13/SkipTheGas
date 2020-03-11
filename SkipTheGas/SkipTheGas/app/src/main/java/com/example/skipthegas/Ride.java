package com.example.skipthegas;

import java.util.Date;

public class Ride {
    private String rider;
    private String driver;
    private String startLocation;
    private String endLocation;
    private Date date;
    private Integer price;

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
