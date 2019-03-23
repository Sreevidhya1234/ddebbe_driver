package com.DDebbie.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
/*import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;*/
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.DDebbie.MyService;
import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.adapter.NavListAdapter;
import com.DDebbie.entity.NavItem;
import com.DDebbie.entity.RideAccept;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.fragment.ArrivedTo1stDestinationFragment;
import com.DDebbie.fragment.ArrivedToDestinationFragment;
import com.DDebbie.fragment.DestinationFragment;
import com.DDebbie.fragment.FareSummaryFragment;
import com.DDebbie.fragment.HomeFragment;
import com.DDebbie.fragment.JobNotificationFragment;
import com.DDebbie.fragment.JobPreviewFragment;
import com.DDebbie.fragment.ManualDestinationFragment;
import com.DDebbie.fragment.PickupInformationFragment;
import com.DDebbie.fragment.PickupNavigationFragment;
import com.DDebbie.util.Constants;
import com.DDebbie.util.Consts;
import com.DDebbie.util.DirectionParser;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.DDebbie.util.MenuArrowDrawable;
import com.DDebbie.util.Util;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, ArrivedTo1stDestinationFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener, JobNotificationFragment.OnBackPressListener,
        PickupInformationFragment.OnFragmentInteractionListener, PickupNavigationFragment.OnFragmentInteractionListener,
        JobPreviewFragment.OnFragmentInteractionListener, ManualDestinationFragment.OnFragmentInteractionListener,
        DestinationFragment.OnFragmentInteractionListener, ArrivedToDestinationFragment.OnFragmentInteractionListener,
        FareSummaryFragment.OnFragmentInteractionListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    ArrayList<LatLng> points;
    SharedPreferences sharedPreferences;
    private GoogleMap mMap;
    protected static final String TAG = "location-updates-sample";
    public static final String TAG1 = DashboardActivity.class.getSimpleName();
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;


    private ListView mNavListView;
    private ArrayList<NavItem> arrayList;
    private NavListAdapter navListAdapter;
    private DrawerLayout drawer;
    private MenuArrowDrawable menuArrowDrawable;
    private NavigationView navigationView;
    // private LinearLayout mLineatBottom;
    private HomeFragment homeFragment;
    public static LinearLayout mLinearNetworkToggle;
    //private ImageView mImgCheckBox;
    private int networkToggle;
    private FragmentManager fragmentManager;
    private LatLng currentLatLang;
    public static Marker mCurrentMarker;
    private Context context = this;

    UserDetailsPojo userDetailsPojo;
    IOUtils ioUtils;
    FragmentTransaction fragmentTransaction;
    int onBackFlag;
    public static boolean isActive = false;
    private RideAccept rideAccept;
    private LatLng pickupLat;
    private ProgressDialog progressDialog;
    private LinearLayout viewGroup;
    private TextView txtTimer;
    private Button btnPause;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private SwitchCompat switchCompat;


    Activity activity;

    TextView text_cancel, text_ok, text_add, text_response, text_add2;
    LinearLayout linear_ok, linear_cancel;

    ImageView img_go, img_cross;
    TextView edit_feed;
    TextView text_send, text_title;
    LinearLayout linear_send;


    public String Dri_id = "";
    public String V_id = "";

    Session session;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();


        session = new Session(this);
        Consts.driver_id = String.valueOf(session.getUser_id());
        Log.e("Driver_id", "Driver_id===>" + Consts.driver_id);

        Dri_id = String.valueOf(session.getUser_id());

        Log.e("dri==>", "dri==>" + Dri_id);
        Drive_view(Dri_id);

        isActive = true;
        onBackFlag = 0;


       /* if (Consts.Splash.equalsIgnoreCase("true")) {

            Consts.Status = "1";
            Consts.Splash = "false";

            createJsonobjectForOffline();
            stopService();

        } else {


        }
*/

        //CHECK PERMISSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checkLocationPermission();
            getPermissionsDetails();
        }else {
            getPermissionsDetails();
        }

        /* if (Build.VERSION.SDK_INT >= 23) {
            checkLocationPermission();
            // Marshmallow+
        } else {
            checkLocationPermission();
            // Pre-Marshmallow
        }
*/
        arrayList = new ArrayList<>();
        arrayList.add(new NavItem("Home", R.mipmap.home));
        arrayList.add(new NavItem("Profile", R.mipmap.about));
        arrayList.add(new NavItem("Trip History", R.mipmap.advance_booking));
        arrayList.add(new NavItem("Earnings", R.mipmap.payment));
        arrayList.add(new NavItem("Help", R.mipmap.info));
        arrayList.add(new NavItem("About Us", R.mipmap.info));
        arrayList.add(new NavItem("Log Out", R.mipmap.signout));


        navListAdapter = new NavListAdapter(arrayList, DashboardActivity.this);
        mNavListView.setAdapter(navListAdapter);
        mNavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                switch (i) {
                    case 0:
                        HomeFragment homeFragment = new HomeFragment();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.container, homeFragment);
                        fragmentTransaction.commit();

                        break;
                    case 1:

                      /*  ProfileFragment profileFragment = new ProfileFragment();
                        fragmentTransaction.addToBackStack(profileFragment.getClass().getName());
                        fragmentTransaction.replace(R.id.container, profileFragment);
                        fragmentTransaction.commit();*/

                        Intent intentProfile = new Intent(DashboardActivity.this, ProfileActivity.class);
                        intentProfile.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentProfile);

                        break;
                    case 2:
                        Intent intentTrip = new Intent(DashboardActivity.this, TripHistoryActivity.class);
                        intentTrip.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentTrip);
                        break;
                    case 3:
                        Intent intentEarn = new Intent(DashboardActivity.this, EarningsActivity.class);
                        intentEarn.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentEarn);
                        break;
                    case 4:
                        Intent intentCon = new Intent(DashboardActivity.this, ContactUsActivity.class);
                        intentCon.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentCon);
                        break;
                    case 5:
                        Intent intentAbout = new Intent(DashboardActivity.this, AboutUsActivity.class);
                        intentAbout.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentAbout);
                        break;
                    case 6:
                        if (IOUtils.isNetworkAvailable(context)) {
                            createJsonobjectForLogout();
                        } else {
                            IOUtils.setAlertForActivity(context);
                        }

                        break;

                }
                drawer.closeDrawer(GravityCompat.START);

            }
        });


        int status = ioUtils.getRideStatus();
        Log.v("ride_status", "" + status);
        switch (status) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, homeFragment, homeFragment.getClass().getName());
                fragmentTransaction.addToBackStack(homeFragment.getClass().getName());
                fragmentTransaction.commit();
                break;
            case 1:
                JobNotificationFragment jobNotificationFragment = new JobNotificationFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, jobNotificationFragment, jobNotificationFragment.getClass().getName());

                fragmentTransaction.addToBackStack(jobNotificationFragment.getClass().getName());
                fragmentTransaction.commit();
                break;
            case 2:
                JobPreviewFragment jobPreviewFragment = new JobPreviewFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, jobPreviewFragment, jobPreviewFragment.getClass().getName());

                //  fragmentTransaction.addToBackStack(jobPreviewFragment.getClass().getName());
                //   fragmentTransaction.commit();
                break;
            case 3:
                ManualDestinationFragment manualDestinationFragment = new ManualDestinationFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, manualDestinationFragment, manualDestinationFragment.getClass().getName());
                fragmentTransaction.addToBackStack(manualDestinationFragment.getClass().getName());
                fragmentTransaction.commit();
                break;
            case 4:
                ArrivedToDestinationFragment arrivedToDestinationFragment = new ArrivedToDestinationFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, arrivedToDestinationFragment, arrivedToDestinationFragment.getClass().getName());
                fragmentTransaction.addToBackStack(arrivedToDestinationFragment.getClass().getName());
                fragmentTransaction.commit();
                break;
            case 5:
                FareSummaryFragment fareSummaryFragment = new FareSummaryFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fareSummaryFragment, fareSummaryFragment.getClass().getName());
                fragmentTransaction.addToBackStack(fareSummaryFragment.getClass().getName());
                fragmentTransaction.commit();
                break;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();

    }

    public void settingsrequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(DashboardActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }



/*
    public void hideProfile(boolean check){
        if(check == true){
            mLineatBottom.setVisibility(View.GONE);
        }else {
            mLineatBottom.setVisibility(View.VISIBLE);

        }

    }*/

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            JobNotificationFragment jobNotificationFragment = (JobNotificationFragment) getSupportFragmentManager().findFragmentByTag("JobNotificationFragment");
            if (jobNotificationFragment != null && jobNotificationFragment.isVisible()) {
                // add your code here
                super.onBackPressed();
            }

            if (fragmentManager != null) {
                if (fragmentManager.getBackStackEntryCount() == 1) {
                    this.finish();
                } else {
//                super.onBackPressed();

                }
            }
            if (onBackFlag == 1) {
                super.onBackPressed();
                onBackFlag = 0;
            }

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //isActive = false;
    }

    Point p;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void init() {

        activity = DashboardActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rideAccept", "");
        rideAccept = gson.fromJson(json, RideAccept.class);
        menuArrowDrawable = new MenuArrowDrawable(this);
        getSupportActionBar().setHomeAsUpIndicator(menuArrowDrawable);
        ioUtils = new IOUtils(context);
        userDetailsPojo = ioUtils.getUser();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavListView = (ListView) findViewById(R.id.listNav);
        //mLineatBottom = (LinearLayout) findViewById(R.id.relativeBottom);
        mLinearNetworkToggle = (LinearLayout) findViewById(R.id.linearNetworkToggle);
        //mImgCheckBox = (ImageView) findViewById(R.id.imgCheckbox);
        mLinearNetworkToggle.setOnClickListener(this);
        progressDialog = new ProgressDialog(context);
        viewGroup = (LinearLayout) findViewById(R.id.popup);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);
        txtTimer = (TextView) layout.findViewById(R.id.txtTime);
        btnPause = (Button) layout.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this);
        switchCompat = (SwitchCompat) findViewById(R.id.switch_compat);
        Log.e("driver_stsus_first", "Online" + ioUtils.getDriverStatus());

        if (ioUtils.getRideStatus() == Constants.RIDE_NEW || ioUtils.getRideStatus() == Constants.RIDE_DRIVER_ASSIGNED) {
            startService();
            showOnOffToggle(false);
        } else {

            if (ioUtils.getDriverStatus()) {
                switchCompat.setChecked(true);
                startService();
            } else {
                switchCompat.setChecked(false);
                stopService();
            }

        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (IOUtils.isNetworkAvailable(DashboardActivity.this)) {
                    if (ioUtils.getRideStatus() != Constants.RIDE_NEW || ioUtils.getRideStatus() != Constants.RIDE_DRIVER_ASSIGNED) {

                        // Toast.makeText(DashboardActivity.this, "Ride is active", Toast.LENGTH_SHORT).show();

                        switchCompat.setChecked(false);


                        if (Consts.Driver_Enable.equalsIgnoreCase("true")) {

                            Consts.Driver_Enable = "false";
                            if (isChecked) {
                                //switchCompat.setText("Go offline");
                                ioUtils.setDriverStatus(true);
                                createJsonobjectForOffline();
                                startService();

                            } else {
                                //switchCompat.setText("Go online");
                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                stopService();
                            }

                            if (ioUtils.getDriverStatus()) {
                                switchCompat.setChecked(true);
                            } else {
                                switchCompat.setChecked(false);
                            }

                        }/* else if (Consts.Status.equalsIgnoreCase("5")) {
                            ioUtils.setDriverStatus(false);
                            createJsonobjectForOffline();
                            stopService();

                        }*/ else {

                            Dri_id = String.valueOf(session.getUser_id());

                            V_id = Consts.Vehical_Id;

                            Log.e("dri==>", "dri==>" + Dri_id);
                            Log.e("Vehical_id==>", "Vehical_id==>" + V_id);

                            Check_Doc(Dri_id, V_id);


                        }


                    } else {
                        if (isChecked) {
                            //switchCompat.setText("Go offline");
                            ioUtils.setDriverStatus(true);
                            createJsonobjectForOffline();
                            startService();

                        } else {
                            //switchCompat.setText("Go online");
                            ioUtils.setDriverStatus(false);
                            createJsonobjectForOffline();
                            stopService();
                        }

                        if (ioUtils.getDriverStatus()) {
                            switchCompat.setChecked(true);
                        } else {
                            switchCompat.setChecked(false);
                        }
                    }

                    Log.e("driver_stsus", "Online" + ioUtils.getDriverStatus());
                }

            }
        });

    }


    public void showOnOffToggle(boolean s) {
        if (s)
            switchCompat.setVisibility(View.VISIBLE);
        else
            switchCompat.setVisibility(View.GONE);

    }


    public void clearMap() {
        LatLng latLng = mCurrentMarker.getPosition();
        if (mMap != null) {
            mMap.clear();
            MarkerOptions options = new MarkerOptions().title("Pickup location")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                    .anchor(0.5f, 0.5f);
            options.position(latLng);
            mCurrentMarker = mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            currentLatLang = latLng;
        }
    }


    public int getWatingTime() {
        return mins;
    }


    public String getWatingCharge() {
        Log.v("CENTS mins", mins + " ");
        double charge = 0;
        if (mins > 0) {

            //  IOUtils ioUtils = new IOUtils(DashboardActivity.this);
            //VEHICLE TYPE TAXT


            charge = mins * 0.35;
           /* if (ioUtils.getVehicleType().equalsIgnoreCase("Taxi")) {
                charge = mins * 0.25;
            } else {
                charge = mins * 0.35;
            }*/

            Log.v("CENTS", charge + " ");

        }
        return charge + "";
    }


    @Override
    public void onFragmentInteraction() {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        HomeFragment homeFragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, homeFragment, homeFragment.getClass().getName());
        fragmentTransaction.addToBackStack(homeFragment.getClass().getName());
        fragmentTransaction.commit();
        if (Util.getPlaceName(DashboardActivity.this, clat) != null)
            HomeFragment.mEdtSearch.setText(Util.getPlaceName(DashboardActivity.this, clat));
    }

    boolean sentWaiting = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
       /*     case R.id.linearNetworkToggle:
                toggleNetwork();

                break;*/
            case R.id.btnPause:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (btnPause.getText().toString().equals("Pause")) {
                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);
                    editor.putBoolean("pause_service", true);
                    editor.commit();
                    btnPause.setText("Resume");
                } else {
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    editor.putBoolean("resume_service", true);
                    editor.commit();
                    btnPause.setText("Pause");
                    if (IOUtils.isNetworkAvailable(context)) {
                        createJsonobjectForWaitingCharge();
                    }
                }
                break;
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.clear();

        MarkerOptions options = new MarkerOptions().title("Point your current location")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                .anchor(0.5f, 0.5f);
        options.position(latLng);
        mCurrentMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);




    }





    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */


    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */

    public static LatLng clat;

    private void updateUI() {

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            //HomeFragment.mEdtSearch.setText(Util.getPlaceName(DashboardActivity.this, latLng));
            clat = latLng;
            if (mMap != null) {
                mMap.clear();
                MarkerOptions options = new MarkerOptions().title("Pickup location")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                        .anchor(0.5f, 0.5f);
                options.position(latLng);
                mCurrentMarker = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                currentLatLang = latLng;
                Log.e(TAG1,"currentLatLang==>"+currentLatLang);

                if (getIntent().getExtras() != null) {
                    if (getIntent().getExtras().getBoolean("notify")) {
                        rideAccept = (RideAccept) getIntent().getSerializableExtra("ride");
                        if (rideAccept.getAction().equalsIgnoreCase("RIDE_REQUEST")) {
                            JobNotificationFragment jobNotificationFragment = new JobNotificationFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("ride", rideAccept);
                            jobNotificationFragment.setArguments(bundle);
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.container, jobNotificationFragment, jobNotificationFragment.getClass().getName());
                            transaction.addToBackStack(jobNotificationFragment.getClass().getName());
                            transaction.commit();

                      /*  LatLng latLng1 = new LatLng(Double.parseDouble(IOUtils.fromLat), Double.parseDouble(IOUtils.fromLang));
                        DashboardActivity.mCurrentMarker.setPosition(latLng1);*/
                        } else if (getIntent().getExtras().getBoolean("RIDE_CANCELLED")) {
                            Toast.makeText(getApplicationContext(), "Ride cancelled by passenger", Toast.LENGTH_SHORT).show();
                          /*  homeFragment = new HomeFragment();
                            fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, homeFragment, homeFragment.getClass().getName());
                            fragmentTransaction.addToBackStack(homeFragment.getClass().getName());
                            fragmentTransaction.commit();*/
                            finish();
                            Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }


                    }


                } else {
               /*     HomeFragment homeFragment = new HomeFragment();
                    Log.e("place",Util.getPlaceName(DashboardActivity.this, latLng ));
                    homeFragment.newInstance(Util.getPlaceName(DashboardActivity.this, latLng));
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, homeFragment, homeFragment.getClass().getName());
                    fragmentTransaction.addToBackStack(homeFragment.getClass().getName());
                    fragmentTransaction.commit();*/


                }
            }
            startUpdatesButtonHandler();
        } else {
            Toast.makeText(getApplicationContext(), "Please start GPS", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(-34, 151);

            mMap.clear();
            MarkerOptions options = new MarkerOptions().title("Point your current location")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                    .anchor(0.5f, 0.5f);
            options.position(latLng);
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        settingsrequest();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


        //ToDo : Api call
        // createJsonobjectForApiCall(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));


        currentLatLang = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentMarker.setPosition(currentLatLang);

    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.e(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPickupInfoFragmentInteraction() {
        Log.e(TAG1,"onPickupInfoFragmentInteraction==>");
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLng latLngDest = new LatLng(mCurrentLocation.getLatitude() + 0.005, mCurrentLocation.getLongitude() + 0.005);


        mMap.clear();
        MarkerOptions options = new MarkerOptions().title("Pickup Point")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                .anchor(0.5f, 0.5f);
        options.position(latLng);
        mCurrentMarker = mMap.addMarker(options);

        MarkerOptions optionsDest = new MarkerOptions().title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin1))
                .anchor(0.5f, 0.5f);
        optionsDest.position(latLngDest);
        mMap.addMarker(optionsDest);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

// Getting URL to the Google Directions API
        String url = Util.getDirectionsUrl(latLng, latLngDest);

        DownloadTask downloadTask = new DownloadTask();

// Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    @Override
    public void onBack() {
        onBackFlag = 1;
        onBackPressed();
    }

    @Override
    public void onFragmentInteraction(final LatLng latLng) {
        if (mCurrentLocation != null) {
            pickupLat = latLng;
            startRepeatingTask();
        }

    }

    private final static int INTERVAL = 1000 * 15; //15sec
    public Handler mHandler = new Handler();

    public Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            drawMap(pickupLat);
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void startRepeatingTask() {
        mHandlerTask.run();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask);
    }


    private void drawMap(LatLng l) {
        Log.e("draw", "map draw");
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLng latLngDest = new LatLng(mCurrentLocation.getLatitude() + 0.005, mCurrentLocation.getLongitude() + 0.005);


        mMap.clear();
        MarkerOptions options = new MarkerOptions().title("Current location")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                .anchor(0.5f, 0.5f);
        options.position(latLng);
        mCurrentMarker = mMap.addMarker(options);

        MarkerOptions optionsDest = new MarkerOptions().title("Pickup location")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin1))
                .anchor(0.5f, 0.5f);
        optionsDest.position(l);
        mMap.addMarker(optionsDest);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(l));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = Util.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.e("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionParser parser = new DirectionParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            points = null;
            PolylineOptions lineOptions = null;


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route-
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);

        }
    }

    // Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), MyService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), MyService.class));
    }


    public void createJsonobjectForLogout() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ID, session.getUser_id());
            jsonObject.put(Constants.EMAIL, session.getD_email());
            Log.e("JsonObject", jsonObject.toString());
            logoutApiCall(jsonObject);

        } catch (Exception e) {

        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void logoutApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_LOG_OUT, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        try {
                            if (response.getString("response").equalsIgnoreCase("Logged out")) {
                                stopService();

                                Consts.driver_id = "";
                                Consts.Vehical_Id = "";
                                IOUtils ioUtils = new IOUtils(DashboardActivity.this);
                                ioUtils.clearData();
                                finish();
                                Intent intent = new Intent(DashboardActivity.this, SigninActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);

                            } else {

                              /*  stopService();
                                IOUtils ioUtils = new IOUtils(DashboardActivity.this);
                                ioUtils.clearData();
                                finish();
                                Intent intent = new Intent(DashboardActivity.this, SigninActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);*/

                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");

                            }
                        } catch (JSONException e) {
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


    public void createJsonobjectForOffline() {
        try {
            progressDialog.setMessage("Changing status");
            progressDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ID, session.getUser_id());
            //Consts.driver_id = userDetailsPojo.getId();
            //String.valueOf(session.getUser_id());
            Log.e("D_email", "D_email==>" + session.getD_email());
            jsonObject.put(Constants.EMAIL, session.getD_email());

            if (switchCompat.isChecked()) {
                jsonObject.put(Constants.STATUS, Constants.ONLINE);
            } else {


                if (Consts.Status.equalsIgnoreCase("1")) {
                    ioUtils.setDriverStatus(false);
                    Consts.Status = "";
                    stopService();
                    jsonObject.put(Constants.STATUS, Constants.OFFLINE);

                } else {

                    jsonObject.put(Constants.STATUS, Constants.ONLINE);
                }

            }
            Log.e("JsonObject", jsonObject.toString());
            offlineApiCall(jsonObject);

        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void offlineApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_CHANGE_STATUS, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        try {
                            if (response.getString("result").equals("true")) {
                                if (response.getString("response").equalsIgnoreCase("Status changed")) {
                                    if (response.getJSONObject("driverData").getInt("status") == 1) {
                                        //switchCompat.setText("Go offline");
                                        ioUtils.setDriverStatus(true);
                                        switchCompat.setChecked(true);
                                        Consts.Status = "1";
                                        startService();


                                    } else if (response.getJSONObject("driverData").getInt("status") == 4) {

                                        Check();
                                    } else {
                                        // switchCompat.setText("Go online");
                                        ioUtils.setDriverStatus(false);
                                        switchCompat.setChecked(false);
                                        stopService();


                                    }


                                    /*if (ioUtils.getDriverStatus()) {
                                        switchCompat.setChecked(true);
                                    } else {
                                        switchCompat.setChecked(false);
                                    }
*/
                                }

                            } else {


                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Response", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);
    }


    private int mins;
    boolean time_status = true;

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            txtTimer.setText("" + mins + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };


    LayoutInflater layoutInflater;
    View layout;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    // The method that displays the popup.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showPopup() {
        if (sentWaiting) {
            if (IOUtils.isNetworkAvailable(context)) {
                createJsonobjectForWaitingCharge();
                sentWaiting = false;
            }
        }
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        p = new Point();
        p.x = width - 100;
        p.y = 160;
        if (time_status) {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            time_status = false;
        }

        int popupWidth = 500;
        int popupHeight = 400;

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);


    }


    public void createJsonobjectForWaitingCharge() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, session.getUser_id());
            Log.e("JsonObject", jsonObject.toString());
            watingChargeApiCall(jsonObject);

        } catch (Exception e) {

        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void watingChargeApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_BASE + "Drivers/SendWaitingPush", js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        try {
                            IOUtils.toastMessage(context, response.getString("response"));
                            sentWaiting = false;
                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Response", "Error: " + error.getMessage());
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }


    public void Status_PopUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        //builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View content = inflater.inflate(R.layout.exit, null);
        builder.setView(content);


        text_add = (TextView) content.findViewById(R.id.text_add);

        text_add2 = (TextView) content.findViewById(R.id.text_add2);
        // Util.setFont(4, activity, text_add, "Are you Exit the Fliptron");
        linear_ok = (LinearLayout) content.findViewById(R.id.linear_ok);
        linear_cancel = (LinearLayout) content.findViewById(R.id.linear_cancel);
        text_ok = (TextView) content.findViewById(R.id.text_ok);
        text_cancel = (TextView) content.findViewById(R.id.text_cancel);

        // Util.setFont(2, activity, text_ok, "YES");

        //  Util.setFont(2, activity, text_cancel, "NO");

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.card_view));


        linear_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                alertDialog.dismiss();


                //  UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                // userDetailsPojo.getEmail();
                //Consts.Email = userDetailsPojo.getEmail();
                Intent intent = new Intent(DashboardActivity.this, QuickSignupUploadDocActivity.class);
                // Consts.Check = "true";
                startActivity(intent);
                finish();


            }
        });

        linear_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("cancel", "cancel");


                alertDialog.dismiss();
            }
        });

    }


    public void Pending_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        //  builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View content = inflater.inflate(R.layout.customdialog, null);
        builder.setView(content);

        //  AlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        text_title = (TextView) content.findViewById(R.id.text_title);


        text_title.setText("Pending Approval");

        edit_feed = (TextView) content.findViewById(R.id.edit_feed);
        edit_feed.setText("Already Upload your Vechicles Document \n Please Wait for Admin Approval");


        img_cross = (ImageView) content.findViewById(R.id.img_cross);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Transparent_white)));

        // alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.card_view));

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        img_cross.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                alertDialog.dismiss();

            }
        });


    }


    private void Check_Doc(String Dri_id, String V_id) {

        final ProgressDialog dialog = ProgressDialog.show(context, null, "Loading...");
        JSONObject input = new JSONObject();
        try {
            input.put("driverId", Dri_id);
            input.put("vehicleId", V_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e("test", "input:" + input.toString());

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_CHECK_DOC, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.e("test", "response:" + response.toString());
                            dialog.dismiss();

                           /* if (response.getString("result").equalsIgnoreCase("true")) {


                                Status_PopUp();

                                //Pending_Dialog();


                            }
*/

                            if (response.getString("response").equalsIgnoreCase("0")) {

                                IOUtils.toastMessage(context, response.getString("msg"));

                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                Consts.Status = "1";
                                stopService();

                            } else if (response.getString("response").equalsIgnoreCase("1")) {
                                IOUtils.toastMessage(context, response.getString("msg"));

                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                Consts.Status = "1";
                                stopService();

                            } else if (response.getString("response").equalsIgnoreCase("2")) {

                                IOUtils.toastMessage(context, response.getString("msg"));

                                Consts.Driver_Enable = "true";
                                switchCompat.setChecked(true);
                                createJsonobjectForSetVehicalApiCall();
                                ioUtils.setDriverStatus(true);
                                //  Consts.Status = "1";
                                startService();


                                //Enable
                            } else if (response.getString("response").equalsIgnoreCase("3")) {

                                IOUtils.toastMessage(context, response.getString("msg"));
                                Pending_Dialog();
                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                Consts.Status = "1";
                                stopService();
                            } else if (response.getString("response").equalsIgnoreCase("4")) {

                                IOUtils.toastMessage(context, response.getString("msg"));
                                Status_PopUp();

                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                Consts.Status = "1";
                                stopService();
                            } else if (response.getString("response").equalsIgnoreCase("5")) {

                                IOUtils.toastMessage(context, response.getString("msg"));
                                Status_PopUp();

                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                Consts.Status = "1";
                                stopService();
                            } else if (response.getString("response").equalsIgnoreCase("6")) {

                                IOUtils.toastMessage(context, response.getString("msg"));
                                Status_PopUp();
                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                Consts.Status = "1";
                                stopService();
                            } else if (response.getString("response").equalsIgnoreCase("7")) {

                                IOUtils.toastMessage(context, response.getString("msg"));
                                Status_PopUp();
                                ioUtils.setDriverStatus(false);
                                createJsonobjectForOffline();
                                Consts.Status = "1";
                                stopService();
                            }


                            //IOUtils.toastMessage(context, response.getString("response"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Response", "Error: " + error.getMessage());
                dialog.dismiss();
            }
        });


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);

    }


    /**
     * @param Dri_id
     */


    private void Drive_view(final String Dri_id) {

        final ProgressDialog dialog = ProgressDialog.show(context, null, "Loading...");
        JSONObject input = new JSONObject();
        try {

            input.put("driverId", Dri_id);

            //   input.put("driver_id", Dri_id + " " + Dri_id);
            //Consts.driver_id = Dri_id;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", "input:" + input.toString());
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_DRIVER_DOC, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.e("test", "response:" + response.toString());
                            dialog.dismiss();

                            JSONArray Driver_Info;
                            if (response.getString("result").equalsIgnoreCase("true")) {


                                JSONArray jsonArray = response.getJSONArray("driverdetails");


                                // for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(0);

                                UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                                String driverId = jo.getString("driverId");
                                userDetailsPojo.setId(driverId);

                                userDetailsPojo.setDriverName(jo.getString("driverName"));
                                userDetailsPojo.setVehicleTypeId(jo.getString("vehicleTypeId"));
                                userDetailsPojo.setVehicleId(jo.getString("vehicleId"));
                                Consts.Vehical_Id = jo.getString("vehicleId");

                                Log.e("Vechical", "Vechical-id==>" + Consts.Vehical_Id);
                                userDetailsPojo.setVehicleModel(jo.getString("vehicleModel"));
                                userDetailsPojo.setVehicleNumber(jo.getString("vehicleNumber"));
                                userDetailsPojo.setYear(jo.getString("year"));
                                userDetailsPojo.setNumOfDoors(jo.getString("numOfDoors"));
                                userDetailsPojo.setLicenceType(jo.getString("licenceType"));
                                userDetailsPojo.setOwnership(jo.getString("ownership"));
                                userDetailsPojo.setReferralNo(jo.getString("referralNo"));
                                userDetailsPojo.setInsuranceCompName(jo.getString("insuranceCompName"));
                                userDetailsPojo.setWheelChairFacility(jo.getString("wheelChairFacility"));


                                IOUtils ioUtils = new IOUtils(context);
                                ioUtils.setUser(userDetailsPojo);
                                // }
                                V_id = Consts.Vehical_Id;

                                Log.e("dri==>", "dri==>" + Dri_id);
                                Log.e("Vehical_id==>", "Vehical_id==>" + V_id);

                                if (session.getonoff().equalsIgnoreCase("0")) {

                                    Log.e("Empty==>", "Empty==>");

                                    session.setonoff("2");

                                } else if (session.getonoff().equalsIgnoreCase("1")) {
                                    session.setonoff("2");
                                    Consts.Status = "";
                                    Check_Doc(Dri_id, V_id);


                                } else if (session.getonoff().equalsIgnoreCase("4")) {
                                    //session.setonoff("2");
                                    //  Payment_Dialog();


                                    JobPreviewFragment jobPreviewFragment = new JobPreviewFragment();
                                    fragmentManager = getSupportFragmentManager();
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container, jobPreviewFragment, jobPreviewFragment.getClass().getName());
                                    fragmentTransaction.addToBackStack(jobPreviewFragment.getClass().getName());
                                    fragmentTransaction.commit();

                                } else {

                                    Check_Doc(Dri_id, V_id);
                                }


                            }
                            IOUtils.toastMessage(context, response.getString("response"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Response", "Error: " + error.getMessage());
                dialog.dismiss();
            }
        });


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);

    }


    public void createJsonobjectForSetVehicalApiCall() {
        try {
            //dialog = IOUtils.getProgessDialog(context);
            //dialog.show();

            JSONObject jsonObject = new JSONObject();




         /*   jsonObject.put(Constants.EMAIL,"tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD,"123456");
*/
            jsonObject.put(Constants.ID, session.getUser_id());
            jsonObject.put(Constants.EMAIL, session.getD_email());
            jsonObject.put(Constants.VEHICAL_ID, Consts.Vehical_Id);


            Log.e("JsonObject", jsonObject.toString());


            setVehicalApiCall(jsonObject);


        } catch (Exception e) {

        }
    }


    public void setVehicalApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_SET_VEHICLES, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());

                        //      {"result":"true","response":"Active Vehicle Changed"}
                        try {
                            if (response.getString("result").equals("true")) {

                                Log.e("Response", response.toString());



                               /* IOUtils utils = new IOUtils(HomeActivity.this);
                                utils.setVehicle(true,arrayListCarTypeId.get(carSelectedPosition-1));
                                ioUtils.setDriverStatus(true);
                                Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();*/

                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                            }
                            //dialog.dismiss();
                        } catch (JSONException e) {
                            // dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Response", "Error: " + error.getMessage());
                // dialog.dismiss();
            }
        });


        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        queue.add(jsonObjReq);


    }


    private void Check() {

        final ProgressDialog dialog = ProgressDialog.show(context, null, "Loading...");
        JSONObject input = new JSONObject();
        try {

            input.put("driverId", Dri_id);
            ioUtils.setDriverStatus(true);
            switchCompat.setChecked(true);
            Consts.Status = "1";
            startService();

            //   input.put("driver_id", Dri_id + " " + Dri_id);
            //Consts.driver_id = Dri_id;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e("test", "input:" + input.toString());
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.GET,
                Constants.URL_CHANGE, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            IOUtils.toastMessage(context, response.getString("response"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Response", "Error: " + error.getMessage());
                dialog.dismiss();
            }
        });


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);

    }


    public void Payment_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        //  builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View content = inflater.inflate(R.layout.customdialog, null);
        builder.setView(content);

        //  AlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        text_title = (TextView) content.findViewById(R.id.text_title);


        text_title.setText("Payment Done");

        edit_feed = (TextView) content.findViewById(R.id.edit_feed);
        edit_feed.setText(" \n To Start to Drive");


        img_cross = (ImageView) content.findViewById(R.id.img_cross);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Transparent_white)));

        // alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.card_view));

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        img_cross.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                alertDialog.dismiss();

            }
        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if (Consts.JobPrivew.equalsIgnoreCase("true")) {

                Cancel_Dialog();

            } else {

            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void Cancel_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater inflater = LayoutInflater.from(DashboardActivity.this);
        //  builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View content = inflater.inflate(R.layout.customdialog, null);
        builder.setView(content);

        //  AlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        text_title = (TextView) content.findViewById(R.id.text_title);
        linear_ok = (LinearLayout) content.findViewById(R.id.linear_ok);
        linear_cancel = (LinearLayout) content.findViewById(R.id.linear_cancel);
        linear_ok.setVisibility(View.VISIBLE);
        linear_cancel.setVisibility(View.VISIBLE);

        text_title.setText("Ride Cancel");

        edit_feed = (TextView) content.findViewById(R.id.edit_feed);
        edit_feed.setText(" \n Do you want to Cancel Ride");


        img_cross = (ImageView) content.findViewById(R.id.img_cross);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Transparent_white)));

        // alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.card_view));

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        img_cross.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

            }
        });

        linear_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

                createJsonobjectForCancelRide();
            }
        });
        linear_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                //createJsonobjectForCancelRide();
            }
        });


    }


    public void createJsonobjectForCancelRide() {
        try {
            //stopVibrate();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, session.getUser_id());
            Log.e("JsonObject", jsonObject.toString());
            cancelRideApiCall(jsonObject);

        } catch (Exception e) {

        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void cancelRideApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_REJECT_RIDE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                            } else {
                            }
                            IOUtils.toastMessage(DashboardActivity.this, response.getString("response"));
                            //finish();
                            Consts.JobPrivew ="false";
                            Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            ioUtils.setRideStatus(Constants.RIDE_NEW);


                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Response", "Error: " + error.getMessage());
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA,PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    Log.e("", "WRITE_EXTERNAL_STORAGE PERMISSION_DENIED");
                } else if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    Log.e("", "ACCESS_FINE_LOCATION PERMISSION_DENIED");
                } else if (perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    Log.e("", "ACCESS_COARSE_LOCATION PERMISSION_DENIED");
                } else if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    Log.e("", "READ_EXTERNAL_STORAGE PERMISSION_DENIED");
                } else if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    Log.e("", "CAMERA PERMISSION DENIED");
                }

            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void getPermissionsDetails() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("WRITE_EXTERNAL_STORAGE");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
                permissionsNeeded.add("ACCESS_COARSE_LOCATION");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
                permissionsNeeded.add("ACCESS_FINE_LOCATION");
            if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("READ_EXTERNAL_STORAGE");
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("CAMERA");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            } else {

            }
        } else { //permission is automatically granted on sdk<23 upon installation

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (DashboardActivity.this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }



}
