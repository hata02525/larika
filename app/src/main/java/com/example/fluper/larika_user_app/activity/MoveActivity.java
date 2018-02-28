package com.example.fluper.larika_user_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.fluper.larika_user_app.Network.NetworkThread;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.adapter.PaymentAdapter;
import com.example.fluper.larika_user_app.bean.AddPaymentCardModel;
import com.example.fluper.larika_user_app.bean.HomeDataBean;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.bean.PaymentResponseMainModel;
import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.AppSharedPrefernces;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.LocationManager;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoveActivity extends AppCompatActivity implements View.OnClickListener,
        NetworkCallback {
    private TextView tv_order;
    RelativeLayout rl_fadeLayout;
    Animation animFadein;
    Animation animTransition;
    VideoView videoView;

    private LocationManager manager;
    public Double latitude=0.0, longitude=0.0;
    private TextView tv_mapView;
    private SharedPreferences sharedPrefs;
    private Boolean tempLocationBoolean;
    private PaymentCardMainModel paymentCardMainModel;
    private PaymentAdapter paymentAdapter;
    private Progress progress;
    private RelativeLayout rlv_order_confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        AppSharedPrefernces.getsharedprefInstance(this).setSession(true);
        init();
        listener();
        videoListener();
        loadLogoAnimation();
        UtilPreferences.saveToPrefs(MoveActivity.this,UtilPreferences.UNDELETE_PRODUCT,"1");
        String localLatitude=UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LATITUDE,"");
        String localLongitude=UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LONGITUDE,"");
        if(!localLatitude.equals("CURRENT_LATITUDE") && !localLatitude.equals(""))
        {
            latitude= Double.valueOf(UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LATITUDE,""));
            longitude= Double.valueOf(UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LONGITUDE,""));

        }
        else
        {
            longitude=0.0;
            latitude=0.0;
        }
        if(latitude!=0.0 && longitude!=0.0)
        {
            showUserCurrentAddess();
        }
        else
        {
//            setLocationManager();
            new LocationManagerAsynTask().execute();
        }

       /* if (getIntent().hasExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG)
                && getIntent().getStringExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG).equals("1")) {
            openConfirmOrderDialog();

        }*/



    }



    class LocationManagerAsynTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            setLocationManager();
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progress.isShowing())
        {
            progress.dismiss();
        }
    }

    class GetCardDetailsAsyn extends AsyncTask<Void,Void,Integer>
    {
        Progress progress;
        @Override
        protected void onPreExecute() {

            progress=new Progress(MoveActivity.this);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            super.onPreExecute();
//            progress = ProgressDialog.show(MoveActivity.this,
//                    "Loading", "PleaseWait", true);

        }

        @Override
        protected Integer doInBackground(Void... voids) {

            carddetails();

            return 1;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            progress.dismiss();
            super.onPostExecute(aVoid);

//            progress.dismiss();
//            if(aVoid==1)
//            {
//                progress.dismiss();
//            }
//            if(progress.isShowing())
//                progress.dismiss();
        }
    }
    private void closeConfirmOrderDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_downn);

        rlv_order_confirmation.startAnimation(bottomDown);


        Utils.hideSoftKeyBoard(MoveActivity.this);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                rlv_order_confirmation.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void openConfirmOrderDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_upp);

        rlv_order_confirmation.startAnimation(bottomDown);
        rlv_order_confirmation.setClickable(true);
        rlv_order_confirmation.setVisibility(View.VISIBLE);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                rlv_order_confirmation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void setLocationManager() {
        manager = LocationManager.getInstance(this).buildAndConnectClient()
                .buildLocationRequest().setLocationHandlerListener(new LocationManager.LocationHandlerListener() {
                    @Override
                    public void locationChanged(Location location) {
                        if (location != null) {
                            progress.show();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            UtilPreferences.saveToPrefs(MoveActivity.this,UtilPreferences.CURRENT_LATITUDE,
                                    String.valueOf(latitude));
                            UtilPreferences.saveToPrefs(MoveActivity.this,UtilPreferences.CURRENT_LONGITUDE,
                                    String.valueOf(longitude));
                            tempLocationBoolean = true;
                            manager.stopTracking();
                            showUserCurrentAddess();
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

    private void videoListener() {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.bg_vdo;
        videoView.setVideoURI(Uri.parse(path));
        playBackgroundVdo();
    }

    private void listener() {
        findViewById(R.id.iv_home).setOnClickListener(this);
        findViewById(R.id.tv_order).setOnClickListener(this);
        findViewById(R.id.tv_mapView).setOnClickListener(this);
        rlv_order_confirmation.setOnClickListener(this);
    }

    private void init() {
        progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);


        rl_fadeLayout = (RelativeLayout) findViewById(R.id.rl_fadeLayout);
        videoView = (VideoView) findViewById(R.id.videoView);
        tv_mapView = (TextView) findViewById(R.id.tv_mapView);
        rlv_order_confirmation = (RelativeLayout) findViewById(R.id.rl_order_confirmation);

    }

    private void showUserCurrentAddess() {
//        if (!tempLocationBoolean) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String subThroughFare=addresses.get(0).getSubThoroughfare();
            String subLocality=addresses.get(0).getSubLocality();
            String locality=addresses.get(0).getLocality();
            String adminArea=addresses.get(0).getAdminArea();
            String countryName=addresses.get(0).getCountryName();
            if(subThroughFare==null)
            {
                subThroughFare="";
            }
            else
            {
                subThroughFare=subThroughFare+", ";
            }
             if(subLocality==null)
            {
                subLocality="";
            }
            else
             {
                 subLocality=subLocality+" ";
             }
             if(locality==null)
            {
                locality="";
            }
            else
             {
                 locality=locality+" ";
             }
             if(adminArea==null)
            {
                adminArea="";
            }
            else
             {
                 adminArea=adminArea+" ";
             }
            if(countryName==null)
            {
                countryName="";
            }

            tv_mapView.setText(subThroughFare+subLocality);
            progress.dismiss();
           //// tv_mapView.setText(subThroughFare+subLocality+locality+adminArea+countryName);

        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
//            }
        }
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.setLooping(true);
//            }
//        });
    }

    private void loadLogoAnimation() {
        animFadein = AnimationUtils.loadAnimation(this,
                R.anim.fade_in);
        animTransition = AnimationUtils.loadAnimation(this, R.anim.move_up_transition);
        rl_fadeLayout.startAnimation(animTransition);

    }

    private void playBackgroundVdo() {
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });


    }


    @Override
    protected void onResume() {

        String address = UtilPreferences.getFromPrefs(MoveActivity.this, UtilPreferences.MOVE_ADDRESS,"");
        String localLatitude=UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LATITUDE,"");
        String localLongitude=UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LONGITUDE,"");
        if(!localLatitude.equals("CURRENT_LATITUDE") && !localLatitude.equals(""))
        {
            latitude= Double.valueOf(UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LATITUDE,""));
            longitude= Double.valueOf(UtilPreferences.getFromPrefs(MoveActivity.this,UtilPreferences.CURRENT_LONGITUDE,""));

        }
        else
        {
            longitude=0.0;
            latitude=0.0;
        }
        if(latitude!=0.0 && longitude!=0.0)
        {
            showUserCurrentAddess();
        }
        playBackgroundVdo();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_home:
                Intent intent = new Intent(MoveActivity.this, MenuScreenActivity.class);
                intent.putExtra("finder", "0");
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_up, R.anim.fade_menu);
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);

//                finishAffinity();
                break;
            case R.id.tv_order:

//                new GetCardDetailsAsyn().execute();
                if(checkLoginOrNot()){
                    carddetails();
                }else{
                   callHomeApi();
                }


                break;
            case R.id.tv_mapView:
                startActivity(new Intent(MoveActivity.this, MapActivity.class));
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);

                ///overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                break;
            case R.id.rl_order_confirmation:
                closeConfirmOrderDialog();
                break;
        }

    }

    private void carddetails() {
        progress.show();
        Ion.with(this).load("POST",Constants.BASE_URL + "getAllCard")
                    .setHeader("accessToken",
                            UtilPreferences.getFromPrefs(MoveActivity.this,
                                    UtilPreferences.ACCESS_TOKEN,""))
                    .asString().withResponse().
                    setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            progress.dismiss();

                            String responce = "";


                            if(result!=null)
                            {
                                try
                                {
                                    Intent intent=null;

                                    switch (result.getHeaders().code()) {
                                        case 200:
                                            PaymentResponseMainModel
                                                    paymentResponseMainModel=Utils.
                                                    getgsonInstance().
                                                    fromJson(result.getResult(),
                                                            PaymentResponseMainModel.class);
                                            paymentCardMainModel=new PaymentCardMainModel();

                                            for (int i = 0; i < paymentResponseMainModel.getResult().size(); i++) {
                                                AddPaymentCardModel addCardModel=new AddPaymentCardModel();
                                                addCardModel.setCardNumber(paymentResponseMainModel.getResult().get(i).getNumber());
                                                addCardModel.setCardExpiry(paymentResponseMainModel.getResult().get(i).getExpire_month()
                                                        +"-"+paymentResponseMainModel.getResult().get(i).getExpire_year());
                                                addCardModel.setCardUserName(paymentResponseMainModel.getResult().get(i).getFirst_name()+
                                                        " "+paymentResponseMainModel.getResult().get(i).getLast_name());
                                                addCardModel.setCardCvv(paymentResponseMainModel.getResult().get(i).getCvv2());
                                                addCardModel.setCardId(paymentResponseMainModel.getResult().get(i).getId());

                                                paymentCardMainModel.getAddCardModelList().add(addCardModel);
                                            }



                                            if(paymentCardMainModel !=null && paymentCardMainModel.getAddCardModelList().size()>0)
                                            {
                                                paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
                                            }
                                            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(paymentCardMainModel);
                                            prefsEditor.putString("MyObject", json);
                                            prefsEditor.commit();
                                            if (latitude != null && longitude != null) {
                                                if (Utils.isNetworkConnected(MoveActivity.this)) {
                                                    callHomeApi();

                                                } else {
                                                    Toast.makeText(MoveActivity.this,
                                                            Constants.ERR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();
                                                }
                                            }
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

                                }catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }

                            }



                        }
                    });


        }

    private boolean checkLoginOrNot() {
        boolean isLogin=false;
       SharedPreference prefrence = SharedPreference.getInstance(this);
        if (!prefrence.getString(Constants.ACCESS_TOKEN, "").equals("token")
                && !prefrence.getString(Constants.ACCESS_TOKEN, "").equals("")) {
            isLogin = true;

        } else {
            isLogin = false;

        }
        return isLogin;
    }

    private void callHomeApi() {
        progress.show();

        JsonObject object = new JsonObject();
        try {


            latitude = Double.valueOf(UtilPreferences.getFromPrefs(MoveActivity.this, UtilPreferences.CURRENT_LATITUDE, ""));
            longitude = Double.valueOf(UtilPreferences.getFromPrefs(MoveActivity.this, UtilPreferences.CURRENT_LONGITUDE, ""));
        }catch(Exception ex){

        }

        object.addProperty("lat", latitude);
        object.addProperty("lng", longitude);

        NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "homeScreen");
        thread.getNetworkResponse(this, object, 10000);

    }


    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {
        progress.dismiss();

        if (result != null) {
            try {

                ArrayList<HomeDataBean> list = new ArrayList<>();
                JSONObject object = new JSONObject(result);
                Utils.showToast(MoveActivity.this,new JSONObject(result).getString("message"));
                JSONArray array = object.getJSONArray("result");
                if(array.length()>0)
                {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject resultObject = array.getJSONObject(i);
                        HomeDataBean bean = new HomeDataBean();
                        bean.dishId = resultObject.optString("dishId");
                        bean.vendorId = resultObject.optString("vendorId");
                        bean.dishName = resultObject.optString("dishName");
                        bean.dishTitle = resultObject.optString("dishTitle");
                        bean.dishDesc = resultObject.optString("dishDesc");
                        bean.dishImage = resultObject.optString("dishImage");
                        bean.category=resultObject.optString("category");
                        bean.nature=resultObject.optString("nature");

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
                        Intent intent = new Intent(MoveActivity.this, HomeActivity.class);
                        intent.putExtra("homeDataList", list);
                        startActivity(intent);
//                    startActivity(new Intent(MoveActivity.this, HomeActivity.class));

                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
//                    finishAffinity();
                    }
                }
                else
                {
                    AlertDialog.Builder dialog =  new AlertDialog.Builder(MoveActivity.this);
                    dialog.setCancelable(false);
                    dialog
                            .setMessage(object.getString("message"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete

                                    dialog.dismiss();
                                }
                            })
                            .show();
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
}
