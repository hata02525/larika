package com.example.fluper.larika_user_app.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.AllPromoCodeResultModel;

import java.util.List;

/**
 * Created by fluper-pc on 14/8/17.
 */

class AllPromocodeAdapter  extends BaseAdapter {

    Activity activity;
    Context context;
    List<AllPromoCodeResultModel> allpromocode;
    private LayoutInflater layoutinflater;


    public AllPromocodeAdapter(Activity activity, List<AllPromoCodeResultModel> allpromocode) {

    this.activity=activity;
        this.allpromocode=allpromocode;
        layoutinflater=activity.getLayoutInflater();


    }

    @Override
    public int getCount() {
        return allpromocode.size();
    }

    @Override
    public Object getItem(int position) {
        return allpromocode.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        convertView=layoutinflater.inflate(R.layout.allpromocode,null,false);

        TextView tv_discount=(TextView)convertView.findViewById(R.id.tv_discount);
        TextView tv_remove=(TextView)convertView.findViewById(R.id.tv_remove);
        TextView tv_text=(TextView)convertView.findViewById(R.id.tv_text);
        TextView maxdiscount=(TextView)convertView.findViewById(R.id.maxdiscount);


        tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShowPromoCodeActivity)activity).removePromoCode(allpromocode.get(position).getId(),position);
            }
        });


         tv_discount.setText(allpromocode.get(position).getPercent());
        tv_text.setText(allpromocode.get(position).getCode());
        maxdiscount.setText(allpromocode.get(position).getMaxDiscount() + "%");


        return convertView;
    }
}
