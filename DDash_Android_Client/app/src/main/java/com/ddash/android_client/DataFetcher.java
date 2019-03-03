package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class DataFetcher {

    public static String build_name = "android.os.Build";
    public static String build_version_name = "android.os.Build$VERSION";
    public static String build_version_codes = "android.os.Build$VERSION_CODES";

    public static HashMap<String, Object> getBuild(Activity activity) {
        Introspective build_introspective = new Introspective(build_name);
        HashMap<String, Object> build_fields = build_introspective.getFields();

        Introspective build_version_introspective = new Introspective(build_version_name);
        HashMap<String, Object> build_version_fields = build_version_introspective.getFields();

        HashMap<String, Object> build_methods = new HashMap<>();
        build_methods.put("getRadioVersion", Build.getRadioVersion());
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
            try {
                Object serialValue = Build.getSerial();
                build_methods.put("getSerial", serialValue);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        HashMap<String, Object> build_info = new HashMap<>(build_fields);
        build_info.putAll(build_methods);
        build_info.putAll(build_version_fields);
        return build_info;
    }

    public static HashMap<String, Object> getAndroidVersionCodes(Activity activity) {
        Introspective build_version_codes_introspective = new Introspective(build_version_codes);
        return build_version_codes_introspective.getFields();
    }

    public static HashMap<String, Object> getSystem(Activity activity) {
        return new HashMap<String, Object>();
    }
}


class Introspective {

    public String cls_name;
    public Class cls_obj;
    public Object cls_instance;

    public Introspective(String cls_name) {
        this.cls_name = cls_name;
        try {
            cls_obj = Class.forName(cls_name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            cls_instance = cls_obj.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Object> getFields() {
        Field cls_fields[] = cls_obj.getDeclaredFields();
        HashMap<String, Object> field_values = new HashMap<>();
        for (Field f : cls_fields) {
            try {
                field_values.put(f.getName(), f.get(cls_instance));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return field_values;
    }

    public HashMap<String, Object> getMethods() {
        Method cls_methods[] = cls_obj.getDeclaredMethods();
        HashMap<String, Object> method_values = new HashMap<String, Object>();
        for (Method m : cls_methods) {
            try {
                method_values.put(m.getName(), m.invoke(cls_instance));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return method_values;
    }

}
