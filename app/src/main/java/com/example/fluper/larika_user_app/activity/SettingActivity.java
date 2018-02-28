package com.example.fluper.larika_user_app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.database.DbHandler;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {
    private com.suke.widget.SwitchButton notification_switch;
    private SharedPreference preference;
    private DbHandler db;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        preference = SharedPreference.getInstance(this);
        db=new DbHandler(this);
        db.open();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        notification_switch= (com.suke.widget.SwitchButton) findViewById(R.id.notification_switch);

        if(UtilPreferences.getFromPrefs(SettingActivity.this,UtilPreferences.notificationValue,"").equals("1"))
        {
            notification_switch.setChecked(true);

        }else if(UtilPreferences.getFromPrefs(SettingActivity.this,UtilPreferences.notificationValue,"").equals("0"))
        {
            notification_switch.setChecked(false);
        }


        notification_switch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean b) {
                if(notification_switch.isChecked())
                {
                    updateNotification("1");

                }else
                {
                    updateNotification("0");

                }
            }
        });

//        notification_switch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(notification_switch.isChecked())
//                {
//                    updateNotification("1");
//
//                }else
//                {
//                    updateNotification("0");
//
//                }
//
//            }
//        });


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.right_enter, R.anim.right_exit);
            }
        });
        findViewById(R.id.tv_trms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termsAndConditionApi();
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void termsAndConditionApi() {
        Ion.with(this).load("GET", Constants.BASE_URL + "tnc")
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                String responce = "";
                if (e != null) {
                    return;
                }
                try {
                    JSONObject jsonObject=new JSONObject(result.getResult());
//                    Utils.showToast(SettingActivity.this,jsonObject.getString("message"));
                    JSONObject resultObject=jsonObject.getJSONObject("result");
                    String web=resultObject.getString("url");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.larikaapp.com/termos"));
                    startActivity(browserIntent);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }
        });
    }

    private void updateNotification(final String switchValue)
    {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();
        object.addProperty("notification",switchValue);

        Ion.with(this).load("POST", Constants.BASE_URL + "notification")
                .setHeader("accessToken", UtilPreferences.getFromPrefs(SettingActivity.this,UtilPreferences.ACCESS_TOKEN,""))
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                progress.dismiss();
                String responce = "";
                if (e != null) {
                    return;
                }
                if(result!=null)
                {
                    if(result.getHeaders().code()==400)
                    {
                        db.clearCart();
                        db.close();
                        UtilPreferences.saveToPrefs(SettingActivity.this,
                                UtilPreferences.FROM_CART_DIALOG,"FROM_CART");
                        preference.deletePreference();
                        PaymentCardMainModel paymentCardMainModel=new PaymentCardMainModel();
                        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(paymentCardMainModel);
                        prefsEditor.putString("MyObject", json);
                        prefsEditor.commit();
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(SettingActivity.this, LoginSignUpActivity.class));
                        finishAffinity();

                    }
                    else if(result.getHeaders().code()==401)
                    {
                        db.clearCart();
                        db.close();
                        UtilPreferences.saveToPrefs(SettingActivity.this,
                                UtilPreferences.FROM_CART_DIALOG,"FROM_CART");
                        preference.deletePreference();
                        PaymentCardMainModel paymentCardMainModel=new PaymentCardMainModel();
                        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(paymentCardMainModel);
                        prefsEditor.putString("MyObject", json);
                        prefsEditor.commit();
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(SettingActivity.this, LoginSignUpActivity.class));
                        finishAffinity();

                    }
                    else
                    {
                        try {
                            JSONObject jsonObject=new JSONObject(result.getResult());
                            Utils.showToast(SettingActivity.this,jsonObject.getString("message"));
                            UtilPreferences.saveToPrefs(SettingActivity.this,UtilPreferences.notificationValue,switchValue);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                }



            }
        });
    }
}
