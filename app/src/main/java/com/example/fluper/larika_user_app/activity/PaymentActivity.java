package com.example.fluper.larika_user_app.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.fluper.larika_user_app.Network.NetworkThread;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.adapter.PaymentAdapter;
import com.example.fluper.larika_user_app.bean.AddMyProductModel;
import com.example.fluper.larika_user_app.bean.AddPaymentCardModel;
import com.example.fluper.larika_user_app.bean.HomeDataBean;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.bean.PaymentCardServerResponse;
import com.example.fluper.larika_user_app.bean.PaymentResponseMainModel;
import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.ActivityPaymentBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.MonthYearDialog;
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

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener,NetworkCallback {
    ActivityPaymentBinding activityPaymentBinding;
    private SharedPreferences sharedPrefs;
    private PaymentCardMainModel paymentCardMainModel;
    private PaymentAdapter paymentAdapter;
    private List<AddMyProductModel>addMyProductModelList=new ArrayList<>();
    private boolean openCart=false;
    PaymentCardServerResponse paymentCardServerResponse=new PaymentCardServerResponse();
    private SharedPreference prefrence;
    static PaymentCardMainModel paymentCardMainModelInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentCardMainModel=new PaymentCardMainModel();
        activityPaymentBinding= DataBindingUtil.setContentView(this, R.layout.activity_payment);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        getPaymentCardData();
        prefrence = SharedPreference.getInstance(this);
        listener();
        itemSwipe();
        getDataFromIntent();


//        getData();

    }
    private void itemSwipe() {
        final SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem insertItem=new SwipeMenuItem(PaymentActivity.this);
                insertItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
                insertItem.setWidth(dp2px(100));
//                insertItem.setTitle("Delete");
//                insertItem.setTitleColor(getResources().getColor(R.color.black));
                insertItem.setIcon(R.mipmap.cross_white);
//                insertItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(insertItem);
            }
        };
        activityPaymentBinding.cardListview.setMenuCreator(creator);
        activityPaymentBinding.cardListview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if(Utils.isInternetOn(PaymentActivity.this))
                {
                    switch (index) {
                        case 0:
                            removePaymentCardCall(paymentCardMainModel
                                    .getAddCardModelList().
                                            get(position).getCardId(),position);
                            break;
                    }

                }else{
                    Utils.showToast(PaymentActivity.this,"No internet");
                }

                return false;
            }
        });
        activityPaymentBinding.cardListview.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        activityPaymentBinding.cardListview.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
    }

    private void removePaymentCardCall(String cardId, final int position) {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("cardId",cardId);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        editor.putString("cardId", cardId);

        //commits your edits
        editor.commit();

        Ion.with(this).load("POST", Constants.BASE_URL + "deleteCreaditCard")
                ///.setHeader("accessToken", prefrence.getString(Constants.ACCESS_TOKEN, ""))
                .setHeader("accessToken",
                        UtilPreferences.getFromPrefs(PaymentActivity.this,
                                UtilPreferences.ACCESS_TOKEN,""))
                .setJsonObjectBody(jsonObject)
                .asString().withResponse().
                setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String responce = "";
                        progress.dismiss();

                        if(result!=null)
                        {
                            try
                            {


                                Intent intent=null;

                                switch (result.getHeaders().code()) {
                                    case 200:
                                        paymentCardMainModel.getAddCardModelList().remove(position);
                                        if(paymentCardMainModel.getAddCardModelList().size()>0)
                                        {
                                            paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
                                            paymentAdapter=new PaymentAdapter(PaymentActivity.this
                                                    ,paymentCardMainModel.getAddCardModelList());
                                            activityPaymentBinding.cardListview.setAdapter(paymentAdapter);
                                        }


                                        paymentAdapter.notifyDataSetChanged();

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
                                    case 403:
                                        Utils.showToast(PaymentActivity.this,
                                                new JSONObject(result.getResult()).getString("message"));
                                        break;
                                    case 500:

                                        Utils.showToast(PaymentActivity.this,"Please enter valid details");
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

    private int dp2px(float i) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, metrics);
    }

    private void getCardDetailsApi() {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Ion.with(this).load("POST",Constants.BASE_URL + "getAllCard")
                //.setHeader("accessToken", prefrence.getString(Constants.ACCESS_TOKEN, ""))
                .setHeader("accessToken", UtilPreferences.
                        getFromPrefs(PaymentActivity.this, UtilPreferences.ACCESS_TOKEN,""))
                .asString().withResponse().
                setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String responce = "";
                        progress.dismiss();

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
                                        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                                        Gson gson = new Gson();
                                        String json = gson.toJson(paymentCardMainModel);
                                        prefsEditor.putString("MyObject", json);
                                        prefsEditor.commit();

                                        if(paymentCardMainModel !=null && paymentCardMainModel.getAddCardModelList().size()>0)
                                        {
                                            paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
                                            paymentAdapter=new PaymentAdapter(PaymentActivity.this, paymentCardMainModel.getAddCardModelList());
                                            activityPaymentBinding.cardListview.setAdapter(paymentAdapter);
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

                                        Utils.showToast(PaymentActivity.this,"Please enter valid details");
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

    private String getPaymentCardData() {
        String last=null;
        paymentCardMainModel = new PaymentCardMainModel();
        Gson gson = new Gson();
        String json = sharedPrefs.getString("MyObject", "");
//        if(paymentCardMainModel!=null)
        paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
        if(paymentCardMainModel!=null) {
            if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
                if (paymentCardMainModel.getAddCardModelList().size() == 1) {
                    paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
                }
                for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {
                    if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                        paymentAdapter = new PaymentAdapter(PaymentActivity.this, paymentCardMainModel.getAddCardModelList());
                        activityPaymentBinding.cardListview.setAdapter(paymentAdapter);
                    }

                }
            }
        }else{
            getCardDetailsApi();
        }
        return last;
    }


    private void getDataFromIntent() {
        // from Home screen cart dialog

        if(getIntent().hasExtra("workCloseImage"))
        {
            openCart=true;

        }

    }
   static public PaymentCardMainModel getCardMainModel()
    {

        return paymentCardMainModelInstance;
    }
    //    check previous Payment card data
    private void getData() {
        Gson gson = new Gson();
        String json = sharedPrefs.getString("MyObject", "");
//        if(paymentCardMainModel!=null)
        paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
        if(paymentCardMainModel !=null && paymentCardMainModel.getAddCardModelList().size()>0)
        {
            paymentAdapter=new PaymentAdapter(PaymentActivity.this, paymentCardMainModel.getAddCardModelList());
            activityPaymentBinding.cardListview.setAdapter(paymentAdapter);
        }
        Log.e("main","main");

    }

    private void listener() {
        activityPaymentBinding.rlDialogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


            }
        });
        activityPaymentBinding.etSignIn.setOnClickListener(this);
        activityPaymentBinding.relButton.setOnClickListener(this);
        activityPaymentBinding.tvContact.setOnClickListener(this);
        activityPaymentBinding.rlTransparency.setOnClickListener(this);
        activityPaymentBinding.ivCross.setOnClickListener(this);
        activityPaymentBinding.tvAddCard.setOnClickListener(this);
        activityPaymentBinding.cardListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                boolean value=false;
                for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size() ; i++) {
                    if(i==position)
                    {
                        paymentCardMainModel.getAddCardModelList().get(position).setSelected(true);
                    }
                    else {
                        paymentCardMainModel.getAddCardModelList().get(i).setSelected(false);

                    }
                }
                paymentAdapter.notifyDataSetChanged();
                SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(paymentCardMainModel);
                prefsEditor.putString("MyObject", json);
                prefsEditor.commit();
                if(openCart)
                {
                    if(paymentCardMainModel.getAddCardModelList().get(position).isSelected())
                    {
                        UtilPreferences.saveToPrefs(PaymentActivity.this,UtilPreferences.FROM_CART_DIALOG,"1");
                        setResult(111);
                        finish();
                       // callHomeApi();
//                    finish();
                    }
                }



            }

        });


        activityPaymentBinding.etExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthYearDialog pd = new MonthYearDialog();
                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        String month="";
                        if(selectedMonth==-1)
                        {
                            Utils.showToast(getApplicationContext(),"You can not select past month");
                            activityPaymentBinding.etExpiry.setText("");
                        }
                        else if(selectedYear==0 && selectedMonth==0)
                        {
                            Utils.showToast(getApplicationContext(), "Select month");
                            activityPaymentBinding.etExpiry.setText("");
                        }
                        else
                        {
                            if(selectedMonth<10)
                            {
                                month="0"+selectedMonth;
                            }
                            else
                            {
                                month=selectedMonth+"";
                            }
                            activityPaymentBinding.etExpiry.setText(month+"-"+selectedYear%2000);

                        }
                    }
                });
                pd.show(getFragmentManager(), "MonthYearPickerDialog");


            }
        });
    }

    public void callHomeApi() {
        Double latitude=0.0,longitude=0.0;

        JsonObject object = new JsonObject();
        String localLatitude= UtilPreferences.getFromPrefs(PaymentActivity.this,UtilPreferences.CURRENT_LATITUDE,"");
        String localLongitude=UtilPreferences.getFromPrefs(PaymentActivity.this,UtilPreferences.CURRENT_LONGITUDE,"");
        if(!localLatitude.equals("CURRENT_LATITUDE") && !localLatitude.equals(""))
        {
            latitude= Double.valueOf(UtilPreferences.getFromPrefs(PaymentActivity.this,UtilPreferences.CURRENT_LATITUDE,""));
            longitude= Double.valueOf(UtilPreferences.getFromPrefs(PaymentActivity.this,UtilPreferences.CURRENT_LONGITUDE,""));
            object.addProperty("lat", latitude);
            object.addProperty("lng", longitude);
            NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "homeScreen");
            thread.getNetworkResponse(this, object, 10000);
        }


    }


    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {

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
                        bean.dishRating = Float.parseFloat("0.0");

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
                    Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                    intent.putExtra("homeDataList", list);
                    startActivity(intent);
//                    startActivity(new Intent(MoveActivity.this, HomeActivity.class));

                    overridePendingTransition(R.anim.fade_menu, R.anim.slide_down_view);
                    finish();
//                    finishAffinity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {



    }


    @Override
    public void onClick(View view) {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        switch (view.getId())
        {
            case R.id.tv_contact:
                openPaymentDialog();
                break;
            case R.id.rl_transparency:
                closePaymentDialog();
                break;
            case R.id.iv_cross:
              /*  if(openCart)
                {
                    SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(paymentCardMainModel);
                    prefsEditor.putString("MyObject", json);
                    prefsEditor.commit();
                    UtilPreferences.saveToPrefs(PaymentActivity.this,
                            UtilPreferences.FROM_CART_DIALOG,"1");
                    callHomeApi();
//                    finish();
                }else
                {*/
                    //finish();
                    activityPaymentBinding.ivCross.setAlpha(1f);
                    activityPaymentBinding.ivCross.setAnimation(animation1);
                    setResult(111);
                    finish();
//                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);
             //   }
//                finish();

                break;
            case R.id.tv_add_card:
                checkAddCardValidation();
                break;
            case R.id.rel_button:
                startActivity(new Intent(PaymentActivity.this,ShowPromoCodeActivity.class));
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                break;
            case R.id.et_sign_in:
                startActivity(new Intent(PaymentActivity.this,ShowPromoCodeActivity.class));
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                break;
        }


    }
    // add card time validation
    private void checkAddCardValidation() {
        if(activityPaymentBinding.etCardNumber.getText().toString().trim().length()>0)
        {
            if(activityPaymentBinding.etCardNumber.getText().toString().trim().length()>=3)
            {
                if(activityPaymentBinding.etExpiry.getText().toString().trim().length()>0)
                {
                    if(activityPaymentBinding.etExpiry.getText().toString().trim().length()>3)
                    {
                        if(activityPaymentBinding.etUserName.getText().toString().trim().length()>0)
                        {
                            if(activityPaymentBinding.etCvv.getText().toString().trim().length()>0)
                            {
                                if(activityPaymentBinding.etCvv.getText().toString().trim().length()==3)
                                {
                                    addCard();

                                }else
                                {
                                    Utils.showToast(PaymentActivity.this,"Enter valid CVV number");
                                }

                            }else
                            {
                                Utils.showToast(PaymentActivity.this,"Please enter CVV number");
                            }

                        }else
                        {
                            Utils.showToast(PaymentActivity.this,getString(R.string.empty_card_user_name));
                        }

                    }else
                    {
                        Utils.showToast(PaymentActivity.this,getString(R.string.valid_expiry));
                    }

                }else
                {
                    Utils.showToast(PaymentActivity.this,getString(R.string.empty_card_expiry));
                }

            }else
            {
                Utils.showToast(PaymentActivity.this,getString(R.string.valid_card_number));
            }


        }
        else
        {
            Utils.showToast(PaymentActivity.this,getString(R.string.empty_card));
        }
    }

    //   add payment card
    private void addCard() {

        if(paymentCardMainModel ==null)
        {
            paymentCardMainModel =new PaymentCardMainModel();
        }

        addPaymentCardApi();



    }
    public void addCardLocally()
    {
        AddPaymentCardModel addCardModel=new AddPaymentCardModel();
        addCardModel.setCardNumber(activityPaymentBinding.etCardNumber.getText().toString());
        addCardModel.setCardExpiry(activityPaymentBinding.etExpiry.getText().toString());
        addCardModel.setCardUserName(activityPaymentBinding.etUserName.getText().toString());
        addCardModel.setCardCvv(activityPaymentBinding.etCvv.getText().toString());
        addCardModel.setCardId(paymentCardServerResponse.getId());
        if(paymentCardMainModel.getAddCardModelList().size()==0)
        addCardModel.setSelected(true);
        paymentCardMainModel.getAddCardModelList().add(addCardModel);

        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(paymentCardMainModel);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
        if(paymentCardMainModel.getAddCardModelList().size()==1)
        {
            paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
//            UtilPreferences.saveToPrefs(PaymentActivity.this,UtilPreferences.FROM_CART_DIALOG,"1");
//
//
//            callHomeApi();
//                    finish();
        }
          paymentCardMainModelInstance=paymentCardMainModel;
        paymentAdapter=new PaymentAdapter(PaymentActivity.this, paymentCardMainModel.getAddCardModelList());
        activityPaymentBinding.cardListview.setAdapter(paymentAdapter);
        paymentAdapter.notifyDataSetChanged();
        closePaymentDialog();
    }

    private void addPaymentCardApi() {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject jsonObject=getCardJson();


        Ion.with(this).load("POST",Constants.BASE_URL + "paymentDetail")

                .setHeader("accessToken",UtilPreferences.
                        getFromPrefs(PaymentActivity.this,
                        UtilPreferences.ACCESS_TOKEN,""))
               /// .setHeader("accessToken", UtilPreferences.getFromPrefs(PaymentActivity.this, UtilPreferences.ACCESS_TOKEN,""))
                .setJsonObjectBody(jsonObject)
                .asString().withResponse().
                setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String responce = "";
                        progress.dismiss();
                        if(result!=null)
                        {
                            try
                            {


                                Intent intent=null;

                                switch (result.getHeaders().code()) {
                                    case 200:
                                        paymentCardServerResponse=Utils.
                                                getgsonInstance().
                                                fromJson(result.getResult(),
                                                        PaymentCardServerResponse.class);
                                        addCardLocally();


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

                                        Utils.showToast(PaymentActivity.this,"Please enter valid details");
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

    private JsonObject getCardJson() {
        JsonObject jsonObject=new JsonObject();
        String expiryMonth=activityPaymentBinding.etExpiry.getText().toString();
        jsonObject.addProperty("cardType","visa");
        jsonObject.addProperty("cardNumber",
                activityPaymentBinding.etCardNumber.getText().toString());
        jsonObject.addProperty("expiryMonth",expiryMonth.substring(0,2));
        jsonObject.addProperty("expiryYear","20"+expiryMonth.substring(3,5));
        jsonObject.addProperty("cvv",activityPaymentBinding.etCvv.
                getText().toString());
        jsonObject.addProperty("firstName",activityPaymentBinding.etUserName.
                getText().toString());
        jsonObject.addProperty("lastName","");
        return jsonObject;
    }


    private void closePaymentDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_downn);

        activityPaymentBinding.rlTransparency.startAnimation(bottomDown);
        activityPaymentBinding.rlFilterDialog.setClickable(true);
        activityPaymentBinding.rlWhiteLayout.setClickable(true);
        activityPaymentBinding.etCardNumber.setText("");
        activityPaymentBinding.etExpiry.setText("");
        activityPaymentBinding.etUserName.setText("");
        activityPaymentBinding.etCvv.setText("");
        Utils.hideSoftKeyBoard(PaymentActivity.this);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                activityPaymentBinding.rlTransparency.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void openPaymentDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_upp);

        activityPaymentBinding.rlTransparency.startAnimation(bottomDown);
        activityPaymentBinding.rlTransparency.setClickable(true);
        activityPaymentBinding.rlTransparency.setVisibility(View.VISIBLE);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //animation.getStartOffset();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                activityPaymentBinding.rlTransparency.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}