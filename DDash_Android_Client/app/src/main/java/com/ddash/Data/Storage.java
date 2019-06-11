package com.ddash.Data;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ddash.android_client.Utils;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//External storage is not always SdCard
public class Storage {

    // contains the path to the internal storage
    private String internal;
    private File internalFile;
    // contains the path to the SdCard
    private String sdCard;
    private File sdCardFile;


    public Storage(File [] dirs){
        internal = getInternalPath(dirs);
        internalFile = new File(internal);
        sdCard = getSdcardPath(dirs);
        if (sdCard != null)
            sdCardFile = new File(sdCard);
        Log.d("myInit", Integer.toString(dirs.length));
    }

    /**
     * Simply gets the free and used space in the Internal storage
     */
    public List<Double> getInternalStorage(){
        StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        Log.d("myInternal", Environment.getExternalStorageDirectory().getAbsolutePath());
        long available = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            available = stat.getAvailableBytes();
        }
        //if api < 18
        else{
            available = stat.getAvailableBlocks() * stat.getBlockSize();
        }

        double total = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            total = Utils.convertBytes(stat.getTotalBytes());
        }
        else{
            total = stat.getBlockCount() * stat.getBlockSize();
        }
        Double [] osSize = new Double[]{16-total,32-total,64 - total,128 - total};
        List <Double> osSizeList = Arrays.asList(osSize);
        int indexMin = osSizeList.indexOf(Collections.min(osSizeList));

        List <Double> internal = new ArrayList<>();

        Log.d("myInternalTotal", Double.toString(total+osSize[indexMin]));
        internal.add(total + osSize[indexMin]);
        Log.d("myInternalOS", Double.toString(osSize[indexMin]));
        internal.add(osSize[indexMin]);
        Log.d("myInternalFree", Double.toString(Utils.convertBytes(available)));
        internal.add(Utils.convertBytes(available));
        Log.d("myInternalUsed", Double.toString(total - Utils.convertBytes(available)));
        internal.add(total - Utils.convertBytes(available));

        return internal;
    }

    /**
     * Simply gets the free and used space in the SdCard
     */
    public List<Double> getSdCardStorage() {
        if (sdCard != null) {
            StatFs stat = new StatFs(sdCard);
            long available = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                available = stat.getAvailableBytes();
            } else {
                available = stat.getAvailableBlocks() * stat.getBlockSize();
            }
            long total = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                total = stat.getTotalBytes();
            } else {
                total = stat.getBlockCount() * stat.getBlockSize();
            }

            List<Double> sdCard = new ArrayList<>();

            Log.d("mySdTotal", Double.toString(Utils.convertBytes(total)));
            sdCard.add(Utils.convertBytes(total));
            Log.d("mySdAvailable", Double.toString(Utils.convertBytes(available)));
            sdCard.add(Utils.convertBytes(available));
            Log.d("mySdUsed", Double.toString(Utils.convertBytes(total - available)));
            sdCard.add(Utils.convertBytes(total - available));
            return sdCard;
        }
        return null;
    }

    /**
     * Gets the path of the sdCard if there is one
     * dirs is a list of mounted external storages
     * returns the path as a string
     */
    public static String getSdcardPath(File [] dirs){
        // if there are 2 external storages, the second one is the sdcard
        if (dirs.length > 1){
            File path = dirs[1];
            //get the path of the sdcard
            String [] sdcard = path.getAbsolutePath().split("/");
            // String.join() requires api 26, hence I join them with +
            String sdcardPath = "/" + sdcard[1] + "/" + sdcard[2];
            Log.d("Sdcard path", sdcardPath);
            return sdcardPath;
        }
        return null;
    }

    /**
     * Gets the path of the internal storage
     * dirs is a list of mounted external storages
     * Return the path as a string
     */
    public static String getInternalPath(File [] dirs){
        //it's always the first one in dirs
        File path = dirs[0];
        String [] internal = path.getAbsolutePath().split("/");
        //usually the internal storage is storage/emulated/0. Tested on Pixel, AOSP based ROMs
        //More to be tested on Samsung, OnePlus, Sony???, Huawei???
        String internalPath = "/" + internal[1] + "/" + internal[2] + "/" + internal[3];
        Log.d("InternalPath", internalPath);
        return internalPath;
    }

    /**
     * Getter for internal field
     */
    public File getInternal(){
        return internalFile;
    }

    /**
     *  Getter for sdCard field
     */
    public File getSdCard(){
        return sdCardFile;
    }

}

