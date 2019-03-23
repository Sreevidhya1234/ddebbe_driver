package com.DDebbie.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by appsplanet on 16/6/16.
 */
public class Destination implements Serializable {
    @SerializedName("long")
    private String lng;

    private String lat, address;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
