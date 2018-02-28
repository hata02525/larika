package com.example.fluper.larika_user_app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.bean.AllPromoCodeModel;
import com.example.fluper.larika_user_app.bean.OrderResults;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

public class ShowPromoCodeActivity extends AppCompatActivity {
    private SharedPreference preference;
    List<OrderResults> orderResultsList = new ArrayList<>();
    ArrayList<AllPromocodeModel> allpromocode=new ArrayList<>();
    ListView list_viewpromocode;
    TextView text_view;
    AllPromocodeModel allPromocodeModel;
    private AllPromoCodeModel promoCodeModel;
    private AllPromocodeAdapter allPromocodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promo_code);
        promoCodeModel=new AllPromoCodeModel();

        text_view=(TextView)findViewById(R.id.tv_no_promoCode) ;
        list_viewpromocode=(ListView)findViewById(R.id.list_viewpromocode);
        preference = SharedPreference.getInstance(ShowPromoCodeActivity.this);
        getPromoCodeData();
        listener();
    }

    private void listener() {
        findViewById(R.id.add_promocode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ShowPromoCodeActivity.this, AddPromoCodeActivity.class);
                startActivityForResult(intent,200);
                overridePendingTransition(R.anim.lefttorightone, R.anim.righttoleftone);


            }
        });


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);

            }
        });
    }



    public void removePromoCode(String id, final int position) {

        final Progress progress = new Progress(ShowPromoCodeActivity.this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("promoCodeId", id);
        Ion.with(this).load(Constants.BASE_URL + "removePromo")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
                .setJsonObjectBody(jsonObject)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                progress.dismiss();
                String responce = "";
                if (e != null) {
                    return;
                }

                if(result!=null)
                {
                    Log.e("remove_order ",result.getResult());
                    switch (result.getHeaders().code()) {
                        case 200:

                            promoCodeModel.getResult().remove(position);
                            allPromocodeAdapter.notifyDataSetChanged();
                            if(promoCodeModel.getResult().size()==0)
                            {
                                text_view.setVisibility(View.VISIBLE);

                            }
                            Utils.showToast(ShowPromoCodeActivity.this,
                                    "Removed successfully");


                            break;
                        case 201:
                            promoCodeModel.getResult().remove(position);
                            allPromocodeAdapter.notifyDataSetChanged();
                            if(promoCodeModel.getResult().size()==0)
                            {
                                text_view.setVisibility(View.VISIBLE);

                            }
                            Utils.showToast(ShowPromoCodeActivity.this,
                                    "Removed successfully");

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


                        default:
                            responce = result.getResult();
                            break;
                    }
                }



            }
        });

    }



    public void getPromoCodeData() {
        final Progress progress = new Progress(ShowPromoCodeActivity.this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();



        Ion.with(this)
                .load("POST", Constants.BASE_URL +"allPromoCode")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception arg0, Response<String> result) {
                progress.dismiss();
                String responce = "";
                if (arg0 != null) {
                    return;
                }

                if(result!=null){
                    
                    try {
                        promoCodeModel= Utils.getgsonInstance().
                                fromJson(result.getResult(),AllPromoCodeModel.class);

                        if(promoCodeModel.getResult().size()>0)
                        {
                           allPromocodeAdapter=new AllPromocodeAdapter
                                   (ShowPromoCodeActivity.this,
                                    promoCodeModel.getResult());
                            list_viewpromocode.setAdapter(allPromocodeAdapter);
                            text_view.setVisibility(View.GONE);
                               list_viewpromocode.setVisibility(View.VISIBLE);

                        }else {
                            text_view.setVisibility(View.VISIBLE);
                            list_viewpromocode.setVisibility(View.GONE);
                        }
//
//                        JSONObject jObject = new JSONObject(result.getResult());
//                        // String message=jObject.optString("message");
//                        JSONArray jArray=jObject.getJSONArray("result");
//
//
//
//                            for (int i = 0; i < jArray.length(); i++) {
//
//                                JSONObject jObject_0=jArray.getJSONObject(i);
//
//                                String id=jObject_0.optString("id");
//                                String  code=jObject_0.optString("code");
//                                String  percent=jObject_0.optString("percent");
//                                String  maxDiscount=jObject_0.optString("maxDiscount");
//
//                                allPromocodeModel=new AllPromocodeModel();
//                                allPromocodeModel.setId(id);
//                                allPromocodeModel.setCode(code);
//                                allPromocodeModel.setPercent(percent);
//                                allPromocodeModel.setMaxDiscount(maxDiscount);
//                                allpromocode.add(allPromocodeModel);
//
//                                text_view.setVisibility(View.GONE);
//                                list_viewpromocode.setVisibility(View.VISIBLE);
//
//
//
//                            }
//
////                            text_view.setVisibility(View.VISIBLE);
////                            list_viewpromocode.setVisibility(View.GONE);
//
//
//
//
//
//                        AllPromocodeAdapter allPromocodeAdapter=new AllPromocodeAdapter(ShowPromoCodeActivity.this,allpromocode);
//                        list_viewpromocode.setAdapter(allPromocodeAdapter);


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null)
        {
            if(requestCode==200)
            {
                allpromocode.clear();
                list_viewpromocode.setAdapter(null);
                getPromoCodeData();
            }

        }

    }
}
