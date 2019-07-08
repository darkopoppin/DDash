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
    private static final String BATTERY_DISCHARGING ="android.intent.action.ACTION_POWER_DISCONNECTED";
    private Activity activity;

    public BatteryBroadcastReceiver(Activity activity){
        this.activity = activity;
    }
    @Override
    public void onReceive(Context context, Intent intent){
        if (intent!=null){
            String action  = intent.getAction();
            TextView statusView  = activity.findViewById(R.id.main_text_charging_status);
            switch (action){
                case (BATTERY_CHARGING):
                    statusView.setText("true");
                    break;
                case (BATTERY_DISCHARGING):
                    statusView.setText("false");
                }


            }
        }
    }

