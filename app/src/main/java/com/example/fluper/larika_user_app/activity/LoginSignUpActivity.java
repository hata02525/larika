package com.example.fluper.larika_user_app.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.HomeDataBean;
import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.fragment.HomeItemFragment;
import com.example.fluper.larika_user_app.fragment.LoginFragment;
import com.example.fluper.larika_user_app.fragment.SignUpFragment;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.LocationManager;
import com.example.fluper.larika_user_app.utils.Progress;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginSignUpActivity extends AppCompatActivity implements View.OnClickListener,NetworkCallback {

    private TextView tv_signUp, tv_signIn,tv_skip;
    public Progress progress;
    private CallbackManager callbackManager;
    public int status = 1;
    SharedPreference preference;
    private LocationManager manager;
    public Double latitude, longitude;
    private String fromCartDialog="";
    HomeItemFragment homeItemFragment;
    boolean called=false;

   /* public LoginSignUpActivity(HomeItemFragment homeItemFragment) {
        this.homeItemFragment=homeItemFragment;
    }*/
    public LoginSignUpActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        FacebookSdk.sdkInitialize(this);

        preference = SharedPreference.getInstance(this);

        //getHashKey();

        progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        callbackManager = CallbackManager.Factory.create();

        tv_signIn = (TextView) findViewById(R.id.tv_signIn);
        tv_signUp = (TextView) findViewById(R.id.tv_signUp);
        tv_skip = (TextView) findViewById(R.id.tv_skip);

        getDataFromIntent();

        tv_signIn.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);
        tv_skip.setOnClickListener(this);
        ////getDataFromMenuScreen();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment()).commit();
        tv_signIn.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv_signUp.setTextColor(ContextCompat.getColor(this, R.color.black_grey_tint));

        facebookLogin();
        manager = LocationManager.getInstance(this).buildAndConnectClient().
                buildLocationRequest().setLocationHandlerListener(new LocationManager.LocationHandlerListener() {
            @Override
            public void locationChanged(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    UtilPreferences.saveToPrefs(LoginSignUpActivity.this,UtilPreferences.CURRENT_LATITUDE,
                            String.valueOf(latitude));
                    UtilPreferences.saveToPrefs(LoginSignUpActivity.this,UtilPreferences.CURRENT_LONGITUDE,
                            String.valueOf(longitude));
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

    private void getDataFromMenuScreen() {
        if(getIntent().getStringExtra(Constants.FROM_MENU_SCREEN_DIALOG)!=null
                && getIntent().getStringExtra(Constants.FROM_MENU_SCREEN_DIALOG).equals(Constants.FROM_MENU_SCREEN_DIALOG))
        {

            tv_skip.setVisibility(View.GONE);
        }
        else
        {
            tv_skip.setVisibility(View.VISIBLE);
        }
    }




    public void callHomeApi() {
//        addToCartApi();

        JsonObject object = new JsonObject();
        String localLatitude=UtilPreferences.getFromPrefs(LoginSignUpActivity.this,UtilPreferences.CURRENT_LATITUDE,"");
        String localLongitude=UtilPreferences.getFromPrefs(LoginSignUpActivity.this,UtilPreferences.CURRENT_LONGITUDE,"");
        if(!localLatitude.equals("CURRENT_LATITUDE") && !localLatitude.equals(""))
        {
            latitude= Double.valueOf(UtilPreferences.getFromPrefs(LoginSignUpActivity.this,UtilPreferences.CURRENT_LATITUDE,""));
            longitude= Double.valueOf(UtilPreferences.getFromPrefs(LoginSignUpActivity.this,UtilPreferences.CURRENT_LONGITUDE,""));

            object.addProperty("lat", latitude);
            object.addProperty("lng", longitude);

          /*  NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "homeScreen");
            thread.getNetworkResponse(this, object, 10000);*/
        }
        if(called==false&&HomeItemFragment.homeItemFragment!=null) {
            called = true;
            HomeItemFragment.homeItemFragment.loginCallback(true);
            finish();
        }

    }




    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {
        progress.dismiss();
        if (result != null) {
            try {
                ArrayList<HomeDataBean> list = new ArrayList<>();
                JSONObject object = new JSONObject(result);
                JSONArray array = object.getJSONArray("result");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject resultObject = array.getJSONObject(i);
                    HomeDataBean bean = new HomeDataBean();
                    bean.dishId = resultObject.optString("dishId");
                    bean.vendorId = resultObject.optString("vendorId");
                    bean.dishName = resultObject.optString("dishName");
                    bean.dishTitle = resultObject.optString("dishTitle");
                    bean.dishDesc = resultObject.optString("dishDesc");
                    bean.dishImage = resultObject.optString("dishImage");

                    if(resultObject.getString("rating").equals("null"))
                    {
                        bean.dishRating=Float.parseFloat("0.0");
                    }else
                    {
                        bean.dishRating = Float.parseFloat(resultObject.optString("rating"));
                    }

                    bean.dishStock = resultObject.optString("dishStock");
                    bean.dishSummry = resultObject.optString("dishSummary");
                    bean.dishPrice = resultObject.optString("dishPrice");
                    bean.venderName = resultObject.optString("vendorName");
                    list.add(bean);
                }
                if (list.size() > 0) {
                    Intent intent = new Intent(LoginSignUpActivity.this, HomeActivity.class);
                    intent.putExtra("homeDataList", list);
                    intent.putExtra("AddProduct","AddProduct");
                    UtilPreferences.saveToPrefs(LoginSignUpActivity.this,UtilPreferences.ADD_CART,"AddProduct");

                    startActivity(intent);
//                    startActivity(new Intent(MoveActivity.this, HomeActivity.class));

                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    finishAffinity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {

        progress.dismiss();

    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
    }

    private void facebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String id = object.optString("id");
                                    String fName = object.optString("first_name");
                                    String lName = object.optString("last_name");
                                    String email = "";
                                    String contact="";
                                    if (!object.optString("email").equals("") && object.optString("email") != null) {
                                        email = object.optString("email");
                                    }
                                    if (!object.optString("contact").equals("") && object.optString("contact") != null) {
                                        contact = object.optString("contact");
                                    }


                                    JSONObject picture = object.getJSONObject("picture").getJSONObject("data");
                                    String pictureUrl = picture.getString("url");

                                    if (email.equals("")) {
                                        status = 0;
                                        preference.putString(Constants.FB_NAME, fName + " " + lName);
                                        preference.putString(Constants.FB_ID, id);
                                        preference.putString(Constants.FB_EMAIL, "");
                                        preference.putString(Constants.FB_PICTURE, pictureUrl);

//                                        preference.putString(Constants.);

                                    } else {
                                        status = 0;
                                        preference.putString(Constants.FB_NAME, fName + " " + lName);
                                        preference.putString(Constants.FB_ID, id);
                                        preference.putString(Constants.FB_EMAIL, email);
                                        preference.putString(Constants.FB_PICTURE, pictureUrl);

                                    }


          /////phone no.
                                    if (contact.equals("")) {
                                        status = 0;
                                        preference.putString(Constants.FB_NAME, fName + " " + lName);
                                        preference.putString(Constants.FB_ID, id);
                                        preference.putString(Constants.FB_CONTACT, "");
                                        preference.putString(Constants.FB_PICTURE, pictureUrl);

//                                        preference.putString(Constants.);

                                    } else {
                                        status = 0;
                                        preference.putString(Constants.FB_NAME, fName + " " + lName);
                                        preference.putString(Constants.FB_ID, id);
                                        preference.putString(Constants.FB_CONTACT, contact);
                                        preference.putString(Constants.FB_PICTURE, pictureUrl);

                                    }


                                   /* if (email.equals("")) {
                                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment()).commit();
                                    } else {*/

                                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                                        if (fragment instanceof SignUpFragment) {
                                            SignUpFragment signUpFragment = (SignUpFragment) fragment;
                                            signUpFragment.callSignUpApi(latitude, longitude,"true");

                                        } else if (fragment instanceof LoginFragment) {
                                            LoginFragment loginFragment = (LoginFragment) fragment;
                                            loginFragment.callSignInApi(latitude, longitude);
                                        }


//                               }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,gender,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginSignUpActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                if (e instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                        loginFacebook();
                    }
                }else{
                    Toast.makeText(LoginSignUpActivity.this, "Problem connecting to Facebook", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }


    public void  loginFacebook() {
//        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }
    public void openSignUpFragment()
    {
        tv_signIn.setTextColor(ContextCompat.getColor(this, R.color.black_grey_tint));
        tv_signUp.setTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signIn:
                tv_signIn.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_signUp.setTextColor(ContextCompat.getColor(this, R.color.black_grey_tint));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment()).commit();
                break;
            case R.id.tv_signUp:
                tv_signIn.setTextColor(ContextCompat.getColor(this, R.color.black_grey_tint));
                tv_signUp.setTextColor(ContextCompat.getColor(this, R.color.white));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment()).commit();
                break;
            case R.id.tv_skip:
                startActivity(new Intent(LoginSignUpActivity.this,MoveActivity.class));
                //finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getDataFromIntent() {
        if(getIntent().getStringExtra(Constants.FROM_HOME_ITEM_DIALOG)!=null
                && getIntent().getStringExtra(Constants.FROM_HOME_ITEM_DIALOG).equals(Constants.FROM_HOME_ITEM_DIALOG))
        {

                tv_skip.setVisibility(View.GONE);

        }
        else
        {
            tv_skip.setVisibility(View.VISIBLE);
        }
    }

    /*public void getDataFromMenuScreen() {
        if(getIntent().getStringExtra(Constants.FROM_MENU_SCREEN_DIALOG)!=null
                && getIntent().getStringExtra(Constants.FROM_MENU_SCREEN_DIALOG).equals(Constants.FROM_MENU_SCREEN_DIALOG))
        {
            tv_skip.setVisibility(View.GONE);
        }
        else
        {
            tv_skip.setVisibility(View.VISIBLE);
        }
    }*/
}
