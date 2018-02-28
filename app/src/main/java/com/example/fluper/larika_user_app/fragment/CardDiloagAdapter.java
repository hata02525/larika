package com.example.fluper.larika_user_app.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.AddMyProductModel;
import com.example.fluper.larika_user_app.utils.Utils;

import java.util.List;


class CardDiloagAdapter extends BaseAdapter {
    Context context;
    HomeItemFragment homeItemFragment;
    List<AddMyProductModel>cardModelList;
    public CardDiloagAdapter(Context context,
                             List<AddMyProductModel> cardModelList, HomeItemFragment homeItemFragment)
    {
       this.context=context;
        this.cardModelList=cardModelList;
        this.homeItemFragment=homeItemFragment;
    }
    @Override
    public int getCount() {
        return cardModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return cardModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_row_card_dialog_items, null);
            holder = new ViewHolder();
            holder.tv_remove= (TextView) convertView.findViewById(R.id.tv_remove);
            holder.product_name= (TextView) convertView.findViewById(R.id.product_name);
            holder.product_quantity= (TextView) convertView.findViewById(R.id.product_quantity);
            holder.product_price= (TextView) convertView.findViewById(R.id.product_price);
            holder.img_minus= (ImageView) convertView.findViewById(R.id.img_minus);
            holder.img_plus= (ImageView) convertView.findViewById(R.id.img_plus);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AddMyProductModel orderResults=cardModelList.get(i);
        holder.product_name.setText(orderResults.getDishName());
        holder.product_quantity.setText(cardModelList.get(i).getQuantity());

        holder.product_price.setText(orderResults.getDishPrice()+"");
        holder.tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeItemFragment)homeItemFragment).deleteProduct(cardModelList,cardModelList.get(i).getDishId(),
                        cardModelList.get(i).getDishName(),i);
            }
        });


        holder.img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(cardModelList.get(i).getQuantity())
                        <  Integer.parseInt(cardModelList.get(i).getDishStock()))
                {
                    ((HomeItemFragment)homeItemFragment).increaseProduct(cardModelList,i);
                }
                else
                {
                    Utils.showToast(context,"Product is out of stock");
                }
            }
        });
        holder.img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if(cardModelList.size()>0 && Integer.parseInt(cardModelList.get(i).getQuantity())>1)
                    {
                        ((HomeItemFragment)homeItemFragment).decreaseProduct(cardModelList,i);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }





            }
        });




        return convertView;
    }
    class ViewHolder {
        TextView product_name,product_quantity,product_price,tv_remove;
        ImageView img_plus,img_minus;


    }
}
