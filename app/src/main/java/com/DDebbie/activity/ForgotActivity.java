package com.DDebbie.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.DDebbie.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotActivity extends AppCompatActivity {

    private Button mBtnReset,mBtnCancel;
    private EditText mEdtEmail;
    private Context context = this;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        mBtnReset = (Button) findViewById(R.id.btnReset);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mEdtEmail = (EditText) findViewById(R.id.edtEmail);

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
                //Toast.makeText(getApplicationContext(),"Reset",Toast.LENGTH_SHORT).show();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void validate() {


        if (mEdtEmail.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Please enter your Email.", "OK");
        } else if (!mEdtEmail.getText().toString().matches(EMAIL_PATTERN)) {
            IOUtils.alertMessegeDialog(context, "Please enter valid Email.", "OK");
        } else {
            if (IOUtils.isNetworkAvailable(ForgotActivity.this)) {
                try {
                    createJsonobjectForApiCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                IOUtils.setAlertForActivity(ForgotActivity.this);
            }
        }
    }


    /*
    * createJsonobjectForApiCall - In this method we create JsonObject for api call...
    */

    public void createJsonobjectForApiCall(){
        try{
            dialog = IOUtils.getProgessDialog(context);
            dialog.show();

            JSONObject jsonObject = new JSONObject();




            jsonObject.put(Constants.EMAIL,mEdtEmail.getText().toString());

            Log.v("JsonObject", jsonObject.toString());


            regitrationApiCall(jsonObject);


        }catch (Exception e){

        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    */
    public void regitrationApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(ForgotActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_FORGOT_PASSWORD, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        //{"result":true,"response":"Reset code mailed"}
                        try {
                            if (response.getBoolean("result")) {
                                IOUtils.toastMessage(context, response.getString("response"));
                                finish();
                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                            }
                            dialog.dismiss();
                        }catch (JSONException e){
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
