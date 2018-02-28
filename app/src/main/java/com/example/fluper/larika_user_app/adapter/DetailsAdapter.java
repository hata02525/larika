package com.example.fluper.larika_user_app.adapter;

/**
 * Created by fluper-pc on 3/8/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.OnTheGoResultModel;

import java.util.List;

/**
 * Created by rohit on 3/8/17.
 */

public class DetailsAdapter extends BaseAdapter {
    private List<OnTheGoResultModel> addressModelList;
    private Context activity;
    private int Myposition;

    public DetailsAdapter(Context addressBookActivity, List<OnTheGoResultModel> addressModelList, int position) {
        this.Myposition = position;
        this.addressModelList = addressModelList;
        this.activity = addressBookActivity;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return addressModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(activity).
                    inflate(R.layout.single_row_details, parent, false);
        }
        TextView tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
        TextView tv_rs = (TextView) view.findViewById(R.id.tv_rs);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_count);

            final OnTheGoResultModel myOrderModel = addressModelList.get(Myposition);
            tv_product_name.setText(myOrderModel.getDishName());
            tv_rs.setText("R$ " + myOrderModel.getPrice());
            tv_count.setText(myOrderModel.getQuantity() + "");




        return view;
    }
}