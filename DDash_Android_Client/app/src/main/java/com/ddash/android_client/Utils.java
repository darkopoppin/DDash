package com.ddash.android_client;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

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

    /*static void setLocationUpdatesResult(Context context, List<Location> locations) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations))
                .apply();
    }*/

}