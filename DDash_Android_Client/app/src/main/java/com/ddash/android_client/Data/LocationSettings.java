package com.ddash.android_client.Data;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.ddash.MyApplication;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


public class LocationSettings {

    private WeakReference activity;
    private LocationRequest locationRequest;
    private final int REQUEST_CHECK_SETTINGS = 0;


    public LocationSettings(Activity activity){
        this.activity = new WeakReference(activity);
    }


    public LocationRequest createLocationRequest() {
        // LocationRequest stores parameters for requests to the fused location provider
        this.locationRequest = LocationRequest.create();
        // the rate at which location update is received in milliseconds
        locationRequest.setInterval(10000);
        // the fastest rate at which location updates can be handled
        locationRequest.setFastestInterval(5000);
        //the priority of the request
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    /**
     * Checks if the device's location settings meet the requirements of
     * the app location settings
     */
    public void checkLocationSettings(Context context){
        //specifies the types of location services the client is interested in using
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        //get the device's location settings
        SettingsClient client = LocationServices.getSettingsClient(context);
        //checks if the current device's settings satisfy app's location needs
        Task<LocationSettingsResponse> result = client.checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try{
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    //App's location needs are satisfied. Do your thing here:
                }
                //ApiException is an exception to be returned by a Task when a call to Google Play services has failed
                catch(ApiException exception){
                    switch (exception.getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //App's location needs are not satisfied. But it could be fixed by showing the user a dialog
                            try{
                                //ApiException but with a possible resolution
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                //starts an intent to resolve the failure
                                //the result is returned in onActivityResult()
                                Activity mainActivity = (Activity) activity.get();
                                if (mainActivity == null){
                                    activity = new WeakReference(MyApplication.getAppContext());
                                    mainActivity = (Activity) activity.get();
                                }
                                resolvable.startResolutionForResult(mainActivity, REQUEST_CHECK_SETTINGS);
                            }
                            catch(IntentSender.SendIntentException e){
                                //Ignore
                            }
                            catch(ClassCastException e){
                                //Ignore
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //App's location needs are not satisfied and it cannot be fixed
                            break;
                    }
                }
            }
        });
    }

    public static void getLastKnownLocation(Context context){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(Task<Location> task) {
                    if (task.isSuccessful()) {
                        String user = FirebaseAuth.getInstance().getUid();
                        DocumentReference device = FirebaseFirestore.getInstance()
                                .document("users/" + user + "/Devices/" + Build.DEVICE);
                        Map <String, Object> map = new HashMap<>();
                        map.put("location", task.getResult().toString());
                        device.set(map, SetOptions.merge());
                    } else {
                        // when permission is denied, location is turned off
                        Log.d("myLocation", "false");
                    }
                }
            });
        }
    }


}
