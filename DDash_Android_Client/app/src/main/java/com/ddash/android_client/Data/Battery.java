package com.ddash.android_client.Data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddash.MyApplication;
import com.ddash.android_client.Helpers.TimeConvertor;
import com.ddash.android_client.R;

import java.util.HashMap;
import java.util.Map;

public class Battery{

    /** Get battery status.
    Returns map with battery level, battery charging state and the time to full charge
    **/
    public static Map<String, Object> getBattery(Context context) {
        Map<String, Object> batteryInfo = new HashMap<>();
        BatteryManager battery = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.registerReceiver(null, intentFilter);
        float batteryTemperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)/10;
        batteryInfo.put("temperature",batteryTemperature);
        int level = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        batteryInfo.put("level", level);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            boolean charging = battery.isCharging();
            batteryInfo.put("charging", charging);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            long remainingChargeTime = battery.computeChargeTimeRemaining();
            batteryInfo.put("remainingChargeTime", remainingChargeTime);
        }
        return batteryInfo;
    }

    /**
    Initially sets the battery icon and text in main activity
     **/
    public static void setBatteryStatus(Activity activity){
        Map batteryInfo = Battery.getBattery(MyApplication.getAppContext());
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = MyApplication.getAppContext().registerReceiver(null, intentFilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,-1);

        TextView batteryText = activity.findViewById(R.id.main_text_batteryCharging);
        float temperature = (float) batteryInfo.get("temperature");
        TextView batteryTemperature = activity.findViewById(R.id.main_text_temperature);
        ImageView batteryIcon = activity.findViewById(R.id.main_vector_battery);

        if (status == BatteryManager.BATTERY_STATUS_CHARGING){
            batteryText.setText("Charging");
            batteryTemperature.setText(String.format("Temperature: %.1fC", temperature));
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P){
                TextView batteryTime = activity.findViewById(R.id.main_text_time);
                long remainingChargeTime = (long) batteryInfo.get("remainingChargeTime");
                if (remainingChargeTime != -1) {
                    TimeConvertor time = new TimeConvertor(remainingChargeTime);
                    time.displayTime(batteryTime);
                }
                else
                    batteryTime.setText("Calculating");
            }
        }
        else if (status == BatteryManager.BATTERY_STATUS_FULL){
            batteryText.setText("Charged");
            batteryIcon.setImageResource(R.drawable.battery_charging);
            batteryTemperature.setText(String.format("Temperature: %.1fC", temperature));
        }
        else {
            batteryText.setText("Discharging");
            batteryIcon.setImageResource(R.drawable.battery_discharging);
            batteryTemperature.setText(String.format("Temperature: %.1fC", temperature));
        }
    }
}
