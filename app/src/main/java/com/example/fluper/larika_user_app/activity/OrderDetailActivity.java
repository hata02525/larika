package com.example.fluper.larika_user_app.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.OnTheGoModel;
import com.example.fluper.larika_user_app.fragment.DeliveryFragment;
import com.example.fluper.larika_user_app.fragment.DetailFragment;
import com.example.fluper.larika_user_app.utils.Utils;


public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_order, tv_detail;
    private ImageView iv_cross;
    private OnTheGoModel myOrderModel;
    private int postion=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getDataFromIntent();
        initUI();

    }

    private void getDataFromIntent() {
        if(getIntent().hasExtra("order_detail"))
        {
            String json=getIntent().getStringExtra("order_detail");
            postion=getIntent().getIntExtra("position",0);
            myOrderModel= Utils.getgsonInstance().fromJson(json,OnTheGoModel.class);

        }
    }

    private void initUI(){
        iv_cross = (ImageView) findViewById(R.id.iv_cross);
        tv_order = (TextView) findViewById(R.id.tv_order);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_order.setEnabled(false);

        iv_cross.setOnClickListener(this);
        tv_detail.setOnClickListener(this);
        tv_order.setOnClickListener(this);
        Fragment fragment = new DeliveryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
        ((DeliveryFragment) fragment).getModel(myOrderModel,postion);
    }
    public void disableButton()
    {
        tv_order.setEnabled(false);
        tv_detail.setEnabled(false);
    }
    public void enableableButton()
    {
        tv_order.setEnabled(true);
        tv_detail.setEnabled(true);
    }



    @Override
    public void onClick(View v) {
        Fragment fragment;
        FragmentTransaction transaction;
        switch (v.getId()) {
            case R.id.iv_cross:
                finish();
                break;
            case R.id.tv_order:
                tv_order.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_detail.setTextColor(ContextCompat.getColor(this, R.color.black_grey_tint));
                fragment = new DeliveryFragment();

                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.commit();

                ((DeliveryFragment) fragment).getModel(myOrderModel,postion);
                tv_order.setEnabled(false);
                tv_detail.setEnabled(true);
                break;
            case R.id.tv_detail:
                tv_detail.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_order.setTextColor(ContextCompat.getColor(this, R.color.black_grey_tint));
                fragment = new DetailFragment();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
                ((DetailFragment) fragment).getModel(myOrderModel,postion);
                tv_detail.setEnabled(false);
                tv_order.setEnabled(true);
                break;

        }
    }


}