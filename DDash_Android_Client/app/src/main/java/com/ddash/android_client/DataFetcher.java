package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataFetcher {


    public static GroupMapList get(Activity activity) {
        GroupMapList data = new GroupMapList();
        data.add(getBuild(activity));
        data.add(getAndroidVersionCodes(activity));
        data.add(getSystem(activity));
        data.add(getCpu());
        data.add(getMemory(activity));
        return data;
    }

    public static GroupMap getBuild(Activity activity) {
        GroupMap build = new GroupMap("build");

        Introspective build_introspective = new Introspective("android.os.Build");
        Map<String, Object> build_fields = build_introspective.getFields();

        Introspective build_version_introspective = new Introspective("android.os.Build$VERSION");
        Map<String, Object> build_version_fields = build_version_introspective.getFields();

        Map<String, Object> build_methods = new HashMap<>();
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

        build.putAll(build_fields);
        build.putAll(build_methods);
        build.putAll(build_version_fields);
        return build;
    }

    public static GroupMap getAndroidVersionCodes(Activity activity) {
        GroupMap versions = new GroupMap("versions");
        Introspective build_version_codes_introspective = new Introspective("android.os.Build$VERSION_CODES");
        versions.putAll(build_version_codes_introspective.getFields());
        return versions;
    }

    public static GroupMap getMemory(Context context) {
        GroupMap memory = new GroupMap("memory");

        ActivityManager.MemoryInfo meminfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.getMemoryInfo(meminfo);

        final long BYTES_IN_KB = 0x100000L;
        long availMemKB = meminfo.availMem / BYTES_IN_KB;
        memory.put("availMemKB", availMemKB);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            long totalMemKB = meminfo.totalMem / BYTES_IN_KB;
            memory.put("totalMemKB", totalMemKB);
            // TODO: Do unit conversions on the view side of the app (not here)
        }
        // TODO: Get key for hashmap automatically from the method name (use reflection)

        memory.put("threshold", meminfo.threshold);
        memory.put("lowMemory", meminfo.lowMemory);
        return memory;
    }

    public static GroupMap getSystem(Activity activity) {
        GroupMap map = new GroupMap("system");
        map.put("currentTimeMillis", System.currentTimeMillis());
        map.put("nanoTime", System.nanoTime());

        Properties propsObj = System.getProperties();
        Enumeration<?> propKeys = propsObj.propertyNames();
        Map<String, String> properties = new HashMap<>();
        for (; propKeys.hasMoreElements(); ) {
            String key = (String) propKeys.nextElement();
            String val = propsObj.getProperty(key);
            properties.put(key, val);
        }
        map.put("properties", properties);

        Map<String, String> env = System.getenv();
        map.put("env", env);

        return map;
    }

    public static GroupMap getCpu() {
        GroupMap map =  new GroupMap("cpu");
        String filename = "proc/cpuinfo";
        String sep = "\n";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuffer sb = new StringBuffer();
            try {
                String line = br.readLine();
                while (line != null) {
                    try {
                        sb.append(line);
                        sb.append(sep);
                        line = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // OR rb.lines (Java 8)
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            TextUtils.join("\n", sb);
            String cpuinfo = sb.toString();
            // TODO properly parse the cpuinfo file instead of just getting all its lines in one string
            map.put(filename, cpuinfo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
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
