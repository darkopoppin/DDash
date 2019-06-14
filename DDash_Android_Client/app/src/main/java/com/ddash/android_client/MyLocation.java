package com.ddash.android_client;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;

import android.content.Context;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";

    static final String ACTION_PROCESS_UPDATES =
            "com.google.android.gms.location.sample.locationupdatespendingintent.action" +
                    ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                }
            }
        }
    }
}
