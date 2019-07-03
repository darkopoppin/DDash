package com.ddash.android_client;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import android.view.View;
import android.widget.TextView;


import com.ddash.android_client.Data.Battery;
import com.ddash.android_client.Data.Cpu;
import com.ddash.android_client.Data.InternetSpeedTest;
import com.ddash.android_client.Data.LocationService;
import com.ddash.android_client.Data.Memory;
import com.ddash.android_client.Data.Network;
import com.ddash.android_client.Data.ScanStorage;
import com.ddash.android_client.Data.Storage;
import com.ddash.android_client.Data.Connectivity;
import com.ddash.android_client.Data.SystemData;
import com.ddash.android_client.Threading.ThreadManager;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String FETCHED_DATA = "com.ddash.android_client.FETCHED_DATA";
    public static final String DATA_TAG = "FETCHED_DATA";
    private final int PERMISSION_REQUEST_CODE = 0;
    private final int REQUEST_CHECK_SETTINGS = 0;

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
        setContentView(R.layout.activity_main);
        checkRequestPermissions();

        ThreadManager threadPool = ThreadManager.getManagerInstance();
        threadPool.runTasks(this);

        googlePlayServices = checkPlayServices();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(googleApiCallback)
                .addOnConnectionFailedListener(googleApiCallback)
                .build();


        googleApiClient.connect();
        Gson gson = new Gson();


        /* Display CPU data */

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

//        String cpuData = gson.toJson(cpuSummary);
//        Utils.largeLog("CPU_ABOUT", displayFeatures);
//        Utils.largeLog("CPU_ABOUT", displayImplementers);

        TextView cpuText = findViewById(R.id.main_text_cpuabout);
        String cpuData = "Number of Cores: " + cores + "\n" +
                         "Available Features: " + displayFeatures + "\n" +
                         "Implementers: " + displayImplementers + "\n";
        cpuText.setText(cpuData);


        /* Display NETWORK data */

//        Network network = new Network(getApplicationContext().getSystemService(WIFI_SERVICE));
//        String ssid = network.getSsid();
//        String ip = network.getIp();
//        String mac = network.getmacAddress();
//        TextView networkText = findViewById(R.id.main_text_net);
//        String text = "SSID: "+ ssid + "\n" +
//                      "IP: " + ip + "\n" +
//                      "MAC: " + mac;
//        networkText.setText(text);

        /* Some other ideas:
            IP, MAC, Network ID (?), not SSID (known)
            Connectivity status
            Speed (provisional);

            Consider icons indicating approximate speed, kind (mobile, wifi), etc.
        */


        /* Log all data for debugging */
//        List<Object> data = getAllData();
//        String jsonData = gson.toJson(data);
//        Utils.largeLog(DATA_TAG, jsonData);

        /* consider dynamic (changing, real-time) vs static (constant, once-off) data
            Different UI views for each type?
        */

//        CardView systemCard = (CardView) findViewById(R.id.main_card_system);
//        systemCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openSystemActivity();
//            }
//        });
    }

    @Override
    public void onStart(){
        super.onStart();
        getStorage();
        displaySystemData();
    }


    public void refreshStorage(View view){
        getStorage();
    }

    public void getStorage(){
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

        //if storage permission has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //ArrayLists containing the general info of the storage internal[total, os total, free, used] | external[total, free, used]
            List<Long> intStorage = phoneStorage.getInternalStorage();
            List<Long> extStorage = phoneStorage.getSdCardStorage();

            //Display the respective storage in UI
            TextView internalText = findViewById(R.id.main_text_internal_storage);
            internalText.setText(String.format("%.1f of %.1f free", Utils.convertBytes(intStorage.get(2)), Utils.convertBytes(intStorage.get(0))));
            TextView externalText = findViewById(R.id.main_text_external_storage);

            if (extStorage == null){
                externalText.setText("No sd card.");
            } else {
                externalText.setText(String.format("%.1f of %.1f free", Utils.convertBytes(extStorage.get(1)), Utils.convertBytes(extStorage.get(0))));
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
    public void onDestroy(){
        super.onDestroy();
        googleApiClient.disconnect();

    public void onStop(){
        super.onStop();
//        googleApiClient.disconnect();

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

    public List<Object> getAllData() {
        List<Object> data = new ArrayList<>();

        data.add(SystemData.getSystemData(getApplicationContext()));
        data.add(Cpu.getCpu());
        data.add(Memory.getMemory(getApplicationContext()));
        data.add(Battery.getBattery(getApplicationContext()));

        Network network = new Network(getApplicationContext().getSystemService(WIFI_SERVICE));
        List<Object> networkInfo = network.getAllWifiDetails();
        data.add(networkInfo);

        Connectivity connection = new Connectivity(getApplicationContext());
        List<Object> connectionInfo = connection.getConnectivityStatus();
        data.add(connectionInfo);
        
//        Double downloadSpeed = null;
//        try {
//            downloadSpeed = InternetSpeedTest.run();
//            data.add(downloadSpeed + "");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return data;
    }
    public void displaySystemData(){
        Map<String, Object> systemData = SystemData.getSystemData(getApplicationContext());
        Log.d(TAG, "getSystemData: The following are the system data" + systemData.toString());
        TextView systemView = findViewById(R.id.main_text_system);
        systemView.setText("Summary:\n" +
                "API Level "+systemData.get("version_int")+ "\n" +
                "Version Number "+ systemData.get("version_release")+ "\n" +
                "Version Codename "+systemData.get("version_codename"));

    }
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
    public void openSystemActivity(View view){
        Intent intent = new Intent(this, SystemActivity.class);
        startActivity(intent);
    }
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
