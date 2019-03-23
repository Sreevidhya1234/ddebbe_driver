package com.DDebbie.entity;

/**
 * Created by appsplanet on 8/2/16.
 */
public class TripHistory {
    private String rideId;
    private String date;
    private String address;
    private String vInfo;
    private String time;
    private String totalDistance;
    private String waitingCharges;
    private String driver_rating, customer_rating;
    public boolean box;

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }

    public String getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(String totalCharges) {
        this.totalCharges = totalCharges;
    }

    private String totalCharges;

    public TripHistory(String rideId, String date, String address, String vInfo, String time, String totalDistance, String waitingCharges,
                       String totalCharges, String driver_rating, String customer_rating, boolean box) {
        this.rideId = rideId;
        this.date = date;
        this.address = address;
        this.vInfo = vInfo;
        this.time = time;
        this.totalDistance = totalDistance;
        this.waitingCharges = waitingCharges;
        this.totalCharges = totalCharges;
        this.driver_rating = driver_rating;
        this.customer_rating = customer_rating;
        this.box = box;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriver_rating() {
        return driver_rating;
    }

    public void setDriver_rating(String driver_rating) {
        this.driver_rating = driver_rating;
    }

    public String getCustomer_rating() {
        return customer_rating;
    }

    public void setCustomer_rating(String customer_rating) {
        this.customer_rating = customer_rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getvInfo() {
        return vInfo;
    }

    public void setvInfo(String vInfo) {
        this.vInfo = vInfo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getWaitingCharges() {
        return waitingCharges;
    }

    public void setWaitingCharges(String waitingCharges) {
        this.waitingCharges = waitingCharges;
    }
}
