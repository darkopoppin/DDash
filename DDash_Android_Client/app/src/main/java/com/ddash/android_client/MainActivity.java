package com.ddash.android_client;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;



import android.os.Bundle;
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
import com.ddash.android_client.Helpers.GoogleApiCallback;
import com.ddash.android_client.Threading.BackgroundWorker;
import com.ddash.android_client.Threading.ThreadManager;
import com.ddash.android_client.Helpers.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String FETCHED_DATA = "com.ddash.android_client.FETCHED_DATA";
    private final int PERMISSION_REQUEST_CODE = 0;
    private final int READ_EXTERNAL_STORAGE = 0;
    private final int ACCESS_FINE_LOCATION = 1;
    private final int REQUEST_CHECK_SETTINGS = 0;
    private static final int RC_SIGN_IN = 123;
    private boolean googlePlayServices = false;
    private GoogleApiClient googleApiClient;
    private GoogleApiCallback googleApiCallback = new GoogleApiCallback(this);
    private boolean lessThan23SDK = false;

    String [] appPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE};
    private String TAG =  "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build()
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

        final Button authentication = findViewById(R.id.main_button_signin);
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
                Button signin = findViewById(R.id.main_button_signin);
                signin.setVisibility(View.VISIBLE);
            }
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            String name = auth.getCurrentUser().getEmail();
            authentication.setVisibility(View.GONE);

            /*
            * To get the display name (First name and last name used on sign up) of the current user:
            *           auth.getCurrentUser().getDisplayName()
            * */
            for(UserInfo userInfo : auth.getCurrentUser().getProviderData()){
                if (userInfo != null)
                    name = userInfo.getDisplayName();
            }
            Toast.makeText(this, "Welcome "+name, Toast.LENGTH_LONG).show();

        } else {
            // not signed in
            logout.setVisibility(View.GONE);
        }

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

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueue(workRequest);

        //broadcast receiver for location updates
        BroadcastReceiver locationBR = new LocationBroadcastReceiver();
        IntentFilter locationFilter = new IntentFilter();
        locationFilter.addAction("com.google.android.gms.location.locationupdatespendingintent.action.PROCESS_UPDATES");

        Intent notificationIntent = new Intent(this, BatteryBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("test")
                .setContentText("testing")
                .setContentIntent(pendingIntent)
                .build();
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
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseFirestore firebase = FirebaseFirestore.getInstance();
                Map<String,Object> map = new HashMap<>();

                String userID = auth.getUid();
                String email = auth.getCurrentUser().getEmail();
                map.put("email", email);
                firebase.collection("users").document(userID).set(map, SetOptions.merge());

                String userNames = auth.getCurrentUser().getDisplayName();
                Toast.makeText(this, "Successfully logged in: "+ userNames, Toast.LENGTH_SHORT).show();


                //Remove the sign in button
                Button authentication = findViewById(R.id.main_button_signin);
                authentication.setVisibility(View.GONE);

                Constraints constraints = new Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class)
                        .setConstraints(constraints)
                        .build();

                WorkManager workManager = WorkManager.getInstance(getApplicationContext());
                workManager.enqueue(workRequest);

                //Display the log out button
                Button logout = findViewById(R.id.main_button_logout);
                logout.setVisibility(View.VISIBLE);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, response.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getStorage(){
        Storage phoneStorage = new Storage(getExternalFilesDirs(null));

        Thread internal = new ScanStorage(phoneStorage.getInternal());
        Thread sdCard = new ScanStorage(phoneStorage.getSdCard());

        //ArrayLists containing the general info of the storage internal[total, os total, free, used]
        Map<String, Long> intStorage = phoneStorage.getInternalStorage();

        //Display the internal storage in UI
        TextView internalText = findViewById(R.id.main_text_internal_storage);
        double internalFree= Utils.convertBytes(intStorage.get("internalAvailable"));
        double internalTotal = Utils.convertBytes(intStorage.get("internalTotal"));
        internalText.setText(String.format("%.2fGB used of %.2fGB", internalTotal-internalFree, internalTotal));

        //Vector UI thingy majigga
        int percentage = Utils.convertToPercentage(internalTotal-internalFree,internalTotal);

        VectorMasterView internalUi = findViewById(R.id.main_vector_internal);
        PathModel internalPath = internalUi.getPathModelByName("internal");

        //scan internal storage
        internal.start();

        //if read external storage permission has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //ArrayLists containing the general info of the storage internal[total, os total, free, used] | external[total, free, used]
            Map<String, Long> extStorage = phoneStorage.getSdCardStorage();

            //Display the external storage in UI
            float trimEnd = (float) percentage/100;
            internalPath.setTrimPathEnd(trimEnd);
            TextView externalText = findViewById(R.id.main_text_external_storage);

            VectorMasterView externalUi = findViewById(R.id.main_vector_external);
            PathModel externalPath = externalUi.getPathModelByName("internal");

            if (extStorage == null){
                externalPath.setStrokeColor(Color.RED);
                externalText.setText("No SD card.");
            } else {
                double externalFree = Utils.convertBytes(extStorage.get("sdAvailable"));
                double externalTotal = Utils.convertBytes(extStorage.get("sdTotal"));
                int percentageInternal = Utils.convertToPercentage(externalTotal-externalFree,externalTotal);
                float trimEndExternal = (float) percentageInternal/100;
                externalPath.setTrimPathEnd(trimEndExternal);
                externalText.setText(String.format("%.2fGB used of %.2fGB",externalTotal-externalFree ,externalTotal ));
            }

            sdCard.start();
            try {
                sdCard.join();
            }
            catch (InterruptedException e){

            }
        }
        //if storage permission is not granted
        else {
            TextView internalStorage = findViewById(R.id.main_text_external_storage);
            internalStorage.setText(getString(R.string.storage_permission_denied));
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

    }

    /** Show some network information **/
    public void displayNetworkData() {
        String text = "";

        TextView netText = findViewById(R.id.main_text_networkStats);
        Network network = new Network(getApplicationContext().getSystemService(WIFI_SERVICE));
        Connectivity conn = new Connectivity(getApplicationContext());

        boolean connectionType = conn.isConnected();
        TextView disconnected = findViewById(R.id.main_text_networkDisconnect);

        if (connectionType == false){               //If not connected to internet:
            disconnected.setText("DISCONNECTED");   //Display Disconnected on the Network card UI
        } else {                                    //If connected:
            disconnected.setText("");               //Don't display anything on the widget
        }

        String ssid;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ssid = network.getSsid();            //Hardcoded fields used in the Network card UI
        else
            ssid = "Location permission denied";
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
        TextView systemView = findViewById(R.id.main_text_system);

        String systemDataText = "Summary:\n" +
                "API Level "+systemData.get("version_int")+ "\n" +
                "Version Number "+ systemData.get("version_release")+ "\n" +
                "Version Codename "+systemData.get("version_codename");

        systemView.setText(systemDataText);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i : grantResults){
            switch (i){
                case READ_EXTERNAL_STORAGE:
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        getStorage();
                case ACCESS_FINE_LOCATION:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        displayNetworkData();
            }
        }
    }
}
