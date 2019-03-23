package com.DDebbie.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.DDebbie.entity.RideAccept;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.service.GcmService;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobNotificationFragment.OnBackPressListener} interface
 * to handle interaction events.
 * Use the {@link JobNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobNotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnBackPressListener mListener;
    private Bundle bundle;

    public JobNotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobNotificationFragment newInstance(String param1, String param2) {
        JobNotificationFragment fragment = new JobNotificationFragment();
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
    }

    private View view;
    private RelativeLayout mRelativeJob;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextView txtTime, txtAddress;
    IOUtils ioUtils;
    UserDetailsPojo userDetailsPojo;
    private Vibrator vibrator;
    Handler handler;
    Runnable runnable;
    private Button btnAccept, btnReject;
    SharedPreferences sharedPreferences;
    RideAccept rideAccept;
    boolean isRideAccept = true;
    boolean isRideReject = true;
    Session session;

    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "", Area = "", Connec = "";
    Geocoder geocoder;
    String Check_lat = "";
    String Check_Long = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_job_notification, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rideAccept", "");
        rideAccept = gson.fromJson(json, RideAccept.class);
        txtTime = (TextView) view.findViewById(R.id.txtTime);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtTime.setText(rideAccept.getEst());

        Check(rideAccept.getPickUpLocation());


        // txtAddress.setText(rideAccept.getPickUpLocation());
        ioUtils = new IOUtils(getActivity());
        ioUtils.setRideStatus(Constants.RIDE_DRIVER_ASSIGNED);

        session = new Session(getActivity());
        userDetailsPojo = ioUtils.getUser();
        btnAccept = (Button) view.findViewById(R.id.btnAccept);
        btnReject = (Button) view.findViewById(R.id.btnReject);
        if (IOUtils.DONT_SHOW) {
            mListener.onBack();
        }
        //     startVibrate();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // stopVibrate();
                createJsonobjectForCancelRide();
            }
        };
        handler.postDelayed(runnable, 1000 * 60 * 3);

        DashboardActivity.mLinearNetworkToggle.setVisibility(View.GONE);
        mRelativeJob = (RelativeLayout) view.findViewById(R.id.relativeJob);
        mRelativeJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intentPickup = new Intent(getActivity(), PickUpInformationActivity.class);
                intentPickup.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentPickup);*/
                // createJsonobjectForApiCall();

            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRideAccept) {
                    if (IOUtils.isNetworkAvailable(getActivity())) {
                        isRideAccept = false;
                        createJsonobjectForApiCall();
                    }
                }
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRideAccept) {
                    if (IOUtils.isNetworkAvailable(getActivity())) {
                        isRideReject = false;
                        createJsonobjectForCancelRide();
                    }
                }
            }
        });
        return view;
    }


    public void startVibrate() {
        long pattern[] = {0, 100, 200, 300, 400};
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);
    }

    public void stopVibrate() {
        vibrator.cancel();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onBack();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBackPressListener) {
            mListener = (OnBackPressListener) context;
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
    public interface OnBackPressListener {
        // TODO: Update argument type and name
        void onBack();
    }

    public void createJsonobjectForApiCall() {
        try {
            //stopVibrate();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, session.getUser_id());
            Log.e("JsonObject", jsonObject.toString());
            acceptRideApiCall(jsonObject);

        } catch (Exception e) {

        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void acceptRideApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(getActivity());


        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_ACCEPT_RIDE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {

                                IOUtils.RideStatus = "active";
                                JobPreviewFragment jobPreviewFragment = new JobPreviewFragment();
                                jobPreviewFragment.setArguments(bundle);
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container, jobPreviewFragment, jobPreviewFragment.getClass().getName());
                                fragmentTransaction.addToBackStack(jobPreviewFragment.getClass().getName());
                                fragmentTransaction.commit();
                                fragmentManager.popBackStack("JobNotificationFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            } else {

                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setMessage(response.getString("response"));
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getActivity().finish();

                                        Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        getActivity().startActivity(intent);
                                        ioUtils.setRideStatus(Constants.RIDE_NEW);
                                    }
                                });
                                dialog.show();
                            }
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
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                            IOUtils.toastMessage(getActivity(), response.getString("response"));
                            if (mListener != null) {
                                getActivity().finish();
                                Consts.JobPrivew = "false";
                                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                ioUtils.setRideStatus(Constants.RIDE_NEW);

                            }
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
            Connec = addresses.get(0).getAddressLine(0);

            Connec = addresses.get(0).getFeatureName();


            txtAddress.setText("Pickup location :\n" + knownName + "," + Area + "," + city + "," + state + "," + country + "," + postalCode);

            //txtInfo.setText("Pickup location :\n" + this.address);

            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }


}
