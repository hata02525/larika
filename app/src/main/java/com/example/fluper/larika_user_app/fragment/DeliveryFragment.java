package com.example.fluper.larika_user_app.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.activity.OrderDetailActivity;
import com.example.fluper.larika_user_app.activity.RatingOrderActivity;
import com.example.fluper.larika_user_app.bean.OnTheGoModel;
import com.example.fluper.larika_user_app.bean.OrderCompleteModel;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.FragmentDelieveryBinding;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;


public class DeliveryFragment extends Fragment implements OnMapReadyCallback,
        View.OnClickListener {
    private View view;
    private TextView tv_meet_time,tv_meet_today;
    private AutoCompleteTextView actv_address;
    private ImageView iv_call;
    private Button btn_cnfrm_dlvry;
    private FrameLayout map_frame;
    private SupportMapFragment map_frameLayout;
    private ImageView img_qr;


    private Double latitude,longitude;
    FragmentDelieveryBinding fragmentOrderBinding;
    GoogleMap mMap;
    private String time="";
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int GOOGLE_API_CLIENT_ID = 0;

    LatLng latLng;
    private OnTheGoModel myOrderModel;
    private int position=0;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    String message;
    String staus;
    private boolean checkOrder=false;
    private OrderCompleteModel orderCompleteModel;
    private SharedPreferences sharedPrefs;
    PaymentCardMainModel paymentCardMainModel;
    private int paymentCardPosition=-1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delievery, container, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initUI();
        listener();
        setData();
        getPaymentDetail();
//        paymentDetailApi();

        return view;
    }

    private void getPaymentDetail() {
        paymentCardMainModel = new PaymentCardMainModel();
        Gson gson = new Gson();
        String json = sharedPrefs.getString("MyObject", "");

        paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
        if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
            for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {
                if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                    paymentCardPosition=i;

//                    String last = paymentCardMainModel.
//                            getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.
//                            getAddCardModelList().get(i).getCardNumber().length() - 4);

//                    tv_card_detail.setText("Credito   ....  ....  ....  " + last);
                }

            }
        }
    }


    private void setData() {
        Glide
                .with(getActivity())
                .load(myOrderModel.getResult().get(position).getQrImage()).asBitmap()
                .placeholder(R.mipmap.back_white)
                .into(img_qr);


        actv_address.setText(myOrderModel.getResult().get(position).getPlaceName()+"");
        String time=getTime(myOrderModel.getResult().get(position).getTime());
        tv_meet_time.setText(time);
        tv_meet_today.setText("Meet today  "+myOrderModel.getResult().get(position).getVendorName()+" at");
        double lat= Double.parseDouble(myOrderModel.getResult().get(position).getLat());
        double lng= Double.parseDouble(myOrderModel.getResult().get(position).getLng());

        latLng = new LatLng(lat,lng);
        if (map_frameLayout != null) {
            map_frameLayout.getMapAsync(this);
        }


    }

    private String getTime(String orderResults) {
        String time=orderResults;
        if(time!=null) {
            if (time.contains(":")) {
                String[] array = time.split(":");
                time = array[0] +":"+ array[1];
            }
        }
        return time;
    }

//    public void getModel(MyOrderModel myOrderModel)
//    {
//        this.myOrderModel=myOrderModel;
//
//    }

    private void listener() {
        btn_cnfrm_dlvry.setOnClickListener(this);
        view.findViewById(R.id.rl_qr_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCartDialog();
                Ordercompleted();
                if(orderCompleteModel==null)
                    orderCompleteModel=new OrderCompleteModel();
                orderCompleteModel.setCloseApi(false);

            }
        });
        view.findViewById(R.id.img_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilterDialog();
                orderCompleteModel.setCloseApi(true);



            }
        });
    }

    private void Ordercompleted() {
        JsonObject object = new JsonObject();
        object.addProperty("orderId",myOrderModel.getResult().get(position).getOrderId());
        Ion.with(this).load(Constants.BASE_URL + "isOrderCompleted")
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                String responce = "";
                if (e != null) {
                    return;
                }
                if(result!=null)
                {
                    Log.e("Delievery Order Response",result.getResult());
                    int resultCode=result.getHeaders().code();
                    switch (resultCode) {
                        case 200:
                            try {
                                orderCompleteModel= Utils.getgsonInstance()
                                        .fromJson(result.getResult(),
                                                OrderCompleteModel.class);
                                if(orderCompleteModel.getResult().get(0).getIsConfirmed().equals("1"))
                                {

                                    //paymentDetailApi();
                                   Intent intent=
                                            new Intent(getActivity(),
                                                    RatingOrderActivity.class);

                                    try {
                                       intent.putExtra("orderModel",new JSONObject(Utils.getgsonInstance()
                                                .toJson(myOrderModel)).toString());
                                        intent.putExtra("position",String.valueOf(position));
                                        startActivity(intent);
                                        getActivity().finish();
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
//                                Open rating screen
                                }
                                else if(!orderCompleteModel.isCloseApi())
                                {

                                    Ordercompleted();
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            break;
                        case 201:
                            try {
                                orderCompleteModel=Utils.getgsonInstance()
                                        .fromJson(result.getResult(),
                                                OrderCompleteModel.class);
                                if(orderCompleteModel.getResult().get(0).getIsConfirmed().equals("1"))
                                {
//                                    Intent intent=
//                                            new Intent(getActivity(),
//                                                    RatingOrderActivity.class);
//
//                                    try {
//
//                                        intent.putExtra("orderModel",new JSONObject(Utils.getgsonInstance()
//                                                .toJson(myOrderModel)).toString());
//                                        intent.putExtra("position",String.valueOf(position));
//                                        startActivity(intent);
//                                        getActivity().finish();
//                                    } catch (JSONException e1) {
//                                        e1.printStackTrace();
//                                    }
//                                Open rating screen
                                }
                                else if(!orderCompleteModel.isCloseApi())
                                {

                                    Ordercompleted();
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
                            break;
                        case 401:
                            responce = result.getResult();
                            break;

                        default:
                            responce = result.getResult();
                            break;
                    }
                }



            }
        });

    }

    private void paymentDetailApi() {
        if(paymentCardMainModel!=null && paymentCardPosition!=-1)
        {
            String cardNumber=paymentCardMainModel.getAddCardModelList()
                    .get(paymentCardPosition).getCardNumber();
            String expiryMonth=paymentCardMainModel.getAddCardModelList()
                    .get(paymentCardPosition).getCardExpiry();
            String expiryYear=paymentCardMainModel.getAddCardModelList()
                    .get(paymentCardPosition).getCardExpiry();
            String cvv=paymentCardMainModel.getAddCardModelList()
                    .get(paymentCardPosition).getCardCvv();
            String firstName=paymentCardMainModel.getAddCardModelList()
                    .get(paymentCardPosition).getCardUserName();
            String lastName=paymentCardMainModel.getAddCardModelList()
                    .get(paymentCardPosition).getCardUserName();
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("cardType","visa");
            jsonObject.addProperty("cardNumber",cardNumber);
            jsonObject.addProperty("expiryMonth",expiryMonth.substring(0,2));
            jsonObject.addProperty("expiryYear","20"+expiryMonth.substring(3,5));
            jsonObject.addProperty("cvv","123");
            jsonObject.addProperty("firstName",firstName);
            jsonObject.addProperty("lastName","");

            Ion.with(this).load(Constants.BASE_URL + "paymentDetail")
                    .setHeader("accessToken",
                            UtilPreferences.getFromPrefs(getActivity(),
                                    UtilPreferences.ACCESS_TOKEN,""))
                    .setJsonObjectBody(jsonObject)
                    .asString().withResponse().
                    setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            String responce = "";
                            if (e != null) {
                                return;
                            }
                            if(result!=null)
                            {
                                Intent intent=null;

                                switch (result.getHeaders().code()) {
                                    case 200:

                                        intent=
                                                new Intent(getActivity(),
                                                        RatingOrderActivity.class);

                                        try {
                                            intent.putExtra("orderModel",new JSONObject(Utils.getgsonInstance()
                                                    .toJson(myOrderModel)).toString());
                                            intent.putExtra("position",String.valueOf(position));
                                            startActivity(intent);
                                            getActivity().finish();
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                        break;
                                    case 201:
                                        intent=
                                                new Intent(getActivity(),
                                                        RatingOrderActivity.class);

                                        try {
                                            intent.putExtra("orderModel",new JSONObject(Utils.getgsonInstance()
                                                    .toJson(myOrderModel)).toString());
                                            intent.putExtra("position",String.valueOf(position));
                                            startActivity(intent);
                                            getActivity().finish();
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }

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
                                        Utils.showToast(getActivity(),
                                                "Please check card details");
                                        closeFilterDialog();
                                        orderCompleteModel.setCloseApi(true);
                                        break;

                                    default:
                                        responce = result.getResult();
                                        break;
                                }
                            }



                        }
                    });

        }



    }





    private void openCartDialog() {

        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_upp);
        view.findViewById(R.id.rl_home_transparency).startAnimation(bottomDown);
        ((OrderDetailActivity)getActivity()).disableButton();



        view.findViewById(R.id.rl_home_transparency).setClickable(true);
        view.findViewById(R.id.rl_home_transparency).setVisibility(View.VISIBLE);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.findViewById(R.id.rl_home_transparency).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    private void closeFilterDialog() {
        ((OrderDetailActivity)getActivity()).enableableButton();
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_downn);

        view.findViewById(R.id.rl_home_transparency).startAnimation(bottomDown);

        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.findViewById(R.id.rl_home_transparency).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initUI() {
        img_qr= (ImageView) view.findViewById(R.id.img_qr);
        tv_meet_time = (TextView) view.findViewById(R.id.tv_meet_time);
        tv_meet_today = (TextView) view.findViewById(R.id.tv_meet_today);
        iv_call = (ImageView) view.findViewById(R.id.iv_call);
        actv_address = (AutoCompleteTextView) view.findViewById(R.id.actv_address);
        btn_cnfrm_dlvry = (Button) view.findViewById(R.id.btn_cnfrm_dlvry);
        view.findViewById(R.id.rl_qr_code).setOnClickListener(this);

        map_frame = (FrameLayout) view.findViewById(R.id.map_frame);

        map_frameLayout = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().add(R.id.map_frame, map_frameLayout).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_qr_code:
//                Intent intent=new Intent(getActivity(), OrderConfirmationActivity.class);
//                intent.putExtra("orderId",myOrderModel.getOrderId());
//                startActivity(intent);
                break;
//            case R.id.btn_here:
//                if (map_frameLayout != null) {
//                    map_frameLayout.getMapAsync(this);
//                }
//                break;
        }
    }

    public void getModel(OnTheGoModel myOrderModel, int postion) {
        this.myOrderModel=myOrderModel;
        this.position=postion;

    }
}