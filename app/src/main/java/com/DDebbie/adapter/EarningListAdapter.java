package com.DDebbie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.entity.Earnings;

import java.util.ArrayList;

/**
 * Created by appsplanet on 21/1/16.
 */
public class EarningListAdapter extends BaseAdapter {

    private ArrayList<Earnings> arrayList;
    private Context context;

    public EarningListAdapter(ArrayList<Earnings> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.earning_list_item, null);
            viewHolder.date = (TextView) view.findViewById(R.id.txtDate);
            viewHolder.count = (TextView) view.findViewById(R.id.txtCount);
            viewHolder.total = (TextView) view.findViewById(R.id.txtTotal);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Earnings navItem = arrayList.get(i);
        viewHolder.date.setText(navItem.getEarningDate());
        String amt = "" + navItem.getTotalRides();
        viewHolder.count.setText(amt);
        viewHolder.total.setText(navItem.getCurrencySymbol() + navItem.getTotalEarns());

        return view;
    }

    public class ViewHolder {
        TextView count, total, date;
    }
}
