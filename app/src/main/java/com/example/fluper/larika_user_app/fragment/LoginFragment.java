package com.example.fluper.larika_user_app.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment implements NetworkCallback {
    private View view;
    private EditText et_email, et_passwrd;
    private TextView tv_facebook, forgot_password;
    private LoginSignUpActivity activity;
    private SharedPreference prefrence;
    int tempStatus;
    private RelativeLayout relativeLayout;
    PopupWindow pw;
    Context context;
    String email;
    Activity activiti;
    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        activity = (LoginSignUpActivity) getActivity();
        prefrence = SharedPreference.getInstance(activity);

        et_email = (EditText) view.findViewById(R.id.et_email);
        et_passwrd = (EditText) view.findViewById(R.id.et_passwrd);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relatively);
        forgot_password = (TextView) view.findViewById(R.id.forgot_password);
        setupUI(relativeLayout);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.popupwindows);

                LinearLayout forgot_button = (LinearLayout) dialog.findViewById(R.id.forgot_button);
                final EditText edit_emailid = (EditText) dialog.findViewById(R.id.edit_emailid);

                forgot_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        email=edit_emailid.getText().toString();

                        if (edit_emailid.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Please enter email id", Toast.LENGTH_SHORT).show();
                        }else {
                            forgotpassword();
                        }



                    }

                });

                dialog.show();

            }



        });

     ///et_passwrd.setEnabled(false);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        view.findViewById(R.id.tv_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(activity)) {
                    Utils.hideSoftKeyBoard(activity);
                    activity.loginFacebook();
                } else {
                    Toast.makeText(activity, Constants.ERR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyBoard(activity);
                validationField();
//                if (validateField()) {
//                    if (Utils.isNetworkConnected(activity)) {
//
//                        if (activity.status == 0) {
//                            tempStatus = 0;
//                        } else {
//                            tempStatus = 1;
//                        }
//                        callSignInApi(activity.latitude, activity.longitude);
//                    } else {
//                        Toast.makeText(activity, Constants.ERR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();
//                    }
//                }

            }
        });


        return view;
    }

    private void forgotpassword( ) {
        activity.progress.show();

        /*JsonObject object = new JsonObject();
        object.addProperty("email",email);

        NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "forgetPassword");
        thread.getNetworkResponse(activity, object, 15000);*/

        JsonObject object = new JsonObject();

        object.addProperty("email",email);

        Ion.with(getActivity()).load("POST", Constants.BASE_URL + "forgetPassword")
                //.setHeader("accessToken", UtilPreferences.getFromPrefs(context,UtilPreferences.ACCESS_TOKEN,""))
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                activity.progress.dismiss();
                String response;
                if(result!=null)
                {

                    try {
                        JSONObject jsonObject=new JSONObject(result.getResult());
                        Utils.showToast(getActivity(),jsonObject.getString("message"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
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

    private void validationField() {
        if (et_email.getText().toString().trim().length() > 0) {
            if (Utils.validateEmail(et_email.getText().toString())) {
                if (et_passwrd.getText().toString().trim().length() > 0) {
                    if (Utils.isNetworkConnected(activity)) {
                        if (activity.status == 0) {
                            tempStatus = 0;
                        } else {
                            tempStatus = 1;
                        }
                        callSignInApi(activity.latitude, activity.longitude);
                    } else {
                        Toast.makeText(activity, Constants.ERR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utils.showToast(getActivity(), "Please enter password");
                }
            } else {
                Utils.showToast(getActivity(), "Invalid email");
            }

        } else {
            Utils.showToast(getActivity(), "Please enter email id");
        }
    }

    public void callSignInApi(Double latitude, Double longitude) {
        activity.progress.show();
        JsonObject object = new JsonObject();
        if (tempStatus == 1) {
            object.addProperty("email", et_email.getText().toString());
            object.addProperty("password", et_passwrd.getText().toString());
            object.addProperty("lat", latitude);
            object.addProperty("lng", longitude);
            object.addProperty("deviceType", 2);
            object.addProperty("type", "user");
            object.addProperty("deviceToken", FirebaseInstanceId.getInstance().getToken());
            NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "login");
            thread.getNetworkResponse(activity, object, 10000);
        } else {
            prefrence.putString(Constants.FB_EMAIL, et_email.getText().toString());
            object.addProperty("email", prefrence.getString(Constants.FB_EMAIL, ""));
            object.addProperty("idFacebook", prefrence.getString(Constants.FB_ID, ""));
            object.addProperty("password", "");
            object.addProperty("name", prefrence.getString(Constants.FB_NAME, ""));
            object.addProperty("type", "user");
            object.addProperty("imageUrl", prefrence.getString(Constants.FB_PICTURE, ""));
            object.addProperty("contact", "");
            object.addProperty("type", "user");
            object.addProperty("lat", latitude);
            object.addProperty("lng", latitude);
            object.addProperty("deviceType", 2);
            object.addProperty("deviceToken", FirebaseInstanceId.getInstance().getToken());
            NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "register");
            thread.getNetworkResponse(activity, object, 10000);
        }

    }

    private boolean validateField() {

        if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
            Toast.makeText(activity, "Please enter valid email id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tempStatus != 0) {
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

                if(!new JSONObject(result).getString("message").contains("required")){
                    Utils.showToast(getActivity(),new JSONObject(result).getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (status) {
                case 200:
                    JSONObject object1 = null;
                    try {
                        object1 = new JSONObject(result);
                        JSONObject object = object1.getJSONObject("result");
                        prefrence.putBoolean(Constants.SESSION, true);
                        UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.ACCESS_TOKEN, object.optString("accessToken"));
                        prefrence.putString(Constants.ACCESS_TOKEN, object.optString("accessToken"));
                        prefrence.putString(Constants.USERID, object.optString("userId"));
                        prefrence.putString(Constants.USER_MAIL, object.optString("email"));
                        prefrence.putString(Constants.USER_NAME, object.optString("name"));
                        prefrence.putString(Constants.COUNTYR_CODE, object.optString("countryCode"));
                        prefrence.putString(Constants.USER_CONTACT, object.optString("contact"));
                        prefrence.putString(Constants.USER_PIC, object.optString("profilePic"));

                       // activity.onBackPressed();

                        if (UtilPreferences.getFromPrefs(getActivity(), UtilPreferences.FROM_CART_DIALOG, "").equals("2")) {
                            ((LoginSignUpActivity) getActivity()).callHomeApi();

                        } else {
                            startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();
                        }


                      /*  if (UtilPreferences.getFromPrefs(getActivity(), UtilPreferences.FROM_CART_DIALOG, "").equals("3")) {
                            Intent intent=new Intent (getActivity(), MenuScreenActivity.class);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();
                        }*/


//                        activity.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 201:
                    try {
                        object1 = new JSONObject(result);
                        JSONObject object = object1.getJSONObject("result");
                        prefrence.putBoolean(Constants.SESSION, true);
                        UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.ACCESS_TOKEN, object.optString("accessToken"));
                        prefrence.putString(Constants.ACCESS_TOKEN, object.optString("accessToken"));
                        prefrence.putString(Constants.USERID, object.optString("userId"));
                        prefrence.putString(Constants.USER_MAIL, object.optString("email"));
                        prefrence.putString(Constants.USER_NAME, object.optString("name"));
                        prefrence.putString(Constants.COUNTYR_CODE, object.optString("countryCode"));
                        prefrence.putString(Constants.USER_CONTACT, object.optString("contact"));
                        prefrence.putString(Constants.USER_PIC, object.optString("profilePic"));

                       // activity.onBackPressed();


                        if (UtilPreferences.getFromPrefs(getActivity(), UtilPreferences.FROM_CART_DIALOG, "").equals("2")) {
                            //activity.setResult(111);
//                            startActivity(new Intent(activity, HomeActivity.class));
                            ((LoginSignUpActivity) getActivity()).callHomeApi();
                        } else
                        {
                            startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();
                        }

                       /* if (UtilPreferences.getFromPrefs(getActivity(), UtilPreferences.FROM_CART_DIALOG, "").equals("3")) {
                            Intent intent=new Intent (getActivity(), MenuScreenActivity.class);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();
                        }*/


                            /*startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();*/


                      /*  if(UtilPreferences.getFromPrefs(getActivity(),UtilPreferences.FROM_CART_DIALOG_menu_screen,"").equals("3")){
                            //startActivity(new Intent(activity, MenuScreenActivity.class));
                            Intent intent=new Intent (getActivity(), MenuScreenActivity.class);
                            startActivity(intent);
                        }
                        else {
                            startActivity(new Intent(activity, MoveActivity.class));
                            getActivity().finishAffinity();
                        }*/



//                        activity.finish();
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
                            ((LoginSignUpActivity)getActivity()).openSignUpFragment();
                            Toast.makeText(activity, "Please Sign up first", Toast.LENGTH_SHORT).show();
                            break;

                        }
                   //     Toast.makeText(activity, obj.optString("message"), Toast.LENGTH_SHORT).show();
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
        activity.progress.dismiss();

    }

    private void initiatePopupWindow() {
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popupwindows,
                    null);

            pw = new PopupWindow(layout, 600, 800, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            //TextView tv_forgot=(TextView) layout.findViewById(R.id.tv_forgot);
            // EditText edit_forgot=(EditText) layout.findViewById(R.id.edit_forgot);
            //RelativeLayout hidrkeyword=(RelativeLayout)layout.findViewById(R.id.layoutpopup);
            //hidrkeyword.setOnClickListener(new View.OnClickListener() {
                /*@Override
                public void onClick(View view) {

                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);



                }
            });*/
            /*tv_forgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
