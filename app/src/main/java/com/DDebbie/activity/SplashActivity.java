package com.DDebbie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.DDebbie.R;
import com.DDebbie.entity.UserDetailsPojo;
import com.DDebbie.util.Constants;
import com.DDebbie.util.Consts;
import com.DDebbie.util.IOUtils;


public class SplashActivity extends AppCompatActivity {

    UserDetailsPojo userDetailsPojo;
    IOUtils ioUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ioUtils = new IOUtils(SplashActivity.this);
        userDetailsPojo = ioUtils.getUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                try {
                    String name = userDetailsPojo.getDriverName();

                    Log.v("Name", userDetailsPojo.getDriverName());

                    if (!name.equals("")) {
                        IOUtils utils = new IOUtils(SplashActivity.this);

                        //QUICK SIGNUP
                        if (ioUtils.getIsQuickSignUp()) {
                            Constants.QUICK_SIGNUP_STEP quick_signup_step = ioUtils.getQuickSignupStep();
                            if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.NONE) {
                                startActivity(new Intent(SplashActivity.this, SignupPersonalInfoActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, true));
                                finish();
                            } else if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.PERSONAL) {
                                startActivity(new Intent(SplashActivity.this, SignupVehicleInfoActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, true));
                                finish();
                            } else if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.VEHICLE) {
                                if (ioUtils.getDocLater())
                                    startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                                else
                                    startActivity(new Intent(SplashActivity.this, QuickSignupUploadDocActivity.class).putExtra(Constants.INTENT_QUICK_SIGNUP, true));
                                finish();
                            } else if (quick_signup_step == Constants.QUICK_SIGNUP_STEP.DOCS) {
                                startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                                finish();

                            }


                        } else {//NORMAL

                        //    Consts.Splash = "true";


                            Consts.Status = "";
                            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                       /*     if (utils.getVehicle()) {
                                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }*/
                        }
                    }

                } catch (NullPointerException e) {
                    Intent intent = new Intent(SplashActivity.this, SigninActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        }, 3000);
    }


}
