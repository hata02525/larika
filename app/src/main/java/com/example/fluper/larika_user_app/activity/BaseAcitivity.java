//package com.example.fluper_pc.larikauser.activity;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
//import android.util.DisplayMetrics;
//import android.widget.Toast;
//
//import com.example.fluper_pc.larikauser.R;
//import com.example.fluper_pc.larikauser.fragment.DrawerMenuFragment;
//
//import net.simonvt.menudrawer.MenuDrawer;
//import net.simonvt.menudrawer.Position;
//
//
///**
// * Created by owner on 14/6/16.
// */
//public class BaseAcitivity extends AppCompatActivity {
//    public static MenuDrawer mDrawer;
//    public DrawerMenuFragment menuFrag;
//    private long mBackPressed = 0;
//    private long TIME_INTERVAL = 2000;
//    private static final int MENU_WIDTH = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int MENU_WIDTH = displayMetrics.heightPixels;
//        mDrawer = MenuDrawer.attach(BaseAcitivity.this, MenuDrawer.Type.OVERLAY, Position.TOP, MenuDrawer.MENU_DRAG_CONTENT);
//        mDrawer.setMenuSize(MENU_WIDTH);
//        mDrawer.setContentView(R.layout.activity_move);
//        mDrawer.setMenuView(R.layout.leftpanel_frame);
//        mDrawer.setDropShadowEnabled(false);
//
//
//        FragmentManager fManager = getSupportFragmentManager();
//        FragmentTransaction fTransaction = fManager.beginTransaction();
//        menuFrag = new DrawerMenuFragment();
//        fTransaction.replace(R.id.menu_frame, menuFrag);
//        fTransaction.commit();
//    }
//
//    @Override
//    public void onBackPressed() {
////        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
////        if (visibleFragment instanceof HomeFragment) {
////            closeActivityWithDoubleClick();
////        } else {
////            super.onBackPressed();
////        }
//        super.onBackPressed();
//    }
//
//    private void closeActivityWithDoubleClick() {
//        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
//            finish();
//            return;
//        } else {
//            Toast.makeText(getApplicationContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
//        }
//        mBackPressed = System.currentTimeMillis();
//    }
//}
