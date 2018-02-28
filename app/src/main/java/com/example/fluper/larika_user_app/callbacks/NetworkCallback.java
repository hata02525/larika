package com.example.fluper.larika_user_app.callbacks;


/**
 * Created by fourthscreen on 3/11/2016.
 */
public interface NetworkCallback {
    void onNetworkSuccess(String result, String fromUrl, int status);
    void onNetworkTimeOut(String message, String fromUrl);
}
