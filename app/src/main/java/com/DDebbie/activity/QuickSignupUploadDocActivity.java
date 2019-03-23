package com.DDebbie.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.DDebbie.util.URIUtils;
import com.DDebbie.util.VolleyMultipartRequest;
import com.DDebbie.util.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.DDebbie.util.Constants.URL_DRIVER_PROFILE_UPDATE;

public class QuickSignupUploadDocActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTxtOwnership, mTxtInsurance, mTxtDriverLicence, mTxtVehicleSafety;
    final int FILE_SELECT_CODE = 101;
    private static final int SELECT_PICTURE = 1;
    private Context mContext;
    TextView mTxtSelectedTextView;
    private ProgressDialog mProgressDialog;
    private String TAG = QuickSignupUploadDocActivity.class.getSimpleName();
    private String attachmentFilePath;
    public String image_name="";
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_signup_upload_doc);

        mTxtOwnership = (TextView) findViewById(R.id.txtOwnership);
        mTxtInsurance = (TextView) findViewById(R.id.txtInsurancePolicy);
        mTxtDriverLicence = (TextView) findViewById(R.id.txtDriverLicence);
        mTxtVehicleSafety = (TextView) findViewById(R.id.txtVehicleSafety);

        mContext = this;
        session = new Session(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnOwnership) {
            mTxtSelectedTextView = mTxtOwnership;
            openFileChooser();
        } else if (view.getId() == R.id.btnInsurancePolicy) {
            mTxtSelectedTextView = mTxtInsurance;
            openFileChooser();
        } else if (view.getId() == R.id.btnDriverLicence) {
            mTxtSelectedTextView = mTxtDriverLicence;
            openFileChooser();
        } else if (view.getId() == R.id.btnVehicleSafety) {
            mTxtSelectedTextView = mTxtVehicleSafety;
            openFileChooser();
        } else if (view.getId() == R.id.btnSave) {
            if(!mTxtOwnership.getText().toString().equalsIgnoreCase("")&& !mTxtInsurance.getText().toString().equalsIgnoreCase("")&&
            !mTxtDriverLicence.getText().toString().equalsIgnoreCase("")&& !mTxtVehicleSafety.getText().toString().equalsIgnoreCase("")) {


                uploadFile();
            }else {
                Toast.makeText(getApplicationContext(),"Select all the documents!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFileChooser() {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, FILE_SELECT_CODE);
/*

            startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    FILE_SELECT_CODE);
*/

        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, FILE_SELECT_CODE);

            /*startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    FILE_SELECT_CODE);
*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();

                       attachmentFilePath = URIUtils.getPath(QuickSignupUploadDocActivity.this, uri);
                      //  mTxtSelectedTextView.setText(attachmentFilePath);
                       // attachmentFilePath = getPath(uri);
                        String filePath = attachmentFilePath;
                        //image_name_tv.setText(filePath);
                        File file1 = new File(filePath);
                        Log.d(TAG,"file==>"+file1.getAbsolutePath());
                        String[] split = filePath.split("\\/");
                        Log.e(TAG,"filesplit==>"+ Arrays.toString(split));
                        if(split.length>0) {
                            Log.e(TAG, "split_value==>" + split[split.length - 1]);
                            image_name = split[split.length - 1];
                        }

                        if(image_name!=null){
                            try {
                                //InputStream ims = new FileInputStream(file1.getAbsolutePath());
                                //ivPreview.setImageBitmap(BitmapFactory.decodeStream(ims));
                                Bitmap bitmap = StringToBitMap(filePath);;
                               String convert_image = BitMapToString(bitmap);
                                Log.e(TAG,"convert_image==>"+bitmap+"\n"+convert_image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if(image_name.contains(".png")){
                            mTxtSelectedTextView.setText(attachmentFilePath);
                        }else {
                            Toast.makeText(getApplicationContext(),"Upload only PNG files",Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        mTxtSelectedTextView.setText("");
                    }
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
    public String BitMapToString(Bitmap bitmap){
        String temp="";
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,80, baos);
        byte [] b=baos.toByteArray();
        try {
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }
        return temp;
    }
    public Bitmap StringToBitMap(String image){
        try{
           // byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
          //  Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
           // return bitmap;
            byte[] decodedByte = Base64.decode(image, 0);
            return BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }



    private void uploadFile() {
        mProgressDialog = new ProgressDialog(QuickSignupUploadDocActivity.this);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.show();
        String url = Constants.URL_UPLOAD_DRIVER_DOC;
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d(TAG, "image upload response" + resultResponse);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    //       {"result":true,"response":"Upload Result","ownership":false,"insurancePolicy":false,"driverLicence":false,"vehicleSafety":false}
                    if (jsonObject.getString("result").equalsIgnoreCase("true")) {
                        mProgressDialog.dismiss();
                        // submitCase(title, history, jsonObject.getJSONArray("photos"));
                        //addAssignment(jsonObject.getString("attachment"));

                        saveVehicleInfo(jsonObject.getString("ownership"), jsonObject.getString("insurancePolicy"), jsonObject.getString("driverLicence"), jsonObject.getString("vehicleSafety"));
                    } else {
                        mProgressDialog.dismiss();
                    }
                    IOUtils.toastMessage(QuickSignupUploadDocActivity.this, jsonObject.getString("response"));
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error");
                error.printStackTrace();
                mProgressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.API_KEY, Constants.API_KEY_VALUE);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String ownership = mTxtOwnership.getText().toString();
                String insurancePolicy = mTxtInsurance.getText().toString();
                String driverLicence = mTxtDriverLicence.getText().toString();
                String vehicleSafety = mTxtVehicleSafety.getText().toString();

                if (!TextUtils.isEmpty(ownership)) {
                    File file1 = new File(ownership);
                    params.put("ownership", new DataPart(file1.getName(), getFromFile(ownership),"image/png"));

                }else {
                    Toast.makeText(getApplicationContext(),"Upload only .png images",Toast.LENGTH_SHORT).show();
                }
                if (!TextUtils.isEmpty(insurancePolicy)) {
                    File file1 = new File(insurancePolicy);
                    params.put("insurancePolicy", new DataPart(file1.getName(), getFromFile(insurancePolicy),"image/png"));
                }else {
                Toast.makeText(getApplicationContext(),"Upload only .png images",Toast.LENGTH_SHORT).show();
                }
                if (!TextUtils.isEmpty(driverLicence)) {
                    File file1 = new File(driverLicence);
                    params.put("driverLicence", new DataPart(file1.getName(), getFromFile(driverLicence),"image/png"));
                }else {
                Toast.makeText(getApplicationContext(),"Upload only .png images",Toast.LENGTH_SHORT).show();
               }
                if (!TextUtils.isEmpty(vehicleSafety)) {
                    File file1 = new File(vehicleSafety);
                    params.put("vehicleSafety", new DataPart(file1.getName(), getFromFile(vehicleSafety),"image/png"));
                }else {
                Toast.makeText(getApplicationContext(),"Upload only .png images",Toast.LENGTH_SHORT).show();
            }

//                ownership
//                insurancePolicy
//                driverLicence
//                vehicleSafety


                return params;
            }
        };
        Log.e(TAG,"request==>"+multipartRequest);

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }


    /**
     * @param path
     * @return
     */
    private byte[] getFromFile(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            Log.d("test", "file array size" + bytes.length);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }


    /**
     * @param ownership
     * @param insurancePolicy
     * @param driverLicence
     * @param vehicleSafety
     */
    private void saveVehicleInfo(String ownership, String insurancePolicy, String driverLicence, String vehicleSafety) {
        final ProgressDialog dialog = ProgressDialog.show(mContext, null, "Loading...");
        JSONObject input = new JSONObject();
        try {
            IOUtils ioUtils = new IOUtils(mContext);
            input.put("id", ioUtils.getUser().getId());
            input.put("id", session.getUser_id());
            // input.put("email", ioUtils.getUser().getEmail());
            input.put("email", session.getD_email());

            if (!ownership.equalsIgnoreCase("false"))
                input.put("ownership", ownership);
            if (!insurancePolicy.equalsIgnoreCase("false"))
                input.put("insurancePolicy", insurancePolicy);
            if (!driverLicence.equalsIgnoreCase("false"))
                input.put("driverLicence", driverLicence);
            if (!vehicleSafety.equalsIgnoreCase("false"))
                input.put("vehicleSafety", vehicleSafety);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", "input:" + input.toString());
        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                URL_DRIVER_PROFILE_UPDATE, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            Log.d("test", "response:" + response.toString());


                            if (response.getString("result").equalsIgnoreCase("true")) {
                                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                            IOUtils ioUtils = new IOUtils(mContext);
                            ioUtils.setQuickSignupStep(Constants.QUICK_SIGNUP_STEP.DOCS);


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

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
