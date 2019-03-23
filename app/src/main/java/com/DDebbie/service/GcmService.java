package com.DDebbie.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.DDebbie.R;
import com.DDebbie.Session.Session;
import com.DDebbie.activity.DashboardActivity;
import com.DDebbie.entity.RideAccept;
import com.DDebbie.fragment.JobPreviewFragment;
import com.DDebbie.fragment.ManualDestinationFragment;
import com.DDebbie.util.IOUtils;
import com.DDebbie.util.Util;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tushar Katakdound on 7/28/2015.
 */


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {

    Gson gson;
    public RideAccept rideAccept;
    private IOUtils ioUtils;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String paymentStatus = "0";
    Session session;
    long[] v = {500, 1000};

    Context context;
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;

    public GcmService() {

    }

    @Override
    public void onMessageReceived(String from, Bundle data) {

        ioUtils = new IOUtils(this);

        session = new Session(this);
        context = GcmService.this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        Log.v("Notification", from + " " + data.toString());
        String message = data.getString("message");

        try {

            JSONObject jsonObject = new JSONObject(message);
            gson = new Gson();
            rideAccept = gson.fromJson(jsonObject.toString(), RideAccept.class);
            Log.e("Json1", jsonObject.toString());
            if (jsonObject.getString("action").equals("RIDE_CANCELLED")) {
                if (DashboardActivity.isActive) {
                    session.setonoff("1");
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("RIDE_CANCELLED", true);
                    startActivity(intent);
                    showNotification();
                    ioUtils.setRideStatus(0);
                } else {
                    generateNotification(getBaseContext(), message);
                }
            } else if (jsonObject.getString("action").equals("RIDE_REQUEST")) {

                int status = ioUtils.getRideStatus();
                if (status == 0) {

                    String json = gson.toJson(rideAccept);
                    editor.putString("rideAccept", json);
                    editor.commit();
                    Log.e("responsen", json.toString());
                    ioUtils.setPName(rideAccept.getPassengerName());
                    IOUtils.pName = jsonObject.getString("passengerName");
                    paymentStatus = jsonObject.getString("paymentStatus");
                    ioUtils.setDestination_lat(rideAccept.getToLatitude());
                    ioUtils.setDestination_lang(rideAccept.getToLongitude());
                   // IOUtils.destination_lat = jsonObject.getString("toLatitude");
                   // IOUtils.destination_lang = jsonObject.getString("toLongitude");
                    if (paymentStatus.equals("1")) {
                        ioUtils.setPaymentStatus(true);
                    } else {
                        ioUtils.setPaymentStatus(false);
                    }

                    if (DashboardActivity.isActive) {
                        session.setonoff("0");
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("notify", true);
                        intent.putExtra("ride", rideAccept);
                        startActivity(intent);
                        showNotification();
                    } else {
                        generateNotification(getBaseContext(), message);
                    }
                }
            } else if (jsonObject.getString("action").equals("RIDE_PAYMENT_DONE")) {

                if (DashboardActivity.isActive) {

                    session.setonoff("4");
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.color.colorPrimary);
                    // Build notification
                    // Actions are just fake
                    Notification noti = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        noti = new Notification.Builder(this)
                                .setContentTitle(rideAccept.getTitle())
                                .setContentText(rideAccept.getDescription())
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setVibrate(v)
                                .setLights(0xff00ff00, 300, 100)
                                .setSmallIcon(R.mipmap.notification)
                                .setContentIntent(pIntent).build();
                    }
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    // Play default notification sound
                    noti.defaults |= Notification.DEFAULT_SOUND;
                    noti.defaults |= Notification.FLAG_SHOW_LIGHTS;

                    noti.defaults |= Notification.FLAG_NO_CLEAR;
                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, noti);
                } else {
                    Notification noti = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        noti = new Notification.Builder(this)
                                .setContentTitle("Ride Payment Done")
                                .setContentText("Ride Payment Done")
                                .setLights(0xff00ff00, 300, 100)
                                .setSmallIcon(R.mipmap.notify_icon)
                                .setAutoCancel(true)
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setVibrate(v)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification))
                                .build();
                    }

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    // Play default notification sound
                    noti.defaults |= Notification.DEFAULT_SOUND;
                    noti.defaults |= Notification.FLAG_SHOW_LIGHTS;
                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, noti);

                }
                ioUtils.setPaymentStatus(true);
            } else if (jsonObject.getString("action").equals("RIDE_WAITING_PAYMENT_DONE")) {

                Notification noti = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    noti = new Notification.Builder(this)
                            .setContentTitle("Waiting charge payment done")
                            .setContentText("Waiting charge payment done")
                            .setLights(0xff00ff00, 300, 100)
                            .setSmallIcon(R.mipmap.notification)
                            .setAutoCancel(true)
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setVibrate(v)
                            .build();
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;
                // Play default notification sound
                noti.defaults |= Notification.DEFAULT_SOUND;
                noti.defaults |= Notification.FLAG_SHOW_LIGHTS;
                // Vibrate if vibrate is enabled
                noti.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(0, noti);

                ioUtils.setPaymentStatus(true);
            }

        } catch (JSONException e) {


        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showNotification() {
        Notification noti = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = new Notification.Builder(this)
                    .setContentTitle(rideAccept.getTitle())
                    .setContentText(rideAccept.getDescription())
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(v)
                    .setLights(0xff00ff00, 300, 100)
                    .setSmallIcon(R.mipmap.notification)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        // Play default notification sound
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.FLAG_SHOW_LIGHTS;
        // Vibrate if vibrate is enabled
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, noti);
    }

    @Override
    public void onDeletedMessages() {
        generateNotification(getBaseContext(), "Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        generateNotification(getBaseContext(), "Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        generateNotification(getBaseContext(), "Upstream message send error. Id=" + msgId);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void generateNotification(Context context, String message) {

        IOUtils.DONT_SHOW = false;
        session.setonoff("0");
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("notify", true);
        intent.putExtra("ride", rideAccept);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.color.colorPrimary);
        // Build notification
        // Actions are just fake
        Notification noti = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = new Notification.Builder(context)
                    .setContentTitle(rideAccept.getTitle())
                    .setContentText(rideAccept.getDescription())
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(v)
                    .setLights(0xff00ff00, 300, 100)
                    .setSmallIcon(R.mipmap.notification)
                    .setContentIntent(pIntent).build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        // Play default notification sound
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.FLAG_SHOW_LIGHTS;

        noti.defaults |= Notification.FLAG_NO_CLEAR;
        // Vibrate if vibrate is enabled
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, noti);

    }

}