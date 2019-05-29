package darko.storage_location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import darko.storage_location.Storage;
import java.io.File;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private final int READ_STORAGE_PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkReadExternalStoragePermission() == -1)
            requestReadExternalStoragePermission();
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
            requestReadExternalStoragePermission();
        }
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

    /**
     * requests read external storage permission
     */
    public void requestReadExternalStoragePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
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
