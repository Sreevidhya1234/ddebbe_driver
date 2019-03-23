package com.DDebbie.util;

/**
 * Created by savera on 10/2/16.
 */
public interface Constants {


    String URL_BASE = "http://www.ddebbie.com/api/index.php?r=";
    String URL_SIGNIN = URL_BASE + "drivers/login";

    String URL_RELOGIN = URL_BASE + "drivers/relogin";

    String URL_REGISTER = URL_BASE + "drivers/register";

    String URL_DRIVER_DOC = URL_BASE + "drivers/driverdetails";

    String URL_CHANGE = URL_BASE + "drivers/validatedriverstatus";

    String URL_DRIVER_UPDATE = URL_BASE + "drivers/driverdetails";

    // String URL_CHECK_DOC = URL_BASE + "drivers/driverdocument&driver_id=" + Consts.driver_id + "&vehicle_id=" + Consts.Vehical_Id;
    String URL_CHECK_DOC = URL_BASE + "drivers/driverdocument";

    String URL_ADD_VEHICLE = URL_BASE + "Drivers/AddVehicle";
    String URL_DRIVER_PROFILE_UPDATE = URL_BASE + "Drivers/ProfileUpdate";
    String URL_UPLOAD_DRIVER_DOC = URL_BASE + "Drivers/UploadDoc";
    String URL_UPLOAD_DRIVER_PHOTO = URL_BASE + "Drivers/UploadPhoto";
    String URL_FORGOT_PASSWORD = URL_BASE + "drivers/forgotpassword";
    String URL_PROFILE_UPDATE = URL_BASE + "customers/profileupdate";
    String URL_GET_VEHICLES = URL_BASE + "drivers/getvehicles";
    String URL_SET_VEHICLES = URL_BASE + "drivers/setactivevehicle";
    String URL_UPDATE_DRIVER_LOCATION = URL_BASE + "drivers/updatelocation";
    String URL_ACCEPT_RIDE = URL_BASE + "drivers/acceptride";
    String URL_REJECT_RIDE = URL_BASE + "drivers/rejectride";
    String URL_ON_LOCATION = URL_BASE + "drivers/onlocation";
    String URL_START_RIDE = URL_BASE + "drivers/startride";
    String URL_END_RIDE = URL_BASE + "drivers/endride";
    String URL_LOG_OUT = URL_BASE + "drivers/logout";
    String URL_RATE_RIDE = URL_BASE + "drivers/rateride";
    String URL_GET_RIDES = URL_BASE + "drivers/getrides";
    String URL_GET_FARE = URL_BASE + "customers/ridedetails";
    String URL_CHANGE_STATUS = URL_BASE + "Drivers/ChangeStatus";
    String URL_EARNINGS = URL_BASE + "drivers/earnings";
    String PDF_SAFETY_INSPECTION_FROM = "http://www.ddebbie.com/website/ddebbie/Safty%20Inspection%20form%20in%20pdf.pdf";
    String PDF_TOWING_TRUCK_DOCS = "http://www.ddebbie.com/towing-truck-docs.pdf";
    String PDF_TAXI_CAR_SUV_LIMO_DRIVERS_DOCS = "http://www.ddebbie.com/taxi-car-SUV-Limo-Drivers-DOC.pdf";


    int LOCATION_INTERVAL = 1000 * 15;
    String STATUS = "status";
    String ONLINE = "1";
    String OFFLINE = "6";


    int RIDE_NEW = 0;
    int RIDE_DRIVER_ASSIGNED = 1;
    int RIDE_DRIVER_ACCEPTED = 2;
    int RIDE_DRIVER_ON_PICK = 3;
    int RIDE_START = 4;
    int RIDE_COMPLETE = 5;
    // ************************************GCM SENDER ID*****************************************

    String GCM_SENDER_ID = "976236736209";


    // ************************************HEADER KEYS*****************************************

    String CONTENT_TYPE = "Content-Type";
    String API_KEY = "Api-Key";

    // ************************************HEADER VALUES*****************************************

    String CONTENT_TYPE_VALUE = "application/json";
    String API_KEY_VALUE = "8b64d2451b7a8f3fd17390f88ea35917";


    // ************************************PARAMETER KEYS*****************************************

    String ID = "id";
    String NEW_EMAIL = "newEmail";
    String CUSTOMER_NAME = "customerName";
    String EMAIL = "email";
    String PASSWORD = "password";
    String PAGE_MO = "pageNum";
    String DEVICE_TOKEN = "deviceToken";
    String DEVICE_TYPE = "deviceType";
    String LONGITUDE = "longitude";
    String LATITUDE = "latitude";
    String VEHICAL_ID = "vehicleId";
    String CONTACT_NUMBER = "contactNumber";
    String EMERGENCY_CONTACT_NUMBER = "emgContactNumber";
    String RIDE_ID = "rideId";
    String DRIVER_ID = "driverId";
    String RATE = "rating";
    String WAIT_TIME = "waitingTime";
    String WAIT_CHARGE = "waitingCharges";
    String WAIT_COUNTRY = "country";


    //    String INTENT_DRIVER_ID="driverId";
//    String INTENT_DRIVER_EMAIL="driverEmail";
    String INTENT_QUICK_SIGNUP = "quick_signup";

    enum QUICK_SIGNUP_STEP {NONE, PERSONAL, VEHICLE, DOCS}


}
