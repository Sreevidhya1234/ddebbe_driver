package com.DDebbie.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.DDebbie.R;
import com.DDebbie.entity.TripHistory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by appsplanet on 21/1/16.
 */
public class TripHistoryListAdapter extends BaseAdapter {

    public ArrayList<TripHistory> arrayList;
    private Context context;
    LayoutInflater lInflater;
    //Map
    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "", Des_address = "";
    Geocoder geocoder;
    private LatLng mCenterLatLong;

    public TripHistoryListAdapter(ArrayList<TripHistory> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (arrayList.size() != 0) {
            return arrayList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            //convertView = lInflater.inflate(R.layout.trip_list_item, viewGroup, false);

            LayoutInflater inflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.trip_list_item, null);
            viewHolder = new ViewHolder();


            viewHolder.date = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.pick = (TextView) convertView.findViewById(R.id.txtPick);
            viewHolder.dest = (TextView) convertView.findViewById(R.id.txtDest);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating);
            viewHolder.cbBuy = (CheckBox) convertView.findViewById(R.id.cbBox);
            viewHolder.cbBuy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    arrayList.get(i).setBox(b);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Check(arrayList.get(i).getAddress());
        Destination(arrayList.get(i).getvInfo());


        viewHolder.date.setText(Html.fromHtml("<b>Date and Time:</b>        " + arrayList.get(i).getDate()));

        // viewHolder.pick.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
        viewHolder.pick.setText(this.address);
        viewHolder.dest.setText(this.Des_address);

        //viewHolder.pick.setText(Html.fromHtml("<b>Pickup:</b>      " + arrayList.get(i).getAddress()));
      //  viewHolder.dest.setText(Html.fromHtml("<b>Destination:</b>   " + arrayList.get(i).getvInfo()));
        viewHolder.ratingBar.setRating(Float.parseFloat(arrayList.get(i).getCustomer_rating()));
        LayerDrawable stars = (LayerDrawable) viewHolder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        viewHolder.cbBuy.setTag(i); // This line is important.

        viewHolder.cbBuy.setChecked(arrayList.get(i).isBox());

        return convertView;
    }

    static class ViewHolder {
        protected TextView date, pick, dest;
        protected RatingBar ratingBar;
        protected CheckBox cbBuy;
    }


    TripHistory getProduct(int position) {
        return ((TripHistory) getItem(position));
    }

    public ArrayList<TripHistory> getBox() {
        /*ArrayList<TripHistory> box = new ArrayList<TripHistory>();
        for (TripHistory p : box) {
            if (p.box)
                box.add(p);
        }*/
        return arrayList;
    }

    /*OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            getProduct((Integer) buttonView.getTag()).box = isChecked;
        }
    };*/


    public void Check(String lat_address) {

        Log.e("lat_lng==>", "latlng==>" + lat_address);
        String lat_str = lat_address.substring(10, lat_address.length() - 1);
        Log.e("lat_address==>", "lat_address==>" + lat_str);
        String[] lat = lat_str.split(",");
        Log.e("SPLIT_lat==>", "lat==>" + lat[0] + "\n" + lat[1]);
        double Source_Lat = 0.0;
        double Source_Long = 0.0;

        try {


            Source_Lat = Double.parseDouble(lat[0]);
            Source_Long = Double.parseDouble(lat[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        geocoder = new Geocoder(context, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(Source_Lat, Source_Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            this.address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();

            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }


    public void Destination(String lat_address) {

        Log.e("lat_lng==>", "latlng==>" + lat_address);
        String lat_str = lat_address.substring(10, lat_address.length() - 1);
        Log.e("lat_address==>", "lat_address==>" + lat_str);
        String[] lat = lat_str.split(",");
        Log.e("SPLIT_lat==>", "lat==>" + lat[0] + "\n" + lat[1]);
        double Source_Lat = 0.0;
        double Source_Long = 0.0;

        try {


            Source_Lat = Double.parseDouble(lat[0]);
            Source_Long = Double.parseDouble(lat[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        geocoder = new Geocoder(context, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(Source_Lat, Source_Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            this.Des_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();

            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }


}
