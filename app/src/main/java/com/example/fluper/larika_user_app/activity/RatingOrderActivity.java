package com.example.fluper.larika_user_app.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.OnTheGoModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.ActivityRatingOrderBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class RatingOrderActivity extends AppCompatActivity {
    private OnTheGoModel myOrderModel;
    ActivityRatingOrderBinding activityOrderCompleteBinding;
    private int position=0;
    private SharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderCompleteBinding= DataBindingUtil.
                setContentView(this, R.layout.activity_rating_order);
        preference = SharedPreference.getInstance(this);
        getDataFromIntent();
        listener();

    }

    private void listener() {
        LayerDrawable stars = (LayerDrawable)activityOrderCompleteBinding.rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        activityOrderCompleteBinding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activityOrderCompleteBinding.rating.getRating()!=0)
                {
                    ratingOrderApi();
                }
                else
                {
                    Utils.showToast(RatingOrderActivity.this,"Please rate product");
                }

            }
        });
    }

    private void ratingOrderApi() {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();
        object.addProperty("rating",
                activityOrderCompleteBinding.rating.getRating());
        object.addProperty("vendorDishId",myOrderModel.getResult().get(0).getVendorDishId());
        Ion.with(this).load("POST", Constants.BASE_URL + "userRating")
                .setHeader("accessToken",
                        preference.getString(Constants.ACCESS_TOKEN,""))
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                String response;
                progress.dismiss();

                if(result!=null)
                {
                    try {
                        JSONObject jsonObject=new JSONObject(result.getResult());
                        Utils.showToast(RatingOrderActivity.this,jsonObject.getString("message"));
                        Intent intent=new Intent(RatingOrderActivity.this,MoveActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }








            }
        });
    }

    private void getDataFromIntent() {
        if(getIntent().hasExtra("orderModel"))
        {
            myOrderModel=Utils.getgsonInstance().
                    fromJson(getIntent().getStringExtra("orderModel"),OnTheGoModel.class);
            position= Integer.parseInt(getIntent().getStringExtra("position"));

            activityOrderCompleteBinding.tvName.setText(myOrderModel.getResult().get(position).getDishName());
            activityOrderCompleteBinding.tvLocationName.setText("Meet at "+myOrderModel.getResult().get(position).getPlaceName());
            activityOrderCompleteBinding.tvDishName.setText(myOrderModel.getResult().get(position).getDishName());
            activityOrderCompleteBinding.tvDishPrice.setText("R$ "+myOrderModel.getResult().get(position).getPrice());
            activityOrderCompleteBinding.tvTimee.setText(myOrderModel.getResult().get(position).getTime());


        }
    }
}
