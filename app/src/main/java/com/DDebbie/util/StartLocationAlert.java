package com.DDebbie.util;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class StartLocationAlert extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LOCATION1 = 1;
    Context context;
    Activity activity;
    Fragment fragment;
    GoogleApiClient googleApiClient;
    boolean status = false;
    GpsObserver gpsObserver;

    public StartLocationAlert(Context context) {
        this.context = context;
        googleApiClient = getInstance();
        if (googleApiClient != null) {
            //googleApiClient.connect();
            settingsrequest();
            googleApiClient.connect();
        }
        gpsObserver = (GpsObserver) activity;
    }

    public GoogleApiClient getInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    public void settingsrequest() {
        Log.e("settingsrequest", "Comes");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.CANCELED:
                        Toast.makeText(context, "CANCELED", Toast.LENGTH_SHORT).show();
                        break;
/*
                    case LocationSettingsStatusCodes.API_NOT_CONNECTED:
                        Toast.makeText(context, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
                        break;
*/
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        // Log.e("Application","Button Clicked");
/*                        setgpsStatus(true);
                        GPSTracker gps = new GPSTracker(context);
                        double latitude = 0, longitude=0;
                        if (gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                        }
                        String latLongString = "Lat:" + latitude + "\nLong:" + longitude;
                        MLog.e("current_latLongString:", "" + latLongString);
                        Toast.makeText(context, latLongString, Toast.LENGTH_SHORT).show();*/
                        gpsObserver.onSuccess(true);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        // Log.e("Application","Button Clicked1");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) context, REQUEST_LOCATION1);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e("Applicationsett", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //Log.e("Application","Button Clicked2");
                        Toast.makeText(context, "Location is Enabled", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public boolean gpsStatus() {
        return status;
    }

    public void setgpsStatus(boolean status1) {
        status = status1;
    }
}