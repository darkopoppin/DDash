package com.ddash.android_client.Data;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.HashMap;
import java.util.Map;


/* TODO: Provide a static method that does all the Network instantiation
   and data getting in one call */

/** Retrieve network information **/
public class Network {

    // Initialising variables
    private int speed, networkId;
    private String ip, macAddress, ssid;

    public Network(Object context) {
        /* Context is passed as a parameter to the init of the Network class */
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

    /* getters: */

    public String getIp() { return ip; }

    public int getNetworkId() {return networkId; }

    public int getSpeed() {return speed; }

    public String getSsid() {return ssid; }

    public String getmacAddress() {return macAddress; }

    /* convenience methods: */

    /** Converts the IP from (int) numerical value to IPv4 format. **/
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

    /** Returns all the network information in a data structure
     * [IP address, Download speed (Mbps), Network ID, MAC address, SSID] **/
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
