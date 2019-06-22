package com.ddash;

import android.app.Application;
import android.content.Context;


        private static Context applicationContext;
        private static Context context;

        public void onCreate(){
            super.onCreate();
            applicationContext = getApplicationContext();
            context = getAppContext();
        }

        public static Context getAppContext(){
            return applicationContext;
        }

        public static Context getContext() { return  context; }

public class MyApplication extends Application {

    public static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();

    }

    public static Context getAppContext(){
        return MyApplication.context;
    }
}