package com.ddash;

import android.app.Application;
import android.content.Context;

    public class MyApplication extends Application {

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
    }
