package com.ddash.android_client.Threading;

import android.util.Log;

import com.ddash.android_client.Utils;

import java.util.Map;

public class MemoryTask{
    private ThreadManager threadManager = ThreadManager.getManagerInstance();
    private double totalMemory;
    private double usedMemory;
    private boolean lowMemory;

    public void handleData(){
        threadManager.handleData(this, 1);
    }

    public void extractData(Map memoryData){
        totalMemory = Math.round(Utils.convertKB((long) memoryData.get("totalMemKB")));
        usedMemory = totalMemory - Utils.convertKB((long) memoryData.get("availMemKB"));
        lowMemory = (boolean) memoryData.get("lowMemory");
        Log.d("MemoryTaskThreshold", Long.toString((long)memoryData.get("threshold")));

    }

    public double getTotalMemory() { return totalMemory; }

    public double getUsedMemory() { return  usedMemory; }
    /*
    public getView(){
        threadManager.
    }*/
}
