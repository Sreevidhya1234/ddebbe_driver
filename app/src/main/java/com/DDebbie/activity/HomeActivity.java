package com.DDebbie.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ImageView mImgCkeckbox;
    private ProgressDialog dialog;
    Context context = this;
    private IOUtils ioUtils;
    Spinner sprCarName;
    private UserDetailsPojo userDetailsPojo;
    private ArrayList<String> arrayListCarName;
    private ArrayList<String> arrayListCarId;
    private ArrayList<String> arrayListCarTypeId;
    ArrayAdapter<String> dataAdapter;
    int carSelectedPosition = 0;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        session = new Session(this);
        ioUtils = new IOUtils(HomeActivity.this);
        userDetailsPojo = ioUtils.getUser();

        sprCarName = (Spinner) findViewById(R.id.carName);
        arrayListCarName = new ArrayList<String>();
        arrayListCarId = new ArrayList<String>();
        arrayListCarTypeId = new ArrayList<>();
        arrayListCarName.add("Please select the car.");

        createJsonobjectForGetVehicalApiCall();


        sprCarName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carSelectedPosition = position;
                Log.v("CarPosition", carSelectedPosition + "");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mImgCkeckbox = (ImageView) findViewById(R.id.imgCheckbox);
        mImgCkeckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImgCkeckbox.setImageResource(R.mipmap.check_mark_on);
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }
                },500);*/
                if (carSelectedPosition != 0)
                    createJsonobjectForSetVehicalApiCall();
                else {
                    Toast.makeText(getApplicationContext(), "Please select the car.", Toast.LENGTH_SHORT).show();
                    mImgCkeckbox.setImageResource(R.mipmap.check_mark);

                }


            }
        });
    }



    /*
    * createJsonobjectForApiCall - In this method we create JsonObject for api call...
    */

    public void createJsonobjectForGetVehicalApiCall() {
        try {
            dialog = IOUtils.getProgessDialog(context);
            dialog.show();

            JSONObject jsonObject = new JSONObject();




         /*   jsonObject.put(Constants.EMAIL,"tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD,"123456");
*/
            jsonObject.put(Constants.ID, session.getUser_id());
            jsonObject.put(Constants.EMAIL, session.getD_email());


            Log.v("JsonObject", jsonObject.toString());


            getVehicalApiCall(jsonObject);


        } catch (Exception e) {

        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    */
    public void getVehicalApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_GET_VEHICLES, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());


                        jsonDataExtract(response);


                      /*  dataAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, arrayListCarName);

                        sprCarName.setAdapter(dataAdapter);*/

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

    public void jsonDataExtract(JSONObject jsonObject) {

       /* {"result":false,"response":"Vehicle List","vehiclesData":[{"vehicleId":"1","vehicleNumber":"MH 12 1212","year":"2015","model":"Mercedes A-Class","numOfDoors":"4","photoPath":"http:\/\/vishalbhosale.com\/projects\/debbie\/vimg\/car-one.jpg","ownership":"Self","licensing":"HDFC","insuranceCompName":"LIC","driverId":"2","status":"0","vehicleType":"Small"},
            {"vehicleId":"4","vehicleNumber":"MH 12 1212","year":"2015","model":"Swift Delux","numOfDoors":"4","photoPath":"http:\/\/vishalbhosale.com\/projects\/debbie\/vimg\/default.png","ownership":"Self","licensing":"No lice","insuranceCompName":"LIC","driverId":"2","status":"0","vehicleType":"Medium"},
            {"vehicleId":"3","vehicleNumber":"MH 16 1616","year":"2016","model":"Mercedes C-Class","numOfDoors":"4","photoPath":"http:\/\/vishalbhosale.com\/projects\/debbie\/vimg\/car-two.jpg","ownership":"Self","licensing":"HDFC","insuranceCompName":"ICICI","driverId":"2","status":"1","vehicleType":"Limousine"},
            {"vehicleId":"2","vehicleNumber":"MH 14 1414","year":"2014","model":"Mercedes B-Class","numOfDoors":"4","photoPath":"http:\/\/vishalbhosale.com\/projects\/debbie\/vimg\/car-three.jpg","ownership":"Self","licensing":"Self LIC","insuranceCompName":"LIC","driverId":"2","status":"1","vehicleType":"SUV"}]}
        */
        try {

            JSONArray jsonArray = jsonObject.getJSONArray("vehiclesData");


            for (int i = 0; i < jsonArray.length(); i++) {
                arrayListCarName.add(jsonArray.getJSONObject(i).getString("model") + " - " + jsonArray.getJSONObject(i).getString("vehicleNumber"));
                arrayListCarId.add(jsonArray.getJSONObject(i).getString("vehicleId"));
                arrayListCarTypeId.add(jsonArray.getJSONObject(i).getString("vehicleType"));

            }

            dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, arrayListCarName);

            sprCarName.setAdapter(dataAdapter);
            dialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



     /*
    * createJsonobjectForApiCall - In this method we create JsonObject for api call...
    */

    public void createJsonobjectForSetVehicalApiCall() {
        try {
            dialog = IOUtils.getProgessDialog(context);
            dialog.show();

            JSONObject jsonObject = new JSONObject();




         /*   jsonObject.put(Constants.EMAIL,"tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD,"123456");
*/
            jsonObject.put(Constants.ID, session.getUser_id());
            jsonObject.put(Constants.EMAIL, session.getD_email());
            jsonObject.put(Constants.VEHICAL_ID, arrayListCarId.get(carSelectedPosition - 1));


            Log.v("JsonObject", jsonObject.toString());


            setVehicalApiCall(jsonObject);


        } catch (Exception e) {

        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    */
    public void setVehicalApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_SET_VEHICLES, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());

                        //      {"result":"true","response":"Active Vehicle Changed"}
                        try {
                            if (response.getString("result").equals("true")) {
                                IOUtils utils = new IOUtils(HomeActivity.this);
                                utils.setVehicle(true, arrayListCarTypeId.get(carSelectedPosition - 1));
                                ioUtils.setDriverStatus(true);
                                Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                            }
                            dialog.dismiss();
                        } catch (JSONException e) {
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

}
