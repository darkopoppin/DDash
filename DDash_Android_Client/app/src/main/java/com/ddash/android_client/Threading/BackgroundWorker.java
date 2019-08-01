package com.ddash.android_client.Threading;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;

import com.ddash.android_client.Data.Battery;
import com.ddash.android_client.Data.LocationService;
import com.ddash.android_client.Data.Memory;
import com.ddash.android_client.Data.SystemData;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class BackgroundWorker extends Worker {

    private Context context;
    private FirebaseFirestore firebase;
    private String user;
    public BackgroundWorker(Context context, WorkerParameters parameters){
        super(context, parameters);
        this.context = context;
        this.firebase = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseAuth fireAuth = FirebaseAuth.getInstance();
        Map memory = Memory.getMemory(context);
        Map battery = Battery.getBattery(context);
        LocationService.getLastKnownLocation(context);
        String userId = fireAuth.getUid();
        String userNames = fireAuth.getCurrentUser().getDisplayName();
        DocumentReference document = firebase
                .document("users/" + userId + "/Devices/" + Build.DEVICE);
        document.set(memory, SetOptions.merge());
        document.set(battery, SetOptions.merge());
        return Result.success();
    }
}
