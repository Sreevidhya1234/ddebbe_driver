package com.DDebbie.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private Button mBtnSignin, mBtnForgot;
    private EditText mEdtEmail;

    private ShowHidePasswordEditText mEdtPassword;
    private Context context = this;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private ProgressDialog dialog;
    private LinearLayout main_layout;
    String token = "";
    private IOUtils ioUtils;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mContext = this;
        session = new Session(this);

        ioUtils = new IOUtils(mContext);
        mBtnSignin = (Button) findViewById(R.id.btnSignIN);
        mBtnForgot = (Button) findViewById(R.id.btnForgot);
        gcmRegistration();


        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtPassword = (ShowHidePasswordEditText) findViewById(R.id.edtPassword);
        //  mEdtPassword.setTypeface(Typeface.DEFAULT);
        //   mEdtPassword.setTransformationMethod(new PasswordTransformationMethod());


        //   mEdtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        main_layout = (LinearLayout) findViewById(R.id.main_layout);

        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOUtils.hideSoftKeyboard(SigninActivity.this);

            }
        });


        mBtnForgot.setOnClickListener(this);
        mBtnSignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIN:
                ioUtils.setIsQuickSignUp(false);
                validate();
                break;

            case R.id.btnForgot:
                Intent intent = new Intent(SigninActivity.this, ForgotActivity.class);
                startActivity(intent);
                break;

            case R.id.btnSignUp://SIGNUP
                ioUtils.setIsQuickSignUp(false);
                startActivity(new Intent(SigninActivity.this, SignupPersonalInfoActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, false));
                break;

            case R.id.btnQuickSignUp:
                ioUtils.setIsQuickSignUp(true);
                Constants.QUICK_SIGNUP_STEP quick_signup_step = ioUtils.getQuickSignupStep();
                if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.NONE) {
                    startActivity(new Intent(SigninActivity.this, SignupPersonalInfoActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, true));
                } else if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.PERSONAL) {
                    startActivity(new Intent(SigninActivity.this, SignupVehicleInfoActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, true));
                } else if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.VEHICLE) {
                    startActivity(new Intent(SigninActivity.this, QuickSignupUploadDocActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, true));
                } else if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.DOCS) {
                    startActivity(new Intent(SigninActivity.this, SignupPersonalInfoActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, true));
                }


                break;
        }
    }

    /*
     *
     * gcmRegistration - This method is user to register Device Google Cloud messaging...
     *
     * */

    public void gcmRegistration() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    token =
                            InstanceID.getInstance(context).getToken(Constants.GCM_SENDER_ID,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.v("Registration id", token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(null, null, null);
    }


    /*
     * validate - This method is used validate app all the feild...
     */
    public void validate() {


        if (mEdtEmail.getText().toString().equals("")) {
            mEdtEmail.setError("Please enter your Email.");
            IOUtils.alertMessegeDialog(context, "Please enter your Email.", "OK");
        } else if (!mEdtEmail.getText().toString().matches(EMAIL_PATTERN)) {
            IOUtils.alertMessegeDialog(context, "Please enter valid Email.", "OK");
        } else if (mEdtPassword.getText().toString().equals("")) {
            mEdtPassword.setError("Please enter your Password.");
            IOUtils.alertMessegeDialog(context, "Please enter Password.", "OK");
        } else {

            if (IOUtils.isNetworkAvailable(SigninActivity.this)) {
                try {
                    createJsonobjectForApiCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                IOUtils.setAlertForActivity(SigninActivity.this);
            }


        }
    }


    /*
     * createJsonobjectForApiCall - In this method we create JsonObject for api call...
     */

    public void createJsonobjectForApiCall() {
        try {
            dialog = IOUtils.getProgessDialog(context);
            dialog.show();

            JSONObject jsonObject = new JSONObject();

         /*   jsonObject.put(Constants.EMAIL,"tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD,"123456");
*/
            jsonObject.put(Constants.EMAIL, mEdtEmail.getText().toString());

            jsonObject.put(Constants.PASSWORD, mEdtPassword.getText().toString());
            jsonObject.put(Constants.DEVICE_TOKEN, token);
            jsonObject.put(Constants.DEVICE_TYPE, "1");

            Log.v("JsonObject", jsonObject.toString());


            regitrationApiCall(jsonObject);


        } catch (Exception e) {

        }
    }

    /*
     * regitrationApiCall - In this method we call the api...
     */
    public void regitrationApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(SigninActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_SIGNIN, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());

                        //{"result":"true","response":"Successfully login","driverData":{"id":"2","driverName":"Tushar","contactNumber":"8983472919","address":"Lives in air",
                        // "photo":"http:\/\/vishalbhosale.com\/projects\/debbie\/dimg\/default.png","postalCode":"000000","licenceNum":"MH122547874154","prepaidBalance":"0.00",
                        // "subscriptionExpiryDate":"0000-00-00 00:00:00","email":"tushar.appsplanet@gmail.com","status":1}}

                        try {
                            if (response.getString("response").equals("Successfully login")) {

                                JSONObject jsonObject = response.getJSONObject("driverData");

                                UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                                userDetailsPojo.setId(jsonObject.getString("id"));
                                session.setUser_id(jsonObject.getString("id"));
                                //  Consts.driver_id = jsonObject.getString("id");
                                Log.e("id", "id===>" + session.getUser_id());
                                userDetailsPojo.setDriverName(jsonObject.getString("driverName"));
                                session.setD_name(jsonObject.getString("driverName"));
                                Log.e("name", "name===>" + session.getD_name());
                                userDetailsPojo.setContactNumber(jsonObject.getString("contactNumber"));
                                session.setD_number(jsonObject.getString("contactNumber"));
                                userDetailsPojo.setAddress(jsonObject.getString("address"));
                                userDetailsPojo.setPhoto(jsonObject.getString("photo"));
                                userDetailsPojo.setPostalCode(jsonObject.getString("postalCode"));
                                userDetailsPojo.setLicenceNum(jsonObject.getString("licenceNum"));
                                userDetailsPojo.setPrepaidBalance(jsonObject.getString("prepaidBalance"));
                                userDetailsPojo.setSubscriptionExpiryDate(jsonObject.getString("subscriptionExpiryDate"));
                                userDetailsPojo.setEmail(jsonObject.getString("email"));
                                session.setD_email(jsonObject.getString("email"));

                                Log.e("D_email", "D_email==>" + session.getD_email());


                                Consts.Email = jsonObject.getString("email");
                                userDetailsPojo.setStatus(jsonObject.getString("status"));
                                IOUtils ioUtils = new IOUtils(context);
                                ioUtils.setUser(userDetailsPojo);
                                IOUtils.toastMessage(context, response.getString("response"));
                                finish();
                                Consts.Status = "";
                                Intent intent2 = new Intent(SigninActivity.this, DashboardActivity.class);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent2);
                                finish();


                            } else if (response.getString("response").equals("Always login Other Device")) {
                                // relog_Dialog();
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");

                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
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


    //New Check


    public void reloginJsonobjectForApiCall() {
        try {
            dialog = IOUtils.getProgessDialog(context);
            dialog.show();

            JSONObject jsonObject = new JSONObject();

           /* jsonObject.put(Constants.EMAIL, "tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD, "123456");
*/

            jsonObject.put(Constants.EMAIL, mEdtEmail.getText().toString());
            jsonObject.put(Constants.PASSWORD, mEdtPassword.getText().toString());
            jsonObject.put(Constants.DEVICE_TOKEN, token);
            jsonObject.put(Constants.DEVICE_TYPE, "1");

            Log.v("JsonObject", jsonObject.toString());


            reApiCall(jsonObject);


        } catch (Exception e) {

        }
    }


    public void reApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(SigninActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_RELOGIN, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());

                        //{"result":"true","response":"Successfully login","driverData":{"id":"2","driverName":"Tushar","contactNumber":"8983472919","address":"Lives in air",
                        // "photo":"http:\/\/vishalbhosale.com\/projects\/debbie\/dimg\/default.png","postalCode":"000000","licenceNum":"MH122547874154","prepaidBalance":"0.00",
                        // "subscriptionExpiryDate":"0000-00-00 00:00:00","email":"tushar.appsplanet@gmail.com","status":1}}

                        try {
                            if (response.getString("response").equals("Successfully login")) {

                                JSONObject jsonObject = response.getJSONObject("driverData");

                                UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                                userDetailsPojo.setId(jsonObject.getString("id"));
                                session.setUser_id(jsonObject.getString("id"));
                                Consts.driver_id = jsonObject.getString("id");
                                Log.e("id", "id===>" + session.getUser_id());
                                userDetailsPojo.setDriverName(jsonObject.getString("driverName"));
                                session.setD_name(jsonObject.getString("driverName"));
                                Log.e("name", "name===>" + session.getD_name());
                                userDetailsPojo.setContactNumber(jsonObject.getString("contactNumber"));
                                session.setD_number(jsonObject.getString("contactNumber"));
                                userDetailsPojo.setAddress(jsonObject.getString("address"));
                                userDetailsPojo.setPhoto(jsonObject.getString("photo"));
                                userDetailsPojo.setPostalCode(jsonObject.getString("postalCode"));
                                userDetailsPojo.setLicenceNum(jsonObject.getString("licenceNum"));
                                userDetailsPojo.setPrepaidBalance(jsonObject.getString("prepaidBalance"));
                                userDetailsPojo.setSubscriptionExpiryDate(jsonObject.getString("subscriptionExpiryDate"));
                                userDetailsPojo.setEmail(jsonObject.getString("email"));
                                session.setD_email(jsonObject.getString("email"));

                                Log.e("D_email", "D_email==>" + session.getD_email());


                                Consts.Email = jsonObject.getString("email");
                                userDetailsPojo.setStatus(jsonObject.getString("status"));

                                IOUtils ioUtils = new IOUtils(context);
                                ioUtils.setUser(userDetailsPojo);
                                IOUtils.toastMessage(context, response.getString("response"));
                                finish();


                                Intent intent2 = new Intent(SigninActivity.this, DashboardActivity.class);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent2);
                                finish();


                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
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


    public void relog_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivity.this);
        LayoutInflater inflater = LayoutInflater.from(SigninActivity.this);
        View content = inflater.inflate(R.layout.re_log_dailog, null);
        builder.setView(content);

        final LinearLayout linear_ok, linear_cancel;
        TextView text_cancel, text_ok, text_mark;


        linear_ok = (LinearLayout) content.findViewById(R.id.linear_ok);
        linear_cancel = (LinearLayout) content.findViewById(R.id.linear_cancel);

        text_cancel = (TextView) content.findViewById(R.id.text_cancel);
        text_ok = (TextView) content.findViewById(R.id.text_ok);
        text_mark = (TextView) content.findViewById(R.id.text_mark);


        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.card_view));


        linear_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                reloginJsonobjectForApiCall();

            }
        });

        linear_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });


    }


}
