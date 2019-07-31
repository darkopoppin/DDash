package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.ddash.MyApplication;
import com.ddash.android_client.Data.LocationBroadcastReceiver;
import com.ddash.android_client.Data.LocationIntent;
import com.ddash.android_client.Data.LocationService;
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
        Intent intentTest = new Intent(activity, LocationBroadcastReceiver.class);
        intentTest.setAction(LocationBroadcastReceiver.ACTION_PROCESS_UPDATES);
        Intent intent = new LocationIntent();
        activity.startService()
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }
}
