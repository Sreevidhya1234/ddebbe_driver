package com.DDebbie.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.DDebbie.R;
import com.DDebbie.fragment.ContactUsFragment;


public class ContactUsActivity extends AppCompatActivity implements ContactUsFragment.OnFragmentInteractionListener{
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
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

        ContactUsFragment contactUsFragment = new ContactUsFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, contactUsFragment, contactUsFragment.getClass().getName());
        fragmentTransaction.addToBackStack(contactUsFragment.getClass().getName());
        fragmentTransaction.commit();

    }
    @Override
    public void onBackPressed() {

        if(fragmentManager.getBackStackEntryCount() == 1)
        {
            this.finish();
            overridePendingTransition(0, 0);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
