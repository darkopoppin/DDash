package com.ddash.android_client.Threading;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ddash.android_client.Data.Memory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class BackgroundWorker extends Worker {

    private Context context;

    public BackgroundWorker(Context context, WorkerParameters parameters){
        super(context, parameters);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Map memory = Memory.getMemory(context);
        DocumentReference device = FirebaseFirestore.getInstance()
                .document("users/QKHu8goODYYJPYRKyHqAPrgCXuw1/Devices/LG-D852");
        device.set(memory);
        return Result.success();
    }
}
