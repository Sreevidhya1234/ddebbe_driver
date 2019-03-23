package com.DDebbie.entity;

import java.util.ArrayList;

/**
 * Created by appsplanet on 8/2/16.
 */
public class Earnings {

    private String earningDate;
    private String totalRides;
    private String totalEarns;
    private String currencySymbol;
    public ArrayList<RideDataPojo> rideData;

    public String getEarningDate() {
        return earningDate;
    }

    public void setEarningDate(String earningDate) {
        this.earningDate = earningDate;
    }

    public String getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(String totalRides) {
        this.totalRides = totalRides;
    }

    public String getTotalEarns() {
        return totalEarns;
    }

    public void setTotalEarns(String totalEarns) {
        this.totalEarns = totalEarns;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public ArrayList<RideDataPojo> getRideData() {
        return rideData;
    }

    public void setRideData(ArrayList<RideDataPojo> rideData) {
        this.rideData = rideData;
    }
}
