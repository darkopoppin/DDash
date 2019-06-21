package com.ddash.android_client;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import android.widget.TextView;

import com.ddash.MyApplication;
import com.ddash.android_client.Data.Memory;

import java.util.Map;
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
    private Activity mainActivity;

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
                Log.d("myHandler", "inside");
                MemoryTask memoryTask =(MemoryTask) inputMessage.obj;
                Map memoryData = memoryTask.getDataMap();
                TextView memoryText = mainActivity.findViewById(R.id.textView2);
                long availableM = (long) memoryData.get("availMemKB");
                memoryText.setText(Double.toString(Utils.convertBytes(availableM)));
            }
        };
    }

    public void runTasks(Activity activity){
        mainActivity = activity;
        EXECUTOR.scheduleWithFixedDelay(new Memory(), 3, 3, TimeUnit.SECONDS);
    }
    public void handleData(MemoryTask memoryTask, int task){
        Message message = handler.obtainMessage(task, memoryTask);
        message.sendToTarget();
    }

    public static ThreadManager getManagerInstance(){
        return managerInstance;
    }

}

