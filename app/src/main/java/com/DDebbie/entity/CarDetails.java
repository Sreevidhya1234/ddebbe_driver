package com.DDebbie.entity;

/**
 * Created by appsplanet on 19/2/16.
 */
public class CarDetails {
    String vehicleId, vehicleNumber, year, model, numOfDoors, photoPath;
    String ownership, licensing, insuranceCompName, driverId, status, vehicleType;

    public CarDetails(String vehicleId, String vehicleNumber,
                      String year, String model, String numOfDoors, String photoPath,
                      String ownership, String licensing, String insuranceCompName,
                      String driverId, String status, String vehicleType) {
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.year = year;
        this.model = model;
        this.numOfDoors = numOfDoors;
        this.photoPath = photoPath;
        this.ownership = ownership;
        this.licensing = licensing;
        this.insuranceCompName = insuranceCompName;
        this.driverId = driverId;
        this.status = status;
        this.vehicleType = vehicleType;
    }

    public CarDetails(String vehicleNumber, String licensing) {
        this.vehicleNumber = vehicleNumber;
        this.licensing = licensing;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumOfDoors() {
        return numOfDoors;
    }

    public void setNumOfDoors(String numOfDoors) {
        this.numOfDoors = numOfDoors;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getLicensing() {
        return licensing;
    }

    public void setLicensing(String licensing) {
        this.licensing = licensing;
    }

    public String getInsuranceCompName() {
        return insuranceCompName;
    }

    public void setInsuranceCompName(String insuranceCompName) {
        this.insuranceCompName = insuranceCompName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
