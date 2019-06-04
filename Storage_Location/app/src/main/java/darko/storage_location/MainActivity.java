package darko.storage_location;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import darko.storage_location.Storage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private final int READ_STORAGE_PERMISSION_REQUEST_CODE = 0;
    private final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private final int REQUEST_CHECK_SETTINGS = 0;
    private final int LOCATION_REQUEST_CODE = 0;
    private boolean googlePlayServices = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googlePlayServices = checkPlayServices();
        if (googlePlayServices == true) {
            MyLocation myLocation = new MyLocation(MainActivity.this);
            LocationRequest locationRequest = myLocation.createLocationRequest();
            myLocation.checkLocationSettings(getApplicationContext());
            Intent intentTest = new Intent(this, MyLocation.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    LOCATION_REQUEST_CODE, intentTest, PendingIntent.FLAG_CANCEL_CURRENT);
            if(checkFineLocationPermission() == 0)
            LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(locationRequest, pendingIntent);
        }
        if (checkReadExternalStoragePermission() == -1)
            requestReadExternalStoragePermission();

        if (checkFineLocationPermission() == -1)
            requestFineLocationPermission();
    }

    @Override
    public void onStart(){
        super.onStart();
        Storage phoneStorage = new Storage(getApplicationContext().getExternalFilesDirs(null));

        phoneStorage.getInternalStorage();
        Thread internal = new ScanStorage(phoneStorage.getInternal());
        Thread sdCard = new ScanStorage(phoneStorage.getSdCard());

        //if permission has been granted
        if (checkReadExternalStoragePermission() == 0) {
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
            internal.start();
        }
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
    };

    public  void getLastKnownLocation(){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if(checkFineLocationPermission() == 0) {
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
    public int checkReadExternalStoragePermission(){
        // checks if sdk >= 23
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // determines whether the permission is granted
            return ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        //sdk < 23
        else
            return 1;
    }

    public int checkFineLocationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        else
            return 1;
    }

    /**
     * requests permissions
     */
    public void requestReadExternalStoragePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        }

    public void requestFineLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_PERMISSION_REQUEST_CODE);
     }
    /**
     *  Callback received when a permission request is completed
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] grantResults){
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
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
    }

}
