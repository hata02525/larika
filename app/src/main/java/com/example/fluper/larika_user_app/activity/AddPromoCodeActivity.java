package com.example.fluper.larika_user_app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.Progress;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class AddPromoCodeActivity extends AppCompatActivity {
    private SharedPreference preference;
    EditText editText;
    TextView textView;
    String promocode;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocode);
        preference = SharedPreference.getInstance(AddPromoCodeActivity.this);

        editText=(EditText)findViewById(R.id.et_your_code);
        textView=(TextView)findViewById(R.id.tv_send);



        findViewById(R.id.iv_backs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);

            }

        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promocode=editText.getText().toString().trim();

                if(editText.getText().toString().equals("")){
                    Toast.makeText(AddPromoCodeActivity.this, "Please enter promocode", Toast.LENGTH_SHORT).show();
                }else

                    {
                        Promocodeapply();

                }



                ///Toast.makeText(AddPromoCodeActivity.this, "Under development", Toast.LENGTH_SHORT).show();
            }
        });




    }



    private void Promocodeapply() {

        final Progress progress = new Progress(AddPromoCodeActivity.this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

         JsonObject jsonObject = new JsonObject();

         jsonObject.addProperty("code", promocode);



        Ion.with(this).load(Constants.BASE_URL + "addPromoCode")
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
                    Log.e("order ",result.getResult());
                    switch (result.getHeaders().code()) {
                        case 200:
                            try {
                    JSONObject jObject = new JSONObject(result.getResult());

                     message=jObject.optString("message");

                    Toast.makeText(AddPromoCodeActivity.this, message , Toast.LENGTH_SHORT).show();

                   Intent intent=new Intent();
                                setResult(200,intent);
                    overridePendingTransition(R.anim.lefttorightone, R.anim.righttoleftone);
                                finish();
                                //editText.setText("");


                } catch (JSONException e2) {
                    e2.printStackTrace();
                }


                            break;
                        case 201:
                            try {
                                JSONObject jObject = new JSONObject(result.getResult());

                                message=jObject.optString("message");

                                Toast.makeText(AddPromoCodeActivity.this, message , Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent();
                                setResult(200,intent);
                                overridePendingTransition(R.anim.lefttorightone, R.anim.righttoleftone);
                                finish();
                                //editText.setText("");


                            } catch (JSONException e2) {
                                e2.printStackTrace();
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
                        case 403:
                                            try {
                    JSONObject jObject = new JSONObject(result.getResult());

                     message=jObject.optString("message");
                                                Toast.makeText(AddPromoCodeActivity.this, message , Toast.LENGTH_SHORT).show();




                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                        default:
                            responce = result.getResult();
                            break;
                    }
                }



            }
        });

    }
}
