package com.ddash.android_client.Threading;

import java.util.HashMap;

public class BatteryTask {

    private boolean isCharging;
    private int batteryLevel;
    private long remainingChargeTime;

    public void extractData(HashMap batteryMap){
        if (batteryMap.size() >= 1){
            batteryLevel = (int) batteryMap.get("level");
        }
        if (batteryMap.size() >= 2){
            isCharging = (boolean) batteryMap.get("charging");
        }
        if (batteryMap.size() == 3){
            remainingChargeTime = (long) batteryMap.get("remainingChargeTime");
        }
    }

}
