package com.ddash.android_client.Threading;

import java.util.Map;

public class MemoryTask{
    private ThreadManager threadManager = ThreadManager.getManagerInstance();
    private Map<String,Object> dataMap;

    public void handleData(){
        threadManager.handleData(this, 1);
    }

    public void setDataMap(Map map){
        dataMap = map;
    }

    public Map getDataMap(){
        return dataMap;
    }

    /*
    public getView(){
        threadManager.
    }*/
}
