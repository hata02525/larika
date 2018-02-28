package com.example.fluper.larika_user_app.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fluper.larika_user_app.R;
import com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences;
import com.example.fluper.larika_user_app.utils.LocationManager;
import com.example.fluper.larika_user_app.utils.Progress;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import static com.example.fluper.larika_user_app.sharedPrefernces.UtilPreferences.CURRENT_LATITUDE;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private SupportMapFragment map_frameLayout;
    private GoogleMap googleMap;
    private LocationManager manager;
    public Double latitude, longitude;
    private Double lat, lng;
    TextView tv_mapView;
    Progress progress;
    ImageView currentLocation;
    private boolean isfirst = true;
    private LatLng latLng;
    private boolean isCurrentLocationClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        progress = new Progress(this);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        map_frameLayout = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_mapView, map_frameLayout).commit();

        tv_mapView = (TextView) findViewById(R.id.tv_mapView);
        currentLocation = (ImageView) findViewById(R.id.iv_current_location);
        currentLocation.setOnClickListener(this);
        currentLocation.setTag(true);

        if (map_frameLayout != null) {
            map_frameLayout.getMapAsync(this);
        }
        if (UtilPreferences.getFromPrefs(MapActivity.this, UtilPreferences.IS_MY_CURRENT_LOCATION, "false").equalsIgnoreCase(String.valueOf(true))) {
            currentLocation.setImageResource(R.mipmap.ac_my_loca);
            isCurrentLocationClicked = true;
        }else{
            isCurrentLocationClicked = false;
        }
        //setLocationManager();

        findViewById(R.id.iv_cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        latitude = Double.valueOf(UtilPreferences.getFromPrefs(MapActivity.this, CURRENT_LATITUDE, ""));
        longitude = Double.valueOf(UtilPreferences.getFromPrefs(MapActivity.this, UtilPreferences.CURRENT_LONGITUDE, ""));
        latLng = new LatLng(latitude, longitude);
        moveToCurrentLocation(latLng, googleMap);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


            }
        });


        //Move map within marker and set lat long
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                String isMyCurrentLocation = UtilPreferences.getFromPrefs(MapActivity.this, UtilPreferences.IS_MY_CURRENT_LOCATION, String.valueOf(false));
                if (isMyCurrentLocation.equalsIgnoreCase("true")) {
                    currentLocation.setImageResource(R.mipmap.ac_my_loca);
                } else {
                    currentLocation.setImageResource(R.mipmap.de_my_loca);
                }

                /* UtilPreferences.saveToPrefs(MapActivity.this, CURRENT_LATITUDE,
                        String.valueOf(cameraPosition.target.latitude));
                UtilPreferences.saveToPrefs(MapActivity.this, UtilPreferences.CURRENT_LONGITUDE,
                        String.valueOf(cameraPosition.target.longitude));

                lat = Double.parseDouble(UtilPreferences.getFromPrefs(MapActivity.this, UtilPreferences.CURRENT_LATITUDE, ""));

                lng = Double.parseDouble(UtilPreferences.getFromPrefs(MapActivity.this, UtilPreferences.CURRENT_LONGITUDE, ""));*/


                showUserCurrentAddess(cameraPosition.target.latitude, cameraPosition.target.longitude);


                Log.i("Latitude", String.valueOf(cameraPosition.target.latitude));

                Log.i("Longitude", String.valueOf(cameraPosition.target.longitude));

            }
        });


    }


    private void moveToCurrentLocation(LatLng currentLocation, GoogleMap googleMap) {
        CameraPosition myLocation = new CameraPosition.Builder().target(currentLocation).zoom(15).bearing(90).tilt(30).build();
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(myLocation));
        // Zoom in, animating the camera.
        //googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }

    private void showUserCurrentAddess(final Double latitude, final Double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String subThroughFare = addresses.get(0).getSubThoroughfare();
            String subLocality = addresses.get(0).getSubLocality();
            String locality = addresses.get(0).getLocality();
            String adminArea = addresses.get(0).getAdminArea();
            String countryName = addresses.get(0).getCountryName();
            if (subThroughFare == null) {
                subThroughFare = "";
            } else {
                subThroughFare = subThroughFare + ", ";
            }
            if (subLocality == null) {
                subLocality = "";
            } else {
                subLocality = subLocality + " ";
            }
            if (locality == null) {
                locality = "";
            } else {
                locality = locality + " ";
            }
            if (adminArea == null) {
                adminArea = "";
            } else {
                adminArea = adminArea + " ";
            }
            if (countryName == null) {
                countryName = "";
            }

            final String finalSubThroughFare = subThroughFare;
            final String finalSubLocality = subLocality;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String address = finalSubThroughFare + finalSubLocality;
                    tv_mapView.setText(address);
                    UtilPreferences.saveToPrefs(MapActivity.this, UtilPreferences.MOVE_ADDRESS, address);
                    UtilPreferences.saveToPrefs(MapActivity.this, UtilPreferences.CURRENT_LATITUDE, String.valueOf(latitude));
                    UtilPreferences.saveToPrefs(MapActivity.this, UtilPreferences.CURRENT_LONGITUDE, String.valueOf(longitude));

                    if(isCurrentLocationClicked){
                        currentLocation.setImageResource(R.mipmap.ac_my_loca);
                        UtilPreferences.saveToPrefs(MapActivity.this, UtilPreferences.IS_MY_CURRENT_LOCATION, String.valueOf(true));
                        isCurrentLocationClicked = false;
                    }else{
                        currentLocation.setImageResource(R.mipmap.de_my_loca);
                        UtilPreferences.saveToPrefs(MapActivity.this, UtilPreferences.IS_MY_CURRENT_LOCATION, String.valueOf(false));
                    }


                }
            }, 500);

        } catch (Exception e) {
            e.printStackTrace();
            //// progress.dismiss();
//            }
        }
    }


    private void setLocationManager() {
        manager = LocationManager.getInstance(this).buildAndConnectClient()
                .buildLocationRequest().setLocationHandlerListener(new LocationManager.LocationHandlerListener() {
                    @Override
                    public void locationChanged(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            moveToCurrentLocation(latLng, googleMap);
                            manager.stopTracking();
                        }
                    }

                    @Override
                    public void lastKnownLocationAfterConnection(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            moveToCurrentLocation(latLng, googleMap);

                        }
                    }
                });
        manager.requestLocation();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_current_location:
                isCurrentLocationClicked = true;
                setLocationManager();
                break;
        }
    }
}
