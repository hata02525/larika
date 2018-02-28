package com.example.fluper.larika_user_app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationManager implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<LocationSettingsResult>,
		android.location.LocationListener {

	private Activity context;
	private LocationHandlerListener listener;
	private static LocationManager instance;
	public static final int REQUEST_CHECK_SETTINGS = 1005;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest request;
	private boolean isReqLocation;
	private LocationSettingsRequest.Builder builder;
	private PendingResult<LocationSettingsResult> result;
	private Location currentLocation;
	private android.location.LocationManager locationManager;
	private LocationSettingsRequest settingReq;

	private LocationManager(Activity context) {
		this.context = context;
	}

	public static LocationManager getInstance(Activity context) {
		if (instance == null) {
			instance = new LocationManager(context);
		}
		return instance;
	}

	public LocationManager buildAndConnectClient() {
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		if (!mGoogleApiClient.isConnected()) {
			mGoogleApiClient.connect();
		}
		if (locationManager == null) {
			locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0, this);
			try {
				locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 0, 0, this);
			}catch (Exception e){}
		}
		return this;
	}

	public LocationManager setLocationHandlerListener(LocationHandlerListener listener) {
		this.listener = listener;
		return this;
	}

	public LocationManager buildLocationRequest() {
		if (settingReq == null) {
			request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			request.setInterval(25000);
			request.setFastestInterval(20000);
			builder = new LocationSettingsRequest.Builder().setAlwaysShow(true).addLocationRequest(request);
			settingReq = builder.build();
		}
		return this;
	}

	public boolean requestLocation() {
		if (!mGoogleApiClient.isConnected()) {
			isReqLocation = true;
			return false;
		}
		result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, settingReq);
		result.setResultCallback(this);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CHECK_SETTINGS:
			switch (resultCode) {
			case Activity.RESULT_OK:
				LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, instance);
				break;
			case Activity.RESULT_CANCELED:
				Toast.makeText(context, "You must enable Location Service", Toast.LENGTH_SHORT).show();
				requestLocation();
				break;
			default:
				break;
			}
			break;
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		if (listener != null) {
			listener.locationChanged(arg0);
			currentLocation = arg0;
		}
	}

	public interface LocationHandlerListener {
		public void locationChanged(Location location);
		public void lastKnownLocationAfterConnection(Location location);
	}

	@Override
	public void onConnected(Bundle arg0) {
		if (isReqLocation) {
			requestLocation();
		}
		currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (listener != null) {
			listener.lastKnownLocationAfterConnection(currentLocation);
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@Override
	public void onResult(LocationSettingsResult result) {
		final Status status = result.getStatus();
		switch (status.getStatusCode()) {
		case LocationSettingsStatusCodes.SUCCESS:
			try
			{
				LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, instance);

			}catch (IllegalStateException i)
			{
				i.printStackTrace();
			}
			break;
		case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
			try {
				if (context != null) {
					status.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
				}
			} catch (SendIntentException e) {
				e.printStackTrace();
			}
			break;
		case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
			break;
		}
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void stopTracking() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			if(!mGoogleApiClient.isConnecting()){
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, instance);
			mGoogleApiClient.disconnect();
			listener=null;
			Log.e("Tracking", "Stop");
			}
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {
		if (result != null) {
			result.cancel();
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		if (result != null) {
			result.cancel();
			requestLocation();
		}
	}
}
