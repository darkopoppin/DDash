package com.ddash.android_client.Data;

import android.content.Context;
import android.os.BatteryManager;

import java.util.HashMap;
import java.util.Map;

public class Battery{

    private HashMap oldBatteryInfo;

    public Battery(){
    }

    public static int getBatteryLevel(Context context) {
        Map<String, Object> batteryInfo = new HashMap<>();
        BatteryManager battery = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int level = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        batteryInfo.put("level", level);
        return level;
    }
        /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int charging = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
            batteryInfo.put("charging", charging);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            long remainingChargeTime = battery.computeChargeTimeRemaining();
            batteryInfo.put("remainingChargeTime", remainingChargeTime);
        }
        return batteryInfo;
    }*/


}
