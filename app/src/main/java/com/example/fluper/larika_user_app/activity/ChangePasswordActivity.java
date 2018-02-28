package com.example.fluper.larika_user_app.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.ActivityNewChangePasswordBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityNewChangePasswordBinding changePasswordBinding;
    private SharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePasswordBinding= DataBindingUtil.setContentView(this,R.layout.activity_new_change_password);
        preference = SharedPreference.getInstance(this);
        listener();
    }

    private void listener() {
        changePasswordBinding.tvSaveChnages.setOnClickListener(this);
        changePasswordBinding.ivCross.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_saveChnages:
                checkAllField();
                break;
            case R.id.iv_cross:
                finish();
                break;
        }

    }

    private void checkAllField() {
        if(changePasswordBinding.etLpasswrd.getText().toString().trim().length()>0)
        {
            if(changePasswordBinding.etNpasswrd.getText().toString().trim().length()>0)
            {
                if(changePasswordBinding.etNpasswrd.getText().toString().trim().length()>=8)
                {
                    if(changePasswordBinding.etRpasswrd.getText().toString().trim().length()>0)
                    {
                        if(changePasswordBinding.etRpasswrd.getText().toString().equals(changePasswordBinding.etNpasswrd.getText().toString()))
                        {
                            changePasswordApi();

                        }else
                        {
                            Utils.showToast(ChangePasswordActivity.this,"Password don't match");
                        }

                    }else
                    {
                        Utils.showToast(ChangePasswordActivity.this,"Please enter repeat password");
                    }

                }
                else
                {
                    Utils.showToast(ChangePasswordActivity.this,"Password should be atleast 8 character");
                }

            }else
            {
                Utils.showToast(ChangePasswordActivity.this,"Please enter new password");
            }
        }else
        {
            Utils.showToast(ChangePasswordActivity.this,"Please enter last password");
        }
    }

    private void changePasswordApi() {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();
        object.addProperty("oldPassword",changePasswordBinding.etLpasswrd.getText().toString());
        object.addProperty("newPassword",changePasswordBinding.etRpasswrd.getText().toString());


        Ion.with(this).load(Constants.BASE_URL+"changePassword")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, "")).setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                progress.dismiss();
                String responce = "";
                if (e != null) {
                    return;
                }
                switch (result.getHeaders().code()) {
                    case 200:
                        responce = result.getResult();

                        Utils.showToast(ChangePasswordActivity.this,"Password change successfully");
                        finish();
                        break;
                    case 201:
                        responce = result.getResult();
                        break;

                    case 204:
                        responce = result.getResult();

                        break;
                    case 400:
                        responce = result.getResult();
                        break;
                    case 401:
                        responce = result.getResult();
                        Utils.showToast(ChangePasswordActivity.this,"Incorrect password");
                        changePasswordBinding.etLpasswrd.setText("");
                        changePasswordBinding.etNpasswrd.setText("");
                        changePasswordBinding.etRpasswrd.setText("");
                        break;

                    default:
                        responce = result.getResult();
                        break;
                }


            }
        });


    }
}
