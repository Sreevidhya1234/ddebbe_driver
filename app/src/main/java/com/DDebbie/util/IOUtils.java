package com.DDebbie.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.DDebbie.entity.UserDetailsPojo;
import com.google.gson.Gson;

/**
 * Created by savera on 10/2/16.
 */
public class IOUtils {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static String mSharedPrefName = "user_deatils";
    private String mCurrentUser = "CurrentUser";
    public static Location CURRENT_LOC;
    public static String RideStatus = "not_active";
    public static String title, msg, rideId, pEst, pId, pName, pLoc, pPhone, pEmail, pTime, pPick, pDest, fromLang, fromLat, toLang, toLat, totalKm;
    public static boolean DONT_SHOW = false;
    public static String destination_lat="lat";
    public static String destination_lang="lang";


    public IOUtils(Context context) {
        preferences = context.getSharedPreferences(mSharedPrefName,
                Context.MODE_PRIVATE);
        editor = preferences.edit();



    }

    public void setDestination_lang(String longitude) {
        editor.putString(destination_lang, longitude);
        editor.commit();
    }


    public String getDestination_lang() {
        return preferences.getString(destination_lang, "");
    }

    public void setDestination_lat(String latitude) {
        editor.putString(destination_lat, latitude);
        editor.commit();
    }


    public String getDestination_lat() {
        return preferences.getString(destination_lat, "");
    }


    public void setRideStatus(int status) {
        editor.putInt("ride_status", status);
        editor.commit();
    }


    public int getRideStatus() {
        return preferences.getInt("ride_status", 0);
    }


    public void setQuickSignupStep(Constants.QUICK_SIGNUP_STEP step) {
        editor.putInt("quicksignup", step.ordinal());
        editor.commit();
    }


    public Constants.QUICK_SIGNUP_STEP getQuickSignupStep() {
        int pos = preferences.getInt("quicksignup", 0);
        return Constants.QUICK_SIGNUP_STEP.values()[pos];

    }

    public void setOnline(boolean b) {
        editor.putBoolean("online_driver", b);
        editor.commit();
    }


    public boolean getOnline() {
        return preferences.getBoolean("online_driver", true);
    }

    public void setVehicle(boolean s, String type) {
        editor.putBoolean("vehicleStatus", s);
        editor.putString("vehicleType", type);
        Log.d("test","car details:"+type);
        editor.commit();
    }

    public boolean getVehicle() {
        return preferences.getBoolean("vehicleStatus", false);

    }

    public String getVehicleType() {
        return preferences.getString("vehicleType", null);

    }

    public void setPName(String n) {
        editor.putString("p_name", n);
        editor.commit();
    }

    public String getPName() {
        return preferences.getString("p_name", "");
    }

    public void setStatus(Boolean s) {
        editor.putBoolean("status", s);
        editor.commit();
    }

    public boolean getStatus() {
        return preferences.getBoolean("status", true);
    }

    public void setUser(UserDetailsPojo user) {
        Log.e("", "set user value" + user);

        Gson gson = new Gson();
        if (user != null) {


            editor.putString(mCurrentUser, gson.toJson(user)).commit();
        } else {
            editor.putString(mCurrentUser, null).commit();
        }

    }

    public void setPaymentStatus(boolean s) {
        editor.putBoolean("payment_status", s);
        editor.commit();
    }

    public boolean getPaymentStatus() {
        return preferences.getBoolean("payment_status", false);
    }


    public void setIsQuickSignUp(boolean s) {
        editor.putBoolean("quick_signup", s);
        editor.commit();
    }

    public boolean getIsQuickSignUp() {
        return preferences.getBoolean("quick_signup", false);
    }


    public UserDetailsPojo getUser() {
        Log.e("", "**getUser**");

        Gson gson = new Gson();
        if (preferences.getString(mCurrentUser, null) != null) {
//			return null;
//		} else {
            return gson.fromJson(preferences.getString(mCurrentUser, null), UserDetailsPojo.class);
        } else {
            return null;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {

        }
    }

    public static void alertMessegeDialog(Context context, String message, String positiveButtonName) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                paramDialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog getProgessDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(null);
        dialog.setMessage("Loading...");
        return dialog;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void setAlertForActivity(final Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // Add the buttons
        builder.setMessage("Please check network connection");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "Network connection not available",
                        Toast.LENGTH_SHORT).show();

                Intent dialogIntent = new Intent(
                        android.provider.Settings.ACTION_SETTINGS);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialogIntent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clearData() {
        editor.clear();
        editor.commit();

    }




    public void setDocLater(boolean s) {
        editor.putBoolean("later", s);
        editor.commit();
    }



    public boolean getDocLater() {
        return preferences.getBoolean("later", false);
    }

    public void setDriverStatus(boolean s){
        editor.putBoolean("driver_status", s);
        editor.commit();
    }

    public boolean getDriverStatus(){
        return preferences.getBoolean("driver_status", false);
    }




}
