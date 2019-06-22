package com.ddash.android_client.Data;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.ddash.MyApplication;
import com.ddash.android_client.Threading.MemoryTask;

import java.util.HashMap;
import java.util.Map;


public class Memory implements Runnable{

    private MemoryTask memoryTask;

    public Memory(){
        memoryTask = new MemoryTask();
    }

    public static Map<String, Object> getMemory(Context context) {
        // FIXME: Should Context or Activity be taken as an argument?
        Map<String, Object> memory = new HashMap<>();
        ActivityManager.MemoryInfo meminfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        manager.getMemoryInfo(meminfo);
        //final long BYTES_IN_KB = 0x100000L;
        long availMemBytes = meminfo.availMem;
        memory.put("availMemKB", availMemBytes);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            long totalMemKb = meminfo.totalMem;
            memory.put("totalMemKB", totalMemKb);
            // TODO: Do unit conversions on the view side of the app (not here)
        }
        // TODO: Get key for hashmap automatically from the method name (use reflection)
        long threshold = meminfo.threshold;
        memory.put("threshold", threshold);
        boolean lowMemory = meminfo.lowMemory;
        memory.put("lowMemory", lowMemory);
        return memory;
        // TODO: consider parsing /proc/meminfo for extra memory information
    }

    @Override
    public void run(){
        Log.d("MyTask", "is running");
        memoryTask.setDataMap(getMemory(MyApplication.getAppContext()));
        memoryTask.handleData();
    }
}
