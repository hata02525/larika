package com.example.fluper.larika_user_app.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.fluper.larika_user_app.bean.AllPromoCodeModel;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.fragment.HomeItemFragment;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;

/**
 * Created by fluper-pc on 6/10/17.
 */

public class GetAllPromoCodes {
    private SharedPreferences sharedPreferences;
    private PaymentCardMainModel paymentCardMainModel;
    private Activity context;
    ArrayList<AllPromoCodeModel> allpromocode=new ArrayList<>();
    private HomeItemFragment homeItemFragment;
    private AllPromoCodeModel promoCodeModel;

    public GetAllPromoCodes(Activity context, HomeItemFragment homeItemFragment){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
        this.homeItemFragment=homeItemFragment;
    }


    public void carddetails() {
        Ion.with(context).load("POST", Constants.BASE_URL + "allPromoCode")
                .setHeader("accessToken",
                        UtilPreferences.getFromPrefs(context,
                                UtilPreferences.ACCESS_TOKEN, ""))
                .asString().withResponse().
                setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String responce = "";
                        if (result != null) {
                            try {
                                Intent intent = null;

                                switch (result.getHeaders().code()) {
                                    case 200:
                                        promoCodeModel= Utils.getgsonInstance().
                                                fromJson(result.getResult(),AllPromoCodeModel.class);

                                        homeItemFragment.callbackForPromocode(promoCodeModel);

                                        break;
                                    case 201:
                                        break;
                                    case 204:
                                        responce = result.getResult();

                                        break;
                                    case 400:
                                        responce = result.getResult();
                                        break;
                                    case 401:
                                        responce = result.getResult();
                                        break;
                                    case 500:

                                        // Utils.showToast(PaymentActivity.this,"Please enter valid details");
//                                            closePaymentDialog();
                                        break;

                                    default:
                                        responce = result.getResult();
                                        break;
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                        }


                    }
                });

    }




}
