package com.ddash.android_client.Data;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddash.android_client.Helpers.TimeConvertor;
import com.ddash.android_client.R;

import java.util.Map;

/**
 * handles the following battery broadcast messages:
 * ACTION_POWER_CONNECTED, ACTION_POWER_DISCONNECTED, ACTION_BATTERY_CHANGED
 */

public class BatteryBroadcastReceiver extends BroadcastReceiver {

    private static final String BATTERY_CHARGING = "android.intent.action.ACTION_POWER_CONNECTED";
    private static final String BATTERY_DISCHARGING = "android.intent.action.ACTION_POWER_DISCONNECTED";
    private static final String BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
    private Activity activity;

    public BatteryBroadcastReceiver(Activity activity){
        this.activity = activity;

    }
    @Override
    public void onReceive(Context context, Intent intent){
        if (intent!=null){
            String action  = intent.getAction();
            TextView batteryCharging = activity.findViewById(R.id.main_text_batteryCharging);
            ImageView batteryImage = activity.findViewById(R.id.main_vector_battery);
            Map batteryData = Battery.getBattery(context);
            switch (action){
                case(BATTERY_CHARGING):
                    batteryImage.setImageResource(R.drawable.battery_charging);
                    batteryCharging.setText("Charging");
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P){
                        TextView batteryTime = activity.findViewById(R.id.main_text_time);
                        long remainingChargeTime = (long)batteryData.get("remainingChargeTime");
                        if (remainingChargeTime != -1){
                            TimeConvertor time = new TimeConvertor(remainingChargeTime);
                            time.displayTime(batteryTime);
                        }
                        else
                            batteryTime.setText("Calculating");
                    }
                    break;
                case(BATTERY_DISCHARGING):
                    batteryImage.setImageResource(R.drawable.battery_discharging);
                    batteryCharging.setText("Discharging");
                    break;
                case(BATTERY_CHANGED):
                    TextView batteryLevel = activity.findViewById(R.id.main_text_batteryLevel);
                    int level = (int) batteryData.get("level");
                    batteryLevel.setText(String.format("%d%%",level));
                }
            }
        }
    }

