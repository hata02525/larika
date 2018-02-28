package com.example.fluper.larika_user_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.adapter.MyPageAdapter;
import com.example.fluper.larika_user_app.bean.AddCartMainModel;
import com.example.fluper.larika_user_app.bean.AddCartModel;
import com.example.fluper.larika_user_app.bean.AddMyProductMainModel;
import com.example.fluper.larika_user_app.bean.AddMyProductModel;
import com.example.fluper.larika_user_app.bean.HomeDataBean;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.database.DbHandler;
import com.example.fluper.larika_user_app.databinding.ActivityHomeBinding;
import com.example.fluper.larika_user_app.fragment.HomeItemFragment;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.AppSharedPrefernces;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.LocationManager;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {
    private MyPageAdapter adapter;
    public Progress progress;
    ArrayList<HomeDataBean> homeList;
    ActivityHomeBinding activityHomeBinding;
    boolean filterAllValue = false, filterSavoryValue = false, filterSweetValue = false,
            filterVegValue = false, filterFitnessValue = false, filterGreasyValue = false;
    private LocationManager manager;
    public Double latitude, longitude;
    private ImageView iv_logo;
    private FrameLayout main_container;
    SharedPreference preference;
    private ImageView[] dots;
    private int dotsCount = 0;
    private DbHandler db;
    List<AddMyProductModel> addProductModelList = new ArrayList<>();
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        AppSharedPrefernces.getsharedprefInstance(HomeActivity.this).setSession(true);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        preference = SharedPreference.getInstance(this);
        db = new DbHandler(this);
        db.open();
        addProductModelList = db.getData();
        init();
        getLatLng();
        listener();

        if (getIntent().hasExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG)
                && getIntent().getStringExtra(UtilPreferences.OPEN_CONFIRM_ORDER_DIALOG).equals("1")) {
            callHomeApi();

        }
        if (getIntent().hasExtra("AddProduct")) {
            addToCartApi();

        } else {
            setViewPagerAdapter();
        }


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void callHomeApi() {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();
        object.addProperty("lat", UtilPreferences.getFromPrefs(HomeActivity.this,UtilPreferences.CURRENT_LATITUDE,""));
        object.addProperty("lng", UtilPreferences.getFromPrefs(HomeActivity.this,UtilPreferences.CURRENT_LONGITUDE,""));

        Ion.with(this).load(Constants.BASE_URL + "homeScreen")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
                .setJsonObjectBody(object)
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
                        openConfirmOrderDialog();
                        responce = result.getResult();
                        try {
                            ArrayList<HomeDataBean> homeList = new ArrayList<>();
                            JSONObject object = new JSONObject(responce);
                            if (object.has("result")) {
                                activityHomeBinding.tvNoDish.setVisibility(View.GONE);
                                JSONArray array = object.getJSONArray("result");
                                if(array.length()>0)
                                {
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
//                                    bean.dishRating = Float.parseFloat(resultObject.optString("rating"));
                                        bean.dishStock = resultObject.optString("dishStock");
                                        bean.dishSummry = resultObject.optString("dishSummary");
                                        bean.dishPrice = resultObject.optString("dishPrice");
                                        bean.venderName = resultObject.optString("vendorName");
                                        bean.quantity = resultObject.optString("quantity");
                                        homeList.add(bean);
                                    }
                                    List<Fragment> fragments = getFragments(homeList);
                                    adapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
                                    activityHomeBinding.viewPager.setAdapter(adapter);
                                    dotsCount = fragments.size();
                                    setUiPageViewController();
                                    adapter.notifyDataSetChanged();
                                    closeFilterDialog();

                                }
                                else
                                {
                                    AlertDialog.Builder dialog =  new AlertDialog.Builder(HomeActivity.this);
                                    dialog.setCancelable(false);
                                    dialog
                                            .setMessage(object.getString("message"))
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                    startActivity(new Intent(HomeActivity.this,MoveActivity.class));
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .show();
                                }

                            } else {
                                Utils.showToast(HomeActivity.this, object.getString("message"));
                                activityHomeBinding.tvNoDish.setVisibility(View.VISIBLE);

//                                Intent intent=new Intent(HomeActivity.this,MoveActivity.class);
//                                startActivity(intent);
//                                finishAffinity();

                            }



                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case 201:
                        responce = result.getResult();
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
        });

    }

    public void hideDots() {
        activityHomeBinding.viewPagerCountDots.setVisibility(View.GONE);

        activityHomeBinding.viewPager.disableScroll(true);

    }

    public void showDots() {
        activityHomeBinding.viewPagerCountDots.setVisibility(View.VISIBLE);
        activityHomeBinding.viewPager.disableScroll(false);
        activityHomeBinding.rlTransparency.setClickable(false);
        activityHomeBinding.ivFilter.setClickable(true);
        activityHomeBinding.tvHome.setClickable(true);
        //        activityHomeBinding.viewPager.endFakeDrag();

    }

    private void addToCartApi() {
        progress.show();
        final AddCartMainModel addCartMainModel = new AddCartMainModel();
        AddCartModel addCartModel = new AddCartModel();
        final List<AddCartModel> addCartModelList = new ArrayList<>();
        for (int i = 0; i < addProductModelList.size(); i++) {
            addCartModel.setQuantity(String.valueOf(addProductModelList.get(i).getQuantity()));
            addCartModel.setVendorDishId(addProductModelList.get(i).getDishId());
//            addCartModelList.add(addCartModel);

        }

        addCartModelList.add(addCartModel);
        AddCartMainModel.getInstance().setCart(addCartModelList);
        AddCartMainModel.getInstance().setDeleteCart("1");
        String string = Utils.getgsonInstance().toJson(AddCartMainModel.getInstance());
        JSONObject jsonObject = null;
        JsonObject obj = null;
        try {
            jsonObject = new JSONObject(string);
            JsonParser parser = new JsonParser();
            obj = (JsonObject) parser.parse(jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        Ion.with(this).load("POST", Constants.BASE_URL + "addToCart")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
                .setJsonObjectBody(obj)
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

                            JSONObject mainObject = new JSONObject(responce);
                            JSONObject resultObject = mainObject.getJSONObject("result");

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }


                        break;
                    case 201:
                        responce = result.getResult();
                        try {
                            JSONObject mainObject = new JSONObject(responce);
                            AddMyProductMainModel addProductModel = Utils.getgsonInstance().
                                    fromJson(responce, AddMyProductMainModel.class);

                            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(addProductModel);
                            prefsEditor.putString("MyObject", json);
                            prefsEditor.commit();
                            setViewPagerAdapter();


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

    private void getLatLng() {
        manager = LocationManager.getInstance(this).buildAndConnectClient()
                .buildLocationRequest().setLocationHandlerListener(new LocationManager.LocationHandlerListener() {
                    @Override
                    public void locationChanged(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            manager.stopTracking();
                        }
                    }

                    @Override
                    public void lastKnownLocationAfterConnection(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }
                });
        manager.requestLocation();
    }

    private void setViewPagerAdapter() {
        homeList = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        if (getIntent() != null) {
            if (getIntent().hasExtra("homeDataList")) {
                homeList = (ArrayList<HomeDataBean>) getIntent().getSerializableExtra("homeDataList");
                if (homeList.size() > 0) {
                    fragments = getFragments(homeList);
                    dotsCount = fragments.size();
                    adapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
                    activityHomeBinding.viewPager.setAdapter(adapter);
                    activityHomeBinding.viewPager.setCurrentItem(0);
                    activityHomeBinding.viewPager.addOnPageChangeListener(this);
                    setUiPageViewController();
                }
            }
        }
    }


    private void listener() {
        activityHomeBinding.rlOrderConfirmation.setOnClickListener(this);
        activityHomeBinding.ivFilter.setOnClickListener(this);
        activityHomeBinding.imgCross.setOnClickListener(this);
        activityHomeBinding.tvHome.setOnClickListener(this);
        activityHomeBinding.tvAll.setOnClickListener(this);
        activityHomeBinding.tvSavry.setOnClickListener(this);
        activityHomeBinding.tvSweet.setOnClickListener(this);
        activityHomeBinding.tvVeg.setOnClickListener(this);
        activityHomeBinding.tvFitness.setOnClickListener(this);
        activityHomeBinding.tvGreasy.setOnClickListener(this);


    }

    private void init() {
        progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        findViewById(R.id.rl_transparency).setOnClickListener(this);
        main_container = (FrameLayout) findViewById(R.id.main_container);
        iv_logo.setOnClickListener(this);
        main_container.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        activityHomeBinding.ivFilter.setClickable(true);
        activityHomeBinding.tvHome.setClickable(true);
//        callHomeApi();

    }

    private List<Fragment> getFragments(ArrayList<HomeDataBean> list) {
        List<Fragment> fList = new ArrayList<Fragment>();
        for (int i = 0; i < list.size(); i++) {
            fList.add(HomeItemFragment.newInstance(list.get(i)));
        }
        return fList;
    }

    private void openFilterDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_upp);

        activityHomeBinding.rlTransparency.startAnimation(bottomDown);
        activityHomeBinding.rlTransparency.setClickable(true);
        activityHomeBinding.rlTransparency.setVisibility(View.VISIBLE);
        activityHomeBinding.viewPager.setBackgroundResource(R.color.black_transparent);

        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                activityHomeBinding.rlTransparency.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void closeConfirmOrderDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_downn);

        activityHomeBinding.rlOrderConfirmation.startAnimation(bottomDown);


        Utils.hideSoftKeyBoard(HomeActivity.this);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Intent intent=new Intent(HomeActivity.this,OrdersActivity.class);
                startActivity(intent);

                activityHomeBinding.rlOrderConfirmation.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void openConfirmOrderDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_upp);

        activityHomeBinding.rlOrderConfirmation.startAnimation(bottomDown);
        activityHomeBinding.rlOrderConfirmation.setClickable(true);
        activityHomeBinding.rlOrderConfirmation.setVisibility(View.VISIBLE);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                activityHomeBinding.rlOrderConfirmation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        switch (view.getId()) {
            case R.id.main_container:
                closeFilterDialog();
                break;
            case R.id.iv_logo:
                activityHomeBinding.ivLogo.setAlpha(1f);
                activityHomeBinding.ivLogo.startAnimation(animation1);
                startActivity(new Intent(this, MoveActivity.class));
                finishAffinity();
                break;
            case R.id.iv_filter:
                activityHomeBinding.ivFilter.setAlpha(1f);
                activityHomeBinding.ivFilter.startAnimation(animation1);
                openFilterDialog();
                break;
            case R.id.img_cross:
                activityHomeBinding.imgCross.setAlpha(1f);
                activityHomeBinding.imgCross.startAnimation(animation1);
                filterApi();

//                closeFilterDialog();

                break;
            case R.id.rl_transparency:
                activityHomeBinding.imgCross.setAlpha(1f);
                activityHomeBinding.imgCross.startAnimation(animation1);
                filterApi();

//                closeFilterDialog();

                break;

            case R.id.rl_order_confirmation:
                closeConfirmOrderDialog();
                break;
            case R.id.tv_home:
                intent = new Intent(HomeActivity.this, MenuScreenActivity.class);
                intent.putExtra("finder", "1");
                intent.putExtra("tempList", homeList);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_up, R.anim.fade_menu);
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                break;
            case R.id.tv_all:
                if (filterAllValue) {
//                    activityHomeBinding.tvAll.setBackgroundResource(android.R.color.transparent);
                    filterAllValue = false;
                } else {

                    activityHomeBinding.tvAll.setBackgroundResource(R.drawable.rectangle_white_border);
                    activityHomeBinding.tvFitness.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvGreasy.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvSavry.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvVeg.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvSweet.setBackgroundResource(android.R.color.transparent);
                    filterAllValue = true;
                    filterSavoryValue = false;
                    filterSweetValue = false;
                    filterVegValue = false;
                    filterFitnessValue = false;
                    filterGreasyValue = false;

                }
                break;
            case R.id.tv_savry:
                if (!filterSavoryValue) {
                    activityHomeBinding.tvSavry.setBackgroundResource(R.drawable.rectangle_white_border);
                    activityHomeBinding.tvAll.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvSweet.setBackgroundResource(android.R.color.transparent);
                    filterSavoryValue = true;
                    filterAllValue = false;
                    filterSweetValue = false;

                } else {
                    activityHomeBinding.tvSavry.setBackgroundResource(android.R.color.transparent);
                    filterSavoryValue = false;
                    setAllFilterField();

                }
                break;
            case R.id.tv_sweet:
                if (!filterSweetValue) {
                    activityHomeBinding.tvSweet.setBackgroundResource(R.drawable.rectangle_white_border);
                    activityHomeBinding.tvAll.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvSavry.setBackgroundResource(android.R.color.transparent);
                    filterSweetValue = true;
                    filterAllValue = false;
                    filterSavoryValue = false;
                } else {
                    activityHomeBinding.tvSweet.setBackgroundResource(android.R.color.transparent);
                    filterSweetValue = false;
                    setAllFilterField();
                }
                break;
            case R.id.tv_veg:
                if (!filterVegValue) {
                    activityHomeBinding.tvVeg.setBackgroundResource(R.drawable.rectangle_white_border);
                    activityHomeBinding.tvAll.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvFitness.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvGreasy.setBackgroundResource(android.R.color.transparent);
                    filterVegValue = true;
                    filterFitnessValue = false;
                    filterAllValue = false;
                    filterGreasyValue = false;
                } else {
                    activityHomeBinding.tvVeg.setBackgroundResource(android.R.color.transparent);
                    filterVegValue = false;
                    setAllFilterField();

                }
                break;
            case R.id.tv_fitness:
                if (!filterFitnessValue) {
                    activityHomeBinding.tvFitness.setBackgroundResource(R.drawable.rectangle_white_border);
                    activityHomeBinding.tvAll.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvVeg.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvGreasy.setBackgroundResource(android.R.color.transparent);
                    filterFitnessValue = true;
                    filterVegValue = false;
                    filterAllValue = false;
                    filterGreasyValue = false;
                } else {

                    activityHomeBinding.tvFitness.setBackgroundResource(android.R.color.transparent);
                    filterFitnessValue = false;
                    setAllFilterField();

                }
                break;
            case R.id.tv_greasy:
                if (!filterGreasyValue) {

                    activityHomeBinding.tvGreasy.setBackgroundResource(R.drawable.rectangle_white_border);
                    activityHomeBinding.tvAll.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvVeg.setBackgroundResource(android.R.color.transparent);
                    activityHomeBinding.tvFitness.setBackgroundResource(android.R.color.transparent);
                    filterGreasyValue = true;
                    filterVegValue = false;
                    filterFitnessValue = false;
                    filterAllValue = false;
                } else {
                    activityHomeBinding.tvGreasy.setBackgroundResource(android.R.color.transparent);
                    filterGreasyValue = false;
                    setAllFilterField();
                }
                break;


        }

    }

    private void setAllFilterField() {
        if (!filterSavoryValue) {
            if (!filterSweetValue) {
                if (!filterVegValue) {
                    if (!filterFitnessValue) {
                        if (!filterGreasyValue) {
                            activityHomeBinding.tvAll.setBackgroundResource(R.drawable.rectangle_white_border);
                            activityHomeBinding.tvFitness.setBackgroundResource(android.R.color.transparent);
                            activityHomeBinding.tvGreasy.setBackgroundResource(android.R.color.transparent);
                            activityHomeBinding.tvSavry.setBackgroundResource(android.R.color.transparent);
                            activityHomeBinding.tvVeg.setBackgroundResource(android.R.color.transparent);
                            activityHomeBinding.tvSweet.setBackgroundResource(android.R.color.transparent);
                            filterAllValue = true;
                            filterSavoryValue = false;
                            filterSweetValue = false;
                            filterVegValue = false;
                            filterFitnessValue = false;
                            filterGreasyValue = false;

                        }
                    }
                }
            }
        }
    }

    public void mainClickFalseFromFragment() {
        activityHomeBinding.rlTransparency.setClickable(true);
        activityHomeBinding.ivFilter.setClickable(false);
        activityHomeBinding.tvHome.setClickable(false);
    }

    private void filterApi() {
        int categoryValue = 0, nature = 0;
        if (filterAllValue) {
            categoryValue = 0;
        } else if (filterSavoryValue) {
            categoryValue = 1;
        } else if (filterSweetValue) {
            categoryValue = 2;
        } else if (filterVegValue) {
            nature = 0;
        } else if (filterFitnessValue) {
            nature = 1;
        } else if (filterGreasyValue) {
            nature = 2;
        }
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject object = new JsonObject();
        object.addProperty("lat", latitude);
        object.addProperty("lng", longitude);
        if (categoryValue != 0) {
            object.addProperty("category", categoryValue);
            object.addProperty("nature", nature);
        }

        Ion.with(this).load(Constants.BASE_URL + "homeScreen")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
                .setJsonObjectBody(object)
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
                            ArrayList<HomeDataBean> homeList = new ArrayList<>();
                            JSONObject object = new JSONObject(responce);
                            if (object.has("result")) {
                                JSONArray array = object.getJSONArray("result");
                                if(array.length()>0)
                                {
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

                                       /// bean.dishRating = Float.parseFloat(resultObject.optString("rating"));

                                        bean.dishStock = resultObject.optString("dishStock");
                                        bean.dishSummry = resultObject.optString("dishSummary");
                                        bean.dishPrice = resultObject.optString("dishPrice");
                                        bean.venderName = resultObject.optString("vendorName");
                                        bean.quantity = resultObject.optString("quantity");
                                        homeList.add(bean);
                                    }

                                    List<Fragment> fragments = getFragments(homeList);
                                    adapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
                                    activityHomeBinding.viewPager.setAdapter(adapter);
                                    dotsCount = fragments.size();
                                    setUiPageViewController();
                                    adapter.notifyDataSetChanged();
                                    closeFilterDialog();
                                }
                                else
                                {
                                    AlertDialog.Builder dialog =  new AlertDialog.Builder(HomeActivity.this);
                                    dialog.setCancelable(false);
                                    dialog
                                            .setMessage(object.getString("message"))
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                    startActivity(new Intent(HomeActivity.this,MoveActivity.class));
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .show();
                                }

                            } else {
                                Utils.showToast(HomeActivity.this, object.getString("message"));
                            }


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case 201:
                        responce = result.getResult();
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
        });


    }

    private void closeFilterDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_downn);

        activityHomeBinding.rlTransparency.startAnimation(bottomDown);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                activityHomeBinding.rlTransparency.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (dotsCount > 5) {
            for (int i = 0; i < 5; i++) {
                int dotToChange = position % 5;
                if (dotToChange == i) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));
                } else {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.unselected_dot));
                }

            }

        } else {
            for (int i = 0; i < dotsCount; i++) {
                if (i == position) {
                    dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));
                } else {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.unselected_dot));
                }

            }
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {


    }

    private void setUiPageViewController() {
        activityHomeBinding.viewPagerCountDots.removeAllViews();


//        dotsCount = adapter.getCount();
        if (dotsCount > 5) {
            dots = new ImageView[5];
            for (int i = 0; i < 5; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.unselected_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(4, 0, 4, 0);
                activityHomeBinding.viewPagerCountDots.addView(dots[i], params);

            }
            try {
                dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else {
            dots = new ImageView[dotsCount];

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.unselected_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(4, 0, 4, 0);
                activityHomeBinding.viewPagerCountDots.addView(dots[i], params);

            }
            try {
                dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }


    }


    public void callFinishActivity() {
        finish();
    }
}
