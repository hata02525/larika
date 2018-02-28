package com.example.fluper.larika_user_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.MeetingTimeModel;

import java.util.List;

/**
 * Created by rohit on 7/7/17.
 */

public class MeetingPointTimeAdapter extends BaseAdapter {

    private List<MeetingTimeModel> addCardModelList;
    private Context context;


    public MeetingPointTimeAdapter(Context context, List<MeetingTimeModel> addCardModelList) {
        this.addCardModelList=addCardModelList;
        this.context=context;

    }
    @Override
    public int getCount() {
        return addCardModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return addCardModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_row_meeting_time, null);
            holder = new ViewHolder();
            holder.tv_meet_time= (TextView) convertView.findViewById(R.id.tv_meet_time);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String time=addCardModelList.get(position).getTime();
        if(time.contains(":")){
            String[] timearray=time.split(":");
            time=timearray[0]+":"+timearray[1];
            holder.tv_meet_time.setText(time+"");
        }



        final ViewHolder finalHolder = holder;
        return convertView;

    }




    class ViewHolder {
        TextView tv_meet_time;



    }

}