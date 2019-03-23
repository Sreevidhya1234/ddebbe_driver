package com.DDebbie.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.DDebbie.R;
import com.DDebbie.util.Constants;

public class SignupDownloadActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_download);

        mContext = this;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnDownloadForm) {//DOWNLOAD FORM

            final CharSequence[] items = {"Safety Inspection form", "Towing Truck Docs", "Taxi Car SUV Limo Drivers Docs"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Download Form:");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        openLink(Constants.PDF_SAFETY_INSPECTION_FROM);
                    } else if (item == 1) {
                        openLink(Constants.PDF_TOWING_TRUCK_DOCS);
                    } else if (item == 2) {
                        openLink(Constants.PDF_TAXI_CAR_SUV_LIMO_DRIVERS_DOCS);
                    }
                    // Do something with the selection
                    // mDoneButton.setText(items[item]);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } else if (view.getId() == R.id.btnSubmit) {//SUBMIT
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    /**
     * @param path
     */
    private void openLink(String path) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                path)));
    }
}
