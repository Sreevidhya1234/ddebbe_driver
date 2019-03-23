package com.DDebbie.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import com.DDebbie.service.LocalService;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.DDebbie.view.SlideButton;
import com.DDebbie.view.SlideButtonListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArrivedTo1stDestinationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArrivedTo1stDestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArrivedTo1stDestinationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RideAccept rideAccept;
    private JSONArray jsonArray;
    private JSONObject jsonObject2, jsonObject1;

    public ArrivedTo1stDestinationFragment() {
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
    public static ArrivedTo1stDestinationFragment newInstance(String param1, String param2) {
        ArrivedTo1stDestinationFragment fragment = new ArrivedTo1stDestinationFragment();
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_arrived_to1st_destination, container, false);
        Consts.JobPrivew = "false";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rideAccept", "");
        rideAccept = gson.fromJson(json, RideAccept.class);
        ioUtils = new IOUtils(getActivity());
        ioUtils.setRideStatus(Constants.RIDE_START);

        userDetailsPojo = ioUtils.getUser();
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
        txtAddress.setText("Arrived to\n" + rideAccept.getDropOffLocation());
        slideButton = (SlideButton) view.findViewById(R.id.sliderStartTrip);
        slideButton.setSlideButtonListener(new SlideButtonListener() {
            @Override
            public void handleSlide() {
                createJsonobjectForEndRide();
            }
        });


        try {
            jsonArray = new JSONArray(rideAccept.getDestinations());
            jsonObject1 = jsonArray.getJSONObject(0);
            jsonObject2 = jsonArray.getJSONObject(1);
            txtAddress.setText("Arrived to\n" + jsonObject2.getString("address"));


            Log.e("json_array", jsonArray.toString());


            imgNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = null;
                    try {
                        gmmIntentUri = Uri.parse("google.navigation:q=" + jsonObject2.getString("address") + "&mode=d");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put("location",jsonObject2.getString("address"));

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
                Constants.URL_BASE + "Drivers/SendArrivedPush", js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                ArrivedToDestinationFragment arrivedToDestinationFragment = new ArrivedToDestinationFragment();
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container, arrivedToDestinationFragment, arrivedToDestinationFragment.getClass().getName());
                                fragmentTransaction.addToBackStack(arrivedToDestinationFragment.getClass().getName());
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
}
