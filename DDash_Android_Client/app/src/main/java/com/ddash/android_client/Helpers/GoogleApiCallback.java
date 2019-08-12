package com.ddash.android_client.Helpers;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.ddash.MyApplication;
import com.ddash.android_client.Data.LocationService;
import com.ddash.android_client.Data.LocationSettings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GoogleApiCallback implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Activity activity;
    private final int LOCATION_REQUEST_CODE = 0;

    public GoogleApiCallback (Activity activity){
        this.activity = activity;
    }

    @Override
    public void onConnected(Bundle bundle){
        Intent intentTest = new Intent(activity, LocationService.class);
        LocationSettings locationSettings = new LocationSettings(activity);
        LocationRequest locationRequest = locationSettings.createLocationRequest();
        locationSettings.checkLocationSettings(MyApplication.getAppContext());
        PendingIntent pendingIntent;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        pendingIntent = PendingIntent.getForegroundService(MyApplication.getAppContext(),
                LOCATION_REQUEST_CODE, intentTest, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            pendingIntent = PendingIntent.getService(MyApplication.getAppContext(),
                    LOCATION_REQUEST_CODE, intentTest, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.getFusedLocationProviderClient(MyApplication.getAppContext()).requestLocationUpdates(locationRequest, pendingIntent);
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }
}
