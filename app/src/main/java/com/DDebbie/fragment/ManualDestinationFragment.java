package com.DDebbie.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.util.Consts;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.DDebbie.activity.DashboardActivity;
import com.DDebbie.entity.Destination;
import com.DDebbie.entity.RideAccept;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.service.GcmService;
import com.DDebbie.service.LocalService;
import com.DDebbie.service.MyService;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.DDebbie.view.SlideButton;
import com.DDebbie.view.SlideButtonListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManualDestinationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManualDestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManualDestinationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static boolean isLive = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RideAccept rideAccept;

    public ManualDestinationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManualDestinationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManualDestinationFragment newInstance(String param1, String param2) {
        ManualDestinationFragment fragment = new ManualDestinationFragment();
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

    //private SlideButton slideButton;
    private RelativeLayout layoutBottom;
    private View view;
    Point p;
    private Button mBtnArrive;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private TextView txtPickup, txtDest, txtDest2;
    UserDetailsPojo userDetailsPojo;
    IOUtils ioUtils;
    public TextView txtTimer;
    private Button btnPause;
    LinearLayout viewGroup;
    LayoutInflater layoutInflater;
    View layout;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private BroadcastReceiver mReceiver;

    private JSONArray jsonArray;

    Intent intent;
    TextView txtview;
    MyResultReceiver resultReceiver;
    SharedPreferences sharedPreferences;
    String destinations;

    Session session;


    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "", Area = "";
    Geocoder geocoder;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manual_destination, container, false);
        Consts.JobPrivew = "false";


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rideAccept", "");
        rideAccept = gson.fromJson(json, RideAccept.class);
        txtPickup = (TextView) view.findViewById(R.id.edt_pickup);
        txtDest = (TextView) view.findViewById(R.id.edt_destination);
        txtDest2 = (TextView) view.findViewById(R.id.edt_destination2);
        Geocoder geocoder;
        session = new Session(getActivity());

        try {
            jsonArray = new JSONArray(rideAccept.getDestinations());
            if (jsonArray.length() == 2) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                JSONObject jsonObject2 = jsonArray.getJSONObject(1);
                txtDest2.setText(jsonObject1.getString("address"));
                txtDest2.setVisibility(View.VISIBLE);
            }


            Log.e("json_array", jsonArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // txtPickup.setText(rideAccept.getPickUpLocation());
        // txtDest.setText(rideAccept.getDropOffLocation());


        Check(rideAccept.getPickUpLocation());
        Drop(rideAccept.getDropOffLocation());

        ioUtils = new IOUtils(getActivity());
        ioUtils.setRideStatus(3);
        userDetailsPojo = ioUtils.getUser();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        viewGroup = (LinearLayout) view.findViewById(R.id.popup);
        layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);
        txtTimer = (TextView) layout.findViewById(R.id.txtTime);
        btnPause = (Button) layout.findViewById(R.id.btnPause);
      // slideButton = (SlideButton) view.findViewById(R.id.sliderStartTrip);
        layoutBottom = (RelativeLayout) view.findViewById(R.id.layoutBottom);


        layoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IOUtils.isNetworkAvailable(getActivity()))
                    createJsonobjectForStartRide();
            }
        });

      /*  layoutBottom.setSlideButtonListener(new SlideButtonListener() {
            @Override
            public void handleSlide() {
                if (IOUtils.isNetworkAvailable(getActivity()))
                    createJsonobjectForStartRide();

            }
        });*/

        isLive = true;


       /* btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnPause.getText().toString().equals("Pause")) {
                   *//* timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);*//*
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("pause_service", true);
                    editor.commit();
                    btnPause.setText("Resume");
                }else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("resume_service", true);
                    editor.commit();
                    btnPause.setText("Pause");
                }
            }
        });*/

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

    private boolean wcharge = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_timer:
                //  getActivity().startService(new Intent(getActivity(), LocalService.class));
              /*  if(!isMyServiceRunning(MyService.class)) {
                    intent = new Intent(getActivity(), MyService.class);
                    intent.putExtra("receiver", resultReceiver);
                    getActivity().startService(intent);

                }
                if (p != null)
                    showPopup(p);
                    */

                if (getActivity().getClass() == DashboardActivity.class) {
                    ((DashboardActivity) getActivity()).showPopup();
                }


                break;

        }
        return super.onOptionsItemSelected(item);

    }


    public void createJsonobjectForStartRide() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, session.getUser_id());

            Log.v("CENTS", mins + " ");

            if (mins > 0) {
                jsonObject.put(Constants.WAIT_TIME, "" + mins);
                double centCost = mins * 35;
                Log.v("CENTS", centCost + " ");
                double charge = centCost / 100;
                Log.v("CENTS", charge + " ");

                jsonObject.put(Constants.WAIT_CHARGE, "" + charge);
            }
            Log.v("JsonObject", jsonObject.toString());
            startRideApiCall(jsonObject);

        } catch (Exception e) {

        }
    }
 /*
    * regitrationApiCall - In this method we call the api...
    */

    public void startRideApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_START_RIDE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                if (jsonArray.length() == 2) {
                                    ArrivedTo1stDestinationFragment arrivedTo1stDestinationFragment = new ArrivedTo1stDestinationFragment();
                                    fragmentManager = getFragmentManager();
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container, arrivedTo1stDestinationFragment, arrivedTo1stDestinationFragment.getClass().getName());
                                    fragmentTransaction.addToBackStack(arrivedTo1stDestinationFragment.getClass().getName());
                                    fragmentTransaction.commit();
                                } else {
                                    ArrivedToDestinationFragment arrivedToDestinationFragment = new ArrivedToDestinationFragment();
                                    fragmentManager = getFragmentManager();
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container, arrivedToDestinationFragment, arrivedToDestinationFragment.getClass().getName());
                                    fragmentTransaction.addToBackStack(arrivedToDestinationFragment.getClass().getName());
                                    fragmentTransaction.commit();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(response.getString("response"));
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);

                                    }
                                });
                                builder.show();
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


    public void createJsonobjectForWaitingCharge() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, session.getD_email());

            Log.v("JsonObject", jsonObject.toString());
            watingChargeApiCall(jsonObject);

        } catch (Exception e) {

        }
    }
 /*
    * regitrationApiCall - In this method we call the api...
    */

    public void watingChargeApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_BASE + "Drivers/SendWaitingPush", js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            IOUtils.toastMessage(getActivity(), response.getString("response"));
                            sentWaiting = false;
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


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    boolean sentWaiting = true;


    private int mins;
    private Runnable updateTimerThread = new Runnable() {

        public void run() {


            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            txtTimer.setText("" + mins + ":"
                    + String.format("%02d", secs) + " min.");
            customHandler.postDelayed(this, 0);
        }

    };

    // The method that displays the popup.
    public void showPopup(Point p) {

        int popupWidth = 400;
        int popupHeight = 250;

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(getActivity());
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


    class UpdateUI implements Runnable {
        String updateString;

        public UpdateUI(String updateString) {
            this.updateString = updateString;
        }

        public void run() {
            txtTimer.setText(updateString);
        }
    }

    @SuppressLint("ParcelCreator")
    class MyResultReceiver extends ResultReceiver {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

        /*    if(resultCode == 100){
                runOnUiThread(new UpdateUI(resultData.getString("start")));
            }
            else if(resultCode == 200){
                runOnUiThread(new UpdateUI(resultData.getString("end")));
            }
            else{*/
            runOnUiThread(new UpdateUI(resultData.getString("time")));
            //}
        }
    }


    public void Check(String lat_address) {

        Log.e("lat_lng==>", "latlng==>" + lat_address);
        String lat_str = lat_address.substring(10, lat_address.length() - 1);
        Log.e("lat_address==>", "lat_address==>" + lat_str);
        String[] lat = lat_str.split(",");
        Log.e("SPLIT_lat==>", "lat==>" + lat[0] + "\n" + lat[1]);
        double Source_Lat = 0.0;
        double Source_Long = 0.0;
        String Check_lat = "";
        String Check_Long = "";

        try {


            Source_Lat = Double.parseDouble(lat[0]);
            Source_Long = Double.parseDouble(lat[1]);

            Check_lat = String.valueOf(Source_Lat);
            Check_Long = String.valueOf(Source_Long);

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

            txtPickup.setText("Pickup location :\n" + knownName + "," + Area + "," + city + "," + state + "," + country + "," + postalCode);

            //txtPickup.setText("Pickup location :\n" + this.address);


         /*   Uri gmmIntentUri = null;
            try {
                gmmIntentUri = Uri.parse("google.navigation:q=" + Check_lat + "," + Check_Long + "&mode=d");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);*/


            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }


    public void Drop(String lat_address) {

        Log.e("lat_lng==>", "latlng==>" + lat_address);
        String lat_str = lat_address.substring(10, lat_address.length() - 1);
        Log.e("lat_address==>", "lat_address==>" + lat_str);
        String[] lat = lat_str.split(",");
        Log.e("SPLIT_lat==>", "lat==>" + lat[0] + "\n" + lat[1]);
        double Source_Lat = 0.0;
        double Source_Long = 0.0;
        String Check_lat = "";
        String Check_Long = "";

        try {


            Source_Lat = Double.parseDouble(lat[0]);
            Source_Long = Double.parseDouble(lat[1]);

            Check_lat = String.valueOf(Source_Lat);
            Check_Long = String.valueOf(Source_Long);

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


            txtDest.setText("Drop location :\n" + knownName + "," + Area + "," + city + "," + state + "," + country + "," + postalCode);
            //txtDest.setText("Drop location :\n" + this.address);


/*

            Uri gmmIntentUri = null;
            try {
                gmmIntentUri = Uri.parse("google.navigation:q=" + Check_lat + "," + Check_Long + "&mode=d");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
*/


            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }


}
