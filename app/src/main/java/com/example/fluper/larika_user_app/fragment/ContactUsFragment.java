package com.example.fluper.larika_user_app.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fluper.larika_user_app.Network.NetworkThread;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.activity.ContactUsActivity;
import com.example.fluper.larika_user_app.activity.MoveActivity;
import com.example.fluper.larika_user_app.callbacks.NetworkCallback;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment implements NetworkCallback {

    private EditText et_subject, et_messgaBox;
    private View view;
    private ContactUsActivity activity;
    RelativeLayout layoutcontactus,layout_send;
    TextView tv_send;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        activity = (ContactUsActivity) getActivity();
        initView();
        return view;
    }

    private void initView() {
        et_subject = (EditText) view.findViewById(R.id.et_subject);
        et_messgaBox = (EditText) view.findViewById(R.id.et_messgaBox);
        layoutcontactus=(RelativeLayout)view.findViewById(R.id.layoutcontactus);
        //layout_send=(RelativeLayout)view.findViewById(R.id.layout_send);
        ///tv_send=(TextView)view.findViewById(R.id.tv_send);

        layoutcontactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        et_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tv_send.setVisibility(View.GONE);
            }
        });

        et_messgaBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // tv_send.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(activity)) {
                    if(checkValidation())
                    {
                        callApiForContactUs();
                    }

                } else {
                    Toast.makeText(activity, Constants.ERR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean checkValidation()
    {
        if(et_subject.getText().toString().trim().length()==0)
        {
            Utils.showToast(getActivity(),"Please enter subject");
            return false;
        }
        else if(et_messgaBox.getText().toString().trim().length()==0)
        {
            Utils.showToast(getActivity(),"Please enter message");
            return false;
        }
        return true;
    }

    private void callApiForContactUs() {
        activity.progress.show();

        JsonObject object = new JsonObject();
        object.addProperty("subject", et_subject.getText().toString());
        object.addProperty("message", et_messgaBox.getText().toString());

        NetworkThread thread = new NetworkThread(this, Constants.BASE_URL + "contact");
        thread.getNetworkResponse(activity, object, 15000);
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {
        activity.progress.dismiss();
        if (result != null && !result.equalsIgnoreCase("")) {
            JSONObject object = null;
            switch (status) {
                case 200:
                    try {
                        object = new JSONObject(result);
                        JSONObject resultObject = object.getJSONObject("message");
                        Toast.makeText(activity, resultObject.optString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity, MoveActivity.class));
                        activity.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 201:
                    try {
                        object = new JSONObject(result);
                        String message = object.getString("message");
                        if(message!=null && !message.equals(""))
                        {
                            Utils.showToast(activity,message);
                        }
                        activity.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    startActivity(new Intent(activity, MoveActivity.class));
//                    activity.finish();
                    break;
                case 204:
                    break;
                case 400:
                    try {
                        object = new JSONObject(result);
                        JSONObject resultObject = object.getJSONObject("message");
                        Toast.makeText(activity, resultObject.optString("message"), Toast.LENGTH_SHORT).show();
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
}
