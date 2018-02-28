package com.example.fluper.larika_user_app.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.fluper.larika_user_app.R;

/**
 * Created by Daniel on 01-08-2017.
 */

public class HelpListAdapter extends BaseAdapter {

    private Context activity;


    public HelpListAdapter(Context addressBookActivity)
    {


        this.activity=addressBookActivity;
    }
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).
                    inflate(R.layout.help_baseadapter, parent, false);
        }





        return convertView;
    }
}


