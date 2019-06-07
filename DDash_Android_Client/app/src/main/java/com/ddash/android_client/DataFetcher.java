package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DataFetcher {

    public static Map<String, Object> getBuild(Activity activity) {
        Map<String, Object> build = new HashMap<>();

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

    public static Map<String, Object> getSystem(Activity activity) {
        Map<String, Object> map = new HashMap<>();
        long currentTimeMillis = System.currentTimeMillis();
        map.put("currentTimeMillis", currentTimeMillis);
        long nanoTime = System.nanoTime();
        map.put("nanoTime", nanoTime);

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

    public static Map<String, Integer> getAndroidVersionCodesMap() {
        Introspective build_version_codes_introspective = new Introspective("android.os.Build$VERSION_CODES");
        Map<String, Object> versions = build_version_codes_introspective.getFields();
        Map<String, Integer> coerced_versions = new HashMap<>();
        for (Map.Entry<String, Object> entry : versions.entrySet()) {
            coerced_versions.put(entry.getKey(), (int) entry.getValue());
        }
        return coerced_versions;
    }

    public static String[] getVersionInfo() {
        // [code name (shortened), release code, SDK version code or the "API level"]
        // eg: ["O", "8.0.0", "26"]
        int version_int = Build.VERSION.SDK_INT;

        Map<String, Integer> versionCodes = getAndroidVersionCodesMap();
        Map<Integer, String> codesToNames = invertMap(versionCodes);
        String codename = codesToNames.get(version_int);

        String release = Build.VERSION.RELEASE;
        return new String[]{codename, release, String.valueOf(version_int)};
    };

    public static <K, V> Map<V, K> invertMap(Map<K, V> m) {
        Map<V, K> inverted = new HashMap<>();
        for (Map.Entry<K, V> entry : m.entrySet()) {
            inverted.put(entry.getValue(), entry.getKey());
        }
        return inverted;
    }

    public static Map<String, Object> getMemory(Context context) {
        // FIXME: Should Context or Activity be taken as an argument?
        Map<String, Object> memory = new HashMap<>();
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
        long threshold = meminfo.threshold;
        memory.put("threshold", threshold);
        boolean lowMemory = meminfo.lowMemory;
        memory.put("lowMemory", lowMemory);
        return memory;
    }

    public static List<String[]> getCpu() {
        Map<String, Object> map =  new HashMap<>();
        // in this file we can find information about cores
        // https://www.thegeekdiary.com/proccpuinfo-file-explained/
        String cpuinfoFilename = "proc/cpuinfo";
        List<String> cpuinfoLines = readLines(cpuinfoFilename);
        map.put(cpuinfoFilename, cpuinfoLines);
        // parse the file
        List<String[]> cpuInfo = new ArrayList<>();
        for (String line : cpuinfoLines) {
            String[] tokens = line.split(":");
            String[] pair = new String[2];
            if (tokens.length == 2) {
                String key = tokens[0];
                String value = tokens[1]; // TODO: Change type of value to match what is being parsed
                // TODO: might want to trim/strip the strings
                pair[0] = key;
                pair[1] = value;
            } else if (tokens.length == 1) {
                String key = tokens[0];
                pair[0] = key;
                pair[1] = null;
            }
            cpuInfo.add(pair);
            // TODO: need to parse each processor separately
        }
        return cpuInfo;
        // TODO: also parse proc/stat, http://www.linuxhowtos.org/System/procstat.htm
    }

    public static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
                try {
                    String line = br.readLine();
                    while (line != null) {
                        try {
                            lines.add(line);
                            line = br.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static Map<String, Object> getBattery(Context context) {
        Map<String, Object> batteryInfo = new HashMap<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager battery = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            int level = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            batteryInfo.put("level", level);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                boolean charging = battery.isCharging();
                batteryInfo.put("charging", charging);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                long remainingChargeTime = battery.computeChargeTimeRemaining();
                batteryInfo.put("remainingChargeTime", remainingChargeTime);
            }
        }
        return batteryInfo;
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
