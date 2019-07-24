package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ddash.android_client.Data.Battery;
import com.ddash.android_client.Data.BatteryBroadcastReceiver;
import com.ddash.android_client.Data.Connectivity;
import com.ddash.android_client.Data.Cpu;
import com.ddash.android_client.Data.LocationBroadcastReceiver;
import com.ddash.android_client.Data.Memory;
import com.ddash.android_client.Data.Network;
import com.ddash.android_client.Data.ScanStorage;
import com.ddash.android_client.Data.Storage;
import com.ddash.android_client.Data.SystemData;
import com.ddash.android_client.Threading.ThreadManager;
import com.ddash.android_client.Helpers.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String FETCHED_DATA = "com.ddash.android_client.FETCHED_DATA";
    private final int PERMISSION_REQUEST_CODE = 0;
    private final int REQUEST_CHECK_SETTINGS = 0;
    private static final int RC_SIGN_IN = 123;
    private boolean googlePlayServices = false;
    private GoogleApiClient googleApiClient;
    private GoogleApiCallback googleApiCallback = new GoogleApiCallback(this);
    private boolean lessThan23SDK = false;
    String [] appPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private String TAG =  "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());
        );

        setContentView(R.layout.activity_main);
        checkRequestPermissions();

        ThreadManager threadPool = ThreadManager.getManagerInstance();
        threadPool.runTasks(this);

        // initiation of google play services
        googlePlayServices = checkPlayServices();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(googleApiCallback)
                .addOnConnectionFailedListener(googleApiCallback)
                .build();

        googleApiClient.connect();

        final Button authentication = findViewById(R.id.main_button_authentication);
        authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        });

        final Button logout = findViewById(R.id.main_button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "SUCCESSFULLY SIGNED OUT!", Toast.LENGTH_SHORT).show();
                //Remove logout button since the user just logged out
                Button logout = findViewById(R.id.main_button_logout);
                logout.setVisibility(View.GONE);
                //Re-display the sign in button
                Button signin = findViewById(R.id.main_button_authentication);
                signin.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        getStorage();
        //creates broadcast receiver for changes in the battery state
        BroadcastReceiver batteryBR = new BatteryBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(batteryBR, filter);
        Battery.setBatteryStatus(this);

        //broadcast receiver for location updates
        BroadcastReceiver locationBR = new LocationBroadcastReceiver();
        IntentFilter locationFilter = new IntentFilter();
        locationFilter.addAction("com.google.android.gms.location.locationupdatespendingintent.action.PROCESS_UPDATES");

        displaySystemData();
        displayNetworkData();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Successfully logged in: "+ user.toString(), Toast.LENGTH_SHORT).show();

                //Remove the sign in button
                Button authentication = findViewById(R.id.main_button_authentication);
                authentication.setVisibility(View.GONE);

                //Display the log out button
                Button logout = findViewById(R.id.main_button_logout);
                logout.setVisibility(View.VISIBLE);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getStorage(){
        Storage phoneStorage = new Storage(getExternalFilesDirs(null));

        Thread internal = new ScanStorage(phoneStorage.getInternal());
        Thread sdCard = new ScanStorage(phoneStorage.getSdCard());

        //if storage permission has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //ArrayLists containing the general info of the storage internal[total, os total, free, used] | external[total, free, used]
            List<Long> intStorage = phoneStorage.getInternalStorage();
            List<Long> extStorage = phoneStorage.getSdCardStorage();

            //Display the respective storage in UI
            TextView internalText = findViewById(R.id.main_text_internal_storage);
            double internalUsed = Utils.convertBytes(intStorage.get(2));
            double internalTotal = Utils.convertBytes(intStorage.get(0));
            internalText.setText(String.format("%.2fGB used of %.2fGB", internalTotal-internalUsed, internalTotal));

            //Vector UI thingy majigga
            int percentage = Utils.convertToPercentage(internalTotal-internalUsed,internalTotal);

            VectorMasterView internalUi = findViewById(R.id.main_vector_internal);
            PathModel internalPath = internalUi.getPathModelByName("internal");
            float trimEnd = (float) percentage/100;
            internalPath.setTrimPathEnd(trimEnd);
            TextView externalText = findViewById(R.id.main_text_external_storage);

            VectorMasterView externalUi = findViewById(R.id.main_vector_external);
            PathModel externalPath = externalUi.getPathModelByName("internal");

            if (extStorage == null){
                externalPath.setStrokeColor(Color.RED);
                externalText.setText("No SD card.");
            } else {

                double externalUsed = Utils.convertBytes(extStorage.get(1));
                double externalTotal = Utils.convertBytes(extStorage.get(0));
                int percentageInternal = Utils.convertToPercentage(externalTotal-externalUsed,externalTotal);
                float trimEndExternal = (float) percentageInternal/100;
                externalPath.setTrimPathEnd(trimEndExternal);


                externalText.setText(String.format("%.2fGB used of %.2fGB",externalTotal-externalUsed ,externalTotal ));
            }

            internal.start();
            sdCard.start();
            try {
                sdCard.join();
            }
            catch (InterruptedException e){

            }
            ((ScanStorage) sdCard).printFiles();
        }//if storage permission is not granted
        else {
            phoneStorage.getInternalStorage();
            internal.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }



    /** Log all data for debugging **/
    void logAll(String tag) {
        List<Object> data = new ArrayList<>();
        data.add(SystemData.getSystemData(getApplicationContext()));
        data.add(Cpu.getCpu());
        data.add(Memory.getMemory(getApplicationContext()));
        data.add(Battery.getBattery(getApplicationContext()));

        /* we use JSON serialization because it can show values
        of nested data */
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);
        Utils.largeLog(tag, jsonData);
        //Added sample comment
    }

    /** Show some network information **/
    public void displayNetworkData() {
        String text = "";

        TextView netText = findViewById(R.id.main_text_networkStats);
        Network network = new Network(getApplicationContext().getSystemService(WIFI_SERVICE));
        Connectivity conn = new Connectivity(getApplicationContext());
        boolean connectionType = conn.isConnected();
        TextView disconnected = findViewById(R.id.main_text_networkDisconnect);
        if (connectionType == false){
            disconnected.setText("DISCONNECTED");
        } else {
            disconnected.setText("");
        }
        String ssid = network.getSsid();
        String ip = network.getIp();
        String mac = network.getmacAddress();
        text = text+ "SSID: "+ ssid + "\n" +
                "IP: " + ip + "\n" +
                "MAC: " + mac;
        netText.setText(text);
    }

    /** Show facts about CPU **/
    public void displayCpuData(TextView cpuText) {
        List<Map<String, Object>> cpuAbout = Cpu.getCpuAbout();
        List<Set<String>> cpuSummary = Cpu.getCpuAboutSummary(cpuAbout);
        List<String> features = new ArrayList<>(cpuSummary.get(0));
        List<String> implementers = new ArrayList<>(cpuSummary.get(1));
        StringBuffer sb = new StringBuffer();
        for (String feature : features.subList(0, features.size()-1)) {
            sb.append(feature);
            sb.append(", ");
        }
        sb.append(features.get(features.size()-1));
        String displayFeatures = sb.toString();

        StringBuffer sb_impl = new StringBuffer();
        for (String implementer : implementers.subList(0, implementers.size()-1)) {
            sb_impl.append(implementer);
            sb_impl.append(", ");
        }
        sb_impl.append(implementers.get(implementers.size()-1));
        String displayImplementers = sb_impl.toString();

        int cores = Cpu.getCoresNumber();

        String cpuData = "Number of Cores: " + cores + "\n" +
                         "Available Features: " + displayFeatures + "\n" +
                         "Implementers: " + displayImplementers + "\n";
        cpuText.setText(cpuData);
    }

    /** Show a summary of system information **/
    public void displaySystemData(){
        Map<String, Object> systemData = SystemData.getSystemData(getApplicationContext());
        Log.d(TAG, "getSystemData: The following are the system data" + systemData.toString());
        TextView systemView = findViewById(R.id.main_text_system);
        systemView.setText("Summary:\n" +
                "API Level "+systemData.get("version_int")+ "\n" +
                "Version Number "+ systemData.get("version_release")+ "\n" +
                "Version Codename "+systemData.get("version_codename"));
    }

    /*
    //  ########################Disabled indefinitely, unstable#################################################

    public void getDownloadSpeed(View view){
        Log.d(TAG,"getDownloadSpeed : Commencing Internet download speed ");
        TextView test = findViewById(R.id.main_text_netspeed);
        Double downloadSpeed = null;
        try {
            downloadSpeed = InternetSpeedTest.run();
            test.setText(String.format("Download: %.2f Mbps", downloadSpeed));
        } catch (InterruptedException e) {
            e.printStackTrace();
            test.setText("Something went wrong!");
        }
    }
    */

    public void openSystemActivity(View view){
        Intent intent = new Intent(this, SystemActivity.class);
        startActivity(intent);
    }
    public void openNetworkActivity(View view){
        Intent intent = new Intent(this, NetworkActivity.class);
        startActivity(intent);
    }

    /**
     * last known location might use it later
     */
    public  void getLastKnownLocation(){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(Task<Location> task) {
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
     * it attempts to make them available.
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
     * checks and request permissions if sdk >= 23
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
