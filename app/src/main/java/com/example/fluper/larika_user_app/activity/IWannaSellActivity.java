package com.example.fluper.larika_user_app.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.ActivityIwannaSellBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;


public class IWannaSellActivity extends AppCompatActivity {

    private SharedPreference preference;

    // activityEditProfileBinding;

    private ActivityIwannaSellBinding activityIwannaSellBinding;
  TextView tv_countrycode;
    EditText edit_text;

    ListView pro_listview;
     private String countryCode;
    ImageView button_onclick;

    private List<String> productList = new ArrayList<>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityIwannaSellBinding= DataBindingUtil.setContentView(this,R.layout.activity_iwanna_sell);
        preference = SharedPreference.getInstance(this);
        tv_countrycode=(TextView)findViewById(R.id.tv_countrycode);

        activityIwannaSellBinding.etEmail.setText(preference.getString(Constants.USER_MAIL,""));
        activityIwannaSellBinding.etName.setText(preference.getString(Constants.USER_NAME,""));
       activityIwannaSellBinding.etNumber.setText(preference.getString(Constants.USER_CONTACT,""));



        if (preference.getString(Constants.USER_CONTACT, "").equals("null"))
        {
            activityIwannaSellBinding.etNumber.setText("");
            activityIwannaSellBinding.etNumber.setClickable(true);
            activityIwannaSellBinding.etNumber.setFocusableInTouchMode(true);
            activityIwannaSellBinding.etNumber.setFocusable(true);

        }


        tv_countrycode.setText(preference.getString(Constants.COUNTYR_CODE, ""));


      /*  if(preference.getString(Constants.COUNTYR_CODE, "").equals("null")){

            tv_countrycode.setText("+91");
            tv_countrycode.setEnabled(true);
            tv_countrycode.setClickable(true);
            tv_countrycode.setFocusable(true);
            tv_countrycode.setFocusableInTouchMode(true);

        }*/

        tv_countrycode.setOnClickListener(new View.OnClickListener() {
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

                        tv_countrycode.setText("+" + GetCountryPhoneCode(code));
                        countryCode = tv_countrycode.getText().toString().trim();
                        picker.dismiss();
                    }


                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");

            }
        });
       /* {
            activityIwannaSellBinding.etNumber.setText(preference.getString(Constants.USER_CONTACT, ""));
        }*/


        // activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        init();
        listener();
//        getUserDetails();



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

    private void getUserDetails() {


        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();



        Ion.with(this).load("POST", Constants.BASE_URL + "sell")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                String response;

                progress.dismiss();



            }
        });
    }

    private void listener() {
        activityIwannaSellBinding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation())

                    updateWannaSell();

            }
        });
        final AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);

        animation1.setDuration(500);
        button_onclick.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                if(edit_text.getText().toString().trim().length()>0)
                {
                    productList.add(edit_text.getText().toString());

                    edit_text.getText().clear();

                    ProductAdapter productAdapter = new ProductAdapter(IWannaSellActivity.this, productList);
                    pro_listview.setAdapter(productAdapter);
                    Utils.hideSoftKeyBoard(IWannaSellActivity.this);
                }
                else
                {
                    Utils.showToast(IWannaSellActivity.this,"Please enter product");
                }

            }

        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                findViewById(R.id.iv_back).setAlpha(1f);

                findViewById(R.id.iv_back).setAnimation(animation1);

                finish();

                overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);


            }

        });

        activityIwannaSellBinding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityIwannaSellBinding.etName.setClickable(true);
                activityIwannaSellBinding.etNumber.setClickable(true);
                activityIwannaSellBinding.etEmail.setClickable(true);
                tv_countrycode.setClickable(true);
                tv_countrycode.setEnabled(true);

                activityIwannaSellBinding.etName.setFocusableInTouchMode(true);
                activityIwannaSellBinding.etNumber.setFocusableInTouchMode(true);
                activityIwannaSellBinding.etEmail.setFocusableInTouchMode(true);
                tv_countrycode.setFocusableInTouchMode(true);

                activityIwannaSellBinding.etName.setFocusable(true);
                activityIwannaSellBinding.etNumber.setFocusable(true);
                activityIwannaSellBinding.etEmail.setFocusable(true);
                tv_countrycode.setFocusable(true);

            }
        });
    }

    private boolean validation() {
        if(activityIwannaSellBinding.etName.getText().toString().trim().length()==0)
        {
            Utils.showToast(IWannaSellActivity.this,"Please enter your name");
            return false;
        }
        if(activityIwannaSellBinding.etNumber.getText().toString().trim().length()==0)
        {
            Utils.showToast(IWannaSellActivity.this,"Please enter number");
            return false;
        }

        if (activityIwannaSellBinding.etNumber.getText().toString().length() < 8) {
            Utils.showToast(IWannaSellActivity.this, "Mobile number should be more than 8 digits");
            return false;
        }

        /*if(tv_countrycode.getText().toString().trim().length()==0)
        {
            Utils.showToast(IWannaSellActivity.this,"Please select country code");
            return false;
        }*/
       /* if(activityIwannaSellBinding.etNumber.getText().toString().trim().length()==8)
        {
            Utils.showToast(IWannaSellActivity.this,"Please enter 8 digits phone no.");
            return false;
        }
*/

       /* if(activityIwannaSellBinding.etEmail.getText().toString().trim().length()==0)
        {
            Utils.showToast(IWannaSellActivity.this,"Please enter email");
            return false;
        }
*/

        if(!Utils.validateEmail(activityIwannaSellBinding.etEmail.getText().toString()))
        {
            Utils.showToast(IWannaSellActivity.this,"Please enter valid email");
            return false;
        }
        if(productList.size()==0)
        {
            Utils.showToast(IWannaSellActivity.this,"Please add at least one product");
            return false;
        }
        return true;
    }

    private void init() {
        pro_listview = (ListView) findViewById(R.id.tv_iwfnnff);
        button_onclick = (ImageView) findViewById(R.id.button_onclick);
        edit_text = (EditText) findViewById(R.id.tv_fruitmame);
    }


    private void updateWannaSell()
    {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("name",activityIwannaSellBinding.etName.getText().toString());
        jsonObject.addProperty("contact",activityIwannaSellBinding.etNumber.getText().toString());
        jsonObject.addProperty("email",activityIwannaSellBinding.etEmail.getText().toString());
        jsonObject.addProperty("countryCode",tv_countrycode.getText().toString());

        JsonArray productArray=new JsonArray();
        for (int i = 0; i <productList.size() ; i++) {
            JsonObject productObject=new JsonObject();
            productObject.addProperty("name",productList.get(i));
            productArray.add(productObject);

        }
        jsonObject.add("productName",productArray);
        final Progress progress = new Progress(IWannaSellActivity.this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Ion.with(this)
                .load("POST", Constants.BASE_URL + "sell")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(jsonObject)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception arg0, Response<String> result) {

                progress.dismiss();
                Intent intent;
                if(result!=null)
                {
                    switch (result.getHeaders().code())
                    {
                        case 200:
                            Toast.makeText(IWannaSellActivity.this, "Update successfull", Toast.LENGTH_SHORT).show();
//                            intent=new Intent(IWannaSellActivity.this,MenuScreenActivity.class);
//                            startActivity(intent);
                            finish();
                            break;
                        case 201:
                            Toast.makeText(IWannaSellActivity.this, "Update successfull", Toast.LENGTH_SHORT).show();
//                           intent=new Intent(IWannaSellActivity.this,MenuScreenActivity.class);
//                            startActivity(intent);
                            finish();
                            break;
                        case 400:
                            break;
                        case 401:
                            break;
                    }


                    try {

//                        JSONObject jObject = new JSONObject(result.getResult());
//
//
//                        /// JSONObject object= jObject.JSONObject("message");
//
//
//                        JSONArray jArray = jObject.getJSONArray("search");
//
//                        for (int i = 0; i < jArray.length(); i++) {
//
//                            JSONObject jObject_0 = jArray.getJSONObject(i);
//
//                            JSONObject jObj = jObject_0
//
//                                    .getJSONObject("item_info");









                    } catch (Exception e) {

                        e.printStackTrace();

                    }
                }


            }

        });


    }

}