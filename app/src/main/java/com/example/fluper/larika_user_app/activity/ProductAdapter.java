package com.example.fluper.larika_user_app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;

import java.util.List;


class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<String> productList;


    public ProductAdapter(Context context, List<String> productList) {

        this.productList = productList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.single_row_product_list, parent, false);
        }


        TextView tv_product = (TextView) convertView.findViewById(R.id.tv_product);
        tv_product.setText(productList.get(position));


        return convertView;
    }
}