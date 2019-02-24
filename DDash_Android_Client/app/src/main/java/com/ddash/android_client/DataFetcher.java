package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class DataFetcher {

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static HashMap<String, Object> getBuild(Activity activity) {
        HashMap<String, Object> build_info = new HashMap();
        try {
            Class build = Class.forName("android.os.Build");
            Build build_obj = new Build();
            Field build_fields[] = build.getDeclaredFields();
            for (Field f : build_fields) {
                build_info.put(f.getName(), f.get(build_obj));
            }
            Method build_methods[] = build.getDeclaredMethods();
//            for (Method m : build_methods) {
//                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                }
//                build_info.put(m.getName(), m.invoke(build_obj));
            build_info.put("getRadioVersion", Build.getRadioVersion());
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                build_info.put("getSerial", Build.getSerial());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return build_info;
    }

}
