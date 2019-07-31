package com.ddash.android_client.Data;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.ddash.MyApplication;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class LocationIntent extends IntentService {

    private final int LOCATION_REQUEST_CODE = 0;

    public LocationIntent(){
        super("LocationIntent");
    }
    @Override
    public void onHandleIntent(Intent intent){
        LocationService locationService = new LocationService(activity);
        LocationRequest locationRequest = locationService.createLocationRequest();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getAppContext(),
                LOCATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.getFusedLocationProviderClient(MyApplication.getAppContext()).requestLocationUpdates(locationRequest, pendingIntent);
    }
}
