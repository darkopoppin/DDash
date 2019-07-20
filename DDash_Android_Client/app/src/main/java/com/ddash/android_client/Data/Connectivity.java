package com.ddash.android_client.Data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/* TODO: (tomas comment) callbacks with data update
    for when network status changes?
*/

/* TODO: (tomas comment) Simplify class to only expose methods
*  (see comment for Network.java as well ) */

/*
To create an instance of the Connectivity class, it must be passed the application context via getApplicationContext()
 */
public class Connectivity {
    private Context context;
    private String connectionType;

    public Connectivity(Context context) {
        this.context = context;
        this.connectionType = getConnectionType();
    }

    /*
     * Get the network info
     */
    public NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /*
     * Check if there is any connectivity
     */
    public boolean isConnected() {
        NetworkInfo info = this.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /*
     * Check if there is any connectivity to a Wifi network
     */
    public boolean isConnectedWifi() {
        NetworkInfo info = this.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /*
     * Check if there is any connectivity to a mobile network
     */
    public boolean isConnectedMobile() {
        NetworkInfo info = this.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /*
     * Check if there is fast connectivity
     */
    public boolean isConnectedFast() {
        NetworkInfo info = this.getNetworkInfo(context);
        return (info != null && info.isConnected() && this.isConnectionFast(info.getType(), info.getSubtype()));
    }

    /*
     * Check if the connection is fast
     */
    public boolean isConnectionFast(int type, int subType) {

        if (type == ConnectivityManager.TYPE_WIFI) {
            this.connectionType = "WIFI";
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    this.connectionType = "1xRTT";
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    this.connectionType = "CDMA";
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    this.connectionType = "EDGE";
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    this.connectionType = "EVDO_0";
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    this.connectionType = "EVDO_A";
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    this.connectionType = "GPRS";
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    this.connectionType = "HSDPA";
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    this.connectionType = "HSPA";
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    this.connectionType = "HSUPA";
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    this.connectionType = "UMTS";
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    this.connectionType = "EHRPD";
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    this.connectionType = "EVDO_B";
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    this.connectionType = "HSPAP";
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    this.connectionType = "IDEN";
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    this.connectionType = "LTE";
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    this.connectionType = "UNKNOWN";
                    return false;
            }
        } else {
            this.connectionType = "DISCONNECTED";
            return false;
        }
    }

    /*
    Get the current connection type.
     */
    public String getConnectionType() {
        return this.connectionType;

    }
    public Map<String,Object> getConnectivityStatus(){

        Map<String,Object> connection = new HashMap<>();
        connection.put("Connected To Network",isConnected());
        if (isConnectedWifi() == true) {
            connection.put("Connection Type","Wifi");
        }
        else if(isConnectedMobile()){
            connection.put("Connection Type","Mobile-Data");
        } else{
            connection.put("Connection Type","Disconnected");
        }
        if (isConnectedFast()){
            connection.put("Connection Speed","Fast");
        } else{
            connection.put("Connection Speed","Slow");
        }
        if(isConnected() != false){
        connection.put("Network Type",connectionType);
        } else{
            connection.put("Network Type","Disconnected");
        }

        return connection;
    }
}
