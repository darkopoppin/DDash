package com.ddash.android_client.Threading;

import android.app.Activity;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import android.widget.TextView;

import com.ddash.android_client.Data.Memory;
import com.ddash.android_client.R;
import com.ddash.android_client.Helpers.Utils;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;


import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
    private static ThreadManager managerInstance;
    private final ScheduledThreadPoolExecutor EXECUTOR;
    Handler handler;
    private Activity mainActivity;

    private final int MEMORY = 1;

    //this is static block, it gets called only once, when the class is initialised
    //no matter how many objects are created
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
                switch (inputMessage.what){
                    case(MEMORY):
                        MemoryTask memoryTask =(MemoryTask) inputMessage.obj;
                        TextView memoryText = mainActivity.findViewById(R.id.main_text_ram);
                        double totalM = memoryTask.getTotalMemory();
                        Log.d("myMemoryTotal", Double.toString(totalM));
                        double usedM = memoryTask.getUsedMemory();
                        Log.d("myMemoryUsed", Double.toString(usedM));
                        int percentage = Utils.convertToPercentage(usedM, totalM);
                        //setting the text
                        memoryText.setText(String.format("RAM\n%d%%", percentage));
                        //changing the size of the vector to match the used memory
                        VectorMasterView ramVector = mainActivity.findViewById(R.id.main_vector_ram);
                        PathModel path = ramVector.getPathModelByName("path");
                        float trimEnd = (float) percentage / 100;
                        path.setTrimPathEnd(trimEnd);
                        ramVector.update();

                }

            }
        };
    }

    public void runTasks(Activity activity){
        mainActivity = activity;
        //runnable object, initial delay, delay, time unit
        EXECUTOR.scheduleWithFixedDelay(new Memory(), 3, 3, TimeUnit.SECONDS);
    }

    //the task object containing all the data (see MemoryTask), taskID (memory - 1, CPU - 2)
    public void handleData(Object task, int taskID){
        Message message = handler.obtainMessage(taskID, task);
        message.sendToTarget();
    }

    public static ThreadManager getManagerInstance(){
        return managerInstance;
    }
    
}

