package com.ddash.android_client;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

import com.ddash.MyApplication;
import com.ddash.android_client.Data.Memory;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

public class ThreadManager {
    private static ThreadManager managerInstance;
    private final ScheduledThreadPoolExecutor EXECUTOR;
    Handler handler;
    //this is static block, it gets called only once, when the class is initialised
    //no matter how many object are created
    //therefore there is only one instance of the thread pool
    static{
        managerInstance = new ThreadManager();
    }

    //singleton class
    private ThreadManager(){
        EXECUTOR = new ScheduledThreadPoolExecutor(3);
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage){

            }
        };
    }

    public void runTasks(Context context){
        EXECUTOR.scheduleWithFixedDelay(new Memory(), 3, 3, TimeUnit.SECONDS);
    }

    public static ThreadManager getManagerInstance(){
        return managerInstance;
    }

}

class MemoryTask implements Runnable{
    private Context context;

    public MemoryTask(Context context){
        this.context = MyApplication.getAppContext();
    }
    @Override
    public void run(){

    }
}
