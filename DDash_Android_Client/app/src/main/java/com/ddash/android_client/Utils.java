package com.ddash.android_client;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Utils {

    /**
     * Converts bytes to GB if the value is > 1000 MB otherwise it converts to MB
     * returns double number
     */
    public static double convertBytes(long bytes) {
        double fBytes = (double) bytes;
        for (int i = 0; i < 4; i++) {
            if (fBytes>1000)
                fBytes = fBytes / 1024;
        }
        return fBytes;
    }

    /**
     * Converts KB to GB
     */
    public static double convertKB(long bytes) {
        double fBytes = (double) bytes;
        for (int i = 0; i < 3; i++) {
            fBytes = fBytes / 1024;
        }
        return fBytes;
    }

    /**
     * Returns the percentage of number1 of number2
     */
    public static int convertToPercentage(double number1, double number2) {
        return (int) Math.round((number1*100)/number2);
    }

    /*static void setLocationUpdatesResult(Context context, List<Location> locations) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations))
                .apply();
    }*/

    /** Return the "inversion" of a map.
     * That is, given a map from key to value,
     * return another map that maps from value to key.
     * Example:
     *  The inversion of {'a':2, 'b':3} is {2:'a', 3:'b'}.
     *  The map is assumed to have unique values.
     * **/
    public static <K, V> Map<V, K> invertMap(Map<K, V> m) {
        Map<V, K> inverted = new HashMap<>();
        for (Map.Entry<K, V> entry : m.entrySet()) {
            inverted.put(entry.getValue(), entry.getKey());
        }
        return inverted;
    }

    /** Read the lines of a file into a list. **/
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

    /** Log a large message, breaking it up into smaller log messages if needed. **/
    public static void largeLog(String tag, String content) {
        /* Log messages have a maximum length.
         * This is a recursive method for breaking a message up,
         * taken from here:
         * https://stackoverflow.com/a/25734136/11555448
        */
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    /** Use reflection to access class values at runtime. **/
    public static class Introspective {

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

        /** Get all field values for an object. **/
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

        /** Get all method return values for an object. **/
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
}