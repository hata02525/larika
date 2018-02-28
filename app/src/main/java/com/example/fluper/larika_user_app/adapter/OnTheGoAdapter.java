package com.example.fluper.larika_user_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.activity.OrdersActivity;
import com.example.fluper.larika_user_app.bean.OnTheGoModel;
import com.example.fluper.larika_user_app.bean.OnTheGoResultModel;

import java.text.DecimalFormat;
import java.util.List;

import static com.example.fluper.larika_user_app.R.id.tv_status;

/**
 * Created by rohit
 */

public class OnTheGoAdapter extends BaseAdapter {

    private List<OnTheGoResultModel> orderResultsList;
    private OnTheGoModel onTheGoModel;
    private Context context;
    String value;


    public OnTheGoAdapter(String value, Context context, List<OnTheGoResultModel> orderResultsList, OnTheGoModel onTheGoModel) {
        this.orderResultsList = orderResultsList;
        this.context = context;
        this.onTheGoModel = onTheGoModel;
        this.value = value;

    }

    @Override
    public int getCount() {
        return orderResultsList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderResultsList.get(position);
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
            convertView = mInflater.inflate(R.layout.single_row_onthego, null);
            holder = new ViewHolder();
            holder.tv_header = (TextView) convertView.findViewById(R.id.tv_header);
            holder.tv_header2 = (TextView) convertView.findViewById(R.id.tv_header2);
            holder.tv_status = (TextView) convertView.findViewById(tv_status);
            holder.item_listview = (ListView) convertView.findViewById(R.id.item_listview);
            holder.itemName = (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.itemprice = (TextView) convertView.findViewById(R.id.tv_item_price);
            holder.image_view=(ImageView) convertView.findViewById(R.id.image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OnTheGoResultModel orderResults = orderResultsList.get(position);
        if (value.equals("1")) {
            holder.tv_header.setVisibility(View.VISIBLE);
            holder.tv_header2.setVisibility(View.GONE);
            String time=getTime(orderResults);
            holder.tv_header.setText("Today ," + time + "\n Meet at " + orderResults.getPlaceName());
        } else {
            holder.tv_header.setVisibility(View.GONE);
            holder.tv_header2.setVisibility(View.VISIBLE);
            holder.image_view.setVisibility(View.GONE);
            String time=getTime(orderResults);
            holder.tv_header2.setText("Today ," + time + "\n Meet at " + orderResults.getPlaceName());


            ///if (orderResults.getStatus() != null) {
            //} else {
            //}


        }
        if(orderResults.getStatus()!=null)
        {
            if (orderResults.getStatus().equals("1")) {
                holder.tv_status.setText("Done");
                holder.tv_status.setVisibility(View.VISIBLE);
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.light_blue));
            } else {
                holder.tv_status.setText("Cancelled");
                holder.tv_status.setVisibility(View.VISIBLE);
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.bg_g_plus));
            }
        }

        String time=getTime(orderResults);
        holder.tv_header.setText("Today ," + time + "\n Meet at " + orderResults.getPlaceName());
        if(orderResults.getPrice().contains("R$ ")){
            holder.itemprice.setText(orderResults.getPrice());
        }else{
            holder.itemprice.setText("R$ " + orderResults.getPrice());
        }
        holder.itemName.setText(orderResults.getDishName());

//        OrderItemAdapter orderItemAdapter=new OrderItemAdapter(context,orderResultsList);
//        holder.item_listview.setAdapter(orderItemAdapter);
        holder.tv_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (value.equals("1")) {
                    ((OrdersActivity) context).getDataFromFragment(onTheGoModel, position);
                }

            }
        });

        final ViewHolder finalHolder = holder;
        return convertView;

    }

    private String getTime(OnTheGoResultModel orderResults) {
        String time=orderResults.getTime();
        if(time!=null) {
            if (time.contains(":")) {
                String[] array = time.split(":");
                time = array[0] +":"+ array[1];
            }
        }
        return time;
    }


    class ViewHolder {
        TextView tv_header, itemName, itemprice, tv_header2, tv_status;
        ListView item_listview;
        ImageView image_view;

    }

    class OrderItemAdapter extends BaseAdapter {
        Context context;
        List<OnTheGoResultModel> orderItemModelList;

        public OrderItemAdapter(Context context, List<OnTheGoResultModel> orderItemModelList) {
            this.context = context;
            this.orderItemModelList = orderItemModelList;
        }

        @Override
        public int getCount() {
            return orderItemModelList.size();
        }

        @Override
        public Object getItem(int i) {
            return orderItemModelList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SubViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.single_row_items_list, null);
                holder = new SubViewHolder();
                holder.itemName = (TextView) convertView.findViewById(R.id.tv_item_name);
                holder.itemprice = (TextView) convertView.findViewById(R.id.tv_item_price);
                convertView.setTag(holder);
            } else {
                holder = (SubViewHolder) convertView.getTag();
            }
            OnTheGoResultModel orderItemModel = orderItemModelList.get(position);


            DecimalFormat myFormatter = new DecimalFormat("#,##");
            String output = myFormatter.format(orderItemModel.getPrice());

            holder.itemName.setText(orderItemModel.getDishName());


            holder.itemprice.setText("R$ " + output );


            final SubViewHolder finalHolder = holder;
            return convertView;
        }

        class SubViewHolder {
            TextView itemName, itemprice;
        }
    }
}