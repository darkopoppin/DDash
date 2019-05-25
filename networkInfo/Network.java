package com.example.networkdataminer;

import android.app.Application;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;

/*
Class for retrieving the network information
 */

public class Network extends Application {
    // Initialising variables
    private int ip, speed, networkId;
    private String macAddress, ssid;



    public Network() {


        //Create WifiManager object from with Context.
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);


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


