package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.firstButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstTextbox = (EditText) findViewById(R.id.firstTextbox);
                EditText secondTextbox = (EditText) findViewById(R.id.secondTextbox);
                TextView resultBox = (TextView) findViewById(R.id.resultBox);

                int num1 =  Integer.parseInt(firstTextbox.getText().toString());
                int num2 =  Integer.parseInt(secondTextbox.getText().toString());
                int result = num1 + num2;
                resultBox.setText(result + "");

                TextView collectionData1 = (TextView) findViewById(R.id.collectionData1);
                collectionData1.setText(isOnline()+"");

                EditText networkStatusLabel1 = (EditText) findViewById(R.id.networkStatusLabel1);
                EditText networkStatusLabel2 = (EditText) findViewById(R.id.networkStatusLabel2);
                EditText networkStatusLabel3 = (EditText) findViewById(R.id.networkStatusLabel3);

                TextView collectionData2 = (TextView) findViewById(R.id.collectionData2);
                collectionData2.setText("Sample");

                TextView collectionData3 = (TextView) findViewById(R.id.collectionData3);
                int n = getWiFiSpeed();
                collectionData3.setText("Sample");
            }
        });

    }

    public boolean isOnline(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public int getWiFiSpeed(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int speedMbps = wifiInfo.getLinkSpeed();
        return speedMbps;
    }

    public String getSsid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            String ssid = wifiInfo.getSSID();
            return ssid;
        }
        return String.format("<Unknown/NULL>");
    }

}
