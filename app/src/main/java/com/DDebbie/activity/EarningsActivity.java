package com.DDebbie.activity;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.DDebbie.R;
import com.DDebbie.fragment.EarningListFragment;
import com.DDebbie.fragment.TripInfoFragment;
import com.DDebbie.util.Consts;
import com.DDebbie.util.GPSTracker;
import com.DDebbie.util.StartLocationAlert;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EarningsActivity extends AppCompatActivity implements EarningListFragment.OnFragmentInteractionListener,
        TripInfoFragment.OnFragmentInteractionListener{
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    //Map
    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "";
    Geocoder geocoder;
    private LatLng mCenterLatLong;
    GPSTracker gps;
    // Context mContext;
    Activity mactivity;
    StartLocationAlert alert;
    Double current_latitude = 0.0, current_longitude = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.back_icon);

        mactivity = EarningsActivity.this;

        gps = new GPSTracker(mactivity);


        GpsLocation();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        EarningListFragment earningListFragment = new EarningListFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, earningListFragment, earningListFragment.getClass().getName());
        fragmentTransaction.addToBackStack(earningListFragment.getClass().getName());
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount()==1){
            this.finish();
            overridePendingTransition(0, 0);
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    public void GpsLocation() {


        gps = new GPSTracker(mactivity);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            current_latitude = gps.getLatitude();
            current_longitude = gps.getLongitude();

            // Consts.curretLat = String.valueOf(current_latitude);
            ///  Consts.curretLng = String.valueOf(current_longitude);
            Log.e("GpsLocation", "\ncurrent_latitude==>" + current_latitude + "\ncurrent_longitude==>" + current_longitude);
            // \n is for new line

            Address();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            alert = new StartLocationAlert(mactivity);
        }

    }

    public void Address() {
        geocoder = new Geocoder(mactivity, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(current_latitude, current_longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            Log.e("Check==>", "Check===>" + country);
            Consts.Country = country;

            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("Check==>", "Check===>" + country);
        // mEdtSearchPlace.setText(address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);

    }
}
