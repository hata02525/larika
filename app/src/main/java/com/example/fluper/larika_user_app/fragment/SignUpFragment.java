package com.example.fluper.larika_user_app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;
import com.example.fluper.larika_user_app.Network.NetworkThread;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.activity.LoginSignUpActivity;
import com.example.fluper.larika_user_app.activity.MoveActivity;
import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpFragment extends Fragment
        implements View.OnClickListener, NetworkCallback {
    View view;
    LoginSignUpActivity activity;
    Activity context;
    private EditText et_fullName, et_email, et_numbr, et_passwrd;
    private TextView tv_signUp, country_code;
    private boolean isFromFaceobook;
    private String value = "";
    private SharedPreference preference;
    private String countryCode;
    private RelativeLayout rl_Parent;

    //    private int tempStatus;
/////String country_code;
    public SignUpFragment() {
        // Required empty public constructor
    }


    public static final SignUpFragment newInstance(String message) {
        SignUpFragment f = new SignUpFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        activity = (LoginSignUpActivity) getActivity();
        context = getActivity();
        preference = SharedPreference.getInstance(activity);
        view.findViewById(R.id.tv_facebook).setOnClickListener(this);


        et_fullName = (EditText) view.findViewById(R.id.et_fullName);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_numbr = (EditText) view.findViewById(R.id.et_numbr);
        et_passwrd = (EditText) view.findViewById(R.id.et_passwrd);
        tv_signUp = (TextView) view.findViewById(R.id.tv_signUp);
        country_code = (TextView) view.findViewById(R.id.country_code);
        rl_Parent = (RelativeLayout) view.findViewById(R.id.relativesignup);
        setupUI(rl_Parent);

        country_code.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final CountryPicker picker = CountryPicker.newInstance("Select Country");
                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code) {
//                        Toast.makeText(
//                                RegisterScreen.this,
//                                "Country Name: " + name + " - Code: " + code
//                                        + " - Currency: "
//                                        + CountryPicker.getCurrencyCode(code),
//                                Toast.LENGTH_SHORT).show();
//                        Log.i("phone code", GetCountryPhoneCode(code));

                        country_code.setText("+" + GetCountryPhoneCode(code));
                        countryCode = country_code.getText().toString().trim();
                        picker.dismiss();
                    }
                });
                picker.show(getFragmentManager(), "COUNTRY_PICKER");

            }
        });


        tv_signUp.setOnClickListener(this);

        return view;
    }

    private String GetCountryPhoneCode(String code) {
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        // getNetworkCountryIso
//        CountryID = manager.getSimCountryIso().toUpperCase();
//        Log.e("CountryID", "=" + CountryID);
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(code.trim())) {
                CountryZipCode = g[0];
                return CountryZipCode;
            }
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_facebook:
                Utils.hideSoftKeyBoard(activity);
                activity.loginFacebook();
                break;
            case R.id.tv_signUp:
                Utils.hideSoftKeyBoard(activity);
//                fieldValidation();
                if (validateField()) {
                    if (preference.getString(Constants.FB_ID, "").equals("")) {

                        activity.status = 1;
                    } else {
                        activity.status = 0;
                    }
                    preference.putString(Constants.FB_EMAIL, et_email.getText().toString().trim());
                    callSignUpApi(activity.latitude, activity.longitude, value);
                }
                break;
        }
    }

    public void callSignUpApi(Double latitude, Double longitude, String fbValue) {
        preference.putString(Constants.FB_CONTACT, et_numbr.getText().toString());
        this.value = fbValue;
        activity.progress.show();
        JsonObject object = new JsonObject();
        if (!preference.getString(Constants.FB_EMAIL, "").equals("")) {
            String deviceToken = FirebaseInstanceId.getInstance().getToken();
            if (activity.status == 1 && value.equals("")) {
                object.addProperty("email", et_email.getText().toString());
                object.addProperty("password", et_passwrd.getText().toString());
                object.addProperty("name", et_fullName.getText().toString());
                object.addProperty("type", "user");
                object.addProperty("contact", et_numbr.getText().toString());
                object.addProperty("type", "user");
                object.addProperty("lat", latitude);
                object.addProperty("lng", longitude);
                object.addProperty("deviceType", 2);
                object.addProperty("countryCode", country_code.getText().toString().trim());
                object.addProperty("deviceToken", deviceToken);
                object.addProperty("imageUrl", "");

                NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "register");
                thread.getNetworkResponse(activity, object, 10000);
            } else {
                if (preference.getString(Constants.FB_EMAIL, "").equals("")) {
                    preference.putString(Constants.FB_EMAIL, et_email.getText().toString());
                }
//                    if(preference.getString(Constants.FB_CONTACT,"").equals("")){
//                        et_email.setText(preference.getString(Constants.FB_EMAIL, ""));
//                        ///et_fullName.setText(preference.getString(Constants.FB_NAME, ""));
//
//                        preference.putString(Constants.FB_CONTACT, et_numbr.getText().toString());
//
//                        object.addProperty("email", "");
//                        object.addProperty("password", et_passwrd.getText().toString());
//                        object.addProperty("name",  et_fullName.getText().toString());
//                        object.addProperty("type", "user");
//                        object.addProperty("imageUrl", preference.getString(Constants.FB_PICTURE, ""));
//                        object.addProperty("contact", et_numbr.getText().toString());
//                        object.addProperty("lat", latitude);
//                        object.addProperty("lng", longitude);
//                        object.addProperty("deviceType", 1);
//                        object.addProperty("deviceToken", FirebaseInstanceId.getInstance().getToken());
//                        NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "register");
//                        thread.getNetworkResponse(activity, object, 10000);
//
//                }
//                else
//                    {

                object.addProperty("email", preference.getString(Constants.FB_EMAIL, ""));
                object.addProperty("idFacebook", preference.getString(Constants.FB_ID, ""));
                object.addProperty("password", et_passwrd.getText().toString());
                object.addProperty("name", et_fullName.getText().toString());
                object.addProperty("numbr", preference.getString(Constants.FB_CONTACT, ""));
                object.addProperty("type", "user");
                object.addProperty("imageUrl", preference.getString(Constants.FB_PICTURE, ""));
                object.addProperty("contact", et_numbr.getText().toString());
                object.addProperty("countryCode", country_code.getText().toString().trim());
                object.addProperty("lat", latitude);
                object.addProperty("lng", longitude);
                object.addProperty("type", "user");
                object.addProperty("deviceType", 2);
                object.addProperty("imageUrl", "");
                object.addProperty("deviceToken", FirebaseInstanceId.getInstance().getToken());
                NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "register");
                thread.getNetworkResponse(activity, object, 10000);
//                    }
            }

        } else {
            if (preference.getString(Constants.FB_EMAIL, "").equals("")) {
                preference.putString(Constants.FB_EMAIL, et_email.getText().toString());
            }
            if (preference.getString(Constants.FB_CONTACT, "").equals("")) {

                /// preference.putString(Constants.FB_CONTACT, et_numbr.getText().toString());
            }
            object.addProperty("email", preference.getString(Constants.FB_EMAIL, ""));
            object.addProperty("idFacebook", preference.getString(Constants.FB_ID, ""));
            object.addProperty("password", "");
            object.addProperty("name", et_fullName.getText().toString());
            object.addProperty("type", "user");
            object.addProperty("imageUrl", preference.getString(Constants.FB_PICTURE, ""));
            object.addProperty("contact", et_numbr.getText().toString());
            ///object.addProperty("countryCode", country_code.getText().toString());
            object.addProperty("countryCode", country_code.getText().toString().trim());
            object.addProperty("lat", latitude);
            object.addProperty("lng", longitude);
            object.addProperty("deviceType", 2);
            object.addProperty("deviceToken", FirebaseInstanceId.getInstance().getToken());
            NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "register");
            thread.getNetworkResponse(activity, object, 10000);

        }

    }



    /*public void setField(){
        if(activity.status==1){
            et_fullName.setText(preference.getString(Constants.FB_NAME, ""));
            et_email.setText(preference.getString(Constants.FB_EMAIL, ""));
            et_numbr.setEnabled(true);
            et_passwrd.setEnabled(false);
        }
    }*/

   /* public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }*/

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if(activity!=null){
                        Utils.hideSoftKeyBoard(activity);
                    }
                    return false;
                }
            });

        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }}
    public void setFilds() {
        if (activity.status == 0) {
            et_fullName.setText(preference.getString(Constants.FB_NAME, ""));
            et_email.setEnabled(false);
            et_numbr.setEnabled(true);
            et_fullName.setEnabled(true);
            et_passwrd.setEnabled(false);
        }
    }

    private boolean validateField() {
        if (et_fullName.getText().toString().equals("")) {
            Toast.makeText(activity, "Please enter name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_fullName.getText().toString().length() < 3) {
            Toast.makeText(activity, "Name length should be greater than 3 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
            Toast.makeText(activity, "Please enter valid email id", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (et_numbr.getText().toString().equals("")) {
            Toast.makeText(activity, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (et_numbr.getText().toString().length() != 11) {
            Toast.makeText(activity, "Mobile number should have 11 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        /*if (country_code.getText().toString().equals("+ 55")) {
            Toast.makeText(activity, "Please select country code", Toast.LENGTH_SHORT).show();
            return false;
        }*/


        if (activity.status != 0 && preference.getString(Constants.FB_ID, "").equals("")) {
            if (et_passwrd.getText().toString().equals("")) {
                Toast.makeText(activity, "Please enter password", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (et_passwrd.getText().toString().length() < 8) {
                Toast.makeText(activity, "Password must contains 8 characters", Toast.LENGTH_SHORT).show();
                return false;

            }
        }

        return true;
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {
        activity.progress.dismiss();
        if (result != null && !result.equalsIgnoreCase("")) {
            try {
                Utils.showToast(getActivity(),new JSONObject(result).getString("message"));
                //startActivity(new Intent(activity, MoveActivity.class));
               /// getActivity().finishAffinity();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (status) {
                case 200:
                    JSONObject object1 = null;
                    try {
                        object1 = new JSONObject(result);
                        JSONObject object = object1.getJSONObject("result");
                        preference.putBoolean(Constants.SESSION, true);
                        preference.putString(Constants.ACCESS_TOKEN, object.optString("accessToken"));
                        preference.putString(Constants.USERID, object.optString("userId"));
                        preference.putString(Constants.USER_MAIL, object.optString("email"));
                        preference.putString(Constants.USER_NAME, object.optString("name"));
                        preference.putString(Constants.COUNTYR_CODE, object.optString("countryCode"));
                        UtilPreferences.saveToPrefs(getActivity(),UtilPreferences.ACCESS_TOKEN,
                                object.optString("accessToken"));
                        ///preference.putString(Constants.COUNTYR_CODE, countryCode);
                        preference.putString(Constants.USER_CONTACT, object.optString("contact"));
                        preference.putString(Constants.USER_PIC, object.optString("profilePic"));
                        /// preference.putString(Constants.);
                        if (UtilPreferences.getFromPrefs(getActivity(),
                                UtilPreferences.FROM_CART_DIALOG, "").equals("2")) {
//                            startActivity(new Intent(activity, HomeActivity.class));
                            ((LoginSignUpActivity) getActivity()).callHomeApi();
                        } else {
                            startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();
                        }
                        //startActivity(new Intent(activity, MoveActivity.class));

                        activity.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 201:
                    try {
                        object1 = new JSONObject(result);
                        JSONObject object = object1.getJSONObject("result");
                        preference.putBoolean(Constants.SESSION, true);
                        preference.putBoolean(Constants.SESSION, true);
                        preference.putString(Constants.ACCESS_TOKEN, object.optString("accessToken"));
                        UtilPreferences.saveToPrefs(getActivity(),UtilPreferences.ACCESS_TOKEN,
                                object.optString("accessToken"));
                        preference.putString(Constants.USERID, object.optString("userId"));
                        preference.putString(Constants.USER_MAIL, object.optString("email"));
                        preference.putString(Constants.USER_NAME, object.optString("name"));
                        //// preference.putString(Constants.COUNTYR_CODE, countryCode);
                        preference.putString(Constants.COUNTYR_CODE, object.optString("countryCode"));
                        preference.putString(Constants.USER_CONTACT, object.optString("contact"));
                        preference.putString(Constants.USER_PIC, object.optString("profilePic"));


                        if (UtilPreferences.getFromPrefs(getActivity(),
                                UtilPreferences.FROM_CART_DIALOG, "").equals("2")) {
//                            startActivity(new Intent(activity, HomeActivity.class));
                            ((LoginSignUpActivity) getActivity()).callHomeApi();
                        } else {
                            startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();
                        }

//                        if (UtilPreferences.getFromPrefs(getActivity(),
//                                UtilPreferences.FROM_CART_DIALOG, "").equals("2")) {
////                            startActivity(new Intent(activity, HomeActivity.class));
//                            ((LoginSignUpActivity) getActivity()).callHomeApi();
//
//                        } else {
//                            startActivity(new Intent(activity, MoveActivity.class));
//                            getActivity().finishAffinity();
////                            ((LoginSignUpActivity)getActivity()).callHomeApi();
//                        }
                       // startActivity(new Intent(activity, MoveActivity.class));

                        activity.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 204:
                    break;
                case 400:
                    try {
                        JSONObject obj = new JSONObject(result);

                        if (obj.optString("message").contains("required")) {
                            if (obj.getString("message").equals("Contact number is required")) {
                                et_email.setText(preference.getString(Constants.FB_EMAIL, "") + "");
                                et_fullName.setText(preference.getString(Constants.FB_NAME, "") + "");
                                et_fullName.setEnabled(true);
                                et_email.setEnabled(false);

                            } else {
                                String fbContact = preference.getString(Constants.FB_CONTACT, "");
                                if (fbContact.isEmpty()) {
                                    et_numbr.setText("");
                                    et_numbr.setEnabled(true);
                                } else {
                                    et_numbr.setText(fbContact);
                                    et_numbr.setEnabled(false);
                                }
                                et_fullName.setText(preference.getString(Constants.FB_NAME, "") + "");
                                et_fullName.setEnabled(true);
                                et_email.setEnabled(true);
                            }
                            if (!preference.getString(Constants.FB_ID, "").isEmpty()) {
                                et_passwrd.setVisibility(View.GONE);
                                ((TextView) view.findViewById(R.id.rtretre)).setVisibility(View.GONE);
                                ((View) view.findViewById(R.id.gfsfsrwert)).setVisibility(View.GONE);
                                ((View) view.findViewById(R.id.gfsrert)).setVisibility(View.GONE);
                                //setFilds();
                            }
                        } else {
                            Toast.makeText(activity, obj.optString("message"), Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(activity, MoveActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 401:
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.optString("message").contains("required")) {
//                            int activtiyStatus = activity.status;
//                            if (activtiyStatus == 1) {
//                                et_passwrd.setEnabled(true);
//                                Toast.makeText(activity, obj.optString("message"), Toast.LENGTH_SHORT).show();
//                            } else {
//                                activity.status=0;
                            et_fullName.setEnabled(true);
                            et_passwrd.setEnabled(false);
                            setFilds();
//                            }
                        } else {
                            Toast.makeText(activity, obj.optString("message"), Toast.LENGTH_SHORT).show();
                            ///gggg
                           //// startActivity(new Intent(activity, MoveActivity.class));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {
        activity.progress.dismiss();

    }
}