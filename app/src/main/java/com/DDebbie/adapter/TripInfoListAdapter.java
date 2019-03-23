package com.DDebbie.adapter;

import android.content.Context;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.entity.RideDataPojo;
import com.DDebbie.entity.TripInfo;

import java.util.ArrayList;

/**
 * Created by appsplanet on 21/1/16.
 */
public class TripInfoListAdapter extends BaseAdapter {

    private ArrayList<RideDataPojo> arrayList;
    private Context context;

    public TripInfoListAdapter(ArrayList<RideDataPojo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(arrayList.size()!=0){
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.trip_info_list_item, null);
            viewHolder.tripNo = (TextView) view.findViewById(R.id.txtTripNo);
            viewHolder.tripBill = (TextView) view.findViewById(R.id.txtTripBill);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();

        }


        final RideDataPojo navItem = arrayList.get(i);

        viewHolder.tripNo.setText("Trip ID: "+ navItem.getRideId());
        viewHolder.tripBill.setText("$ "+navItem.getAmount());

        return view;
    }

    public class ViewHolder{
        TextView tripNo, tripBill;
    }
}
