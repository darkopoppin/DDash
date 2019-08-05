package com.ddash;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class MyApplication extends Application {

    private static Context applicationContext;
    private static Context context;
    public static final String CHANNEL_ID = "locationServiceChannel";

    public void onCreate(){
        super.onCreate();
        applicationContext = getApplicationContext();
        context = getAppContext();
        createNotificationChannel();
    }

    public static Context getAppContext(){
        return applicationContext;
    }

    public static Context getContext() { return  context; }

    private void createNotificationChannel (){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel locationServiceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "LocationService",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(locationServiceChannel);
        }
    }
}