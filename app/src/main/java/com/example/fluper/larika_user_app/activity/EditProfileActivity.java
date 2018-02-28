package com.example.fluper.larika_user_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.ActivityEditProfileBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.LocationManager;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.ToastMessage;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.fluper.larika_user_app.R.id.et_Lpasswrd;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, NetworkCallback {
    private SharedPreference preference;
    ActivityEditProfileBinding activityEditProfileBinding;
    private LocationManager manager;
    public Double latitude, longitude;
    Progress progress;
    private RelativeLayout ly_mpssLayout;
    private String countryCode;
    EditText ett_Lpasswrd;
    TextView country_code;
    private RelativeLayout rl_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        preference = SharedPreference.getInstance(this);
        getLatLng();
        listener();
        country_code=(TextView)findViewById(R.id.country_code);

        ett_Lpasswrd=(EditText)findViewById(R.id.et_Lpasswrd);
        rl_parent=(RelativeLayout)findViewById(R.id.edtparent);
        setupUI(rl_parent);


       // activityEditProfileBinding.countryCode.
        activityEditProfileBinding.etFullName.setText(preference.getString(Constants.USER_NAME, ""));
        activityEditProfileBinding.etEmail.setText(preference.getString(Constants.USER_MAIL, ""));
        if (!preference.getString(Constants.USER_CONTACT, "").equals("null"))

        {
            country_code.setText(preference.getString(Constants.COUNTYR_CODE, ""));
            activityEditProfileBinding.etNumbr.setText(preference.getString(Constants.USER_CONTACT, ""));
        }

       /* else
            {
                activityEditProfileBinding.etNumbr.setText(preference.getString(Constants.USER_CONTACT, ""));

        }*/
        country_code.setOnClickListener(new View.OnClickListener() {
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
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");

            }


        });

        progress = new Progress(this);
        ly_mpssLayout = (RelativeLayout) findViewById(R.id.ly_mpssLayout);
        if (!preference.getString(Constants.FB_ID, "").isEmpty()) {
            ly_mpssLayout.setVisibility(View.GONE);
            //ly_mpssLayout.setEnabled(false);
           // ett_Lpasswrd.setEnabled(false);
        }
        country_code.setText("+" +"55");
    }

    private String GetCountryPhoneCode(String code) {

        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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


    private void getLatLng() {
        manager = LocationManager.getInstance(this).buildAndConnectClient()
                .buildLocationRequest().setLocationHandlerListener(new LocationManager.LocationHandlerListener() {
                    @Override
                    public void locationChanged(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            manager.stopTracking();
                        }
                    }

                    @Override
                    public void lastKnownLocationAfterConnection(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }
                });
        manager.requestLocation();
    }

    private void listener() {
        activityEditProfileBinding.tvSignUp.setOnClickListener(this);
        findViewById(R.id.iv_cross).setOnClickListener(this);
        activityEditProfileBinding.etLpasswrd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        switch (view.getId()) {
            case R.id.tv_signUp:
                checkAllValidation();
                break;
            case R.id.iv_cross:

                activityEditProfileBinding.ivCross.setAlpha(1f);
                activityEditProfileBinding.ivCross.setAnimation(animation1);
                finish();
                //startActivity(new Intent(EditProfileActivity.this,AccountActivity.class));
                overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);

                break;
            case et_Lpasswrd:
                startActivity(new Intent(EditProfileActivity.this, ChangePasswordActivity.class));

                break;
        }

    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(EditProfileActivity.this);
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





    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void checkAllValidation() {
        if (activityEditProfileBinding.etFullName.getText().toString().trim().length() > 0) {
            if (activityEditProfileBinding.etFullName.getText().toString().trim().length() > 2) {
                if (activityEditProfileBinding.etEmail.getText().toString().trim().length() > 0) {
                    if (Utils.validateEmail(activityEditProfileBinding.etEmail.getText().toString())) {
                        if (activityEditProfileBinding.etNumbr.getText().toString().trim().length() > 0) {
                            if (activityEditProfileBinding.etNumbr.getText().toString().length() ==11) {
                                updateprofileApi();
                            } else {
                                Utils.showToast(EditProfileActivity.this, ToastMessage.phoneNumberLimit);
                            }

                        } else {
                            Utils.showToast(EditProfileActivity.this, ToastMessage.empltyPhoneNumber);
                        }

                    } else {
                        Utils.showToast(EditProfileActivity.this, ToastMessage.invalidEmail);
                    }

                } else {
                    Utils.showToast(EditProfileActivity.this, ToastMessage.emptyEmailId);
                }

            } else {
                Utils.showToast(EditProfileActivity.this, ToastMessage.fullNameLimit);
            }
        } else {
            Utils.showToast(EditProfileActivity.this, ToastMessage.emptyFullName);
        }


    }

    private void updateprofileApi() {

        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();
        object.addProperty("email", activityEditProfileBinding.etEmail.getText().toString());
        object.addProperty("name", activityEditProfileBinding.etFullName.getText().toString());
        object.addProperty("contact", activityEditProfileBinding.etNumbr.getText().toString());
        object.addProperty("countryCode", country_code.getText().toString());
        object.addProperty("lat", latitude.toString());
        object.addProperty("lng", longitude.toString());
        object.addProperty("type", "user");
        object.addProperty("imageUrl", "");
        object.addProperty("deviceToken", FirebaseInstanceId.getInstance().getToken());

//        NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "register");
//        thread.getNetworkResponse(this, object, 60000);

        Ion.with(this).load("PUT", Constants.BASE_URL + "register")
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
                        try {

                            JSONObject mainObject = new JSONObject(responce);
                            JSONObject resultObject = mainObject.getJSONObject("result");
                            preference.putString(Constants.ACCESS_TOKEN, resultObject.optString("accessToken"));
                            preference.putString(Constants.USERID, resultObject.optString("userId"));
                            preference.putString(Constants.USER_MAIL, resultObject.optString("email"));
                            preference.putString(Constants.USER_NAME, resultObject.optString("name"));
                            preference.putString(Constants.USER_CONTACT, resultObject.optString("contact"));
                            preference.putString(Constants.USER_PIC, resultObject.optString("profilePic"));
                            preference.putString(Constants.COUNTYR_CODE,resultObject.optString("countryCode"));
                            Toast.makeText(EditProfileActivity.this,mainObject.optString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        finish();

                        break;
                    case 201:
                        responce = result.getResult();
                        try {
                            JSONObject mainObject = new JSONObject(responce);
                            JSONObject resultObject = mainObject.getJSONObject("result");
                            preference.putString(Constants.ACCESS_TOKEN, resultObject.optString("accessToken"));
                            preference.putString(Constants.USERID, resultObject.optString("userId"));
                            preference.putString(Constants.USER_MAIL, resultObject.optString("email"));
                            preference.putString(Constants.USER_NAME, resultObject.optString("name"));
                            preference.putString(Constants.USER_CONTACT, resultObject.optString("contact"));
                            preference.putString(Constants.USER_PIC, resultObject.optString("profilePic"));
                            Toast.makeText(EditProfileActivity.this,mainObject.optString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        finish();
                        break;

                    case 204:
                        responce = result.getResult();

                        break;
                    case 400:
                        responce = result.getResult();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(responce);
                            Toast.makeText(EditProfileActivity.this, obj.optString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case 401:
                        responce = result.getResult();
                        break;

                    default:
                        responce = result.getResult();
                        break;
                }



            }
        });


    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {
        progress.dismiss();
        if (result != null && !result.equalsIgnoreCase("")) {
            switch (status) {
                case 200:
                    try {
                        JSONObject mainObject = new JSONObject(result);
                        JSONObject resultObject = mainObject.getJSONObject("result");
                        preference.putString(Constants.ACCESS_TOKEN, resultObject.optString("accessToken"));
                        preference.putString(Constants.USERID, resultObject.optString("userId"));
                        preference.putString(Constants.USER_MAIL, resultObject.optString("email"));
                        preference.putString(Constants.USER_NAME, resultObject.optString("name"));
                        preference.putString(Constants.USER_CONTACT, resultObject.optString("contact"));
                        preference.putString(Constants.USER_PIC, resultObject.optString("profilePic"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    finish();
                    break;
                case 201:
                    try {
                        JSONObject mainObject = new JSONObject(result);
                        JSONObject resultObject = mainObject.getJSONObject("result");
                        preference.putString(Constants.ACCESS_TOKEN, resultObject.optString("accessToken"));
                        preference.putString(Constants.USERID, resultObject.optString("userId"));
                        preference.putString(Constants.USER_MAIL, resultObject.optString("email"));
                        preference.putString(Constants.USER_NAME, resultObject.optString("name"));
                        preference.putString(Constants.USER_CONTACT, resultObject.optString("contact"));
                        preference.putString(Constants.USER_PIC, resultObject.optString("profilePic"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    finish();
                    break;
                case 204:
                    break;
                case 400:
                    try {
                        JSONObject obj = new JSONObject(result);
//                        if (obj.optString("message").contains("required")) {
////                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment()).commit();
//                            Toast.makeText(this, "Please Sign up first", Toast.LENGTH_SHORT).show();
//                            break;
//
//                        }
                        Toast.makeText(this, obj.optString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 401:
                    break;
            }

        }

    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {
        progress.dismiss();
        Toast.makeText(this, Constants.ERR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();

    }
}
