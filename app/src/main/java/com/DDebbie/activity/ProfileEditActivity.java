package com.DDebbie.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.AppHelper;
import com.DDebbie.util.CircleTransform;
import com.DDebbie.util.Constants;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.JsonObjectRequestWithHeader;
import com.DDebbie.util.Util;
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
import com.google.gson.Gson;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.key;
import static com.DDebbie.R.id.edtConfirmPassword;
import static com.DDebbie.R.id.edtEmail;
import static com.DDebbie.R.id.edtPassword;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener, ImageChooserListener {

    private ProgressDialog dialog;
    Context context = this;
    private IOUtils ioUtils;
    private UserDetailsPojo userDetailsPojo;
    private ImageView imgDp;
    private EditText mEdtName, mEdtEmail, mEdtPassword, mEdtConfirmPassword;
    private Context mContext;
    private Button mBtnUpdate;
    public String selectedImagePath;
    private ImageChooserManager imageChooserManager, imageCapturerManager;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        mContext = this;

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


        imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE);
        imageChooserManager.setImageChooserListener(this);
        imageCapturerManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE);
        imageCapturerManager.setImageChooserListener(this);


        mBtnUpdate = (Button) findViewById(R.id.btnProfileUpdate);
        mBtnUpdate.setOnClickListener(this);
        mEdtName = (EditText) findViewById(R.id.edtName);
        mEdtEmail = (EditText) findViewById(edtEmail);
        mEdtPassword = (EditText) findViewById(edtPassword);
        mEdtConfirmPassword = (EditText) findViewById(edtConfirmPassword);
        imgDp = (ImageView) findViewById(R.id.imgUserProfile);


        if (userDetailsPojo != null) {
            mEdtName.setText(session.getD_name());
            mEdtEmail.setText(session.getD_email());
            Picasso.with(context).load(userDetailsPojo.getPhoto()).transform(new CircleTransform())
                    .placeholder(R.mipmap.user_default)
                    .error(R.mipmap.user_default)
                    .into(imgDp);
        }


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnProfileUpdate) {

            String name = mEdtName.getText().toString().trim();
            String email = mEdtEmail.getText().toString().trim();
            String password = mEdtPassword.getText().toString().trim();
            String confPassword = mEdtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                mEdtName.setError("Please enter name");
            } else if (TextUtils.isEmpty(email)) {
                mEdtEmail.setError("Please enter email");
            } else if (!Util.emailValidator(email)) {
                mEdtEmail.setError("Please enter valid email");
            } else if ((!TextUtils.isEmpty(password)) && !password.equalsIgnoreCase(confPassword)) {
                mEdtConfirmPassword.setError("Please password not match");
            } else {
                //SAVE DATA
                if (TextUtils.isEmpty(selectedImagePath))
                    saveVehicleInfo(name, email, password, null);
                else {
                    //UPLOAD IAMGE PATH
                    uploadPhotos(name, email, password, selectedImagePath);
                }


            }
        } else if (view.getId() == R.id.imgUserProfile) {
            final CharSequence[] items = {"Gallery", "Camera"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Select Image");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    try {
                        if (item == 0)
                            imageChooserManager.choose();
                        else
                            imageCapturerManager.choose();
                    } catch (ChooserException e) {
                        e.printStackTrace();
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }


    private void saveVehicleInfo(String name, String email, String password, String photo) {
        final ProgressDialog dialog = ProgressDialog.show(mContext, null, "Loading...");
        JSONObject input = new JSONObject();
        try {
            input.put("id",  session.getUser_id());
            input.put("email", email);
            input.put("driverName", name);

            if (!TextUtils.isEmpty(password))
                input.put("password", password);

            if (!TextUtils.isEmpty(photo))
                input.put("photo", photo);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", "input:" + input.toString());
        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_DRIVER_PROFILE_UPDATE, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            Log.d("test", "response:" + response.toString());

                            IOUtils ioUtils = new IOUtils(mContext);


                            IOUtils.toastMessage(mContext, response.getString("response"));
                            Gson gson = new Gson();
                            UserDetailsPojo userDetailsPojo = gson.fromJson(response.getJSONObject("driverData").toString(), UserDetailsPojo.class);
                            ioUtils.setUser(userDetailsPojo);
                            finish();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ChooserType.REQUEST_PICK_PICTURE)
                imageChooserManager.submit(requestCode, data);
            else if (requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)
                imageCapturerManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage chosenImage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (chosenImage != null) {
                    selectedImagePath = chosenImage.getFileThumbnail();
                    imgDp.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));

                    Log.d("test", "path" + selectedImagePath);
                }
            }
        });
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onImagesChosen(ChosenImages chosenImages) {

    }


    private void uploadPhotos(final String name, final String email, final String password, final String path) {
        final String TAG = "UPLOAD PHOTO";
        final ProgressDialog dialog = ProgressDialog.show(mContext, null, "Loading...");
        String url = Constants.URL_UPLOAD_DRIVER_PHOTO;
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                dialog.dismiss();
                String resultResponse = new String(response.data);
                Log.d(TAG, "image upload response" + resultResponse);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    if (jsonObject.getString("result").equalsIgnoreCase("true")) {
                        String photo = jsonObject.getString("photo");
                        saveVehicleInfo(name, email, password, photo);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error");
                dialog.dismiss();
                error.printStackTrace();
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
                params.put("profilePhoto", new DataPart("avatar+" + key + ".jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), path), "image/jpeg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, 0);

    }
}
