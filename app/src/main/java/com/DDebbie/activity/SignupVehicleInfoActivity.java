package com.DDebbie.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.Constants;
import com.DDebbie.util.Consts;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.DDebbie.util.Constants.URL_ADD_VEHICLE;
import static com.DDebbie.util.Constants.URL_DRIVER_UPDATE;


public class SignupVehicleInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Spinner mSpnVehicleType, mSpnLicenceType;
    private EditText mEdtRefNumber, mEdtYear, mEdtModel, mEdtMake, mEdtNoOfDoors, mEdtOwnership, mEdtPlateno, mEdtInsurance;
    private RadioGroup mRdgWheelChairAvailable, mRadGrpRef;
    private Button mBtnNext, mBtnLater;
    private CheckBox mChkIAgree;
    private boolean isQuickSignup;
    private TextView mTxtTerm;

    private IOUtils ioUtils;
    private UserDetailsPojo userDetailsPojo;

    public String Dri_id = "";
    public String Vechical_Id = "";

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_vehicle_info);
        mContext = this;
        session = new Session(this);

        if (Consts.Check.equalsIgnoreCase("true")) {

            Dri_id = String.valueOf(session.getUser_id());

            Log.e("dri==>", "dri==>" + Dri_id);
            Drive_view(Dri_id);

            init();

            mBtnLater.setVisibility(View.GONE);

        } else {
            init();
        }

    }

    private void init() {
        mSpnVehicleType = (Spinner) findViewById(R.id.spnVehicleType);
        mSpnLicenceType = (Spinner) findViewById(R.id.spnLicenceType);
        mEdtRefNumber = (EditText) findViewById(R.id.edtRefNumber);
        mEdtYear = (EditText) findViewById(R.id.edtYear);
        mEdtModel = (EditText) findViewById(R.id.edtModel);
        mEdtMake = (EditText) findViewById(R.id.edtMake);
        mEdtNoOfDoors = (EditText) findViewById(R.id.edtNoOfDoors);
        mEdtOwnership = (EditText) findViewById(R.id.edtOwnership);
        mEdtPlateno = (EditText) findViewById(R.id.edtPlateno);
        mEdtInsurance = (EditText) findViewById(R.id.edtInsuranceCmpName);
        mRdgWheelChairAvailable = (RadioGroup) findViewById(R.id.rdgWheelChair);
        mBtnNext = (Button) findViewById(R.id.btnNext);
        mBtnLater = (Button) findViewById(R.id.btnLater);
        mChkIAgree = (CheckBox) findViewById(R.id.chkTermsConditions);
        mRadGrpRef = (RadioGroup) findViewById(R.id.radioGpRef);
        mTxtTerm = (TextView) findViewById(R.id.txtTerm);
        mTxtTerm.setText(Html.fromHtml("<u>I agree to the terms and conditions</u>"));
        mTxtTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ddebbie.com")));
            }
        });

//        mSpnLicenceType.getBackground().setColorFilter(getResources().getColor(R.color.yellow_lite), PorterDuff.Mode.SRC_ATOP);
//        mSpnVehicleType.getBackground().setColorFilter(getResources().getColor(R.color.yellow_lite), PorterDuff.Mode.SRC_ATOP);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.view_spinner_item,
                getResources().getStringArray(R.array.licence_type)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnLicenceType.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this,
                R.layout.view_spinner_item,
                getResources().getStringArray(R.array.array_vehicle_type)
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnVehicleType.setAdapter(adapter1);


        isQuickSignup = getIntent().getBooleanExtra(Constants.INTENT_QUICK_SIGNUP, false);

        if (isQuickSignup) {
            mSpnLicenceType.setVisibility(View.VISIBLE);
            mChkIAgree.setVisibility(View.VISIBLE);
            mBtnNext.setText("Now");
        } else {

            if (Consts.Check.equalsIgnoreCase("true")) {

                isQuickSignup = true;

                mSpnLicenceType.setVisibility(View.VISIBLE);
                mChkIAgree.setVisibility(View.VISIBLE);
                mBtnNext.setText("Now");
            } else {
                findViewById(R.id.txtLicenceType).setVisibility(View.GONE);
                mSpnLicenceType.setVisibility(View.GONE);
                mChkIAgree.setVisibility(View.GONE);

            }
        }

        mRadGrpRef.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (mRadGrpRef.getCheckedRadioButtonId() == R.id.rdbRefYes) {
                    mEdtRefNumber.setEnabled(true);

                } else {
                    mEdtRefNumber.setText("");
                    mEdtRefNumber.setEnabled(false);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnNext) {//NEXT
            validation();

        }

        if (view.getId() == R.id.btnLater) {//Later

            IOUtils ioUtils = new IOUtils(mContext);
            ioUtils.setDocLater(true);

            validation();

        }
    }

    /**
     * driverId*
     * vehicleType*
     * vehicleNumber*
     * vehiclesModel*
     * year*
     * numOfDoors
     * ownership
     * licensing
     * insuranceCompName
     * wheelChairFacility
     */
    private void validation() {
        String vehicleType = (mSpnVehicleType.getSelectedItemPosition() + 1) + "";
        //  Toast.makeText(this,"vehicle id"+vehicleType,Toast.LENGTH_SHORT).show();
        String year = mEdtYear.getText().toString().trim();
        String vehiclesModel = mEdtModel.getText().toString().trim();
        String make = mEdtMake.getText().toString().trim();
        String numOfDoors = mEdtNoOfDoors.getText().toString().trim();
//        String ownership = mEdtOwnership.getText().toString().trim();
        String ownership = "";
        String plateNo = mEdtPlateno.getText().toString().trim();
        String insuranceCompName = mEdtInsurance.getText().toString().trim();

        String wheelChairAvailable = mRdgWheelChairAvailable.getCheckedRadioButtonId() == R.id.rdbWheelChairYes ? "Yes" : "No";

        String refNumber = mEdtRefNumber.getText().toString().trim();
        if (TextUtils.isEmpty(plateNo)) {
            mEdtPlateno.setError("Please enter plate number.");

//            IOUtils.toastMessage(mContext, "Please licensing");
        }
        //  IOUtils.toastMessage(mContext, "Please vehicle number");
        else if (TextUtils.isEmpty(year)) {
            mEdtYear.setError("Please enter year.");

            //IOUtils.toastMessage(mContext, "Please year");
        } else if (year.length() < 4) {
            mEdtYear.setError("Please enter valid year.");

            //IOUtils.toastMessage(mContext, "Please year");
        } else if (TextUtils.isEmpty(vehiclesModel)) {
            mEdtModel.setError("Please enter vehicles model.");

            //IOUtils.toastMessage(mContext, "Please vehicles model");
        } else if (TextUtils.isEmpty(make)) {
            mEdtMake.setError("Please enter make.");
            //IOUtils.toastMessage(mContext, "Please vehicles model");
        } else if (TextUtils.isEmpty(numOfDoors)) {
            mEdtNoOfDoors.setError("Please enter no. of doors.");

//            IOUtils.toastMessage(mContext, "Please no. of doors");
        } else if (mRadGrpRef.getCheckedRadioButtonId() == R.id.rdbRefYes && TextUtils.isEmpty(refNumber)) {
            mEdtRefNumber.setError("Please enter referral number.");

        }
//        else if (TextUtils.isEmpty(ownership)) {
//            mEdtOwnership.setError("Please enter ownership.");
//
////            IOUtils.toastMessage(mContext, "Please ownership");
//        }
        else if (TextUtils.isEmpty(insuranceCompName)) {
            mEdtInsurance.setError("Please enter insurance company name.");

//            IOUtils.toastMessage(mContext, "Please insurance company name");
        } else if (isQuickSignup && !mChkIAgree.isChecked()) {
            IOUtils.toastMessage(mContext, "Please accept terms and conditions.");
        } else {

            String licenceType = "";
            if (isQuickSignup) {
                licenceType = mSpnLicenceType.getSelectedItem().toString();
            }


            if (Consts.Check.equalsIgnoreCase("true")) {

                Vechical_Id = Consts.Vehical_Id;

                Update_VehicleInfo(vehicleType, refNumber, vehiclesModel + " " + make, year, numOfDoors, ownership, Vechical_Id, plateNo, insuranceCompName, wheelChairAvailable, licenceType);

            } else {
                saveVehicleInfo(vehicleType, refNumber, vehiclesModel + " " + make, year, numOfDoors, ownership, plateNo, insuranceCompName, wheelChairAvailable, licenceType);

            }

        }
    }


    /**
     * @param Dri_id
     */


    private void Drive_view(final String Dri_id) {

        final ProgressDialog dialog = ProgressDialog.show(mContext, null, "Loading...");
        JSONObject input = new JSONObject();
        try {
            input.put("driver_id", Dri_id + " " + Dri_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", "input:" + input.toString());

        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.GET,
                Constants.URL_DRIVER_DOC, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("test", "response:" + response.toString());
                            dialog.dismiss();

                            JSONArray Driver_Info;
                            if (response.getString("result").equalsIgnoreCase("true")) {


                                JSONArray jsonArray = response.getJSONArray("driverdetails");


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo = jsonArray.getJSONObject(0);

                                    /*Student s=new Student();
                                    s.setId(jo.getString(STUDENT_ID));
                                    s.setName(jo.getString(STUDENT_NAME));
                                    s.setClassNo(jo.getString(STUDENT_CLASS));
                                    s.setBusno(jo.getString(STUDENT_BUS));
                                    s.setStatus(Integer.parseInt(jo.getString(STUDENT_STATUS)));
                                    s.setStopid(jo.getString(STUDENT_STOP));
                                    students.add(s);
*/


                                    // UserDetailsPojo = jsonObject.getJSONArray("driverdetails");

                                    // UserDetailsPojo.getJSONObject()
                                    UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                                    String driverId = jo.getString("driverId");
                                    userDetailsPojo.setId(driverId);

                                    userDetailsPojo.setDriverName(jo.getString("driverName"));
                                    userDetailsPojo.setVehicleTypeId(jo.getString("vehicleTypeId"));
                                    userDetailsPojo.setVehicleId(jo.getString("vehicleId"));
                                    Consts.Vehical_Id = jo.getString("vehicleId");
                                    userDetailsPojo.setVehicleModel(jo.getString("vehicleModel"));
                                    userDetailsPojo.setVehicleNumber(jo.getString("vehicleNumber"));
                                    userDetailsPojo.setYear(jo.getString("year"));
                                    userDetailsPojo.setNumOfDoors(jo.getString("numOfDoors"));
                                    userDetailsPojo.setLicenceType(jo.getString("licenceType"));
                                    userDetailsPojo.setOwnership(jo.getString("ownership"));
                                    userDetailsPojo.setReferralNo(jo.getString("referralNo"));
                                    userDetailsPojo.setInsuranceCompName(jo.getString("insuranceCompName"));
                                    userDetailsPojo.setWheelChairFacility(jo.getString("wheelChairFacility"));


                                    mEdtPlateno.setText(userDetailsPojo.getVehicleTypeId());
                                    mEdtYear.setText(userDetailsPojo.getYear());
                                    mEdtModel.setText(userDetailsPojo.getVehicleModel());
                                    mEdtNoOfDoors.setText(userDetailsPojo.getNumOfDoors());
                                    mEdtRefNumber.setText(userDetailsPojo.getReferralNo());
                                    mEdtInsurance.setText(userDetailsPojo.getInsuranceCompName());
                                    mEdtMake.setText(userDetailsPojo.getVehicleTypeId());

                                    IOUtils ioUtils = new IOUtils(mContext);
                                    ioUtils.setUser(userDetailsPojo);
                                }

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


    /**
     * @param vehicleType
     * @param refNumber
     * @param vehiclesModel
     * @param year
     * @param numOfDoors
     * @param ownership
     * @param plateNo
     * @param insuranceCompName
     * @param wheelChairAvailable
     */
    private void saveVehicleInfo(String vehicleType, String refNumber, String vehiclesModel, String year, String numOfDoors, String ownership, String plateNo, String insuranceCompName, String wheelChairAvailable, String licenceType) {
        Log.d("state", "in saveVehicleInfo");
        final ProgressDialog dialog = ProgressDialog.show(mContext, null, "Loading...");
        final IOUtils ioUtils = new IOUtils(mContext);
        JSONObject input = new JSONObject();
        try {

            input.put("driverId", ioUtils.getUser().getId());
            input.put("vehicleType", vehicleType);
            input.put("vehicleNumber", plateNo);
            input.put("vehiclesModel", vehiclesModel);
            input.put("year", year);
            input.put("numOfDoors", numOfDoors);
            input.put("licenceType", licenceType);
            input.put("ownership", ownership);
            input.put("referralNo", refNumber);
            input.put("insuranceCompName", insuranceCompName);
            input.put("wheelChairFacility", wheelChairAvailable);


            Log.d("req", input.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", "input:" + input.toString());
        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                URL_ADD_VEHICLE, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            Log.d("test", "response:" + response.toString());

                            if (response.getString("result").equalsIgnoreCase("true")) {
                                if (isQuickSignup) {
                                    ioUtils.setQuickSignupStep(Constants.QUICK_SIGNUP_STEP.VEHICLE);
                                    if (ioUtils.getDocLater())
                                        startActivity(new Intent(mContext, SigninActivity.class));
                                    else
                                        startActivity(new Intent(mContext, QuickSignupUploadDocActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(mContext, SignupDownloadActivity.class));
                                }
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


    private void Update_VehicleInfo(String vehicleType, String refNumber, String vehiclesModel, String year, String numOfDoors, String ownership, String Vechical_Id, String plateNo, String insuranceCompName, String wheelChairAvailable, String licenceType) {
        Log.d("state", "in saveVehicleInfo");
        final ProgressDialog dialog = ProgressDialog.show(mContext, null, "Loading...");
        final IOUtils ioUtils = new IOUtils(mContext);
        JSONObject input = new JSONObject();
        try {

            input.put("driverId", ioUtils.getUser().getId());
            input.put("vehicleType", vehicleType);
            input.put("vehicleNumber", plateNo);
            input.put("vehiclesModel", vehiclesModel);
            input.put("year", year);
            input.put("numOfDoors", numOfDoors);
            input.put("licenceType", licenceType);
            input.put("ownership", ownership);
            input.put("vehicleId", Vechical_Id);
            input.put("referralNo", refNumber);
            input.put("insuranceCompName", insuranceCompName);
            input.put("wheelChairFacility", wheelChairAvailable);


            Log.d("req", input.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", "input:" + input.toString());
        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                URL_DRIVER_UPDATE, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            Log.d("test", "response:" + response.toString());

                            if (response.getString("result").equalsIgnoreCase("true")) {
                                if (isQuickSignup) {
                                    ioUtils.setQuickSignupStep(Constants.QUICK_SIGNUP_STEP.VEHICLE);
                                    if (ioUtils.getDocLater())
                                        startActivity(new Intent(mContext, SigninActivity.class));
                                    else
                                        startActivity(new Intent(mContext, QuickSignupUploadDocActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(mContext, SignupDownloadActivity.class));
                                }
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
}
