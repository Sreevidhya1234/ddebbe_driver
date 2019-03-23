package com.DDebbie.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.DDebbie.fragment.ManualDestinationFragment;
import com.DDebbie.util.IOUtils;

import java.util.Timer;

/**
 * Created by appsplanet on 26/5/16.
 */
public class LocalService extends Service
{
    private static Timer timer = new Timer();
    private Context ctx;
    private boolean wcharge = true;

    public static String time = "";

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    Intent i;
    private void startService()
    {
      /*      startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        i = new Intent("Waitingtime");*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        i = new Intent("Waitingtime");
        return super.onStartCommand(intent, flags, startId);
    }

    /*    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }*/

    int mins;
    Runnable updateTimerThread = new Runnable() {

        public void run() {


            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            time = "" + mins + ":"
                    + String.format("%02d", secs) +" min." ;
            customHandler.postDelayed(this, 0);

            i.putExtra("some_msg", time);
            sendBroadcast(i);

            stopSelf();
        }

    };


    public void onDestroy()
    {
        super.onDestroy();
        //Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);

    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            //Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };
}
