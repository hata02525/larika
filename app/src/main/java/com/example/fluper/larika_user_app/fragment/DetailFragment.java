package com.example.fluper.larika_user_app.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.adapter.DetailsAdapter;
import com.example.fluper.larika_user_app.bean.OnTheGoModel;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.Progress;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;


public class DetailFragment extends Fragment {
    private View view;
    private TextView tv_meet_time, tv_building;
    private ImageView iv_call;
    private RecyclerView recyclerView;
    private TextView tv_product_name,tv_count,tv_rs,tv_card_detail,tv_total_price,tv_cancel,tv_meet_today;
    private Button btn_qr_code;
    private OnTheGoModel myOrderModel;
    private int position=0;
    private SharedPreferences sharedPrefs;
    private ListView list_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_detail, container, false);
        initUI();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setData();
        listener();

        return view;
    }

    private void listener() {
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrderApi();
            }
        });
    }

    private void cancelOrderApi() {
        final Progress progress = new Progress(getActivity());
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();
        object.addProperty("orderId", myOrderModel.getResult().get(position).getOrderId());





        Ion.with(getActivity()).load("POST", Constants.BASE_URL + "cancelOrder")
                .setHeader("accessToken", UtilPreferences.getFromPrefs(getActivity(),UtilPreferences.ACCESS_TOKEN,""))
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                String response;


                progress.dismiss();
                getActivity().finish();




            }
        });
    }

    private void setData() {
        String time=myOrderModel.getResult().get(position).getTime();
        if(time.contains(":")){
            String[] stringArray=time.split(":");
            time=stringArray[0]+":"+stringArray[1];
        }
        tv_meet_time.setText(time);
        tv_meet_today.setText("Meet today  "+myOrderModel.getResult().get(position).getVendorName()+" at");
//        long totalPrice=Long.parseLong(myOrderModel.getResult().get(position).getPrice())
//                *Long.parseLong(myOrderModel.getResult().get(position).getQuantity());
//        long price=0;
//        for (int i = 0; i <myOrderModel.getResult().size() ; i++) {
//
//            price= price+Long.parseLong(myOrderModel.getResult().get(i).getPrice());
//        }
        tv_total_price.setText(myOrderModel.getResult().get(position).getPrice()+"");
        DetailsAdapter detailsAdapter=new DetailsAdapter(getActivity(),myOrderModel.getResult(),position);
        list_view.setAdapter(detailsAdapter);

        getPaymentCardData();

    }

    private void getPaymentCardData() {
        PaymentCardMainModel paymentCardMainModel = new PaymentCardMainModel();
        Gson gson = new Gson();
        String json = sharedPrefs.getString("MyObject", "");
//        if(paymentCardMainModel!=null)
        paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
        if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
            for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {
                if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                    String last = paymentCardMainModel.
                            getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.
                            getAddCardModelList().get(i).getCardNumber().length() - 4);

                    tv_card_detail.setText("Credito   ....  ....  ....  " + last);
                }

            }
        }

    }

    private void initUI(){
        tv_product_name= (TextView) view.findViewById(R.id.tv_product_name);
        tv_meet_today= (TextView) view.findViewById(R.id.tv_meet_today);
        tv_total_price= (TextView) view.findViewById(R.id.tv_total_price);
        tv_card_detail= (TextView) view.findViewById(R.id.tv_card_detail);
        tv_cancel= (TextView) view.findViewById(R.id.tv_cancel);
        tv_count= (TextView) view.findViewById(R.id.tv_count);
        tv_rs= (TextView) view.findViewById(R.id.tv_rs);
        tv_meet_time = (TextView) view.findViewById(R.id.tv_meet_time);
        iv_call = (ImageView) view.findViewById(R.id.iv_call);
        list_view= (ListView) view.findViewById(R.id.list_view);

    }

    public void getModel(OnTheGoModel myOrderModel, int postion) {
        this.myOrderModel=myOrderModel;
        this.position=postion;

    }
}