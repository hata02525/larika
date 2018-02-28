package com.example.fluper.larika_user_app.Network;

import android.content.Context;

import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;


public class NetworkThread {
    private String url;
    private NetworkCallback callback;

    private SharedPreference preference;

    public NetworkThread(NetworkCallback callback, String url) {
        this.callback = callback;
        this.url = url;

    }

    private Builders.Any.B getIon(Context context, String url, int timeout) {
        return Ion.with(context).load(url).setTimeout(timeout);
    }

    public void getNetworkResponse(final Context context, JsonObject jsonObject, int timeout) {

        preference = SharedPreference.getInstance(context);
        Builders.Any.B ion = getIon(context, url, timeout);
        ion.addHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""));
        ion.setJsonObjectBody(jsonObject)
                .asString().withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String responce = "";
                        int status = 0;
                        if (e != null) {
                            callback.onNetworkTimeOut(Constants.ERR_NETWORK_TIMEOUT, url);
                            Utils.showToast(context,Constants.ERR_NETWORK_TIMEOUT);
                            return;
                        }
                        switch (result.getHeaders().code()) {
                            case 200:
                                responce = result.getResult();
                                status = 200;
                                break;
                            case 201:
                                responce = result.getResult();
                                status = 201;
                                break;
                            case 204:
                                responce = result.getResult();
                                status = 204;

                                break;
                            case 400:
                                responce = result.getResult();
                                status = 400;
                                break;
                            case 401:
                                responce = result.getResult();
                                status = 401;
                                break;
                            case 500:
                                Utils.showToast(context,"Invalid email/password");
                                break;

                            default:
                                responce = result.getResult();
                                status = 0;
                                break;
                        }

                        callback.onNetworkSuccess(responce, url, status);
                    }
                });
    }

    public void purgeRequestQueue(Context context) {
        Ion.getInstance(context, "ion").cancelAll(context);
    }

//    public void getNetworkResponse(Context context, JsonObject jsonObject, int timeout, final String date) {
//        Builders.Any.B ion = getIon(context, url, timeout);
//        ion.setJsonObjectBody(jsonObject)
//                .asString()
//                .setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//                        if (e != null) {
//                            callback.onNetworkTimeOut(Constants.ERR_NETWORK_TIMEOUT, url);
//                            return;
//                        }
//                        callback.onNetworkSuccess(result, url);
//                    }
//                });
//    }

//    public void getNetworkResponse(Context context, int timeout) {
////        preference=SharedPreference.getInstance(context);
//        Builders.Any.B ion = getIon(context, url, timeout);
//        JsonParser jsonParser = new JsonParser();
//
//        JSONObject jsonObject = new JSONObject();
////        try {
//////            jsonObject.accumulate("accessToken",preference.getString(Constants.ACCESS_TOKEN,""));
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//        JsonObject request = (JsonObject) jsonParser.parse(jsonObject.toString());
//        ion.setJsonObjectBody(request).asString()
//
//                .setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//                        if (e != null) {
//                            callback.onNetworkTimeOut(Constants.ERR_NETWORK_TIMEOUT, url);
//                            return;
//                        }
//                        callback.onNetworkSuccess(result, url);
//                    }
//                });
//    }
}
