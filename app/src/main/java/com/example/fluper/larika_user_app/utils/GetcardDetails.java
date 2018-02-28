package com.example.fluper.larika_user_app.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.fluper.larika_user_app.bean.AddPaymentCardModel;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.bean.PaymentResponseMainModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.fragment.HomeItemFragment;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

/**
 * Created by dell on 13/9/17.
 */

public class GetcardDetails {

    private SharedPreferences sharedPreferences;
    private PaymentCardMainModel paymentCardMainModel;
    private Activity context;
    private HomeItemFragment homeItemFragment;

    public GetcardDetails(Activity context, HomeItemFragment homeItemFragment){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
        this.homeItemFragment=homeItemFragment;
    }


    public void carddetails() {
        Ion.with(context).load("POST", Constants.BASE_URL + "getAllCard")
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
                                        PaymentResponseMainModel
                                                paymentResponseMainModel = Utils.
                                                getgsonInstance().
                                                fromJson(result.getResult(),
                                                        PaymentResponseMainModel.class);
                                        paymentCardMainModel = new PaymentCardMainModel();

                                        for (int i = 0; i < paymentResponseMainModel.getResult().size(); i++) {
                                            AddPaymentCardModel addCardModel = new AddPaymentCardModel();
                                            addCardModel.setCardNumber(paymentResponseMainModel.getResult().get(i).getNumber());
                                            addCardModel.setCardExpiry(paymentResponseMainModel.getResult().get(i).getExpire_month()
                                                    + "-" + paymentResponseMainModel.getResult().get(i).getExpire_year());
                                            addCardModel.setCardUserName(paymentResponseMainModel.getResult().get(i).getFirst_name() +
                                                    " " + paymentResponseMainModel.getResult().get(i).getLast_name());
                                            addCardModel.setCardCvv(paymentResponseMainModel.getResult().get(i).getCvv2());
                                            addCardModel.setCardId(paymentResponseMainModel.getResult().get(i).getId());

                                            paymentCardMainModel.getAddCardModelList().add(addCardModel);
                                        }


                                        if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
                                            paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
                                        }
                                        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                                        Gson gson = new Gson();
                                        String json = gson.toJson(paymentCardMainModel);
                                        prefsEditor.putString("MyObject", json);
                                        prefsEditor.commit();
                                        homeItemFragment.callback();

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
