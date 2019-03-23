package com.DDebbie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.DDebbie.R;
import com.DDebbie.entity.NavItem;

import java.util.ArrayList;

/**
 * Created by appsplanet on 21/1/16.
 */
public class NavListAdapter extends BaseAdapter {

    private ArrayList<NavItem> arrayList;
    private Context context;

    public NavListAdapter(ArrayList<NavItem> arrayList, Context context) {
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
        view= LayoutInflater.from(context).inflate(R.layout.nav_list_item,null);
        viewHolder.title =(TextView) view.findViewById(R.id.txtTitle);
        viewHolder.icon =(ImageView) view.findViewById(R.id.imgIcon);

        final NavItem navItem = arrayList.get(i);
        viewHolder.title.setText(navItem.getTitle());
        viewHolder.icon.setImageResource(navItem.getId());
        return view;
    }

    public class ViewHolder{
        TextView title;
        ImageView icon;
    }
}
