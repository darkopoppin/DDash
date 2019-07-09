package com.ddash.android_client.Data;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.ddash.android_client.R;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

    private static final String BATTERY_CHARGING = "android.intent.action.ACTION_POWER_CONNECTED";

    private Activity activity;

    public BatteryBroadcastReceiver(Activity activity){
        this.activity = activity;
    }
    @Override
    public void onReceive(Context context, Intent intent){
        if (intent!=null){
            String action  = intent.getAction();
            switch (action){
                case (BATTERY_CHARGING):
                    TextView batteryCharging = activity.findViewById(R.id.main_text_batteryCharging);
                    batteryCharging.setText("true");
                }
            }
        }
    }

