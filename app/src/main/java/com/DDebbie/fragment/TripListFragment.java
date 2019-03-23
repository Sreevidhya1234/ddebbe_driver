package com.DDebbie.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.DDebbie.adapter.TripHistoryListAdapter;
import com.DDebbie.entity.TripHistory;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.google.android.gms.maps.model.LatLng;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TripListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TripListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripListFragment newInstance(String param1, String param2) {
        TripListFragment fragment = new TripListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ListView mListView;
    private ArrayList<TripHistory> mArrayList;
    private TripHistoryListAdapter mAdapter;
    private Button mBtnDelete;
    private View view;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ProgressDialog progressDialog;
    IOUtils ioUtils;
    UserDetailsPojo userDetailsPojo;
    private Button btnDelete;
    private int count = 1;
    Session session;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trip_list, container, false);

        session = new Session(getActivity());
        init();
        mArrayList = new ArrayList<>();
        if (IOUtils.isNetworkAvailable(getActivity())) {
            createJsonobjectForRides();
        }

   /*     mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("vtype", mArrayList.get(i).getvInfo());
                bundle.putString("date", mArrayList.get(i).getDate());
                bundle.putString("time", mArrayList.get(i).getTime());
                bundle.putString("distance", mArrayList.get(i).getTotalDistance());
                bundle.putString("waitingcharges", "$ "+mArrayList.get(i).getWaitingCharges());
                bundle.putString("totalcharges", "$ "+mArrayList.get(i).getTotalCharges());
                EarningInfoFragment tripInfoFragment = new EarningInfoFragment();
                tripInfoFragment.setArguments(bundle);
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, tripInfoFragment, tripInfoFragment.getClass().getName());
                fragmentTransaction.addToBackStack(tripInfoFragment.getClass().getName());
                fragmentTransaction.commit();

            }
        });*/

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete trip history?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String result = "Selected Product are :";
                        int totalAmount = 0;
                           /* for (TripHistory tripHistory : mAdapter.getBox()) {
                                if (tripHistory.box){
                                    String id = tripHistory.getRideId()+"\n";
                                    Log.e("ids", id);
                                }*/

                        ArrayList<TripHistory> tripHistories = mAdapter.getBox();

                        try {
                            JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray = new JSONArray();
                            Log.v("RIDS", tripHistories.size() + "");

                            for (int bool = 0; bool < tripHistories.size(); bool++) {
                                Log.v("RIDS", tripHistories.get(bool).isBox() + "");
                                if (tripHistories.get(bool).isBox()) {
                                    jsonArray.put(Integer.parseInt(tripHistories.get(bool).getRideId()));
                                }

                            }


                            jsonObject.put("rideIds", jsonArray.toString());
                            Log.v("RIDS", jsonObject.toString());

                            if (IOUtils.isNetworkAvailable(getActivity())) {
                                createJsonobjectForDeleteRides(jsonObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();

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


    private void init() {

        mListView = (ListView) view.findViewById(R.id.listTrip);
        ioUtils = new IOUtils(getActivity());
        userDetailsPojo = (UserDetailsPojo) ioUtils.getUser();
        btnDelete = (Button) view.findViewById(R.id.btnDelete);

    }


    public void createJsonobjectForRides() {
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.EMAIL, session.getD_email());
            jsonObject.put("pageNum", count);
            Log.v("JsonObject", jsonObject.toString());
            ridesApiCall(jsonObject);


            count = count + 1;
            // Create an OnScrollListener
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view,
                                                 int scrollState) { // TODO Auto-generated method stub
                    int threshold = 1;
                    int c = mListView.getCount();

                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (mListView.getLastVisiblePosition() >= c
                                - threshold) {
                            createJsonobjectForRides1();
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub

                }

            });

        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
 /*
    * regitrationApiCall - In this method we call the api...
    *
    * {"result":true,"response":"Rides",
    * "rides":[{"rideId":"6",
    * "dateTime":"2016-04-11 03:34 PM",
    * "pickUpLocation":"Karve Nagar",
    * "dropOffLocation":"Narveer Tanaji Wadi",
    * "noOfPassengers":"0",
    * "noOfBags":"0",
    * "totalAmount":"55.00",
    * "specialInstruction":"",
    * "rideType":"1",
    * "fromLongitude":"73.8156225",
    * "fromLatitude":"18.4932147",
    * "toLongitude":"73.8474647",
    * "toLatitude":"18.5308225","promoCode":"","discount":"0.00",
    * "totalKm":"0.00",
    * "paymentMode":"2","
    * paymentStatus":"1",
    * "status":"5","driverStatus":"1"},{"rideId":"5","dateTime":"2016-04-11 03:28 PM","pickUpLocation":"Karve Nagar","dropOffLocation":"Narveer Tanaji Wadi","noOfPassengers":"0","noOfBags":"0","totalAmount":"55.00","specialInstruction":"","rideType":"1","fromLongitude":"73.81562","fromLatitude":"18.4932214","toLongitude":"73.8474647","toLatitude":"18.5308225","promoCode":"","discount":"0.00","totalKm":"0.00","paymentMode":"2","paymentStatus":"1","status":"5","driverStatus":"1"}]}
    */

    public void ridesApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_GET_RIDES, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                JSONArray jsonArray = response.getJSONArray("rides");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String[] splited = jsonObject.getString("dateTime").split("\\s+");
                                    mArrayList.add(new TripHistory(jsonObject.getString("rideId"), jsonObject.getString("dateTime"), jsonObject.getString("pickUpLocation"), jsonObject.getString("dropOffLocation"), splited[1], jsonObject.getString("totalKm"), jsonObject.getString("waitingCharges"), jsonObject.getString("totalAmount"), jsonObject.getString("driverRating"), jsonObject.getString("driverRating"), false));

                                }
                                mAdapter = new TripHistoryListAdapter(mArrayList, getActivity());
                                mListView.setAdapter(mAdapter);

                            } else {
                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
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


    public void createJsonobjectForRides1() {
        try {
        /*    progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.show();*/
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.EMAIL, session.getD_email());
            jsonObject.put("pageNum", count);
            Log.v("JsonObject", jsonObject.toString());


            count = count + 1;
            ridesApiCall1(jsonObject);
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }


    public void ridesApiCall1(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_GET_RIDES, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                JSONArray jsonArray = response.getJSONArray("rides");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String[] splited = jsonObject.getString("dateTime").split("\\s+");
                                    mArrayList.add(new TripHistory(jsonObject.getString("rideId"), jsonObject.getString("dateTime"), jsonObject.getString("pickUpLocation"), jsonObject.getString("dropOffLocation"), splited[1], jsonObject.getString("totalKm"), jsonObject.getString("waitingCharges"), jsonObject.getString("totalAmount"), jsonObject.getString("driverRating"), jsonObject.getString("driverRating"), false));

                                }
                                int position = mListView.getLastVisiblePosition();
                                mAdapter = new TripHistoryListAdapter(mArrayList, getActivity());
                                mListView.setAdapter(mAdapter);

                                mListView.setSelectionFromTop(position, 0);

                            } else {
                                count = count - 1;

                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
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


    public void createJsonobjectForDeleteRides(JSONObject jsonObject) {
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Deleting rides..");
            progressDialog.show();
            //jsonObject.put("driverId",userDetailsPojo.getId());
            Log.v("JsonObject", jsonObject.toString());
            deleteRidesApiCall(jsonObject);

        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }


    public void deleteRidesApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_BASE + "Drivers/DeleteRides", js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {

                                mArrayList.clear();

                                IOUtils.toastMessage(getContext(), response.getString("response"));
                                progressDialog.dismiss();
                                mListView.setAdapter(new TripHistoryListAdapter(mArrayList, getContext()));

                                count = 1;
                                createJsonobjectForRides();


                            } else {
                                IOUtils.toastMessage(getContext(), response.getString("response"));

                                mAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();

                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
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





}
