package com.ddash.android_client;

import android.util.Log;

public class Utils {
    /**
     * Converts bytes to GB if the value is > 1000 MB otherwise it converts to MB
     * returns double number
     */
    public static double convertBytes (long bytes){
        Log.d("myConvert", Long.toString(bytes));
        double fBytes = (double) bytes;
        Log.d("myConvert", Double.toString(fBytes));
        for(int i = 0; i < 4; i++) {
            if (fBytes>1000)
                fBytes = fBytes / 1024;
            Log.d("myConvert", Double.toString(fBytes));
        }
        return fBytes;
    }

}