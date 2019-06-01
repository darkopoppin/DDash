package com.bien.networkdataminer;


import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {
    TextView txtWifiInfo;
    Button btnInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtWifiInfo = (TextView) findViewById(R.id.idTxt);
        btnInfo = (Button) findViewById(R.id.idBtn);
        }

    public void getWifiInformation(View view) {
        /*
        Invoke the Network class.
         */
        Network network = new Network();

        //Get the fields required.
        int ip = network.getIp();
        String macAddress = network.getmacAddress();
        int speed = network.getSpeed();
        int networkId = network.getNetworkId();
        String ssid = network.getSsid();

        //Simple representation of the data gathered.
        String summary = "IP ADDRESS " + ip +
                "\n" + "MAC ADDRESS " + macAddress +
                "\n" + "LINK SPEED " + speed +
                "\n" + "NETWORK ID " + networkId +
                "\n" + "SSID " + ssid +
                "\n" + "BUILD:" + Build.BOARD +
                "\n" + "BOOTLOADER:" + Build.BOOTLOADER +
                "\n" + "DEVICE:" + Build.DEVICE +
                "\n" + "DISPLAY:" + Build.DISPLAY +
                "\n" + "PRODUCT:" + Build.PRODUCT +
                "\n" + "FINGERPRINT:" + Build.FINGERPRINT +
                "\n" + "HOST:" + Build.HOST +
                "\n" + "TYPE:" + Build.TYPE +
                "\n" + "USER:" + Build.USER +
                "\n" + "VERSION: " + Build.VERSION.RELEASE +
                "\n" + "SDK: " + Build.VERSION.SDK_INT;

        //Write to the TextView object on display.
        txtWifiInfo.setText(summary);

    }
/*
 Standalone code for retrieving the network information that we need, Not good. Need to create a separate class then instantiate in the main app.
    **WORKING CODE**
    public void getWifiInformation(View view) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String macAddress = wifiInfo.getMacAddress();
        int speed = wifiInfo.getLinkSpeed();
        int networkId = wifiInfo.getNetworkId();
        String ssid = wifiInfo.getSSID();
        String summary = "IP ADDRESS " + ip +
                "\n" + "MAC ADDRESS " + macAddress +
                "\n" + "LINK SPEED " + speed +
                "\n" + "NETWORK ID " + networkId +
                "\n" + "SSID " + ssid;
        txtWifiInfo.setText(summary);

    }
*/
}
