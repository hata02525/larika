package com.example.fluper.larika_user_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.HomeDataBean;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.ActivityMenuScreenBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;

import java.util.ArrayList;

import static com.example.fluper.larika_user_app.R.id.iv_cross;

public class MenuScreenActivity extends AppCompatActivity implements View.OnClickListener {
    int requestCode;
    ArrayList<HomeDataBean> list;
    ActivityMenuScreenBinding activityMenuScreenBinding;
    private SharedPreference prefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMenuScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu_screen);
        prefrence = SharedPreference.getInstance(MenuScreenActivity.this);

       listener();

        getListFromIntent();


    }

    private void listener() {
        activityMenuScreenBinding.ivCross.setOnClickListener(this);
        activityMenuScreenBinding.tvAccount.setOnClickListener(this);
        activityMenuScreenBinding.tvWntsell.setOnClickListener(this);
        activityMenuScreenBinding.tvOrder.setOnClickListener(this);
        activityMenuScreenBinding.tvPayments.setOnClickListener(this);
        activityMenuScreenBinding.header.setOnClickListener(this);
    }

    private void getListFromIntent() {
        if (getIntent() != null) {
            if (getIntent().hasExtra("finder")) {
                if (getIntent().getStringExtra("finder").equals("1")) {
                    list = (ArrayList<HomeDataBean>) getIntent().getSerializableExtra("tempList");
                    requestCode = 101;

                } else {
                    requestCode = 100;
                }

            }
        }

    }

    @Override
    public void onClick(View v) {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        Intent intent;
        switch (v.getId()) {
            case R.id.header:
                if (requestCode == 101) {
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("homeDataList", list);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_menu, R.anim.slide_down_view);
                    finishAffinity();
                } else {
                    startActivity(new Intent(this, MoveActivity.class));
                    overridePendingTransition(R.anim.fade_menu, R.anim.slide_down_view);
                    finishAffinity();
                }
                break;
            case iv_cross:
                activityMenuScreenBinding.ivCross.setAlpha(1f);
                activityMenuScreenBinding.ivCross.startAnimation(animation1);
                finish();
//                if (requestCode == 101) {
//                    intent = new Intent(this, HomeActivity.class);
//                    intent.putExtra("homeDataList", list);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_menu, R.anim.slide_down_view);
//                    finishAffinity();
//                } else {
//                    startActivity(new Intent(this, MoveActivity.class));
//                    overridePendingTransition(R.anim.fade_menu, R.anim.slide_down_view);
//                    finishAffinity();
//                }

                break;
            case R.id.tv_account:

                if(!prefrence.getString(Constants.ACCESS_TOKEN,"").equals("token")
                        && !prefrence.getString(Constants.ACCESS_TOKEN,"").equals("") )
                {
                    activityMenuScreenBinding.tvAccount.setAlpha(1f);
                    activityMenuScreenBinding.tvAccount.startAnimation(animation1);
                    intent = new Intent(this, AccountActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    overridePendingTransition(R.anim.lefttorightone, R.anim.righttoleftone);
                }
                else
                {
                    Intent intent1 = new Intent(MenuScreenActivity.this, LoginSignUpActivity.class);
                    intent1.putExtra(Constants.FROM_HOME_ITEM_DIALOG, Constants.FROM_HOME_ITEM_DIALOG);
                    UtilPreferences.saveToPrefs(MenuScreenActivity.this, UtilPreferences.FROM_CART_DIALOG,"3");
                    MenuScreenActivity.this.startActivity(intent1);
                    //startActivity(new Intent(MenuScreenActivity.this, LoginSignUpActivity.class));
                   // finishAffinity();
                   // openPopUp();
                }

                break;
            case R.id.tv_wntsell:
                activityMenuScreenBinding.tvWntsell.setAlpha(1f);
                activityMenuScreenBinding.tvWntsell.startAnimation(animation1);
                if(!prefrence.getString(Constants.ACCESS_TOKEN,"").equals("token")
                        && !prefrence.getString(Constants.ACCESS_TOKEN,"").equals("") )
                {
                    intent = new Intent(this, IWannaSellActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                }
                else
                {
                    openPopUp();
                }

                break;
            case R.id.tv_order:

                if(!prefrence.getString(Constants.ACCESS_TOKEN,"").equals("token")
                        && !prefrence.getString(Constants.ACCESS_TOKEN,"").equals("") )
                {
                    activityMenuScreenBinding.tvOrder.setAlpha(1f);
                    activityMenuScreenBinding.tvOrder.startAnimation(animation1);

                    intent = new Intent(this, OrdersActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.lefttorightone, R.anim.righttoleftone);
                }
                else
                {
                    openPopUp();
                }
                break;

            case R.id.tv_payments:

                if(!prefrence.getString(Constants.ACCESS_TOKEN,"").equals("token")
                        && !prefrence.getString(Constants.ACCESS_TOKEN,"").equals("") )
                {
                    activityMenuScreenBinding.tvPayments.setAlpha(1f);
                    activityMenuScreenBinding.tvPayments.startAnimation(animation1);

                    intent = new Intent(this, PaymentActivity.class);
                    startActivity(intent);

                    overridePendingTransition(R.anim.lefttorightone, R.anim.righttoleftone);
                }
                else
                {
                    openPopUp();
                }

                break;

               /* activityMenuScreenBinding.tvPayments.setAlpha(1f);
                activityMenuScreenBinding.tvPayments.startAnimation(animation1);
                intent = new Intent(this, PaymentActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                */

        }
    }

    private void openPopUp() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        alertDialog.setTitle("Larika");
        alertDialog.setMessage("Please login first. Do you want to login ?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //startActivity(new Intent(MenuScreenActivity.this, LoginSignUpActivity.class));
                //finishAffinity();
                /*Intent intent1 = new Intent(MenuScreenActivity.this, LoginSignUpActivity.class);
                intent1.putExtra(Constants.FROM_MENU_SCREEN_DIALOG, Constants.FROM_MENU_SCREEN_DIALOG);
                UtilPreferences.saveToPrefs(MenuScreenActivity.this, UtilPreferences.FROM_CART_DIALOG_menu_screen, "3");
                startActivity(intent1);*/

                Intent intent = new Intent(MenuScreenActivity.this, LoginSignUpActivity.class);
                intent.putExtra(Constants.FROM_HOME_ITEM_DIALOG, Constants.FROM_HOME_ITEM_DIALOG);
                UtilPreferences.saveToPrefs(MenuScreenActivity.this, UtilPreferences.FROM_CART_DIALOG,"3");
                MenuScreenActivity.this.startActivity(intent);

                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {




                dialog.cancel();
            }
        });
        alertDialog.show();
    }




}
