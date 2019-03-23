package com.DDebbie.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.DDebbie.adapter.CarDetailsListAdapter;
import com.DDebbie.entity.CarDetails;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.CircleTransform;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog dialog;
    Context context = this;
    private IOUtils ioUtils;
    private UserDetailsPojo userDetailsPojo;

    private CarDetailsListAdapter carDetailsListAdapter;
    private ImageView imgDp;
    private ListView listView;
    private ArrayList<CarDetails> arrayList;
    private TextView txtName, txtEmail, txtPhone, txtInsurance;
    private Button mBtnProfileEdit;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        session = new Session(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.back_icon);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ioUtils = new IOUtils(this);
        userDetailsPojo = ioUtils.getUser();

        listView = (ListView) findViewById(R.id.carList);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtInsurance = (TextView) findViewById(R.id.txtInsurance);
        imgDp = (ImageView) findViewById(R.id.imgUserProfile);
        mBtnProfileEdit = (Button) findViewById(R.id.btnProfileEdit);
        mBtnProfileEdit.setOnClickListener(this);

        if (userDetailsPojo != null) {
            txtName.setText(session.getD_name());
            txtEmail.setText(session.getD_email());
            txtPhone.setText(session.getD_number());
            Picasso.with(context).load(userDetailsPojo.getPhoto()).transform(new CircleTransform())
                    .placeholder(R.mipmap.user_default)
                    .error(R.mipmap.user_default)
                    .into(imgDp);
        }

        arrayList = new ArrayList<>();
        createJsonobjectForGetVehicalApiCall();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtInsurance.setText(arrayList.get(i).getLicensing());
            }
        });


    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, 0);

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
            jsonObject.put(Constants.ID,  session.getUser_id());
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


        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);

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
                CarDetails carDetails = new CarDetails(jsonArray.getJSONObject(i).getString("vehicleNumber"),
                        jsonArray.getJSONObject(i).getString("licensing"));
                arrayList.add(carDetails);

            }

            carDetailsListAdapter = new CarDetailsListAdapter(arrayList, ProfileActivity.this);

            listView.setAdapter(carDetailsListAdapter);
            dialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnProfileEdit) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            finish();
        }
    }
}
