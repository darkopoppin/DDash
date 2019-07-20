package com.ddash.android_client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.ddash.android_client.Data.Connectivity;
import com.ddash.android_client.Data.Network;
import java.util.Map;

public class NetworkActivity extends AppCompatActivity {

    private static final String TAG = "NetworkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Displaying all the network and connectivity data gathered
        Network netw = new Network(getApplicationContext().getSystemService(WIFI_SERVICE));
        Connectivity conn = new Connectivity(getApplicationContext());
        Map<String, Object> networkData = netw.getAllWifiDetails();
        Map<String, Object> connectivityData = conn.getConnectivityStatus();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linLayout = findViewById(R.id.network_linearLayout);

        for(String key: networkData.keySet()){

            View rowview = inflater.inflate(R.layout.label,null);
            TextView l1 = rowview.findViewById(R.id.label);
            l1.setText(key);
            TextView l2 = rowview.findViewById(R.id.contents);
            l2.setText(networkData.get(key).toString() +"\n\n");
            linLayout.addView(rowview);
        }
        for(String key: connectivityData.keySet()) {

            View rowview = inflater.inflate(R.layout.label, null);
            TextView l1 = rowview.findViewById(R.id.label);
            l1.setText(key);
            TextView l2 = rowview.findViewById(R.id.contents);
            l2.setText(connectivityData.get(key).toString() + "\n\n");

            linLayout.addView(rowview);
            }


    }
}
