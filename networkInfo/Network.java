package com.bien.networkdataminer;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import static android.content.Context.WIFI_SERVICE;

/*
Class for retrieving the network information
 */

public class Network {
    // Initialising variables
    private int ip, speed, networkId;
    private String macAddress, ssid;



    public Network() {
         /*
        Retrieve context from the 'MyApplication' class and store it in a variable. (One of the ways to access context from anywhere).
        Must supply the full path to the class in the package.
        */
        Context context = com.bien.MyApplication.getAppContext();

        //Create WifiManager object with Context.
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);


        //Create WifiInfo object which will supply most/all the network information we need.
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        ip = wifiInfo.getIpAddress();
        speed = wifiInfo.getLinkSpeed();
        networkId = wifiInfo.getNetworkId();
        macAddress = wifiInfo.getMacAddress();
        ssid = wifiInfo.getSSID();


    }

    public int getIp() {
        return ip;
    }

    public int getNetworkId() {
        return networkId;
    }

    public int getSpeed() {
        return speed;
    }

    public String getSsid() {
        return ssid;
    }

    public String getmacAddress() {
        return macAddress;
    }
}
