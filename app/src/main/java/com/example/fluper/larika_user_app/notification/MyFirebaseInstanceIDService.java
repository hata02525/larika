package com.example.fluper.larika_user_app.notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
/**
 * Created by fluper on 26/4/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
//    SharedPreference preference;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MyFirebaseInstanceIDService() {
    // preference=SharedPreference.getInstance(this);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", "Refreshed token: " + refreshedToken);
       // preference.putString(PrefConstants.DEVICE_TOKEN,refreshedToken);
//        prefManager.putString(PrefrenceConstants.DEVICE_TOKEN,refreshedToken);
    }
}
