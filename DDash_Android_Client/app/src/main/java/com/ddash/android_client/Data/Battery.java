package com.ddash.android_client.Data;

import android.content.Context;
import android.os.BatteryManager;

import java.util.HashMap;
import java.util.Map;

public class Battery {
    public static Map<String, Object> getBattery(Context context) {
        Map<String, Object> batteryInfo = new HashMap<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager battery = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
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
        }
        return batteryInfo;
    }
}
