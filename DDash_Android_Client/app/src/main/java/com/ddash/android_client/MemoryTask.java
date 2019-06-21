package com.ddash.android_client;

import android.app.Activity;

import java.util.Map;

public class MemoryTask{
    private ThreadManager threadManager = ThreadManager.getManagerInstance();
    private Map<String,Object> dataMap;
    private Activity mainActivity;

    public void handleData(){
        threadManager.handleData(this, 1);
    }
    public void setDataMap(Map map){
        dataMap = map;
    }
    public Map getDataMap(){
        return dataMap;
    }
}
