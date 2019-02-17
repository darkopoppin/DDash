package com.ddash.android_client;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView keyView = findViewById(R.id.textView);
        TextView valueView = findViewById(R.id.textView1);
        String keyText = "";
        String valueText = "";

        keyText += "Device model:\n";
        valueText += Build.MODEL + "\n";

        keyText += "Manufacturer:\n";
        valueText += Build.MANUFACTURER + "\n";

        keyText += "Time:\n";
        valueText += Build.TIME + "\n";

        keyText += "User:\n";
        valueText += Build.USER + "\n";

        keyText += "API Level\n";
        valueText += Build.VERSION.SDK_INT + "\n";

        keyText += "Serial:\n";
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            valueText += Build.getSerial() + "\n";
//        } else {
//            valueText += "(API level too low)\n";
//        }
        valueText += "\n";

        keyText += "Java Vendor:\n";
        valueText += System.getProperty("java.vendor") + "\n";

        keyText += "Nano time:\n";
        valueText += Long.toString(System.nanoTime()) + "\n";

        keyText += "Memory:\n";
        ActivityManager.MemoryInfo minfo = new ActivityManager.MemoryInfo();
        ActivityManager mngr = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        mngr.getMemoryInfo(minfo);
        valueText += minfo.availMem + " out of " + minfo.totalMem + "\n";

        keyText += "Battery level:\n";
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        valueText += bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%" + "\n";

        keyText += "CPU info:\n";
        String cpuText = "";
        ArrayList<String> cpuFiles = new ArrayList<String>();
        cpuFiles.add("/proc/cpuinfo");
        cpuFiles.add("/proc/stat");
        for (String file: cpuFiles) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(file)));
                String line = null;
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (line != null) {
                    cpuText += line;
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                cpuText += "\n\n";
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        valueText += cpuText + "\n";

        keyView.setText(keyText);
        valueView.setText(valueText);
    }
}
