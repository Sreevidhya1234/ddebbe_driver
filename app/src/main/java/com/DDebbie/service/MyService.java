package com.DDebbie.service;

/**
 * Created by appsplanet on 26/5/16.
 */
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;

public class MyService extends Service{

    Timer timer = new Timer();
    MyTimerTask timerTask;
    ResultReceiver resultReceiver;
    Context context = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultReceiver = intent.getParcelableExtra("receiver");

        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);


        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Bundle bundle = new Bundle();
        bundle.putString("end", "Timer Stopped....");
        resultReceiver.send(200, bundle);
    }

    class MyTimerTask extends TimerTask
    {
        public MyTimerTask() {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            Bundle bundle = new Bundle();
            bundle.putString("start", "Timer Started....");
            resultReceiver.send(100, bundle);
        }
        @Override
        public void run() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("s");
            Bundle bundle = new Bundle();
            bundle.putString("time", time);
            resultReceiver.send(Integer.parseInt(dateFormat.format(System.currentTimeMillis())), bundle);

        }
    }




    String time = "";

    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            time = "" + mins + ":"
                    + String.format("%02d", secs);
            customHandler.postDelayed(this, 0);
         /*   SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if(sharedPreferences.getBoolean("pause_service",false)){
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
            }


            if(sharedPreferences.getBoolean("resume_service",false)){
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }*/
        }

    };



}