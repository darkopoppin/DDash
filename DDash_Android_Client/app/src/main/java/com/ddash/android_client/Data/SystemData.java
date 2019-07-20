package com.ddash.android_client.Data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.ddash.android_client.Helpers.Utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static android.content.ContentValues.TAG;

public class SystemData {
    public static Map<String, Object> getSystemData(Context context) {
        Map<String, Object> systemInfo = new HashMap<>();

        // FIXME: if keys are not unique they may override each other in the Map (need to confirm this)
        // build
        Utils.Introspective build_introspective = new Utils.Introspective("android.os.Build");
        Map<String, Object> build_fields = build_introspective.getFields();
        Map<String, Object> build_methods = new HashMap<>();
        build_methods.put("getRadioVersion", Build.getRadioVersion());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Object serialValue = Build.getSerial();
                build_methods.put("getSerial", serialValue);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        systemInfo.putAll(build_fields);
        Log.d(TAG, "getSystemData: " + build_fields.toString());
        systemInfo.putAll(build_methods);

        // version
        // [code name (shortened), release code, SDK version code or the "API level"]
        // eg: ["O", "8.0.0", "26"]
        int version_int = Build.VERSION.SDK_INT;
        String release = Build.VERSION.RELEASE;
        Map<String, Integer> versionCodes = getAndroidVersionCodesMap();
        Map<Integer, String> codesToNames = Utils.invertMap(versionCodes);
        String codename = codesToNames.get(version_int);
        systemInfo.put("version_codename", codename);
        systemInfo.put("version_release", release);
        systemInfo.put("version_int", version_int);

        // system
        long currentTimeMillis = System.currentTimeMillis();
        long nanoTime = System.nanoTime();
        systemInfo.put("currentTimeMillis", currentTimeMillis);
        systemInfo.put("nanoTime", nanoTime);

        Properties propsObj = System.getProperties();
        Enumeration<?> propKeys = propsObj.propertyNames();
        Map<String, String> properties = new HashMap<>();
        for (; propKeys.hasMoreElements(); ) {
            String key = (String) propKeys.nextElement();
            String val = propsObj.getProperty(key);
            properties.put(key, val);
        }
        Map<String, String> env = System.getenv();
        systemInfo.putAll(properties);
        systemInfo.putAll(env);

        return systemInfo;
    }

    public static Map<String, Integer> getAndroidVersionCodesMap() {
        Utils.Introspective build_version_codes_introspective = new Utils.Introspective("android.os.Build$VERSION_CODES");
        Map<String, Object> versions = build_version_codes_introspective.getFields();
        Map<String, Integer> coerced_versions = new HashMap<>();
        for (Map.Entry<String, Object> entry : versions.entrySet()) {
            coerced_versions.put(entry.getKey(), (int) entry.getValue());
        }
        return coerced_versions;
    }
}
