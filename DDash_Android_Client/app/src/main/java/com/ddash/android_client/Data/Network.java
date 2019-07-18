package com.ddash.android_client.Data;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/*
Class for retrieving the network information
*/

/* TODO: (tomas comment) Simplify usage of this class.
   Expose simple methods that can be called multiple times for updated data.
* */

public class Network {

    // Initialising variables
    private int speed, networkId;
    private String ip, macAddress, ssid;


    /*
    Context is passed as a parameter to the init of the Network class
    */
    public Network(Object context) {

        //Create WifiManager object with Context.
        WifiManager wifiManager = (WifiManager) context;


        //Create WifiInfo object which will supply most/all the network information we need.
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        ip = convertIPtoString(wifiInfo);
        speed = wifiInfo.getLinkSpeed();
        networkId = wifiInfo.getNetworkId();
        macAddress = wifiInfo.getMacAddress();
        ssid = wifiInfo.getSSID();
    }
    /*
    Converts the IP from (int) numerical value to IPv4 format.
    */
    public String convertIPtoString(WifiInfo wifiInfo){
        int ip;
        ip = wifiInfo.getIpAddress();
        String result;
        result = String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),(ip >> 24 & 0xff));
        return result;
    }
    /*
    Getter for IP
    */
    public String getIp() {
        return ip;
    }

    /*
    Getter for Network ID
    */
    public int getNetworkId() {return networkId;}


    /*
    Getter for Link Speed
    */
    public int getSpeed() {return speed;}


    /*
    Getter for Network SSID
    */
    public String getSsid() {return ssid;}


    /*
    Getter for Mac Address
    */
    public String getmacAddress() {return macAddress;}


    /*
    Returns all the network information in a List in the order: [ip address, Download speed (Mbps), Network ID, Mac Address, Network SSID]
    */
    public Map<String, Object> getAllWifiDetails(){
        Map<String,Object> networkInfo = new HashMap<>();

        networkInfo.put("IP Address",ip);
        networkInfo.put("Link Speed",speed + "GB/s");
        networkInfo.put("Network ID",Integer.toString(networkId));
        networkInfo.put("MAC Address",macAddress);
        networkInfo.put("SSID",ssid);

        return networkInfo;
    }
}
