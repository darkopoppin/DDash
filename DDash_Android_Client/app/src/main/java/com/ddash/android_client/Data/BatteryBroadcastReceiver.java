package com.ddash.android_client.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

    private static final String BATTERY_CHARGING = "android.intent.action.ACTION_POWER_CONNECTED";

    @Override
    public void onReceive(Context context, Intent intent){
        if (intent!=null){
            String action  = intent.getAction();
            switch (action){
                case (BATTERY_CHARGING):
                    Log.d("BatteryCharging", "True");
                }
            }
        }
    }

