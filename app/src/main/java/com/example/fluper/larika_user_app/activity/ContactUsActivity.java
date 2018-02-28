package com.example.fluper.larika_user_app.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.fragment.ContactUsFragment;
import com.example.fluper.larika_user_app.fragment.HelpFragment;
import com.example.fluper.larika_user_app.utils.Progress;

public class ContactUsActivity extends AppCompatActivity {

    private TextView tv_help, tv_contactUs;
    public Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_contactUs = (TextView) findViewById(R.id.tv_contactUs);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HelpFragment()).commit();

        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_help.setTextColor(ContextCompat.getColor(ContactUsActivity.this, R.color.white));
                tv_contactUs.setTextColor(ContextCompat.getColor(ContactUsActivity.this, R.color.black_grey_tint));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new HelpFragment()).commit();
            }
        });

        findViewById(R.id.tv_contactUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_contactUs.setTextColor(ContextCompat.getColor(ContactUsActivity.this, R.color.white));
                tv_help.setTextColor(ContextCompat.getColor(ContactUsActivity.this, R.color.black_grey_tint));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ContactUsFragment()).commit();
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);


            }
        });
    }
}
