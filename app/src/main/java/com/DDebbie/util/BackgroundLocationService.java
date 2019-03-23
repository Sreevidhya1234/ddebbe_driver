package com.DDebbie.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.DDebbie.entity.UserDetailsPojo;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by appsplanet on 4/3/16.
 */
public class BackgroundLocationService extends Service
{
    public static final String BROADCAST_ACTION = "Location change";
    private static final int TWO_MINUTES = 1000 ;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    Context context = this ;
    Intent intent;
    int counter = 0;
    IOUtils ioUtils;
    UserDetailsPojo userDetailsPojo;

    @Override
    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.LOCATION_INTERVAL, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,Constants.LOCATION_INTERVAL, 0,  listener);

        ioUtils = new IOUtils(context);
        userDetailsPojo = ioUtils.getUser();

        Log.e("service", "Location servicre started");

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createJsonobjectForApiCall();
            }
        },30000);


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                createJsonobjectForApiCall();
            }
        }, 0, 10000);*/






    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }







    public void createJsonobjectForApiCall(){
        try{

            JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            jsonObject.put(Constants.ID,userDetailsPojo.getId());
            jsonObject.put(Constants.LONGITUDE,String.valueOf(IOUtils.CURRENT_LOC.getLongitude()));
            jsonObject.put(Constants.LATITUDE, String.valueOf(IOUtils.CURRENT_LOC.getLatitude()));

            Log.v("JsonObject", jsonObject.toString());

            offerALiftApiCall(jsonObject);

        }catch (Exception e){

        }
    }




 /*
    * regitrationApiCall - In this method we call the api...
    */

    public void offerALiftApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(context);


        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_UPDATE_DRIVER_LOCATION, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());


                        //{"result":true,"response":"Profile updated","customerData":[]}
                        try {

                            if (response.getBoolean("result")) {



                            }else{
                            }


                        }catch (JSONException e){
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
            }
        });



        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        queue.add(jsonObjReq);


    }




    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
                IOUtils.CURRENT_LOC = loc;

                if(IOUtils.isNetworkAvailable(context))
                createJsonobjectForApiCall();

            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }
}