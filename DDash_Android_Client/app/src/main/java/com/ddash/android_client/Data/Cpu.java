package com.ddash.android_client.Data;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.ddash.android_client.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Cpu {

    public static Map<String, Object> getCpu() {
        Map<String, Object> map = new HashMap<>();
        map.put("about", getCpuAbout());
        map.put("use", getCpuUse());
        map.put("usetop", getCpuUseTop());
        map.put("useproc", getCpuUseProc());
        map.put("cores", getCoresNumber());
        return map;
    }

    public static List<Map<String, Object>> getCpuAbout() {
        /*
             in this file we can find information about cores
             https://www.thegeekdiary.com/proccpuinfo-file-explained/
         */
        String file = "/proc/cpuinfo";
        List<String> lines = Utils.readLines(file);
        // parsing step
        // 1. separate lines by processors
        List<Integer> processorIndices = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String contents = lines.get(i);
            if (contents.contains("processor")) {
                processorIndices.add(i);
            }
        }

        List<List<String>> processors = new ArrayList<>();
        for (int i = 1; i < processorIndices.size(); i++) {
            int start_index = processorIndices.get(i-1);
            int end_index = processorIndices.get(i);
            List<String> processor = lines.subList(start_index, end_index);
            processors.add(processor);
        }
        List<String> tail = lines.subList(processorIndices.get(processorIndices.size()-1), lines.size());
        processors.add(tail);

        // 2. parse each processor's lines
        List<Map<String, Object>> cpuInfo = new ArrayList<>();
        for (List<String> processor : processors) {
            // go through each processor
            Map<String, Object> procInfo = new HashMap<>();
            for (String line : processor) {
                // parse each line
                String[] tokens = line.split(":");
                if (0 < tokens.length) {
                    String key = tokens[0].trim().toLowerCase();
                    Object value = null;
                    if (tokens.length == 2) {
                        value = String.valueOf(tokens[1]).trim(); // TODO: Change type of value to match what is being parsed
                    }
                    // add info to map
                    procInfo.put(key, value);
                }
            }
            // add to proc list
            cpuInfo.add(procInfo);
        }
        return cpuInfo;
    }

    public static List<Set<String>> getCpuAboutSummary(List<Map<String, Object>> cpuInfo) {
        // list of all implementers of any core
        Set<String> implementersUnion = new HashSet<>();
        // list of all features on any core
        Set<String> featuresUnion = new HashSet<>();
        for (Map<String, Object> procInfo : cpuInfo) {
            // implementers
            String implementer = (String) procInfo.get("cpu implementer");
            implementersUnion.add(implementer);
            // features
            try {
                String features = (String) procInfo.get("features");
                String[] featuresList = features.split(" ");
                for (String feature : featuresList) {
                    featuresUnion.add(features);
                }
            }
            catch(NullPointerException e){

            }
        }
        List<Set<String>> summaryList = new ArrayList<>();
        summaryList.add(featuresUnion);
        summaryList.add(implementersUnion);
        return summaryList;
    }



    public static List<String> getCpuUse() {
        /** Also check https://github.com/AntonioRedondo/AnotherMonitor
         *  and https://github.com/souch/AndroidCPU/blob/master/app/src/main/java/souch/androidcpu/CpuInfo.java
         *
         * **/
        /* This is no longer accessible
            https://issuetracker.google.com/issues/37140047
            https://github.com/Manabu-GT/DebugOverlay-Android/issues/11
            https://stackoverflow.com/questions/52782894/error-reading-cpu-usage-proc-stat-permission-denied
         */
        String cpustatFilename = "/proc/stat";
        List<String> cpustatLines = Utils.readLines(cpustatFilename);
        return cpustatLines;
        // FIXME: this returns an empty list
    }

    public static String getCpuUseTop() {
        /** Idea from http://notesbyanerd.com/2014/12/05/how-to-check-android-applications-cpu-usage/
         *
         * **/
        String output;
        String[] cmd = {
                    "sh",
                    "-c",
                    "top -m 1000 -d 1 -n 1 | grep \"" + 0 + "\" "};
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            try {
                output = stdInput.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                output = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            output = null;
        }
        return output;
        // FIXME: this might return null
    }

    public static List<String> getCpuUseProc() {
        int pid = android.os.Process.myPid();
        String filename = "/proc/" + String.valueOf(pid) + "/stat";
        List<String> lines = Utils.readLines(filename);
        return lines;
    }

    public static int getCoresNumber() {
        /* See
            https://stackoverflow.com/questions/30119604/how-to-get-the-number-of-cores-of-an-android-device
         */
        return Runtime.getRuntime().availableProcessors();
    }

    public static List<String> getFrequency() {
        String filename = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
        List<String> lines = Utils.readLines(filename);
        return lines;
    }

    public static int getCpuTemperature(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor TempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        SensorEventListener temperatureSensor = new SensorEventListener(){

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub
                float temp = event.values[0];
                Log.i("sensor", "sensor temp = " + temp);
            }
        };

        sensorManager.registerListener(temperatureSensor, TempSensor, SensorManager.SENSOR_DELAY_FASTEST);
        for (int i = 0; i < 100000; i++) {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static double getCpuTemperatureFile() {
        /* Attempted files (n=not found, p=no permission)
            n    "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp"
            n    "/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp"
            p    "/sys/class/thermal/thermal_zone1/temp"
            n    "/sys/class/i2c-adapter/i2c-4/4-004c/temperature"
            n    "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature"
            n    "/sys/devices/platform/omap/omap_temp_sensor.0/temperature"
            n    "/sys/devices/platform/tegra_tmon/temp1_input"
            n    "/sys/kernel/debug/tegra_thermal/temp_tj"
            n    "/sys/devices/platform/s5p-tmu/temperature"
            p    "/sys/class/thermal/thermal_zone0/temp"
            p    "/sys/devices/virtual/thermal/thermal_zone0/temp"
            n    "/sys/class/hwmon/hwmon0/device/temp1_input"
            p    "/sys/devices/virtual/thermal/thermal_zone1/temp"
            n    "/sys/devices/platform/s5p-tmu/curr_temp"
         */
        return -1.0;
    }
}
