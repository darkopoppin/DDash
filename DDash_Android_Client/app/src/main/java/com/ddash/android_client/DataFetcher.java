package com.ddash.android_client;

import android.os.Build;

public class DataFetcher {

    public static String getDeviceModel() {
        return Build.MODEL;
    }
}
