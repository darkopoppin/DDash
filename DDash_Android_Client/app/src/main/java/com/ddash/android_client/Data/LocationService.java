package com.ddash.android_client.Data;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ddash.MyApplication;
import com.ddash.android_client.MainActivity;
import com.ddash.android_client.R;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ddash.MyApplication.CHANNEL_ID;

public class LocationService extends Service {

    public static final String ACTION_PROCESS_UPDATES = "com.google.android.gms.location.locationupdatespendingintent.action.PROCESS_UPDATES";

    @Override
    public void onCreate() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("LocationService")
                    .setContentText("This is a location service")
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        else{
            notification = new Notification.Builder(this)
                    .setContentTitle("LocationService")
                    .setContentText("This is a location service")
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {
            List<Location> locations = result.getLocations();
            Map<String, Object> map = new HashMap<>();
            for (Location l : locations) {
                map.put("location", l);
                Log.d("myLocation", l.toString());
            }
            String user = FirebaseAuth.getInstance().getUid();
            DocumentReference document = FirebaseFirestore.getInstance()
                    .document("users/" + user + "/Devices/" + Build.DEVICE);
            document.set(map, SetOptions.merge());
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
