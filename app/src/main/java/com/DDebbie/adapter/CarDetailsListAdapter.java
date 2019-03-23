package com.DDebbie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.entity.CarDetails;

import java.util.ArrayList;

/**
 * Created by appsplanet on 21/1/16.
 */
public class CarDetailsListAdapter extends BaseAdapter {

    private ArrayList<CarDetails> arrayList;
    private Context context;

    public CarDetailsListAdapter(ArrayList<CarDetails> arrayList, Context context) {
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
        ViewHolder viewHolder = new ViewHolder();
        view= LayoutInflater.from(context).inflate(R.layout.car_list_item,null);
        viewHolder.vehicleNo =(TextView) view.findViewById(R.id.txtCarNo);
        viewHolder.txttype =(TextView) view.findViewById(R.id.txttype);

        final CarDetails navItem = arrayList.get(i);
        viewHolder.vehicleNo.setText(navItem.getVehicleNumber());
        viewHolder.txttype.setText(navItem.getVehicleType());

        return view;
    }

    public class ViewHolder{
        TextView vehicleNo,txttype;
    }
}
