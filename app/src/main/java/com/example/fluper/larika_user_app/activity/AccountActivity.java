package com.example.fluper.larika_user_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.constant.Constants;
import com.example.fluper.larika_user_app.database.DbHandler;
import com.example.fluper.larika_user_app.databinding.ActivityAccountBinding;
import com.example.fluper.larika_user_app.fragment.HomeItemFragment;
import com.example.fluper.larika_user_app.helper.SharedPreference;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.ImageIntent;
import com.example.fluper.larika_user_app.utils.Progress;
import com.example.fluper.larika_user_app.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {


    SharedPreference preference;

    private String currentPath;
    private ProgressBar progress_bar;
    ActivityAccountBinding activityAccountBinding;
    private SharedPreferences sharedPrefs;
    private DbHandler db;
    ImageView iv_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        db = new DbHandler(this);
        db.open();
        preference = SharedPreference.getInstance(this);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String tempMilliSeconds = System.currentTimeMillis() + "";
        iv_img = (ImageView) findViewById(R.id.iv_img);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        listener();

        if (!preference.getString(Constants.USER_PIC, "").isEmpty()) {
            if (preference.getString(Constants.USER_PIC, "").contains("https")) {
                final String imgUrl = preference.getString(Constants.USER_PIC, "");
                Ion.with(AccountActivity.this).load(imgUrl).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            iv_img.setImageBitmap(result);
                        }
                    }
                });
            } else {
               // setProfilePic(0, Constants.SERVER_IMG_URL + preference.getString(Constants.USER_PIC, ""));
            }

        } else {
            activityAccountBinding.ivImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.user));
            activityAccountBinding.ivImg.setBorderColor(Color.WHITE);
            activityAccountBinding.ivImg.setBorderWidth(getResources().getDimensionPixelSize(R.dimen.dp_3));

        }
        if (!preference.getString(Constants.FB_PICTURE, "").isEmpty()) {
            final String imgUrl = preference.getString(Constants.FB_PICTURE, "");
            Ion.with(AccountActivity.this).load(imgUrl).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if (result != null) {
                        iv_img.setImageBitmap(result);
                    }
                }
            });
            activityAccountBinding.tvName.setText(preference.getString(Constants.USER_NAME, ""));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityAccountBinding.tvName.setText(preference.getString(Constants.USER_NAME, ""));

    }

    private void listener() {
        activityAccountBinding.ivCross.setOnClickListener(this);
        activityAccountBinding.tvLogout.setOnClickListener(this);
        activityAccountBinding.tvSetting.setOnClickListener(this);
        activityAccountBinding.tvAbout.setOnClickListener(this);
        activityAccountBinding.tvContact.setOnClickListener(this);
        activityAccountBinding.tvSugest.setOnClickListener(this);
        activityAccountBinding.tvEdit.setOnClickListener(this);
        activityAccountBinding.ivImg.setOnClickListener(this);

    }

    private void openLogouDialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        alertDialog.setTitle("Larika");
        alertDialog.setMessage("Are you sure you want to logout ?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DbHandler dbHandler = new DbHandler(AccountActivity.this);
//                dbHandler.deleteAll();
                db.clearCart();
                db.close();
                UtilPreferences.saveToPrefs(AccountActivity.this,
                        UtilPreferences.FROM_CART_DIALOG, "FROM_CART");
                preference.deletePreference();
                HomeItemFragment.carddata=null;

               // PaymentCardMainModel paymentCardMainModel = new PaymentCardMainModel();
               // paymentCardMainModel=PaymentActivity.getCardMainModel();
                SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                Gson gson = new Gson();
               // String json = gson.toJson(paymentCardMainModel);
                prefsEditor.remove("accessToken");
                prefsEditor.remove("MyObject");
                prefsEditor.clear();
                prefsEditor.commit();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(AccountActivity.this, LoginSignUpActivity.class));
                finishAffinity();
//                logoutNetworkCall();
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

    private void logoutNetworkCall() {
        final Progress progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();


        Ion.with(this).load(Constants.BASE_URL + "logout")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""))
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
                        LoginManager.getInstance().logOut();
                        preference.deletePreference();
                        startActivity(new Intent(AccountActivity.this, LoginSignUpActivity.class));
                        finishAffinity();
                        try {
                            JSONObject resultJson = new JSONObject(responce);
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

    private void removeProfilePicture() {
        JsonObject object = new JsonObject();
        object.addProperty("image", preference.getString(Constants.DELETE_IMG_URL, ""));
        progress_bar.setVisibility(View.VISIBLE);
        Ion.with(this).load("DELETE", Constants.BASE_URL + "uploadProfilePic")
                .setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, "")).setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                setProfilePic(3, "");
                progress_bar.setVisibility(View.GONE);
                String responce = "";
                if (e != null) {
                    return;
                }
                switch (result.getHeaders().code()) {
                    case 200:
                        responce = result.getResult();
                        try {
                            preference.putString(Constants.USER_PIC, "");
//                            UtilPreferences.saveToPrefs(AccountActivity.this, UtilPreferences.UserPIC, "");
                            JSONObject resultJson = new JSONObject(responce);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case 201:
                        preference.putString(Constants.USER_PIC, "");
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

    private void uploadPhotoOnServer(String imgUrl) {
        progress_bar.setVisibility(View.VISIBLE);

        Builders.Any.B b = Ion.with(this).load(Constants.BASE_URL + "uploadProfilePic").setTimeout(60000);
        b.setHeader("accessToken", preference.getString(Constants.ACCESS_TOKEN, ""));
        b.setMultipartFile("image", new File(imgUrl));
        b.asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                progress_bar.setVisibility(View.VISIBLE);
                String responce = "";
                if (e != null) {
                    return;
                }
                switch (result.getHeaders().code()) {
                    case 200:
                        responce = result.getResult();
                        try {
                            JSONObject resultJson = new JSONObject(responce);
                            JSONObject object = resultJson.getJSONObject("result");
                            String servrImgUrl = object.optString("profilePic");
                            final String imgUrl = "http://larika.co/larika" + servrImgUrl;
                            Ion.with(AccountActivity.this).load(imgUrl).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                                @Override
                                public void onCompleted(Exception e, Bitmap result) {
                                    if (result != null) {
                                        iv_img.setImageBitmap(result);
                                        preference.putString(Constants.USER_PIC, imgUrl);
                                        preference.putString(Constants.FB_PICTURE, imgUrl);
                                        preference.putString(Constants.DELETE_IMG_URL, imgUrl);
                                        Utils.showToast(AccountActivity.this, "Profile pic updated successfully");
                                        progress_bar.setVisibility(View.GONE);
                                    }
                                }
                            });
/*                            Glide.with(AccountActivity.this)
                                    .load("flupertech.com/larika"+servrImgUrl)
                                    .placeholder(R.mipmap.user)
                                    .error(R.mipmap.user)
                                    .into(iv_img);*/
//                            preference.putString(Constants.DELETE_IMG_URL, servrImgUrl);
                            //  setProfilePic(1, Constants.SERVER_IMG_URL + servrImgUrl);

                        } catch (JSONException e1) {
                            progress_bar.setVisibility(View.GONE);
                            e1.printStackTrace();
                        }
                        break;
                    case 201:
                        progress_bar.setVisibility(View.GONE);
                        responce = result.getResult();
//                        setProfilePic("fhwekhrkwhe");
                        break;

                    case 204:
                        progress_bar.setVisibility(View.GONE);
                        responce = result.getResult();
                        break;
                    case 400:
                        progress_bar.setVisibility(View.GONE);
                        responce = result.getResult();
                        break;
                    case 401:
                        progress_bar.setVisibility(View.GONE);
                        responce = result.getResult();
                        break;
                    default:
                        progress_bar.setVisibility(View.GONE);
                        responce = result.getResult();
                        break;
                }


            }
        });
    }


    private void setProfilePic(final int status, final String pic_url) {
        if (status == 0) {
            progress_bar.setVisibility(View.GONE);
        } else {
            progress_bar.setVisibility(View.VISIBLE);
        }

        if (pic_url.equals("")) {
            activityAccountBinding.ivImg.setImageDrawable(getResources().getDrawable(R.mipmap.user));
            progress_bar.setVisibility(View.GONE);
            preference.putString(Constants.DELETE_IMG_URL, "");
            activityAccountBinding.ivImg.setBorderColor(Color.WHITE);
            if (status == 3) {
                Utils.showToast(AccountActivity.this, "Profile pic removed successfully");
            }
            activityAccountBinding.ivImg.setBorderWidth(getResources().getDimensionPixelSize(R.dimen.dp_3));
        } else {
            Glide.with(this)
                    .load(pic_url)
                    .placeholder(R.mipmap.user)
                    .error(R.mipmap.user)
                    .into(iv_img);
            preference.putString(Constants.DELETE_IMG_URL, pic_url);
            preference.putString(Constants.USER_PIC, pic_url);
            if (status == 1) {
                Utils.showToast(AccountActivity.this, "Profile pic updated successfully");
            }
           // progress_bar.setVisibility(View.GONE);
           /* Ion.with(this).load(pic_url).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap bitmap) {
                    if (e == null) {
                        if (bitmap != null) {
                            activityAccountBinding.ivImg.setImageBitmap(bitmap);
                            activityAccountBinding.ivImg.setBorderColor(Color.BLACK);
                            activityAccountBinding.ivImg.setBorderWidth(getResources().getDimensionPixelSize(R.dimen.dp_3));
                            preference.putString(Constants.DELETE_IMG_URL, pic_url);
                            preference.putString(Constants.USER_PIC, pic_url);
                            if (status == 1) {
                                Utils.showToast(AccountActivity.this, "Profile pic updated successfully");
                            }
                            progress_bar.setVisibility(View.GONE);

                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });*/
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.PICK_GALLERY) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            try {
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                if (picturePath == null) {
                    picturePath = selectedImage.getPath();
                }
//                setProfilePic(picturePath);
                uploadPhotoOnServer(picturePath);
//                doCrop(picturePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == Constants.PICK_CAMERA) {
            if (currentPath != null) {
//                doCrop(currentPath);
                uploadPhotoOnServer(currentPath);
//                setProfilePic(currentPath);
            }
        }
    }

    @Override
    public void onClick(View view) {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_cross:
                activityAccountBinding.ivCross.setAlpha(1f);
                activityAccountBinding.ivCross.setAnimation(animation1);
                finish();
//                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                overridePendingTransition(R.anim.righttolefttwo, R.anim.lefttorighttwo);
                break;
            case R.id.tv_logout:
                openLogouDialog();
                break;
            case R.id.tv_setting:
                startActivity(new Intent(AccountActivity.this, SettingActivity.class));
//                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                break;
            case R.id.tv_about:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LARIKA_WEB_URL)));
                break;
            case R.id.tv_contact:
                startActivity(new Intent(AccountActivity.this, ContactUsActivity.class));
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                break;
            case R.id.tv_sugest:
                startActivity(new Intent(AccountActivity.this, SuggestActivity.class));
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                break;
            case R.id.tv_edit:
                startActivity(new Intent(AccountActivity.this, EditProfileActivity.class));
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                break;
            case R.id.iv_img:
                openCamera();
                break;
        }

    }

    private void openCamera() {
        final CharSequence[] options = {"Camera", "Gallery", "Remove Picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Add Photo from!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera")) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = ImageIntent.createImageFile(AccountActivity.this);
                    if (file != null) {
                        currentPath = file.getAbsolutePath();
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        cameraIntent.putExtra("return-data", false);
                        cameraIntent.putExtra("path", currentPath);
                        try {
                            startActivityForResult(cameraIntent, Constants.PICK_CAMERA);
                        } catch (Exception e) {
                        }
                    }

                } else if (options[item].equals("Gallery")) {
                    /*Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constants.PICK_GALLERY);*/
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_GALLERY);

                } else if (options[item].equals("Remove Picture")) {
                    /*if (iv_img.getDrawable()==null){

                        Toast.makeText(AccountActivity.this, "Please upload profile picture", Toast.LENGTH_SHORT).show();
                    }else {
                        removeProfilePicture();
                    }*/
                    if (preference.getString(Constants.DELETE_IMG_URL,"").equals("")) {

                        Toast.makeText(AccountActivity.this, "Please upload profile picture", Toast.LENGTH_SHORT).show();

                    } else {

                        removeProfilePicture();
                    }
                }
            }
        });
        builder.show();
    }
}
