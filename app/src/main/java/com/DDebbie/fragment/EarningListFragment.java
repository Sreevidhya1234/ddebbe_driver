package com.DDebbie.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.util.Consts;
import com.DDebbie.util.GPSTracker;
import com.DDebbie.util.StartLocationAlert;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.DDebbie.activity.HomeActivity;
import com.DDebbie.adapter.EarningListAdapter;
import com.DDebbie.entity.Earnings;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EarningListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EarningListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EarningListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ProgressDialog dialog;

    public EarningListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EarningListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EarningListFragment newInstance(String param1, String param2) {
        EarningListFragment fragment = new EarningListFragment();
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
    private ListView mListView;
    private ArrayList<Earnings> mArrayList;
    private EarningListAdapter mAdapter;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    Session session;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_earning_list, container, false);
        mactivity = getActivity();
        session = new Session(getActivity());
        createJsonobjectForApiCall();
        mListView = (ListView) view.findViewById(R.id.listEarning);
        mArrayList = new ArrayList<>();
        gps = new GPSTracker(mactivity);


        GpsLocation();
      /*
*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TripInfoFragment tripInfoFragment = new TripInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Data", mArrayList);
                bundle.putInt("Position", i);
                tripInfoFragment.setArguments(bundle);
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, tripInfoFragment, tripInfoFragment.getClass().getName());
                fragmentTransaction.addToBackStack(tripInfoFragment.getClass().getName());
                fragmentTransaction.commit();

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


    public void createJsonobjectForApiCall() {
        try {
            dialog = IOUtils.getProgessDialog(getContext());
            dialog.show();

            JSONObject jsonObject = new JSONObject();




         /*   jsonObject.put(Constants.EMAIL,"tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD,"123456");
*/

            IOUtils ioUtils = new IOUtils(getContext());
            UserDetailsPojo userDetailsPojo = ioUtils.getUser();

            jsonObject.put(Constants.EMAIL, session.getD_email());
            jsonObject.put(Constants.DRIVER_ID, session.getUser_id());
            jsonObject.put(Constants.WAIT_COUNTRY, Consts.Country);
            jsonObject.put(Constants.PAGE_MO, "0");


            Log.v("JsonObject", jsonObject.toString());


            regitrationApiCall(jsonObject);


        } catch (Exception e) {

        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    */
    public void regitrationApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_EARNINGS, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());

                        //{"result":"true","response":"Successfully login","driverData":{"id":"2","driverName":"Tushar","contactNumber":"8983472919","address":"Lives in air",
                        // "photo":"http:\/\/vishalbhosale.com\/projects\/debbie\/dimg\/default.png","postalCode":"000000","licenceNum":"MH122547874154","prepaidBalance":"0.00",
                        // "subscriptionExpiryDate":"0000-00-00 00:00:00","email":"tushar.appsplanet@gmail.com","status":1}}

                        try {
                            if (response.getBoolean("result")) {

                                Gson gson = new Gson();

                                for (int i = 0; i < response.getJSONArray("earnings").length(); i++) {

                                    mArrayList.add(gson.fromJson(response.getJSONArray("earnings").getJSONObject(i).toString(), Earnings.class));
                                }

                                mAdapter = new EarningListAdapter(mArrayList, getActivity());
                                mListView.setAdapter(mAdapter);

                            } else {
                                IOUtils.alertMessegeDialog(getContext(), response.getString("response"), "OK");
                            }

                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                dialog.dismiss();
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


}
