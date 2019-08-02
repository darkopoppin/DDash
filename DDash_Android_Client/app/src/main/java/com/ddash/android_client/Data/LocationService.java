package com.ddash.android_client.Data;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ddash.MyApplication;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentTest = new Intent(this, LocationBroadcastReceiver.class);
        intentTest.setAction(LocationBroadcastReceiver.ACTION_PROCESS_UPDATES);
        /*LocationSettings locationSettings = new LocationSettings(activity);
        LocationRequest locationRequest = locationSettings.createLocationRequest();
        locationSettings.checkLocationSettings(MyApplication.getAppContext());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getAppContext(),
                LOCATION_REQUEST_CODE, intentTest, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.getFusedLocationProviderClient(MyApplication.getAppContext()).requestLocationUpdates(locationRequest, pendingIntent);
    */
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
