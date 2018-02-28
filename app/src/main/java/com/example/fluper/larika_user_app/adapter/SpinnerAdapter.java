package com.example.fluper.larika_user_app.adapter;

/**
 * Created by rohit on 6/7/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.MeetingPointModel;

import java.util.List;


public class SpinnerAdapter extends BaseAdapter {

    private List<MeetingPointModel> alertList;
    private LayoutInflater mInflater;
    private Context context;
    private int resourceId;

    public SpinnerAdapter(Context context,int reourceId,List<MeetingPointModel> alertList) {
        this.resourceId=reourceId;
        this.alertList=alertList;
        this.context=context;
        mInflater = LayoutInflater.from(context);
        this.mInflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return alertList.size();
    }

    @Override
    public Object getItem(int position) {
        return alertList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getView(position, convertView, parent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_row, null);
            holder = new ViewHolder();
            holder.spinnerValue = (TextView) convertView.findViewById(R.id.spinner_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if(position==0)
//        {
//
//            holder.spinnerValue.setTextColor(context.getResources().getColor(R.color.black_grey_tint));
//            holder.spinnerValue.setText(alertList.get(position).getPlaceName());
//        }
//        else{
            holder.spinnerValue.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.spinnerValue.setText(alertList.get(position).getPlaceName());

//        }




        return convertView;
    }




    static class ViewHolder {
        TextView spinnerValue;
    }
}