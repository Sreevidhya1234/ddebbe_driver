package com.DDebbie.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobPreviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobPreviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public JobPreviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobPreviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobPreviewFragment newInstance(String param1, String param2) {
        JobPreviewFragment fragment = new JobPreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View view;
    Point p;
    private Button mBtnArrive;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    IOUtils ioUtils;
    UserDetailsPojo userDetailsPojo;
    private TextView txtTimer;
    LinearLayout viewGroup;
    LayoutInflater layoutInflater;
    View layout;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private TextView txtInfo;
    private Bundle bundle;
    private RideAccept rideAccept;
    private LatLng pickupLatLng;
    private ImageView imgNav;
    Session session;
    SharedPreferences sharedPreferences;

    ImageView img_go, img_cross;
    TextView edit_feed;
    TextView text_send, text_title;
    LinearLayout linear_send;
    public static final String TAG = JobPreviewFragment.class.getSimpleName();

    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "", Area = "", Connec = "";
    Geocoder geocoder;
    String Check_lat = "";
    String Check_Long = "";
    LinearLayout linear_ok, linear_cancel;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_job_preview, container, false);

        Consts.JobPrivew = "true";

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rideAccept", "");
        rideAccept = gson.fromJson(json, RideAccept.class);
        Geocoder geocoder;


        session = new Session(getActivity());
        imgNav = (ImageView) view.findViewById(R.id.imgNavigate);
        ioUtils = new IOUtils(getActivity());
        ioUtils.setRideStatus(Constants.RIDE_DRIVER_ACCEPTED);
       // ((DashboardActivity) getActivity()).showOnOffToggle(false);

        userDetailsPojo = ioUtils.getUser();
        mBtnArrive = (Button) view.findViewById(R.id.btnArrived);


        mBtnArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IOUtils.isNetworkAvailable(getActivity())) {
                    if (ioUtils.getPaymentStatus() == true) {
                        createJsonobjectForArrived();
                    } else {
                        IOUtils.toastMessage(getActivity(), "Please wait! Payment is not done yet.");
                    }
                }


            }
        });

        txtInfo = (TextView) view.findViewById(R.id.txtInfo);

        Check(rideAccept.getPickUpLocation());

        //  txtInfo.setText("Pickup location\n" + rideAccept.getPickUpLocation());


        pickupLatLng = new LatLng(Double.parseDouble(rideAccept.getFromLatitude()), Double.parseDouble(rideAccept.getFromLongitude()));
        onButtonPressed();

        imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG,"lat==>"+Check_lat+"\nlang==>"+Check_Long);
                Log.e(TAG,"destination_latitude==>"+ioUtils.getDestination_lat()+"\ndestination_longitude==>"+ioUtils.getDestination_lang());


                // Check(rideAccept.getPickUpLocation());


             /*   Uri gmmIntentUri = Uri.parse("google.navigation:q=" + rideAccept.getDropOffLocation() + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);*/
                String url = "https://maps.google.com/?saddr="+Check_lat+"&daddr="+Check_Long+"&views=transit&directionsmode=driving";

                if(!ioUtils.getDestination_lat().equalsIgnoreCase("")&& !ioUtils.getDestination_lang().equalsIgnoreCase("")) {


                    Uri gmmIntentUri = null;
                    try {
                        // gmmIntentUri = Uri.parse("google.navigation:q=" + Check_lat + "," + Check_Long + "&mode=d");
                        gmmIntentUri = Uri.parse("https://maps.google.com/?saddr=" + Check_lat + "," + Check_Long + "&daddr=" + ioUtils.getDestination_lat() + "," + ioUtils.getDestination_lang() + "&views=transit&directionsmode=driving");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    // mapIntent.setPackage(url);
                    startActivity(mapIntent);
                }else {
                    Toast.makeText(getActivity(),"destination location null",Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (session.getonoff().equalsIgnoreCase("4")) {
            session.setonoff("2");
            Payment_Dialog();
        } else {

            Log.e("Check", "Check====>");
        }


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction(pickupLatLng);
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
        void onFragmentInteraction(LatLng latLng);

    }


    public void createJsonobjectForArrived() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.DRIVER_ID, userDetailsPojo.getId());
            Log.v("JsonObject", jsonObject.toString());
            arrivedApiCall(jsonObject);

        } catch (Exception e) {

        }
    }
    /*
     * regitrationApiCall - In this method we call the api...
     */

    public void arrivedApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_ON_LOCATION, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                ManualDestinationFragment manualDestinationFragment = new ManualDestinationFragment();
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container, manualDestinationFragment, manualDestinationFragment.getClass().getName());
                                fragmentTransaction.addToBackStack(manualDestinationFragment.getClass().getName());
                                fragmentTransaction.commit();
                                ioUtils.setRideStatus(Constants.RIDE_DRIVER_ON_PICK);

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


    public void Payment_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        //  builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View content = inflater.inflate(R.layout.customdialog, null);
        builder.setView(content);

        //  AlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        text_title = (TextView) content.findViewById(R.id.text_title);


        text_title.setText("Payment Done");

        edit_feed = (TextView) content.findViewById(R.id.edit_feed);
        edit_feed.setText(" \n To Start Drive");


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


            txtInfo.setText("Pickup location :\n" + knownName + "," + Area + "," + city + "," + state + "," + country + "," + postalCode);

            //txtInfo.setText("Pickup location :\n" + this.address);

            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }



}
