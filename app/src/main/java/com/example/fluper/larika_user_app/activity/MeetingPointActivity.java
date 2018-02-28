package com.example.fluper.larika_user_app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.adapter.MeetingPointTimeAdapter;
import com.example.fluper.larika_user_app.bean.MeetingPointMainModel;
import com.example.fluper.larika_user_app.bean.MeetingPointModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.LocationManager;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MeetingPointActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment map_frameLayout;
    private GoogleMap googleMap;
    private LocationManager manager;
    public Double latitude, longitude;
    private MeetingPointMainModel meetingPointMainModel;
    private Spinner spinner;
    private TextView tv_meet_time;
    private String cartId = "", total = "0", time_id = "", meetingPointId = "", cardid = "", paymentCardId = "";
    private SharedPreference prefrence;
    private LatLng latLng = null;
    ImageView iv_cross;
    private int hour = 0, minutes = 0;
    String Cardid;
    String totalToSend = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_point);
        prefrence = SharedPreference.getInstance(this);
        spinner = (Spinner) findViewById(R.id.spinner);
        tv_meet_time = (TextView) findViewById(R.id.tv_meet_time);
        iv_cross = (ImageView) findViewById(R.id.iv_cross);
        map_frameLayout = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_mapView, map_frameLayout).commit();

        getDataFromIntent();
        listener();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Cardid = sharedPref.getString("cardId", cardid);

    }

    private void listener() {
/*
        findViewById(R.id.iv_cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });*/

        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HomeItemFragment fragment = new HomeItemFragment();
                //getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

                ///getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeItemFragment()).commit();
                //Intent intent=new Intent(MeetingPointActivity.this, HomeActivity.class);
                // startActivity(intent);

                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    if (meetingPointMainModel.getResult().get(0).getTime().size() > 0) {
                        String str = meetingPointMainModel.getResult().get(0).getTime().get(position).getTime();
                        String hourString = str.substring(0, str.length() - 6);
                        String minuteString = str.substring(3, str.length() - 3);
                        hour = Integer.parseInt(hourString);
                        minutes = Integer.parseInt(minuteString);
                    }

                    meetingPointId = meetingPointMainModel.getResult().
                            get(0).getMeetingPoint().get(position).getMeetingPointId();
                    Double latitude = Double.valueOf(meetingPointMainModel.getResult().
                            get(0).getMeetingPoint().get(position).getLat());
                    Double longitude = Double.valueOf(meetingPointMainModel.getResult().
                            get(0).getMeetingPoint().get(position).getLng());
                    LatLng latLng = new LatLng(latitude, longitude);
                    moveToCurrentLocation(latLng, googleMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        findViewById(R.id.img_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (meetingPointMainModel.getResult() != null &&
                        meetingPointMainModel.getResult().get(0).getTime().size() > 0) {
                    openTimeAlertDialog();
                } else {
                    Utils.showToast(MeetingPointActivity.this, "You have no time slot");
                }
            }
        });
        findViewById(R.id.btn_cnfrm_dlvry).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (!tv_meet_time.getText().toString().equals("00:00")) {

                    if (!meetingPointId.equals("")) {
                        confirmDelievery();
                    } else {
                        Utils.showToast(MeetingPointActivity.this, "You dont select meeting point");
                    }

                } else {
                    Utils.showToast(MeetingPointActivity.this, getString(R.string.select_meeting_point_time));
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkSeletedTime(String time) {
//        String time="10:15";

        String hourString = time.substring(0, time.length() - 3);
        String minuteString = time.substring(3, time.length() - 0);
        hour = Integer.parseInt(hourString);
        minutes = Integer.parseInt(minuteString);

        Calendar datetime = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hour);
        datetime.set(Calendar.MINUTE, minutes);
        if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
            tv_meet_time.setText(time);

        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MeetingPointActivity.this);
            alertDialog.setTitle("Larika").setMessage("Please select valid time")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
            AlertDialog alert11 = alertDialog.create();
            alert11.show();
//                            Utills.showToast(SetMeetingPointActivity.this,"Please select valid time");


        }

    }

    private void confirmDelievery() {
        if (total.equals("")) {
            total = "0";
            final Progress progress = new Progress(this);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            JsonObject jsonObject = new JsonObject();
            try {
                jsonObject.addProperty("cartId", Integer.parseInt(cartId));
            }catch (Exception exception){
                jsonObject.addProperty("cartId", 0);
            }
            jsonObject.addProperty("cardId", paymentCardId);
            jsonObject.addProperty("timeId", Integer.parseInt(time_id));
            jsonObject.addProperty("meetingPointId", Integer.parseInt(meetingPointId));
            jsonObject.addProperty("total",total);
            String accessTocken=prefrence.getString(Constants.ACCESS_TOKEN,"");
            Ion.with(this).load("POST", Constants.BASE_URL + "order")
                    .setHeader("accessToken", prefrence.getString(Constants.ACCESS_TOKEN, ""))
                    .setJsonObjectBody(jsonObject)
                    .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    progress.dismiss();
                    String responce = "";
                    if (e != null) {
                        return;
                    }
                    Intent intent;
                    switch (result.getHeaders().code()) {

                        case 200:
                            responce = result.getResult();
//                        Utils.showToast(MeetingPointActivity.this,"Order created successfully");
                            intent = new Intent(MeetingPointActivity.this, HomeActivity.class);
                            intent.putExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG, "1");
                            startActivity(intent);
                            finish();
                            break;
                        case 201:
                            responce = result.getResult();
//                        Utils.showToast(MeetingPointActivity.this,"Order created successfully");
                            intent = new Intent(MeetingPointActivity.this, HomeActivity.class);
                            intent.putExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG, "1");
                            startActivity(intent);
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
        } else {
            final Progress progress = new Progress(this);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            JsonObject jsonObject = new JsonObject();
            try{
                jsonObject.addProperty("cartId", Integer.parseInt(cartId));
            }catch (Exception exception){
                jsonObject.addProperty("cartId", 0);
            }
            jsonObject.addProperty("cardId", paymentCardId);
            jsonObject.addProperty("timeId", Integer.parseInt(time_id));
            jsonObject.addProperty("meetingPointId", Integer.parseInt(meetingPointId));
            jsonObject.addProperty("total",total);
            Ion.with(this).load("POST", Constants.BASE_URL + "order")
                    .setHeader("accessToken", prefrence.getString(Constants.ACCESS_TOKEN, ""))
                    .setJsonObjectBody(jsonObject)
                    .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    progress.dismiss();
                    String responce = "";
                    if (e != null) {
                        return;
                    }
                    Intent intent;
                    switch (result.getHeaders().code()) {

                        case 200:
                            responce = result.getResult();
//                        Utils.showToast(MeetingPointActivity.this,"Order created successfully");
                            intent = new Intent(MeetingPointActivity.this, HomeActivity.class);
                            intent.putExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG, "1");
                            startActivity(intent);
                            finish();


                            break;
                        case 201:
                            responce = result.getResult();
//                        Utils.showToast(MeetingPointActivity.this,"Order created successfully");
                            intent = new Intent(MeetingPointActivity.this, HomeActivity.class);
                            intent.putExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG, "1");
                            startActivity(intent);
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


    }

    private void openTimeAlertDialog() {
        final Dialog dialog = new Dialog(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_meeting_point_time, null);

        ListView lv = (ListView) view.findViewById(R.id.list_view);

        // Change MyActivity.this and myListOfItems to your own values
        MeetingPointTimeAdapter meetingPointTimeAdapter = new MeetingPointTimeAdapter(MeetingPointActivity.this,
                meetingPointMainModel.getResult().get(0).getTime());

        lv.setAdapter(meetingPointTimeAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String str = "";
                str = meetingPointMainModel.getResult().get(0).getTime().get(position).getTime();
                time_id = meetingPointMainModel.getResult().get(0).getTime().get(position).getId();
                str = str.substring(0, str.length() - 3);
                checkSeletedTime(str);

                dialog.dismiss();
            }
        });
        dialog.setContentView(view);

        dialog.show();

    }

    private void getDataFromIntent() {
        if (getIntent().hasExtra("paymentCardId")) {
            paymentCardId = getIntent().getStringExtra("paymentCardId");
        }
        if (getIntent().hasExtra("vendor_id")) {
            String vendor_id = getIntent().getStringExtra("vendor_id");
            getMeetingPointApi(vendor_id);
        }
        if (getIntent().hasExtra("cart_id")) {
            cartId = getIntent().getStringExtra("cart_id");
        }
        if (getIntent().hasExtra("total_price")) {
            total = getIntent().getStringExtra("total_price");
           // totalToSend = total;
         //   total = total.substring(2).replaceAll("\\s+", " ");
            // .replace(",", "");
            //replaceAll("\\s+", ",")
        }
    }

    private void getMeetingPointApi(String vendor_id) {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("vendorId", Integer.parseInt(vendor_id));
        Ion.with(this).load("POST", Constants.BASE_URL + "meetingPoints")
                .setJsonObjectBody(jsonObject)
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

                            meetingPointMainModel = Utils.getgsonInstance().fromJson(responce, MeetingPointMainModel.class);
                            if (meetingPointMainModel.getResult().get(0).getMeetingPoint().size() > 0) {
                                MeetingPointModel resultModelClass = new MeetingPointModel();
//                                resultModelClass.setPlaceName("Choose a place on the map");
//                                meetingPointMainModel.getResult().get(0).getMeetingPoint().add(0, resultModelClass);


                                String str = "";
                                com.example.fluper.larika_user_app.adapter.SpinnerAdapter spinnerAdapter = new com.example.fluper.larika_user_app.adapter.SpinnerAdapter(MeetingPointActivity.this,
                                        R.layout.spinner_row, meetingPointMainModel.getResult().get(0).getMeetingPoint());
                                spinner.setAdapter(spinnerAdapter);
//                                if(meetingPointMainModel.getResult().get(0).getTime().size()>0)
//                                {
//
//                                    str=meetingPointMainModel.getResult().get(0).getTime().get(0).getTime();
//                                    time_id=meetingPointMainModel.getResult().get(0).getTime().get(0).getId();
//                                    str = str.substring(0, str.length() - 3);
//                                    tv_meet_time.setText(str);
//                                }
//                                else
//                                {
//                                   time_id="1";
//
////                                    Utils.showToast(MeetingPointActivity.this,"No time slot available");
//                                }


//                                meetingPointId=meetingPointMainModel.getResult().
//                                        get(0).getMeetingPoint().get(0).getMeetingPointId();

                                Double latitude = Double.valueOf(meetingPointMainModel.getResult().
                                        get(0).getMeetingPoint().get(0).getLat());
                                Double longitude = Double.valueOf(meetingPointMainModel.getResult().
                                        get(0).getMeetingPoint().get(0).getLng());
                                latLng = new LatLng(latitude, longitude);
                                if (map_frameLayout != null) {
                                    map_frameLayout.getMapAsync(MeetingPointActivity.this);
                                }
//                                moveToCurrentLocation(latLng,googleMap);

                            }


                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }


                        break;
                    case 201:
                        responce = result.getResult();
                        try {

                            meetingPointMainModel = Utils.getgsonInstance().fromJson(responce.toString(), MeetingPointMainModel.class);
                            if (meetingPointMainModel.getResult().get(0).getMeetingPoint().size() > 0) {
                                com.example.fluper.larika_user_app.adapter.SpinnerAdapter spinnerAdapter = new com.example.fluper.larika_user_app.adapter.SpinnerAdapter(MeetingPointActivity.this,
                                        R.layout.spinner_row, meetingPointMainModel.getResult().get(0).getMeetingPoint());
                                spinner.setAdapter(spinnerAdapter);

                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        break;

                    case 204:
                        responce = result.getResult();

                        break;
                    case 400:
                        responce = result.getResult();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(responce);
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//        latitude= Double.valueOf(UtilPreferences.getFromPrefs(MeetingPointActivity.this,UtilPreferences.CURRENT_LATITUDE,""));
//        longitude= Double.valueOf(UtilPreferences.getFromPrefs(MeetingPointActivity.this,UtilPreferences.CURRENT_LONGITUDE,""));
//        LatLng latLng = new LatLng(latitude,longitude);
        if (latLng != null)
            moveToCurrentLocation(latLng, googleMap);


    }

    private void moveToCurrentLocation(LatLng currentLocation, GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f));
        // Zoom in, animating the camera.
//        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
//        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }


}
