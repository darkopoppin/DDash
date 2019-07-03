package com.ddash.android_client.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATES = "com.google.android.gms.location.locationupdatespendingintent.action.PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent){
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null){
                    List<Location> locations = result.getLocations();
                    for (Location l : locations){
                        Log.d("myLocation", l.toString());
                    }
                }
            }
        }
    }
}
