package com.DDebbie.entity;

/**
 * Created by appsplanet on 9/2/16.
 */
public class TripInfo {
    private String tripNo, tripBill;

    public TripInfo(String tripNo, String tripBill) {
        this.tripNo = tripNo;
        this.tripBill = tripBill;
    }

    public String getTripNo() {

        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getTripBill() {
        return tripBill;
    }

    public void setTripBill(String tripBill) {
        this.tripBill = tripBill;
    }
}
