package com.example.fluper.larika_user_app.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.activity.HomeActivity;
import com.example.fluper.larika_user_app.activity.LoginSignUpActivity;
import com.example.fluper.larika_user_app.activity.MeetingPointActivity;
import com.example.fluper.larika_user_app.activity.PaymentActivity;
import com.example.fluper.larika_user_app.bean.AddCartMainModel;
import com.example.fluper.larika_user_app.bean.AddCartModel;
import com.example.fluper.larika_user_app.bean.AddMyProductMainModel;
import com.example.fluper.larika_user_app.bean.AddMyProductModel;
import com.example.fluper.larika_user_app.bean.AllPromoCodeModel;
import com.example.fluper.larika_user_app.bean.AllPromoCodeResultModel;
import com.example.fluper.larika_user_app.bean.HomeDataBean;
import com.example.fluper.larika_user_app.bean.PaymentCardMainModel;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.database.DbHandler;
import com.example.fluper.larika_user_app.databinding.FragmentHomeItemBinding;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.GetAllPromoCodes;
import com.example.fluper.larika_user_app.utils.GetcardDetails;
import com.example.fluper.larika_user_app.utils.LoginSuccessFully;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeItemFragment extends Fragment implements View.OnClickListener, LoginSuccessFully {


    private HomeDataBean bean;
    List<AddMyProductModel> addProductModelList = new ArrayList<>();
    List<AddMyProductModel> copyList = new ArrayList<>();
    private DbHandler db;
    FragmentHomeItemBinding fragmentHomeItemBinding;
    private SharedPreference prefrence;
    private CardDiloagAdapter cardDiloagAdapter;
    private RatingBar dishRating;
    private TextView tv_stock, tv_dishSummry, tv_vendrName;
    private SharedPreferences sharedPrefs;
    private PaymentCardMainModel paymentCardMainModel;
    private boolean paymentCardSelected = false;
    private AddMyProductMainModel addMyProductMainModel;
    private boolean isLogin = false;
    private String updatedQuantity = "", updatedDishId = "";
    String priceTosend = "0";
    private TextView tv_NoCard;
    private RelativeLayout rl_card;
    private ImageView ivApplogo;
    private TextView category, nature;
    public static HomeItemFragment homeItemFragment;
    public static String carddata;
    private AllPromoCodeResultModel promocodeToapply;
    private GetAllPromoCodes getAllpromocades;
    private RelativeLayout rlDishbackground;


    public static final HomeItemFragment newInstance(HomeDataBean bean) {

        HomeItemFragment f = new HomeItemFragment();
        Bundle bdl = new Bundle();
        bdl.putSerializable("bean", bean);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeItemBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_item, container, false);
        View v = fragmentHomeItemBinding.getRoot();
        prefrence = SharedPreference.getInstance(getActivity());

        db = new DbHandler(getActivity());
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        checkLoginOrNot();

        if (getArguments() != null) {
            bean = (HomeDataBean) getArguments().getSerializable("bean");
        }
        init(v);
        listener();

        strechView();
        setDataToCard();
        if (isLogin) {
            getAllpromocades = new GetAllPromoCodes(getActivity(), HomeItemFragment.this);
            getAllpromocades.carddetails();
        }
        getPaymentCardData();
        if (UtilPreferences.getFromPrefs(getActivity(),
                UtilPreferences.FROM_CART_DIALOG, "").equals("1")) {
            showPaymentDataAfterSelected();

        } else if (UtilPreferences.getFromPrefs(getActivity(), UtilPreferences.FROM_CART_DIALOG, "").equals("2")) {
            getProductDataFromPrefernces();
        }
//        else
//        {
//            getProductDataFromPrefernces();
//        }

        return v;
    }

    private void checkLoginOrNot() {
        if (!prefrence.getString(Constants.ACCESS_TOKEN, "").equals("token")
                && !prefrence.getString(Constants.ACCESS_TOKEN, "").equals("")) {
            isLogin = true;

        } else {
            isLogin = false;

        }
    }

    //    After selected cards
    private void showPaymentDataAfterSelected() {
        addProductModelList.clear();
//
        Gson gson = new Gson();
        String json = sharedPrefs.getString("addProductList", "");
        addMyProductMainModel = gson.fromJson(json, AddMyProductMainModel.class);
        addProductModelList = addMyProductMainModel.getResult();


        try {
            DecimalFormat df = new DecimalFormat("#.00#");
            if (addProductModelList.size() > 0) {
                int quantity = Integer.parseInt(addProductModelList.get(0).getQuantity());
                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                double price = parseDouble(priceString);
                price = price * quantity;

                String priceToShow = df.format(price);
                priceToShow = priceToShow.replace(".", ",");
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);
               /* int quan = 0;
                String p=addProductModelList.get(0).getDishPrice().replace(",",
                        ".");
                price = price + parseDouble(p);
                quan = parseInt(addProductModelList.get(0).getQuantity());
                price=price * quan;*/
                /*DecimalFormat formatter;*/

               /* if(price<1000)
                {
                    formatter = new DecimalFormat("#,##");
                }
                else
                {
                    formatter = new DecimalFormat("#,###,###");
                }*/
                //  DecimalFormat formatt = new DecimalFormat("#,###");
                /*String pricetoshow=String.valueOf(price).replace(".",",");*/

                //String output = formatter.format(price);
               /* fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + pricetoshow);*/


//                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + price * quan);

                updatedDishId = addProductModelList.get(0).getDishId();
                updatedQuantity = addProductModelList.get(0).getQuantity();
                updateProductQuantityApi();

                cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                        addProductModelList, HomeItemFragment.this);
                fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);


                db.open();
                db.insertContact(addProductModelList.get(0).getDishId(), addProductModelList.get(0).getVendorId(),
                        addProductModelList.get(0).getDishStock(), addProductModelList.get(0).getDishName(),
                        addProductModelList.get(0).getDishPrice(), addProductModelList.get(0).getQuantity());

            }

        } catch (Exception e) {

        }
        openCartDialog();
        UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.FROM_CART_DIALOG, "0");
    }

    public void increaseProduct(List<AddMyProductModel> cardModelList, int position) {
        DecimalFormat df = new DecimalFormat("#.00#");
        if (isLogin) {


//            call add to cart api
// double price = 0;
            addProductModelList = new ArrayList<>();
            addProductModelList.clear();
            addProductModelList.addAll(cardModelList);
            int value = parseInt(cardModelList.get(position).getQuantity());
            value++;
            addProductModelList.get(position).setQuantity(String.valueOf(value));
//            updateQuantityApi();


            if (addProductModelList != null && addProductModelList.size() > 0) {
                updatedDishId = addProductModelList.get(0).getDishId();
                updatedQuantity = addProductModelList.get(0).getQuantity();

                cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                        addProductModelList, HomeItemFragment.this);
                fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);

                int quantity = Integer.parseInt(addProductModelList.get(position).getQuantity());
                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                double price = parseDouble(priceString);
                price = price * quantity;

                String priceToShow = df.format(price);
                priceToShow = priceToShow.replace(".", ",");
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);

               /* String priceStringg = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                price = parseDouble(priceStringg);*/
//                DecimalFormat formatter;
//
//                if(price<1000)
//                {
//                    formatter = new DecimalFormat("#,##");
//                }
//                else
//                {
//                    formatter = new DecimalFormat("#,###,###");
//                }
//                String output = formatter.format(price);
//                addProductModelList.get(0).setDishPrice(output);





              /*  Double priceValue = parseDouble(addProductModelList.get(position).getQuantity());

                price = priceValue * price;
                String priceToShow=String.valueOf(price).replace(".",",");*/

                /*DecimalFormat formatter;*/

                /*if(price<1000)
                {
                    formatter = new DecimalFormat("#,##");
                }
                else
                {
                    formatter = new DecimalFormat("#,###,###");
                }
                String output = formatter.format(price);*/

               /* DecimalFormat formatt = new DecimalFormat("#,###");*/

                /// price = priceValue * Long.parseLong(addProductModelList.get(position).getDishPrice());

//                for (int i = 0; i <addProductModelList.size() ; i++) {
//                    price=price+Long.parseLong(addProductModelList.get(i).getDishPrice());
//                }
                // DecimalFormat myFormatter = new DecimalFormat("#,##");
                //String output = myFormatter.format(price);
              /*  fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);*/

            }
            cardDiloagAdapter.notifyDataSetChanged();
        } else {
            //        update product quantity to database
            int value = parseInt(cardModelList.get(position).getQuantity());
            value++;
            db.open();
            db.updateCol(cardModelList.get(position).getDishName(), String.valueOf(value));
            addProductModelList = db.getData();
            if (addProductModelList != null && addProductModelList.size() > 0) {
                cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                        addProductModelList, HomeItemFragment.this);
                fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);


                int quantity = Integer.parseInt(addProductModelList.get(position).getQuantity());
                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                double price = parseDouble(priceString);
                price = price * quantity;

                String priceToShow = df.format(price);
                priceToShow = priceToShow.replace(".", ",");
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);
                //  long priceValue = parseLong(addProductModelList.get(position).getQuantity());
              /*  int quantity=Integer.parseInt(addProductModelList.get(position).getQuantity());

                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                price = quantity * parseDouble(priceString);
                String pricetoshow=String.valueOf(price).replace(".",",");
*/
                ///price = priceValue * Long.parseLong(addProductModelList.get(position).getDishPrice());

//                for (int i = 0; i <addProductModelList.size() ; i++) {
//                    price=price+Long.parseLong(addProductModelList.get(i).getDishPrice());
//                }
                /// DecimalFormat myFormatter = new DecimalFormat("#,##");
                ///String output = myFormatter.format(price);
                //  fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + pricetoshow);

            }
            cardDiloagAdapter.notifyDataSetChanged();
            db.close();

        }

    }

    private void updateQuantityApi() {

        final Progress progress = new Progress(getActivity());
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("dishId", addProductModelList.get(0).getDishId());
        jsonObject.addProperty("quantity", addProductModelList.get(0).getQuantity());
        Ion.with(this).load("POST", Constants.BASE_URL + "updateDishQuantity")
                .setJsonObjectBody(jsonObject)
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
                            JSONObject resultObject = mainObject.getJSONObject("result");

                        } catch (JSONException e1) {
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


    public void decreaseProduct(List<AddMyProductModel> cardModelList, int position) {
        DecimalFormat df = new DecimalFormat("#.00#");
        addProductModelList = new ArrayList<>();
        addProductModelList.clear();
        addProductModelList.addAll(cardModelList);
        int value = parseInt(cardModelList.get(position).getQuantity());
        value--;

        if (isLogin) {
//            call add to cart api

            addProductModelList.get(position).setQuantity(String.valueOf(value));
//                updateQuantityApi();
            if (addProductModelList != null && addProductModelList.size() > 0) {
                updatedDishId = addProductModelList.get(0).getDishId();
                updatedQuantity = addProductModelList.get(0).getQuantity();
                cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                        addProductModelList, HomeItemFragment.this);
                fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);
                int quantity = Integer.parseInt(addProductModelList.get(position).getQuantity());
                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                double price = parseDouble(priceString);
                price = price * quantity;

                String priceToShow = df.format(price);
                priceToShow = priceToShow.replace(".", ",");
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);

                /// price = priceValue * Long.parseLong(addProductModelList.get(position).getDishPrice());

//                for (int i = 0; i <addProductModelList.size() ; i++) {
//                    price=price+Long.parseLong(addProductModelList.get(i).getDishPrice());
//                }
                // DecimalFormat myFormatter = new DecimalFormat("#,##");
                ///String output = myFormatter.format(price);
                  /*  DecimalFormat formatter;

                    if(price<1000)
                    {
                        formatter = new DecimalFormat("#,##");
                    }
                    else
                    {
                        formatter = new DecimalFormat("#,###,###");
                    }*/
                // DecimalFormat formett = new DecimalFormat("#,###.00");


                //   String output = formatter.format(price);


            }

            cardDiloagAdapter.notifyDataSetChanged();

        } else {
            //        update product quantity to database
            addProductModelList.clear();


//            addProductModelList.get(position).setQuantity(String.valueOf(value));

//            try {
//
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//            }
            db.open();
            db.updateCol(cardModelList.get(position).getDishName(), String.valueOf(value));
            addProductModelList = db.getData();
            if (addProductModelList != null && addProductModelList.size() > 0) {
                cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                        addProductModelList, HomeItemFragment.this);
                fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);

                int quantity = Integer.parseInt(addProductModelList.get(position).getQuantity());
                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                double price = parseDouble(priceString);
                price = price * quantity;

                String priceToShow = df.format(price);
                priceToShow = priceToShow.replace(".", ",");
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);

               /* long priceValue = parseLong(addProductModelList.get(position).getQuantity());

                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                price = priceValue * parseDouble(priceString);
                String priceToshow=String.valueOf(price).replace(".",",");
                //DecimalFormat myFormatter = new DecimalFormat("#,##");
                //String output = myFormatter.format(price);
                //// price = priceValue * Long.parseLong(addProductModelList.get(position).getDishPrice());
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToshow);*/

            }
            cardDiloagAdapter.notifyDataSetChanged();
            db.close();

        }

    }

    // add previous local product to server that are saved in prefernce
    private void getProductDataFromPrefernces() {
        DecimalFormat df = new DecimalFormat("#.00#");
        if (UtilPreferences.getFromPrefs(getActivity(),
                UtilPreferences.ADD_CART, "").equals("AddProduct")) {

            addProductModelList.clear();
//
            Gson gson = new Gson();
            String json = sharedPrefs.getString("MyObject", "");
            addMyProductMainModel = gson.fromJson(json, AddMyProductMainModel.class);
            addProductModelList = addMyProductMainModel.getResult();


            try {

                if (addProductModelList.size() > 0) {
                    updatedDishId = addProductModelList.get(0).getDishId();
                    updatedQuantity = addProductModelList.get(0).getQuantity();

                    cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                            addProductModelList, HomeItemFragment.this);
                    fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);
                    int quantity = Integer.parseInt(addProductModelList.get(0).getQuantity());
                    String priceString = addProductModelList.get(0).getDishPrice()
                            .replace(",", ".");
                    double price = parseDouble(priceString);
                    price = price * quantity;
                    String priceToShow = df.format(price);
                    priceToShow = priceToShow.replace(".", ",");
                    fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);


                }

            } catch (Exception e) {

            }

            UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.ADD_CART, "0");
//            db.open();
//            addProductModelList=db.getData();
//            locallyAddToCart();
            openCartDialog();


        }
    }

    private String getPaymentCardData() {
        String last = null;
        paymentCardMainModel = new PaymentCardMainModel();
        Gson gson = new Gson();
        String json = sharedPrefs.getString("MyObject", "");
//        if(paymentCardMainModel!=null)
        paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
        if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
            if (paymentCardMainModel.getAddCardModelList().size() == 1) {
                paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
            }
            for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {
                if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                    paymentCardSelected = true;
                    carddata = paymentCardMainModel.getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.getAddCardModelList().get(i).getCardNumber().length() - 4);
                }

            }
        }
        return last;
    }

    private void strechView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        fragmentHomeItemBinding.mainStrechLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        fragmentHomeItemBinding.stretchTopScrollView.getTopView();
    }

    private void listener() {
        fragmentHomeItemBinding.tvDishPrice.setOnClickListener(this);
        fragmentHomeItemBinding.tvFindMeetingPoint.setOnClickListener(this);
        fragmentHomeItemBinding.tvKeepLooking.setOnClickListener(this);
        fragmentHomeItemBinding.rlCard.setOnClickListener(this);
        ivApplogo.setOnClickListener(this);
    }

    private void findMeetingPointClick() {
        String totalPrice = fragmentHomeItemBinding.tvTotalPrice.getText().toString();
        Double totalP = 0.0;
        if (paymentCardMainModel != null &&
                paymentCardMainModel.getAddCardModelList().size() == 1) {
            paymentCardMainModel.getAddCardModelList().get(0).setSelected(true);
            paymentCardSelected = true;
        }
        if (promocodeToapply != null) {

            try {
                if (totalPrice.contains(",")) {
                    totalPrice = totalPrice.replace(",", ".");
                    totalPrice = totalPrice.substring(2).replaceAll("\\s+", "");
                    totalP = Double.parseDouble(totalPrice);
                    String discount = promocodeToapply.getMaxDiscount();
                    int dis = Integer.parseInt(discount);
                    totalP = (totalP * dis) / 100;
                    DecimalFormat df = new DecimalFormat("#.00#");
                    totalPrice = df.format(totalP);

                    if (String.valueOf(totalPrice).contains(".")) {
                        totalPrice = totalPrice.replace(".", ",");
                        if (totalP < 1) {
                            totalPrice = "0" + totalPrice;
                        }
                    }
                }


            } catch (Exception e) {

            }
        }


//        User login or not
        if (!prefrence.getString(Constants.ACCESS_TOKEN, "").equals("token")
                && !prefrence.getString(Constants.ACCESS_TOKEN, "").equals("")) {

            // payment card selected or not
            if (paymentCardSelected) {
                Intent intent = new Intent(getActivity(), MeetingPointActivity.class);
                if (addProductModelList.size() > 0) {
                    if (null != fragmentHomeItemBinding.tvTotalPrice.getText().toString() ||
                            !fragmentHomeItemBinding.tvTotalPrice.getText().toString().isEmpty()) {
                        intent.putExtra("vendor_id", addProductModelList.get(0).getVendorId());
                        intent.putExtra("cart_id", addProductModelList.get(0).getCartId());
                        intent.putExtra("total_price", totalPrice);
                        intent.putExtra("paymentCardId", paymentCardMainModel.getAddCardModelList().get(0).getCardId());
                        startActivity(intent);
//
                        //((HomeActivity) getActivity()).callFinishActivity();
                    } else {
                        Toast.makeText(getActivity(), "Please Select Quantity", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            // Go payment activity to select/add  paymnet card
            else {

                addProductListLocally();
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("workCloseImage", "workCloseImage");
                startActivity(intent);
            }
        }

        // Go login/signup screen to login or signup
        else {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialog.setTitle("Larika");
            alertDialog.setMessage("Please login first. Do you want to login ?");
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    homeItemFragment = HomeItemFragment.this;
                    Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
                    intent.putExtra(Constants.FROM_HOME_ITEM_DIALOG, Constants.FROM_HOME_ITEM_DIALOG);
                    UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.FROM_CART_DIALOG, "2");
                    getActivity().startActivity(intent);
                    fragmentHomeItemBinding.rlHomeTransparency.setVisibility(View.VISIBLE);
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
    }

    //   addProductModelList locally in sharedprefernce for handle data
    private void addProductListLocally() {
        AddMyProductMainModel addProductModel = new AddMyProductMainModel();
        addProductModel.setResult(addProductModelList);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addProductModel);
        prefsEditor.putString("addProductList", json);
        prefsEditor.commit();
    }

    private void init(View v) {
        dishRating = (RatingBar) v.findViewById(R.id.dishRating);
        tv_stock = (TextView) v.findViewById(R.id.tv_stock);
        tv_dishSummry = (TextView) v.findViewById(R.id.tv_dishSummry);
        tv_vendrName = (TextView) v.findViewById(R.id.tv_vendrName);
        tv_NoCard = (TextView) v.findViewById(R.id.text_no_card);
        rl_card = (RelativeLayout) v.findViewById(R.id.rl_childcard);
        ivApplogo = (ImageView) v.findViewById(R.id.iv_logo);
        rlDishbackground = (RelativeLayout) v.findViewById(R.id.rl_dish_background);
        category = (TextView) v.findViewById(R.id.txt_category);
        nature = (TextView) v.findViewById(R.id.txt_nature);
        ShowCardLayout();

    }

    private void closeFilterDialog() {
        ((HomeActivity) getActivity()).showDots();
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_downn);
        ///addProductModelList.clear();
        fragmentHomeItemBinding.rlHomeTransparency.startAnimation(bottomDown);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                fragmentHomeItemBinding.rlHomeTransparency.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void openCartDialog() {
        if (addProductModelList.size() > 0) {
            ((HomeActivity) getActivity()).hideDots();
            Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bottom_upp);
            /// addProductModelList.clear();
            fragmentHomeItemBinding.rlHomeTransparency.startAnimation(bottomDown);
            fragmentHomeItemBinding.rlHomeTransparency.setClickable(true);
            ((HomeActivity) getActivity()).mainClickFalseFromFragment();
            fragmentHomeItemBinding.rlHomeTransparency.setVisibility(View.VISIBLE);
            bottomDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    fragmentHomeItemBinding.rlHomeTransparency.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }


    }

    private void setDataToCard() {
        fragmentHomeItemBinding.tvDishName.setText(bean.dishName);
        fragmentHomeItemBinding.tvDiscDesc.setText(bean.dishDesc);
        fragmentHomeItemBinding.tvDishPrice.setText("Eat it! - R$ " + bean.dishPrice);

        tv_stock.setText(bean.dishStock + " avaliable");
        tv_dishSummry.setText(bean.dishSummry);
        tv_vendrName.setText("Vendor Name\n " + bean.venderName);
        dishRating.setRating(bean.dishRating);
        if (null != bean.category) {
            if (bean.nature.equalsIgnoreCase("0")) {
                nature.setText("Not  define");
            }
            if (bean.nature.equalsIgnoreCase("1")) {
                nature.setText("SAVORY");
            }
            if (bean.nature.equalsIgnoreCase("2")) {
                nature.setText("SWEET");
            }
            if (bean.category.equalsIgnoreCase("0")) {
                category.setText("Not  define");
            }
            if (bean.category.equalsIgnoreCase("1")) {
                category.setText("VEGETARIAN");
            }
            if (bean.category.equalsIgnoreCase("2")) {
                category.setText("FITNESS");
            }
            if (bean.category.equalsIgnoreCase("3")) {
                category.setText("GREASY");
            }
        }
        if (bean.dishImage != null) {
            Ion.with(getActivity()).load(bean.dishImage).withBitmap().placeholder(R.drawable.cake)
                    .error(R.drawable.cake).intoImageView(fragmentHomeItemBinding.ivCovrImg).setCallback(new FutureCallback<ImageView>() {
                @Override
                public void onCompleted(Exception e, ImageView result) {
                    rlDishbackground.setVisibility(View.GONE);
                }
            });

        }


//        if(bean.dishImage.isEmpty()){
////            Ion.with(getActivity()).load(bean.dishImage).withBitmap()
////                    .error(R.drawable.cake).intoImageView(fragmentHomeItemBinding.ivCovrImg);
//            Ion.with(this).load(bean.dishImage).
//                    asBitmap().setCallback(new FutureCallback<Bitmap>() {
//                @Override
//                public void onCompleted(Exception e, Bitmap result) {
//                    if (e == null) {
//                        if (result != null && !result.equals("")) {
//                            fragmentHomeItemBinding.ivCovrImg.setImageBitmap(result);
//                        }
//                    }
//                    else
//                    {
//                        fragmentHomeItemBinding.ivCovrImg.setImageDrawable(getActivity().
//                                getResources().getDrawable(R.drawable.cake));
//                    }
//                }
//            });
//        }

    }

    public void deleteProduct(List<AddMyProductModel> cardModelList, String dishId, String name, int position) {
        addProductModelList = new ArrayList<>();
        addProductModelList.clear();
        addProductModelList.addAll(cardModelList);
        DecimalFormat df = new DecimalFormat("#.00#");
        if (isLogin) {
            deleteProductApi(dishId);


            addProductModelList.remove(position);
            if (addProductModelList.size() > 0) {
                cardDiloagAdapter = new CardDiloagAdapter(getActivity(), addProductModelList, HomeItemFragment.this);
                fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);

               /* long priceValue = parseLong(addProductModelList.get(position).getQuantity());

                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", "");
                price = priceValue * parseLong(priceString);
               *//* DecimalFormat myFormatter = new DecimalFormat("#,##");
                String output = myFormatter.format(price);*//*
                /// price = priceValue * Long.parseLong(addProductModelList.get(position).getDishPrice());
                priceTosend = String.valueOf(price);
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + price);*/
                int quantity = Integer.parseInt(addProductModelList.get(position).getQuantity());
                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                double price = parseDouble(priceString);
                price = price * quantity;

                String priceToShow = df.format(price);
                priceToShow = priceToShow.replace(".", ",");
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);

                paymentCardMainModel = new PaymentCardMainModel();
                Gson gson = new Gson();
                String json = sharedPrefs.getString("MyObject", "");
//        if(paymentCardMainModel!=null)
                paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
                if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
                    for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {
                        if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                            paymentCardSelected = true;
                            String last = paymentCardMainModel.
                                    getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.
                                    getAddCardModelList().get(i).getCardNumber().length() - 4);

                            fragmentHomeItemBinding.
                                    tvCardDetail.setText("Credito    ....   ....   ....    " + last);
                        }

                    }
                }

            } else {
                closeFilterDialog();
            }

            cardDiloagAdapter.notifyDataSetChanged();
        } else {
            db.open();
            db.clearCart();
//            db.deleteARow(name);

            addProductModelList.remove(position);


            if (addProductModelList.size() > 0) {
                cardDiloagAdapter = new CardDiloagAdapter(getActivity(), addProductModelList, HomeItemFragment.this);
                fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);
                String priceString = addProductModelList.get(0).getDishPrice()
                        .replace(",", ".");
                double price = parseDouble(priceString);


                String priceToShow = df.format(price);
                priceToShow = priceToShow.replace(".", ",");
                fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);

               /* for (int i = 0; i < addProductModelList.size(); i++) {

                    String priceString = addProductModelList.get(0).getDishPrice()
                            .replace(",", "");
                    price = price + parseLong(priceString);

                   *//* DecimalFormat myFormatter = new DecimalFormat("#,##");
                    String output = myFormatter.format(price);*//*

                    fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + price);
                    /// price = price + Long.parseLong(addProductModelList.get(i).getDishPrice());
                }*/


                paymentCardMainModel = new PaymentCardMainModel();
                Gson gson = new Gson();
                String json = sharedPrefs.getString("MyObject", "");
                paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
                if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
                    for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {
                        if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                            paymentCardSelected = true;
                            String last = paymentCardMainModel.
                                    getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.
                                    getAddCardModelList().get(i).getCardNumber().length() - 4);

                            fragmentHomeItemBinding.
                                    tvCardDetail.setText("Credito    ....   ....   ....    " + last);
                        }

                    }
                }
            } else {
                closeFilterDialog();
                ///addProductModelList.clear();
            }

            cardDiloagAdapter.notifyDataSetChanged();
        }

    }

    private void deleteProductApi(String dishId) {
        final Progress progress = new Progress(getActivity());
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("vendorDishId", dishId);
        Ion.with(this).load("DELETE", Constants.BASE_URL + "removeDish")
                .setHeader("accessToken", prefrence.getString(Constants.ACCESS_TOKEN, ""))
                .setJsonObjectBody(jsonObject)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                String responce = "";
                if (e != null) {
                    return;
                }
                switch (result.getHeaders().code()) {
                    case 200:
                        responce = result.getResult();
                        db.open();
                        db.clearCart();
                        progress.dismiss();
                        Utils.showToast(getActivity(), "Product removed successfully");

                        break;
                    case 201:
                        responce = result.getResult();
                        db.open();
                        db.clearCart();
                        progress.dismiss();
                        Utils.showToast(getActivity(), "Product removed successfully");


                        break;

                    case 204:
                        responce = result.getResult();

                        break;
                    case 400:
                        responce = result.getResult();
                        JSONObject obj = null;
                        progress.dismiss();

                        break;
                    case 401:
                        responce = result.getResult();
                        break;

                    default:
                        responce = result.getResult();
                        progress.dismiss();
                        break;
                }


            }
        });
    }


    //  set Adapter
    public void setCardDialogAdapter() {
        DecimalFormat df = new DecimalFormat("#.00#");
        addProductModelList.clear();
        db.open();

        addProductModelList = db.getData();
        if (addProductModelList != null && addProductModelList.size() > 0) {
            cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                    addProductModelList, HomeItemFragment.this);
            fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);

            int quantity = Integer.parseInt(addProductModelList.get(0).getQuantity());
            String priceString = addProductModelList.get(0).getDishPrice()
                    .replace(",", ".");
            double price = parseDouble(priceString);
            price = price * quantity;

            String priceToShow = df.format(price);
            priceToShow = priceToShow.replace(".", ",");
            fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);

           /* long priceValue = parseLong(addProductModelList.get(0).getQuantity());
            String priceString = addProductModelList.get(0).getDishPrice()
                    .replace(",", "");
            price = priceValue * parseLong(priceString);


            DecimalFormat formatter = new DecimalFormat("#,##,###");
            String output = formatter.format(price);

            // price = priceValue * Long.parseLong(addProductModelList.get(0).getDishPrice());
            fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + output);*/

        }
        db.close();
        cardDiloagAdapter.notifyDataSetChanged();
    }

    private void addToCartApi(String deleteCartValue) {
        final DecimalFormat df = new DecimalFormat("#.00");
        String quantity = "1", deleteCart = "1";
        AddCartModel addCartModel = new AddCartModel();
        for (int i = 0; i < addProductModelList.size(); i++) {
            if (addProductModelList.get(i).getDishId().equals(bean.dishId)) {
                int value = parseInt(addProductModelList.get(i).getQuantity());
                value++;
                quantity = String.valueOf(value);

            }
        }

        if (UtilPreferences.getFromPrefs(getActivity(), UtilPreferences.UNDELETE_PRODUCT, "").equals("1")) {
            deleteCart = "0";
            UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.UNDELETE_PRODUCT, "5");
        } else {
            deleteCart = "1";
        }
        if (!deleteCartValue.equals("")) {
            deleteCart = deleteCartValue;
        }


        updatedQuantity = quantity;
        updatedDishId = bean.dishId;

        addCartModel.setQuantity(quantity);
        addCartModel.setVendorDishId(bean.dishId);
        final List<AddCartModel> addCartModelList = new ArrayList<>();
        addCartModelList.add(addCartModel);

        AddCartMainModel.getInstance().setCart(addCartModelList);
        AddCartMainModel.getInstance().setDeleteCart(deleteCart);
        final Progress progress = new Progress(getActivity());
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
                .setHeader("accessToken", prefrence.getString(Constants.ACCESS_TOKEN, ""))
                .setJsonObjectBody(obj)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                progress.dismiss();
                String responce = "";
//                if (e != null) {
//                    return;
//                }
                if (result != null) {
                    switch (result.getHeaders().code()) {
                        case 200:
                            responce = result.getResult();
                            try {

                                addMyProductMainModel = new AddMyProductMainModel();
                                addMyProductMainModel = Utils.getgsonInstance().
                                        fromJson(responce, AddMyProductMainModel.class);

                                addProductModelList.clear();
                                addProductModelList.addAll(addMyProductMainModel.getResult());
                                if (addMyProductMainModel.getResult().size() > 0) {
                                    copyList.clear();
                                    copyList.addAll(addMyProductMainModel.getResult());

                                    cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                                            addMyProductMainModel.getResult(), HomeItemFragment.this);
                                    fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);
                                    int quantity = Integer.parseInt(addProductModelList.get(0).getQuantity());
                                    String priceString = addProductModelList.get(0).getDishPrice()
                                            .replace(",", ".");
                                    double price = parseDouble(priceString);
                                    price = price * quantity;

                                    String priceToShow = df.format(price);
                                    priceToShow = priceToShow.replace(".", ",");
                                    fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);
                                   /* long priceValue = parseLong(addProductModelList.get(0).getQuantity());

                                    String priceString = addProductModelList.get(0).getDishPrice()
                                            .replace(",", "");

                                    price = priceValue * parseLong(priceString);


                                    DecimalFormat myFormatter = new DecimalFormat("#,##");
                                    String output = myFormatter.format(price);


                                    /// price = priceValue * Long.parseLong(addProductModelList.get(0).getDishPrice());

                                    fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + output);
*/
                                    //String s = NumberFormat.getIntegerInstance().format(price);


                                    //card deatils
                                    paymentCardMainModel = new PaymentCardMainModel();
                                    Gson gson = new Gson();
                                    String json = sharedPrefs.getString("MyObject", "");
                                    paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
                                    if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
                                        for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {


                                            if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                                                String last = paymentCardMainModel.
                                                        getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.
                                                        getAddCardModelList().get(i).getCardNumber().length() - 4);

                                                fragmentHomeItemBinding.
                                                        tvCardDetail.setText("Credito    ....   ....   ....    " + last);
                                                paymentCardSelected = true;

                                            }

                                        }
                                    }

                                    db.open();
                                    db.clearCart();
                                    db.open();
                                    db.insertContact(updatedDishId, bean.vendorId, bean.dishStock, bean.dishName, bean.dishPrice,
                                            updatedQuantity);
                                    openCartDialog();

                                }
                                cardDiloagAdapter.notifyDataSetChanged();


                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }


                            break;
                        case 201:
                            responce = result.getResult();
                            try {

                                addMyProductMainModel = new AddMyProductMainModel();
                                addMyProductMainModel = Utils.getgsonInstance().
                                        fromJson(responce, AddMyProductMainModel.class);
                                addProductModelList.clear();
                                addProductModelList.addAll(addMyProductMainModel.getResult());
                                if (addMyProductMainModel.getResult().size() > 0) {
                                    copyList.clear();
                                    copyList.addAll(addMyProductMainModel.getResult());

                                    cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                                            addMyProductMainModel.getResult(), HomeItemFragment.this);
                                    fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);

                                    int quantity = Integer.parseInt(addProductModelList.get(0).getQuantity());
                                    String priceString = addProductModelList.get(0).getDishPrice()
                                            .replace(",", ".");
                                    double price = parseDouble(priceString);
                                    price = price * quantity;

                                    String priceToShow = df.format(price);
                                    priceToShow = priceToShow.replace(".", ",");
                                    fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);



                                  /*  long priceValue = parseLong(addProductModelList.get(0).getQuantity());

                                    String priceString = addProductModelList.get(0).getDishPrice()
                                            .replace(",", "");

                                    price = priceValue * parseLong(priceString);


                                    DecimalFormat myFormatter = new DecimalFormat("#,##");
                                    String output = myFormatter.format(price);


                                    /// price = priceValue * Long.parseLong(addProductModelList.get(0).getDishPrice());

                                    fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + output);

                                    //String s = NumberFormat.getIntegerInstance().format(price);*/


                                    //card deatils
                                    paymentCardMainModel = new PaymentCardMainModel();
                                    Gson gson = new Gson();
                                    String json = sharedPrefs.getString("MyObject", "");
                                    paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
                                    if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
                                        for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {


                                            if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                                                String last = paymentCardMainModel.
                                                        getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.
                                                        getAddCardModelList().get(i).getCardNumber().length() - 4);

                                                fragmentHomeItemBinding.
                                                        tvCardDetail.setText("Credito    ....   ....   ....    " + last);
                                                paymentCardSelected = true;

                                            }

                                        }
                                    }

                                    db.open();
                                    db.clearCart();
                                    db.open();
                                    db.insertContact(updatedDishId, bean.vendorId, bean.dishStock, bean.dishName, bean.dishPrice,
                                            updatedQuantity);
                                    openCartDialog();

                                }
                                cardDiloagAdapter.notifyDataSetChanged();


                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            break;

                        case 204:
                            responce = result.getResult();

                            break;
                        case 400:
                            Utils.showToast(getActivity(), "Session Expired");
                            db.clearCart();
                            db.close();
                            UtilPreferences.saveToPrefs(getActivity(),
                                    UtilPreferences.FROM_CART_DIALOG, "FROM_CART");
                            prefrence.deletePreference();
                            PaymentCardMainModel paymentCardMainModel = new PaymentCardMainModel();
                            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(paymentCardMainModel);
                            prefsEditor.putString("MyObject", json);
                            prefsEditor.commit();
                            LoginManager.getInstance().logOut();
                            startActivity(new Intent(getActivity(), LoginSignUpActivity.class));
                            getActivity().finishAffinity();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dishPrice:

                dishPriceClick();
                break;
            case R.id.tv_find_meeting_point:
                findMeetingPointClick();
                break;
            case R.id.tv_keep_looking:
                if (isLogin) {
                    updateProductQuantityApi();
                }
                //// addProductModelList.clear();
                closeFilterDialog();
                break;
            case R.id.rl_card:
                if (!prefrence.getString(Constants.ACCESS_TOKEN, "").equals("token")
                        && !prefrence.getString(Constants.ACCESS_TOKEN, "").equals("")) {
                    addProductListLocally();
                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                    intent.putExtra("workCloseImage", "workCloseImage");
                    startActivityForResult(intent, 111);
                }
                break;
            case R.id.iv_logo:
                getActivity().finish();
                break;
        }
    }

    private void updateProductQuantityApi() {
        AddCartModel addCartModel = new AddCartModel();
        addCartModel.setQuantity(updatedQuantity);
        addCartModel.setVendorDishId(updatedDishId);
        ///addProductModelList.clear();
        final List<AddCartModel> addCartModelList = new ArrayList<>();
        addCartModelList.add(addCartModel);
        AddCartMainModel.getInstance().setCart(addCartModelList);
        AddCartMainModel.getInstance().setDeleteCart("1");
        final Progress progress = new Progress(getActivity());
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
                .setHeader("accessToken", prefrence.getString(Constants.ACCESS_TOKEN, ""))
                .setJsonObjectBody(obj)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                db.open();
                db.clearCart();
                db.open();
                db.insertContact(updatedDishId, bean.vendorId, bean.dishStock, bean.dishName, bean.dishPrice,
                        updatedQuantity);
                String responce = "";
                if (e != null) {
                    return;
                }
                switch (result.getHeaders().code()) {
                    case 200:
                        responce = result.getResult();

                        try {
                            addMyProductMainModel = new AddMyProductMainModel();
                            addMyProductMainModel =
                                    Utils.getgsonInstance().fromJson(responce, AddMyProductMainModel.class);
                            addProductModelList = new ArrayList<>();
                            addProductModelList.clear();
                            addProductModelList.addAll(addMyProductMainModel.getResult());
//                            addProductModelList=addProductModel.getResult();
                            if (addProductModelList.size() > 0) {
                                copyList.clear();
                                copyList.addAll(addMyProductMainModel.getResult());
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        progress.dismiss();
                        break;
                    case 201:
                        responce = result.getResult();

                        try {
                            addMyProductMainModel = new AddMyProductMainModel();
                            addMyProductMainModel =
                                    Utils.getgsonInstance().fromJson(responce, AddMyProductMainModel.class);
                            addProductModelList = new ArrayList<>();
                            addProductModelList.clear();
                            addProductModelList.addAll(addMyProductMainModel.getResult());
//                            addProductModelList=addProductModel.getResult();
                            if (addProductModelList.size() > 0) {
                                copyList.clear();
                                copyList.addAll(addMyProductMainModel.getResult());
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        progress.dismiss();
                        break;

                    case 204:
                        progress.dismiss();
                        responce = result.getResult();

                        break;
                    case 400:
                        progress.dismiss();
                        responce = result.getResult();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(responce);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case 401:
                        progress.dismiss();
                        responce = result.getResult();
                        break;

                    default:
                        progress.dismiss();
                        responce = result.getResult();
                        break;
                }


            }
        });
    }

    private void dishPriceClick() {
        DecimalFormat df = new DecimalFormat("#.00");
        String quantity = "1";
        db.open();
        addProductModelList.clear();
        addProductModelList = db.getData();


//      check Cart empty or not
        if (addProductModelList.size() == 1) {

            // When product id  is different  from previous product id
            if (!addProductModelList.get(0).getDishId().equals(bean.dishId)) {
                if (isLogin) {
//                    Remove Previous product from server
                    UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.DELETE_CART, "0");
                    addToCartApi("1");
                } else {
                    db.open();
                    db.clearCart();
                    addProductModelList.clear();
                    db.open();
                    db.insertContact(bean.dishId, bean.vendorId, bean.dishStock, bean.dishName, bean.dishPrice, quantity);
                    setCardDialogAdapter();
                    openCartDialog();
                }
            }

            // Update product quantity
            // if we are adding same product than update quantity
            else {
                // we ,ll check quntinty should be less than of product stock  or not

                if (parseInt(addProductModelList.get(0).getQuantity())
                        < parseInt(bean.dishStock)) {
                    if (isLogin) {
                        quantity = addProductModelList.get(0).getQuantity();
//                        UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.UPDATED_QUANTITY, quantity);
                        UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.DELETE_CART,
                                "1");
                        addToCartApi("");
//                        openCartDialog();
//                        server side updation+
                    } else {
//                        local side updattion
                        db.open();
                        int value = parseInt(addProductModelList.get(0).getQuantity());
                        value++;
                        quantity = String.valueOf(value);
                        db.updateCol(bean.dishName, quantity);
                        setCardDialogAdapter();
                        openCartDialog();
                    }
//                    addCart();
//                    openCartDialog();

                }
                //Out of stock
                else {
//                    openCartDialog();
                    Utils.showToast(getActivity(), "Product is out of stock");
                    if (isLogin) {


                        if (addProductModelList != null && addProductModelList.size() > 0) {
                            updatedDishId = addProductModelList.get(0).getDishId();
                            updatedQuantity = addProductModelList.get(0).getQuantity();
                            updateProductQuantityApi();

                            cardDiloagAdapter = new CardDiloagAdapter(getActivity(),
                                    addProductModelList, HomeItemFragment.this);
                            fragmentHomeItemBinding.listView.setAdapter(cardDiloagAdapter);


                            int quantit = Integer.parseInt(addProductModelList.get(0).getQuantity());
                            String priceString = addProductModelList.get(0).getDishPrice()
                                    .replace(",", ".");
                            double price = parseDouble(priceString);
                            price = price * quantit;

                            String priceToShow = df.format(price);
                            priceToShow = priceToShow.replace(".", ",");
                            fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + priceToShow);

                            /*long priceValue = parseLong(addProductModelList.get(0).getQuantity());

                            String priceString = addProductModelList.get(0).getDishPrice()
                                    .replace(",", "");

                            price = priceValue * parseLong(priceString);

                            DecimalFormat myFormatter = new DecimalFormat("#,##");
                            String output = myFormatter.format(price);

                            /// price = priceValue * Long.parseLong(addProductModelList.get(0).getDishPrice());
                            fragmentHomeItemBinding.tvTotalPrice.setText("R$ " + output);
                            ///fragmentHomeItemBinding.tvTotalPrice.setText(String.format("%.02f", priceValue));
*/

                            paymentCardMainModel = new PaymentCardMainModel();
                            Gson gson = new Gson();
                            String json = sharedPrefs.getString("MyObject", "");
                            paymentCardMainModel = gson.fromJson(json, PaymentCardMainModel.class);
                            if (paymentCardMainModel != null && paymentCardMainModel.getAddCardModelList().size() > 0) {
                                for (int i = 0; i < paymentCardMainModel.getAddCardModelList().size(); i++) {
                                    if (paymentCardMainModel.getAddCardModelList().get(i).isSelected()) {
                                        paymentCardSelected = true;

                                        String last = paymentCardMainModel.
                                                getAddCardModelList().get(i).getCardNumber().substring(paymentCardMainModel.
                                                getAddCardModelList().get(i).getCardNumber().length() - 4);

                                        fragmentHomeItemBinding.tvCardDetail.setText("Credito    ....   ....   ....    " + last);
                                    }

                                }
                            }

                        }
                        cardDiloagAdapter.notifyDataSetChanged();
                        openCartDialog();

                    } else {

                        setCardDialogAdapter();
                        openCartDialog();

                    }

                }
            }
        }
//        when cart is empty
//        In this case we should remove all previous cart data either local or server
        else {
            if (isLogin) {
//                    Remove Previous product from server
                UtilPreferences.saveToPrefs(getActivity(), UtilPreferences.DELETE_CART, "0");
                addToCartApi("");
//                openCartDialog();
            } else {
                db.open();
                db.clearCart();
                addProductModelList.clear();
                db.close();
                db.open();
                db.insertContact(bean.dishId, bean.vendorId, bean.dishStock, bean.dishName, bean.dishPrice, quantity);
                setCardDialogAdapter();
                openCartDialog();

            }
        }
    }

    @Override
    public void loginCallback(boolean login) {
        if (login) {
            checkLoginOrNot();
            dishPriceClick();
            GetcardDetails getcardDetails = new GetcardDetails(getActivity(), HomeItemFragment.this);
            getcardDetails.carddetails();
            getAllpromocades = new GetAllPromoCodes(getActivity(), HomeItemFragment.this);
            getAllpromocades.carddetails();
            ShowCardLayout();
        }
    }

    private void ShowCardLayout() {

        if (isLogin) {
            rl_card.setVisibility(View.VISIBLE);
            tv_NoCard.setVisibility(View.GONE);
        } else {
            rl_card.setVisibility(View.GONE);
            tv_NoCard.setVisibility(View.VISIBLE);
            fragmentHomeItemBinding.tvCardDetail.setText("ADD CARD");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 111) {
            callback();
        }
    }

    public void callback() {
        getPaymentCardData();
        if (carddata != null && !carddata.equalsIgnoreCase("null")) {
            fragmentHomeItemBinding.tvCardDetail.setText("Credito  ... ...  ...  " + carddata);
        } else {
            fragmentHomeItemBinding.tvCardDetail.setText("ADD CARD");
        }
    }

    public void callbackForPromocode(AllPromoCodeModel promoCodeModel) {
        if (promoCodeModel.getResult().size() > 0) {
            promocodeToapply = promoCodeModel.getResult().get(0);
        }
    }
}
