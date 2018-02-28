package com.example.fluper.larika_user_app.fragment;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.adapter.OnTheGoAdapter;
import com.example.fluper.larika_user_app.bean.OnTheGoModel;
import com.example.fluper.larika_user_app.bean.OrderResults;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.databinding.FragmentOnTheGoBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnTheGoFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {
    FragmentOnTheGoBinding fragmentOnTheGoBinding;
    List<OrderResults> orderResultsList = new ArrayList<>();
    private SharedPreference preference;
    private OnTheGoModel onTheGoModel;
    private ListView list_view;
    View view;


    public OnTheGoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentOnTheGoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_the_go, container, false);
        view = fragmentOnTheGoBinding.getRoot();
        list_view = (ListView) view.findViewById(R.id.list_view);
        preference = SharedPreference.getInstance(getActivity());
        getItemData();
        listener();

        return view;
    }

    private void listener() {
        fragmentOnTheGoBinding.swipeRefresh.setOnRefreshListener(this);

    }

    private void getItemData() {
        final Progress progress = new Progress(getActivity());
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();

        String accessToken=preference.getString(Constants.ACCESS_TOKEN, "");

        Log.d("accessTocken",accessToken);
        Ion.with(getActivity()).load("POST",
                Constants.BASE_URL + "onTheGo")
                .setHeader("accessToken", accessToken)
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                String response;
                fragmentOnTheGoBinding.swipeRefresh.setRefreshing(false);
                if (result != null) {
                    switch (result.getHeaders().code()) {
                        case 200:
                            onTheGoModel = Utils.getgsonInstance().fromJson(result.getResult(), OnTheGoModel.class);
                            fragmentOnTheGoBinding.tvNoProduct.setVisibility(View.GONE);
                            fragmentOnTheGoBinding.listView.setVisibility(View.VISIBLE);
                            if (onTheGoModel.getResult().size() > 0) {
                                OnTheGoAdapter onTheGoAdapter = new OnTheGoAdapter("1",
                                        getActivity(), onTheGoModel.getResult(),
                                        onTheGoModel);
                                fragmentOnTheGoBinding.listView.setAdapter(onTheGoAdapter);
                            }
                            break;
                        case 401:
                            fragmentOnTheGoBinding.tvNoProduct.setVisibility(View.VISIBLE);
                            fragmentOnTheGoBinding.listView.setVisibility(View.GONE);
                            try {
                                String message=new JSONObject(result.getResult()).
                                        getString("message");
                                Utils.showToast(getActivity(),message);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                            break;
                        case 500:
                            Toast.makeText(getActivity(),
                                    "Not responding.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }


                progress.dismiss();


            }
        });

    }

    @Override
    public void onRefresh() {
        getItemData();

    }
}