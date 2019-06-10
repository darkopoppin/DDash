package com.ddash.android_client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Cpu {
    public static List<String[]> getCpu() {
        Map<String, Object> map =  new HashMap<>();
        // in this file we can find information about cores
        // https://www.thegeekdiary.com/proccpuinfo-file-explained/
        String cpuinfoFilename = "proc/cpuinfo";
        List<String> cpuinfoLines = Utils.readLines(cpuinfoFilename);
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
}
