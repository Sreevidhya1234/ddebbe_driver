package com.DDebbie;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GpsActivity extends AppCompatActivity {

    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "", Area = "", Connec = "";
    Geocoder geocoder;

    double Source_Lat = 13.037377;
    double Source_Long = 80.212282;
  /*  String Check_lat = "13.037377";
    String Check_Long = "80.212282";
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);


        Check();
    }

    public void Check() {


        geocoder = new Geocoder(GpsActivity.this, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(Source_Lat, Source_Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            this.address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            Area = addresses.get(0).getSubLocality();
            Connec = addresses.get(0).getAddressLine(0);


            // txtInfo.setText("Pickup location :\n" + knownName + "," + Area + "," + city + "," + state + "," + country + "," + postalCode);
            Log.e("NAME==>", "Name==>" + address);
            Log.e("knownName==>", "knownName==>" + knownName);
            Log.e("Connec==>", "Connec==>" + Connec);
            Log.e("Area==>", "Area==>" + Area);
            Log.e("city==>", "city==>" + city);
            Log.e("state==>", "state==>" + state);
            Log.e("country==>", "country==>" + country);
            Log.e("postalCode==>", "postalCode==>" + postalCode);
            Log.e("NAME==>", "Name==>" + address);

            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }


}
