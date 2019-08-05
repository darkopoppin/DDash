package com.ddash.android_client.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    Map<String, Object> map = new HashMap<>();
                    for (Location l : locations){
                        map.put("location", l);
                        Log.d("myLocation", l.toString());
                    }
                    String user = FirebaseAuth.getInstance().getUid();
                    DocumentReference document = FirebaseFirestore.getInstance()
                            .document("users/" + user + "/Devices/" + Build.DEVICE);
                    document.set(map, SetOptions.merge());
                }
            }
        }
    }
}
