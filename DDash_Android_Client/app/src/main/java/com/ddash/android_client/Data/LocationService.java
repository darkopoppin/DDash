package com.ddash.android_client.Data;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ddash.MyApplication;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentTest = new Intent(this, LocationBroadcastReceiver.class);
        intentTest.setAction(LocationBroadcastReceiver.ACTION_PROCESS_UPDATES);
        /*LocationSettings locationSettings = new LocationSettings(activity);
        LocationRequest locationRequest = locationSettings.createLocationRequest();
        locationSettings.checkLocationSettings(MyApplication.getAppContext());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getAppContext(),
                LOCATION_REQUEST_CODE, intentTest, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.getFusedLocationProviderClient(MyApplication.getAppContext()).requestLocationUpdates(locationRequest, pendingIntent);
    */
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                                resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
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
                        try {
                            map.put("location", task.getResult().toString());
                        }catch(Exception e){;}
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
