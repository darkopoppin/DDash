package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ddash.Data.Battery;
import com.ddash.Data.Connectivity;
import com.ddash.Data.Cpu;
import com.ddash.Data.Memory;
import com.ddash.Data.MyLocation;
import com.ddash.Data.Network;
import com.ddash.Data.ScanStorage;
import com.ddash.Data.Storage;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String FETCHED_DATA = "com.ddash.android_client.FETCHED_DATA";
    private final int PERMISSION_REQUEST_CODE = 0;
    private final int REQUEST_CHECK_SETTINGS = 0;
    private final int LOCATION_REQUEST_CODE = 0;
    private boolean googlePlayServices = false;
    private boolean lessThan23SDK = false;
    String [] appPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkRequestPermissions();

        googlePlayServices = checkPlayServices();
        if (googlePlayServices) {
            MyLocation myLocation = new MyLocation(MainActivity.this);
            LocationRequest locationRequest = myLocation.createLocationRequest();
            myLocation.checkLocationSettings(getApplicationContext());
            Intent intentTest = new Intent(this, MyLocation.class);
            intentTest.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    LOCATION_REQUEST_CODE, intentTest, PendingIntent.FLAG_UPDATE_CURRENT);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(locationRequest, pendingIntent);
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Storage phoneStorage = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            phoneStorage = new Storage(getExternalFilesDirs(null));
        }
        else{
            File [] array = {getExternalFilesDir(null), null};
            phoneStorage = new Storage(array);
        }

        Thread internal = new ScanStorage(phoneStorage.getInternal());
        Thread sdCard = new ScanStorage(phoneStorage.getSdCard());

        //if permission has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            phoneStorage.getInternalStorage();
            phoneStorage.getSdCardStorage();
            internal.start();
            sdCard.start();
            try {
                sdCard.join();
            }
            catch (InterruptedException e){

            }
            ((ScanStorage) sdCard).printFiles();
        }//if permission is not granted
        else {
            phoneStorage.getInternalStorage();
            internal.start();
        }
    }

    public void getData(View view) {
        List<Object> data = new ArrayList<>();

        data.add(SystemData.getSystemData(this));
        data.add(Cpu.getCpu());
        data.add(Memory.getMemory(this));
        data.add(Battery.getBattery(this));

        Network network = new Network(getApplicationContext().getSystemService(WIFI_SERVICE));
        List<Object> networkInfo = network.getAllWifiDetails();
        data.add(networkInfo);

        Connectivity connection = new Connectivity(getApplicationContext());
        List<Object> connectionInfo = connection.getConnectivityStatus();
        data.add(connectionInfo);

        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        Intent intent = new Intent(this, DisplayDataActivity.class);
        intent.putExtra(FETCHED_DATA, jsonData);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch(requestCode){
            case REQUEST_CHECK_SETTINGS:
                switch ((resultCode)){
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public  void getLastKnownLocation(){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Log.d("myLocation", task.getResult().toString());
                    } else {
                        // when permission is denied, location is turned off
                        Log.d("myLocation", "false");
                    }
                }
            });
        }
    }
    /**
     * Checks if Google Play Services is available, provided that they are not,
     * it tries to make them available.
     */
    public boolean checkPlayServices(){
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        // 1 - Service Missing, 0 - Success, 18 - Service Updating, 3 - Service Disabled, 9 - Service Invalid
        int resultCode = googleApi.isGooglePlayServicesAvailable(getApplicationContext());
        if(resultCode == ConnectionResult.SERVICE_MISSING || resultCode == ConnectionResult.SERVICE_DISABLED){
            if(googleApi.isUserResolvableError(resultCode))
                googleApi.makeGooglePlayServicesAvailable(this);
            else
                return false;
        }
        return true;
    }

    /**
     * called at the start of mainActivity
     * checks for read storage permissions if sdk >= 23
     * return -1 (denied), 0 (granted), 1(sdk < 23 or marshmallow)
     */
    public void checkRequestPermissions(){
        // checks if sdk >= 23
        List <String> permissionRequired = new ArrayList<>();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(String permission : appPermissions){
                int result = ContextCompat.checkSelfPermission(this, permission);
                if (result != PackageManager.PERMISSION_GRANTED){
                    permissionRequired.add(permission);
                }
            }

            if (!permissionRequired.isEmpty())
                ActivityCompat.requestPermissions(this, permissionRequired.toArray(new String[permissionRequired.size()]),
                        PERMISSION_REQUEST_CODE);
        }
        //sdk < 23
        else
            lessThan23SDK = true;
    }

    /**
     *  Callback received when a permission request is completed
     */
 /*   @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] grantResults){
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Log.i("TagInfo", "Response for storage permission is received");

            //permission granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("TagInfo", "Permmission Granted");
            }
            //permission denied
            else{
                Log.i("TagInfo", "Permmission Denied");
            }
        }
    }*/
}
