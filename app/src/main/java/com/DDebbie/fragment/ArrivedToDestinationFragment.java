package com.DDebbie.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.activity.DashboardActivity;
import com.DDebbie.entity.RideAccept;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.service.LocalService;
import com.DDebbie.util.Constants;
import com.DDebbie.util.Consts;
import com.DDebbie.util.GPSTracker;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.DDebbie.util.StartLocationAlert;
import com.DDebbie.view.SlideButton;
import com.DDebbie.view.SlideButtonListener;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArrivedToDestinationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArrivedToDestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArrivedToDestinationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RideAccept rideAccept;
    Session session;

    public ArrivedToDestinationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArrivedToDestinationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArrivedToDestinationFragment newInstance(String param1, String param2) {
        ArrivedToDestinationFragment fragment = new ArrivedToDestinationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);

    }

    private SlideButton slideButton;
    private View view;
    private Point p;
    private Button mBtnArrive;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private UserDetailsPojo userDetailsPojo;
    private IOUtils ioUtils;
    private TextView txtTimer, txtAddress;
    LinearLayout viewGroup;
    LayoutInflater layoutInflater;
    View layout;
    ImageView imgNav;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    SharedPreferences sharedPreferences;

    private JSONArray jsonArray;
    private JSONObject jsonObject2, jsonObject1;


    //Map
    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "", Area = "";
    Geocoder geocoder;
    private LatLng mCenterLatLong;

    GPSTracker gps;
    // Context mContext;

    Activity mactivity;
    StartLocationAlert alert;
    Double current_latitude = 0.0, current_longitude = 0.0;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_arrived_to_destination, container, false);

        Consts.JobPrivew = "false";
        mactivity = getActivity();
        session = new Session(mactivity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rideAccept", "");
        rideAccept = gson.fromJson(json, RideAccept.class);
        ioUtils = new IOUtils(getActivity());
        ioUtils.setRideStatus(Constants.RIDE_START);
        userDetailsPojo = ioUtils.getUser();

        gps = new GPSTracker(mactivity);


        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        p = new Point();
        p.x = width - 100;
        p.y = 160;

        imgNav = (ImageView) view.findViewById(R.id.imgNavigate);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);

        Check(rideAccept.getDropOffLocation());

        //txtAddress.setText("Arrived to\n" + rideAccept.getDropOffLocation());
        slideButton = (SlideButton) view.findViewById(R.id.sliderStartTrip);
        slideButton.setSlideButtonListener(new SlideButtonListener() {
            @Override
            public void handleSlide() {
                createJsonobjectForEndRide();
            }
        });

        GpsLocation();
        try {
            jsonArray = new JSONArray(rideAccept.getDestinations());
            jsonObject1 = jsonArray.getJSONObject(0);
            jsonObject2 = jsonArray.getJSONObject(1);
            txtAddress.setText("Arrived to\n" + jsonObject1.getString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e("json_array", jsonArray.toString());


        imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = null;
                try {
                    gmmIntentUri = Uri.parse("google.navigation:q=" + jsonObject1.getString("address") + "&mode=d");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    public void setTime(String time) {
        txtTimer.setText(time);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_timer:


                if (getActivity().getClass() == DashboardActivity.class) {
                    ((DashboardActivity) getActivity()).showPopup();
                }


                break;

        }
        return super.onOptionsItemSelected(item);

    }


    public void createJsonobjectForEndRide() {
        try {
            String w_time = ((DashboardActivity) getActivity()).getWatingTime() + "";
            String w_charge = ((DashboardActivity) getActivity()).getWatingCharge();
            getActivity().stopService(new Intent(getActivity(), LocalService.class));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, session.getUser_id());
            jsonObject.put(Constants.WAIT_TIME, w_time);
            jsonObject.put(Constants.WAIT_CHARGE, w_charge);
            jsonObject.put(Constants.WAIT_COUNTRY, Consts.Country);
            Log.v("JsonObject", jsonObject.toString());
            endRideApiCall(jsonObject);


        } catch (Exception e) {

        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void endRideApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_END_RIDE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                FareSummaryFragment fareSummaryFragment = new FareSummaryFragment();
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container, fareSummaryFragment, fareSummaryFragment.getClass().getName());
                                fragmentTransaction.addToBackStack(fareSummaryFragment.getClass().getName());
                                fragmentTransaction.commit();
                                ioUtils.setPaymentStatus(false);
                            } else {
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


    public void Check(String lat_address) {

        Log.e("lat_lng==>", "latlng==>" + lat_address);
        String lat_str = lat_address.substring(10, lat_address.length() - 1);
        Log.e("lat_address==>", "lat_address==>" + lat_str);
        String[] lat = lat_str.split(",");
        Log.e("SPLIT_lat==>", "lat==>" + lat[0] + "\n" + lat[1]);
        double Source_Lat = 0.0;
        double Source_Long = 0.0;


        try {


            Source_Lat = Double.parseDouble(lat[0]);
            Source_Long = Double.parseDouble(lat[1]);

            // Check_lat = String.valueOf(Source_Lat);
            // Check_Long = String.valueOf(Source_Long);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(Source_Lat, Source_Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            this.address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            Area = addresses.get(0).getSubLocality();


            txtAddress.setText("Drop location :\n" + knownName + "," + Area + "," + city + "," + state + "," + country + "," + postalCode);
            //txtAddress.setText("Drop location :\n" + this.address);


            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }
}
