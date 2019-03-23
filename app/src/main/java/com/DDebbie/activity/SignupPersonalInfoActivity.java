package com.DDebbie.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.Constants;
import com.DDebbie.util.Consts;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.DDebbie.util.Util;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignupPersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private EditText mEdtName, mEdtLName, mEdtContactNumber, mEdtEmail, mEdtPassword, mEdtConfirmPassword, mEdtLicenceNumber, mEdtAddress, mEdtPostalCode;
    String token = "";
    private boolean isQuickSignup;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_personal_info);
        mContext = this;
        session = new Session(this);
        init();
        gcmRegistration();

    }

    private void init() {
        mEdtName = (EditText) findViewById(R.id.edtName);
        mEdtLName = (EditText) findViewById(R.id.edtLName);
        mEdtContactNumber = (EditText) findViewById(R.id.edtContactNumber);
        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtPassword = (EditText) findViewById(R.id.edtPassword);
        mEdtPassword.setTypeface(Typeface.DEFAULT);
        mEdtPassword.setTransformationMethod(new PasswordTransformationMethod());
        mEdtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        mEdtConfirmPassword.setTypeface(Typeface.DEFAULT);
        mEdtConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        mEdtLicenceNumber = (EditText) findViewById(R.id.edtLicenceNumber);
        mEdtAddress = (EditText) findViewById(R.id.edtAddress);
        mEdtPostalCode = (EditText) findViewById(R.id.edtPostalCode);
        isQuickSignup = getIntent().getBooleanExtra(Constants.INTENT_QUICK_SIGNUP, false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnNext) {//NEXT
            validation();

        }
    }

    private void validation() {
        String name = mEdtName.getText().toString().trim();
        String lname = mEdtLName.getText().toString().trim();
        String contactNumber = mEdtContactNumber.getText().toString().trim();
        String email = mEdtEmail.getText().toString().trim();
        String password = mEdtPassword.getText().toString().trim();
        String confirmPassword = mEdtConfirmPassword.getText().toString().trim();
        String licence = mEdtLicenceNumber.getText().toString().trim();
        String address = mEdtAddress.getText().toString().trim();
        String postalCode = mEdtPostalCode.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            mEdtName.setError("Please enter first name.");
//            IOUtils.toastMessage(mContext, "Please enter first name");
        } else if (TextUtils.isEmpty(lname)) {
            mEdtLName.setError("Please enter last name.");
//            IOUtils.toastMessage(mContext, "Please enter last name");
        } else if (TextUtils.isEmpty(contactNumber)) {
            mEdtContactNumber.setError("Please enter contact number.");
//            IOUtils.toastMessage(mContext, "Please enter contact number");
        } else if (contactNumber.length() < 10) {
            mEdtContactNumber.setError("Please enter valid number.");
//            IOUtils.toastMessage(mContext, "Please enter contact number");
        } else if (TextUtils.isEmpty(email)) {
            mEdtEmail.setError("Please enter email.");
//            IOUtils.toastMessage(mContext, "Please enter email");
        } else if (!Util.emailValidator(email)) {
            mEdtEmail.setError("Please enter valid email.");
//            IOUtils.toastMessage(mContext, "Please enter email");
        } else if (TextUtils.isEmpty(password)) {
            mEdtPassword.setError("Please enter password.");
//            IOUtils.toastMessage(mContext, "Please password");
        } else if (TextUtils.isEmpty(confirmPassword)) {
            mEdtConfirmPassword.setError("Please enter confirm password.");
//            IOUtils.toastMessage(mContext, "Please confirm password");
        } else if (!password.equalsIgnoreCase(confirmPassword)) {
            mEdtConfirmPassword.setError("Confirm password and password not match");
//            IOUtils.toastMessage(mContext, "Confirm password and password not match");
        } else if (TextUtils.isEmpty(licence)) {
            mEdtLicenceNumber.setError("Please enter licence number.");
//            IOUtils.toastMessage(mContext, "Please licence");
        } else if (TextUtils.isEmpty(address)) {
            mEdtAddress.setError("Please enter address.");
//            IOUtils.toastMessage(mContext, "Please address");
        } else if (TextUtils.isEmpty(postalCode)) {
            mEdtPostalCode.setError("Please enter postal code.");
//            IOUtils.toastMessage(mContext, "Please postal code");
        } else {
            savePersonalInfo(name, lname, contactNumber, email, password, licence, address, postalCode);
        }
    }

    /**
     * @param name
     * @param lname
     * @param contactNumber
     * @param email
     * @param password
     * @param licence
     * @param address
     * @param postalCode
     */
    private void savePersonalInfo(String name, String lname, String contactNumber, final String email, String password, String licence, String address, String postalCode) {

        final ProgressDialog dialog = ProgressDialog.show(mContext, null, "Loading...");
        JSONObject input = new JSONObject();
        try {
            input.put("driverName", name + " " + lname);
            input.put("email", email);
            input.put("password", password);
            input.put("contactNumber", contactNumber);
            //input.put("photo", name);
            input.put("deviceToken", token);
            input.put("licenceNum", licence);
            input.put("deviceType", "1");
            input.put("postalcode", postalCode);
            input.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", "input:" + input.toString());

        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_REGISTER, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("test", "response:" + response.toString());
                            dialog.dismiss();

                            /**
                             * "customerName": "vh",
                             "contactNumber": "9895",
                             "photo": "http:\/\/www.ddebbie.com\/api\/dimg\/default.png",
                             "email": "bdj",
                             "status": "1"
                             */
                            if (response.getString("result").equalsIgnoreCase("true")) {

                                JSONObject jsonObject = response.getJSONObject("customerData");
                                String driverId = jsonObject.getString("id");
                                session.setUser_id(jsonObject.getString("id"));

                                Consts.driver_id = jsonObject.getString("id");

                                Log.e("id", "id===>" + session.getUser_id());
                                UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                                userDetailsPojo.setId(driverId);
                                userDetailsPojo.setDriverName(jsonObject.getString("customerName"));
                                userDetailsPojo.setContactNumber(jsonObject.getString("contactNumber"));
                                userDetailsPojo.setPhoto(jsonObject.getString("photo"));
                                userDetailsPojo.setStatus(jsonObject.getString("status"));
                                userDetailsPojo.setEmail(jsonObject.getString("email"));
                                session.setD_email(jsonObject.getString("email"));


                                IOUtils ioUtils = new IOUtils(mContext);
                                ioUtils.setUser(userDetailsPojo);


                                if (isQuickSignup) {
                                    ioUtils.setQuickSignupStep(Constants.QUICK_SIGNUP_STEP.PERSONAL);
                                }

                                startActivity(new Intent(mContext, SignupVehicleInfoActivity.class)
                                        .putExtra(Constants.INTENT_QUICK_SIGNUP, isQuickSignup));
                                finish();

                            }
                            IOUtils.toastMessage(mContext, response.getString("response"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                dialog.dismiss();
            }
        });


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);

    }

    public void gcmRegistration() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    token =
                            InstanceID.getInstance(mContext).getToken(Constants.GCM_SENDER_ID,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.v("Registration id", token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(null, null, null);
    }
}
