package com.example.fluper.larika_user_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.AppSharedPrefernces;

public class SplashActivity extends AppCompatActivity {
    private AppSharedPrefernces appSharedPrefernce;
    private SharedPreference sharedPrefs;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);
        sharedPrefs = SharedPreference.getInstance(this);
        appSharedPrefernce = AppSharedPrefernces.getsharedprefInstance(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (sharedPrefs.getBoolean(Constants.SESSION, false)) {
                    Intent intent = new Intent(SplashActivity.this, MoveActivity.class);
                    startActivity(intent);
                    finish();

                } else {
//                    setContentView(R.layout.activity_main);
                    Intent intent = new Intent(SplashActivity.this, LoginSignUpActivity.class);
                    startActivity(intent);


                    finish();
                }

            }
        }, 2000);
    }
}


//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashActivity.this, LoginSignUpActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 2000);



