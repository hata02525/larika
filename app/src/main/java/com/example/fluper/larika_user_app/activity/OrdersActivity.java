package com.example.fluper.larika_user_app.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.OnTheGoModel;
import com.example.fluper.larika_user_app.databinding.ActivityOrdersBinding;
import com.example.fluper.larika_user_app.fragment.HistoryFragment;
import com.example.fluper.larika_user_app.fragment.OnTheGoFragment;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class OrdersActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityOrdersBinding activityOrdersBinding;
    private Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrdersBinding= DataBindingUtil.setContentView(this,R.layout.activity_orders);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new OnTheGoFragment()).commit();
        init();
        listener();
    }

    private void listener() {
        activityOrdersBinding.tvHistory.setOnClickListener(this);
        activityOrdersBinding.tvOnthego.setOnClickListener(this);
        activityOrdersBinding.ivCross.setOnClickListener(this);
    }
    public void getDataFromFragment(OnTheGoModel onTheGoModel, int position)
    {
        Intent intent = new Intent(OrdersActivity.this, OrderDetailActivity.class);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(Utils.getgsonInstance().toJson(onTheGoModel));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("order_detail", jsonObject.toString());
        intent.putExtra("position", position);
        startActivity(intent);


    }

    private void init() {
        progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View view) {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        switch (view.getId())
        {
            case R.id.tv_onthego:
                activityOrdersBinding.tvOnthego.setTextColor(getResources().getColor(R.color.white));
                activityOrdersBinding.tvHistory.setTextColor(getResources().getColor(R.color.black_grey_tint));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new OnTheGoFragment()).commit();
                break;
            case R.id.tv_history:
                activityOrdersBinding.tvOnthego.setTextColor(getResources().getColor(R.color.black_grey_tint));
                activityOrdersBinding.tvHistory.setTextColor(getResources().getColor(R.color.white));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new HistoryFragment()).commit();
                break;
            case R.id.iv_cross:
                activityOrdersBinding.ivCross.setAlpha(1f);
                activityOrdersBinding.ivCross.setAnimation(animation1);
                finish();
                break;

        }
    }
}