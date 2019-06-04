package darko.storage_location;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class MyLocation extends IntentService {

    private Activity activity;
    private LocationRequest locationRequest;
    private final int REQUEST_CHECK_SETTINGS = 0;

    public MyLocation(Activity activity){
        super("LocationThread");
        this.activity = activity;
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.d("myLocation", "insideHandle");
    }
    protected LocationRequest createLocationRequest() {
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
    protected void checkLocationSettings(Context context){
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
    public static int checkFineLocationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        else
            return 1;
    }

}
