package com.example.fluper.larika_user_app.sharedPrefernces;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

/**
 * Created by rohit on 6/6/17.
 */

public class UtilPreferences {
    public static final String FROM_CART_DIALOG = "FROM_CART";
    public static final String FROM_CART_DIALOG_menu_screen = "FROM_CART_DIALOG_menu_screen";
    public static final String CURRENT_LATITUDE = "CURRENT_LATITUDE";
    public static final String CURRENT_LONGITUDE = "CURRENT_LONGITUDE";
    public static final String OPEN_PAYMENT_LOCAL = "0";
    public static final String ADD_CART = "ADD_CART";
    public static final String DELETE_CART = "1";
    public static final String UPDATED_QUANTITY = "UPDATED_QUANTITY";
    public static final String UNDELETE_PRODUCT = "UNDELETE_PRODUCT";
    public static final String OPEN_CONFIRM_ORDER_DIALOG = "OPEN_CONFIRM_ORDER_DIALOG";
    public static final String notificationValue = "null";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String MOVE_ADDRESS = "move_addresss";
    public static final String IS_MY_CURRENT_LOCATION = "isMyCurrentLocation";


    public static EditText saveToPrefs(Context context, String key, String value) {
        try {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        return null;
    }

    public static String getFromPrefs(Context context, String key,
                                      String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
