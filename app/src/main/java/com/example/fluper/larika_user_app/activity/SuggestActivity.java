package com.example.fluper.larika_user_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fluper.larika_user_app.Network.NetworkThread;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.utils.Progress;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class SuggestActivity extends AppCompatActivity implements NetworkCallback {
    private EditText  et_whtitIs, et_wfindIt;
    private Progress progress;
    RelativeLayout layoutsuggest;
    EditText et_subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);


        et_subject = (EditText) findViewById(R.id.et_subject);
        et_whtitIs = (EditText) findViewById(R.id.et_whtitIs);
        et_wfindIt = (EditText) findViewById(R.id.et_wfindIt);
        layoutsuggest=(RelativeLayout) findViewById(R.id.layoutsuggest);
        layoutsuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        });

        findViewById(R.id.tv_saveChnages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                if (validateField()) {
                    openPopUp();
                   // callSuggestMealApi();
                }
            }
        });


        et_subject.setEnabled(false);


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);
            }
        });
    }

    private void openPopUp() {


            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

            alertDialog.setTitle("Larika");
            alertDialog.setMessage("Are you sure you want to suggest a meal ?");
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    if (validateField()) {
                        callSuggestMealApi();
                    }


                    dialog.dismiss();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {




                    dialog.cancel();
                }
            });
            alertDialog.show();
        }





    private void callSuggestMealApi() {
        progress.show();

        JsonObject object = new JsonObject();

        object.addProperty("title", et_subject.getText().toString());
        object.addProperty("whatIsIt", et_whtitIs.getText().toString().replaceAll("\n", ""));
        object.addProperty("whereFindIt", et_wfindIt.getText().toString());

        NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "suggestMeals");
        thread.getNetworkResponse(this, object, 15000);
    }

    private boolean validateField() {



       /* if (et_subject.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter Meal title", Toast.LENGTH_SHORT).show();
            return false;
        }*/


        if (et_whtitIs.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter What is it ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_wfindIt.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter where you find it", Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;
    }


    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {
        progress.dismiss();

        if (result != null && !result.equalsIgnoreCase("")) {
            switch (status) {
                case 200:
                    try {

                        JSONObject obj = new JSONObject(result);
                        Toast.makeText(this, obj.optString("message"), Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(this, MoveActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 201:
                    try {
                        JSONObject obj = new JSONObject(result);
                        Toast.makeText(this, obj.optString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MenuScreenActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 204:
                    break;
                case 400:
                    try {
                        JSONObject obj = new JSONObject(result);
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
    }
}
