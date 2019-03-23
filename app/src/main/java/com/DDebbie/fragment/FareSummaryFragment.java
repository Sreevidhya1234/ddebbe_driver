package com.DDebbie.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.DDebbie.service.GcmService;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FareSummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FareSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FareSummaryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FareSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FareSummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FareSummaryFragment newInstance(String param1, String param2) {
        FareSummaryFragment fragment = new FareSummaryFragment();
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
    private ImageView star1, star2, star3, star4, star5;
    private int toggle, rating = 0;
    private UserDetailsPojo userDetailsPojo;
    private IOUtils ioUtils;
    private TextView txtName, txtCategory, txtDistance, txtTime, txtRate, txtCharges, txtTotal;
    private RideAccept rideAccept;
    private ProgressDialog progressDialog;
    private Button btnOnline;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fare_summary, container, false);
        Consts.JobPrivew = "false";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rideAccept", "");
        rideAccept = gson.fromJson(json, RideAccept.class);
        ioUtils = new IOUtils(getActivity());
        ioUtils.setRideStatus(Constants.RIDE_COMPLETE);
        userDetailsPojo = ioUtils.getUser();
        star1 = (ImageView) view.findViewById(R.id.star1);
        star2 = (ImageView) view.findViewById(R.id.star2);
        star3 = (ImageView) view.findViewById(R.id.star3);
        star4 = (ImageView) view.findViewById(R.id.star4);
        star5 = (ImageView) view.findViewById(R.id.star5);
        txtName = (TextView) view.findViewById(R.id.name);
        txtCategory = (TextView) view.findViewById(R.id.txtCategory);
        txtDistance = (TextView) view.findViewById(R.id.txtDistance);
        txtTime = (TextView) view.findViewById(R.id.txtTime);
        txtRate = (TextView) view.findViewById(R.id.txtRate);
        txtCharges = (TextView) view.findViewById(R.id.txtCharges);
        txtTotal = (TextView) view.findViewById(R.id.txtTotal);
        btnOnline = (Button) view.findViewById(R.id.btnOnline);
        txtName.setText("Rate " + ioUtils.getPName());
        progressDialog = new ProgressDialog(getActivity());
        if (IOUtils.isNetworkAvailable(getContext())) {
            createJsonobjectForFareSummery();
        }

        /*  txtName.setText(IOUtils.pName);*/

        toggle = 0;
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle == 0) {
                    star1.setImageResource(R.mipmap.star_yellow);
                    star2.setImageResource(R.mipmap.star_grey);
                    star3.setImageResource(R.mipmap.star_grey);
                    star4.setImageResource(R.mipmap.star_grey);
                    star5.setImageResource(R.mipmap.star_grey);
                    toggle = 1;
                    rating = 1;
                    createJsonobjectForRateRide();
                } else {
                    star1.setImageResource(R.mipmap.star_grey);
                    star2.setImageResource(R.mipmap.star_grey);
                    star3.setImageResource(R.mipmap.star_grey);
                    star4.setImageResource(R.mipmap.star_grey);
                    star5.setImageResource(R.mipmap.star_grey);
                    toggle = 0;
                    rating = 0;
                }

            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_grey);
                star4.setImageResource(R.mipmap.star_grey);
                star5.setImageResource(R.mipmap.star_grey);
                toggle = 0;
                rating = 2;
                createJsonobjectForRateRide();
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_yellow);
                star4.setImageResource(R.mipmap.star_grey);
                star5.setImageResource(R.mipmap.star_grey);
                rating = 3;
                createJsonobjectForRateRide();
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_yellow);
                star4.setImageResource(R.mipmap.star_yellow);
                star5.setImageResource(R.mipmap.star_grey);
                rating = 4;
                createJsonobjectForRateRide();

            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_yellow);
                star4.setImageResource(R.mipmap.star_yellow);
                star5.setImageResource(R.mipmap.star_yellow);
                rating = 5;
                createJsonobjectForRateRide();

            }
        });
        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IOUtils ioUtils = new IOUtils(getActivity());
                ioUtils.setRideStatus(Constants.RIDE_NEW);
                Log.e("ridestatus", ioUtils.getRideStatus() + "");
                ActivityCompat.finishAffinity(getActivity());
                startActivity(new Intent(getActivity(), DashboardActivity.class));
                Consts.Status = "";

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


    public void createJsonobjectForRateRide() {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, userDetailsPojo.getId());
            jsonObject.put(Constants.RATE, "" + rating);
            Log.v("JsonObject", jsonObject.toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Confirm rating?");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog.setMessage("Rating..");
                    progressDialog.show();
                    rateRideApiCall(jsonObject);
                }
            });
            builder.show();


        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void rateRideApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_RATE_RIDE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                Toast.makeText(getActivity(), response.getString("response"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), response.getString("response"), Toast.LENGTH_SHORT).show();

                            }
                            progressDialog.dismiss();
                            ioUtils.setRideStatus(Constants.RIDE_NEW);
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);
    }


    public void createJsonobjectForFareSummery() {
        try {
            progressDialog.setMessage("Getting ride details..");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            Log.v("JsonObject", jsonObject.toString());
            fareSummeryApiCall(jsonObject);

        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void fareSummeryApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_GET_FARE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                          /*  if (response.getBoolean("result")) {
                                Toast.makeText(getActivity(), response.getString("response"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), response.getString("response"), Toast.LENGTH_SHORT).show();

                            }*/
                            JSONObject jsonObject = response.getJSONObject("ridesDetails");
                            txtCategory.setText(jsonObject.getString("vehicleModel"));
                            txtTime.setText(jsonObject.getString("rideTime"));
                            txtDistance.setText(jsonObject.getString("totalKm") + " km");
                            txtRate.setText(jsonObject.getString("totalAmount"));
                            txtCharges.setText("$ " + jsonObject.getString("waitingCharges"));
                            txtTotal.setText(jsonObject.getString("totalAmount"));
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
