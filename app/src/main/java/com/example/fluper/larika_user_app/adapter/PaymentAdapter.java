package com.example.fluper.larika_user_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.AddPaymentCardModel;

import java.util.List;

/**
 * Created by rohit on 30/6/17.
 */

public class PaymentAdapter extends BaseAdapter {

    private List<AddPaymentCardModel> addCardModelList;
    private Context context;


    public PaymentAdapter(Context context,List<AddPaymentCardModel>addCardModelList) {
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
            convertView = mInflater.inflate(R.layout.single_row_cards, null);
            holder = new ViewHolder();
            holder.tv_card_detail= (TextView) convertView.findViewById(R.id.tv_card_detail);
            holder.iv_img= (ImageView) convertView.findViewById(R.id.iv_img);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        String last=addCardModelList.get(position).getCardNumber().substring(addCardModelList.get(position).getCardNumber().length()-4);
        holder.tv_card_detail.setText("Credit    ....   ....   ....    "+last);

        if(addCardModelList.get(position).isSelected())
        {
            holder.iv_img.setVisibility(View.VISIBLE);

        }else
        {
            holder.iv_img.setVisibility(View.GONE);

        }



        final ViewHolder finalHolder = holder;
        return convertView;

    }




    class ViewHolder {
        TextView tv_card_detail;
        ImageView iv_img;
       /// CheckBox iv_img;


    }

}