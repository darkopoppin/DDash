package com.ddash.android_client.Threading;

import com.ddash.android_client.Utils;

import java.util.Map;

public class MemoryTask{
    private ThreadManager threadManager = ThreadManager.getManagerInstance();
    private Map<String,Object> dataMap;
    private double totalMemory;

    public void handleData(){
        threadManager.handleData(this, 1);
    }

    public void setDataMap(Map map){
        dataMap = map;
        totalMemory = Math.round(Utils.convertKB((long) map.get("totalMemKB")));
    }

    public Map getDataMap(){
        return dataMap;
    }

    public double getTotalMemory() { return totalMemory; }

    /*
    public getView(){
        threadManager.
    }*/
}
