package com.example.fluper.larika_user_app.fragment;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    FragmentOnTheGoBinding fragmentOnTheGoBinding;
    List<OrderResults>orderResultsList=new ArrayList<>();
    private SharedPreference preference;



    public HistoryFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentOnTheGoBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_on_the_go,container,false);
        View view=fragmentOnTheGoBinding.getRoot();
        preference = SharedPreference.getInstance(getActivity());
        fragmentOnTheGoBinding.swipeRefresh.setOnRefreshListener(this);
        getItemData();
//        OnTheGoAdapter onTheGoAdapter=new OnTheGoAdapter(getActivity(),orderResultsList);
//        fragmentOnTheGoBinding.listView.setAdapter(onTheGoAdapter);

        return view;
    }

    private void getItemData() {
        final Progress progress = new Progress(getActivity());
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();

        Ion.with(getActivity()).load("POST", Constants.BASE_URL + "orderHistory")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN,""))
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                String response;
                fragmentOnTheGoBinding.swipeRefresh.setRefreshing(false);


                if(result!=null)
                {
                    switch (result.getHeaders().code())
                    {
                        case 200:
                            OnTheGoModel onTheGoModel= Utils.getgsonInstance().fromJson(result.getResult(),OnTheGoModel.class);
                            if(onTheGoModel.getResult().size()>0)
                            {
                                fragmentOnTheGoBinding.tvNoProduct.setVisibility(View.GONE);
                                fragmentOnTheGoBinding.listView.setVisibility(View.VISIBLE);
                                OnTheGoAdapter onTheGoAdapter=new OnTheGoAdapter("2",getActivity(),onTheGoModel.getResult(),onTheGoModel);
                                fragmentOnTheGoBinding.listView.setAdapter(onTheGoAdapter);
                            }
                            else
                            {
                                fragmentOnTheGoBinding.tvNoProduct.setVisibility(View.VISIBLE);
                                fragmentOnTheGoBinding.listView.setVisibility(View.GONE);
                               /// fragmentOnTheGoBinding.tvNoProduct.setText(onTheGoModel.getMessage());
                            }
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
