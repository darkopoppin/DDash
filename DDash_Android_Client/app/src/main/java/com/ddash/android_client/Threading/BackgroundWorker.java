package com.ddash.android_client.Threading;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ddash.android_client.Data.Battery;
import com.ddash.android_client.Data.Memory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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
        Map battery = Battery.getBattery(context);
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        String user = FirebaseAuth.getInstance().addAuthStateListener();
        DocumentReference device = FirebaseFirestore.getInstance()
                .document("users/" + user + "/Devices/LG-D852");
        device.set(memory);
        device.set(battery, SetOptions.merge());
        return Result.success();
    }
}
