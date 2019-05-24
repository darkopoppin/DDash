package darko.storage_location;

import android.annotation.TargetApi;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import java.io.File;

//External storage is not always SdCard
public class Storage {

    // contains the path to the internal storage
    private String internal;
    // contains the path to the SdCard
    private String sdCard;

    public Storage(File [] dirs){
        internal = getInternalPath(dirs);
        sdCard = getSdcardPath(dirs);
        Log.d("myInit", Integer.toString(dirs.length));
    }

    /**
     * Simply gets the free and used space in the Internal storage
     */
    public void getInternalStorage(){
        StatFs stat = new StatFs(internal);
        long available = stat.getAvailableBytes();
        long total = stat.getTotalBytes();

        Log.d("myInternalFree", Long.toString(available));
        Log.d("myInternalTotal", Long.toString(total));
    }

    /**
     * Simply gets the free and used space in the SdCard
     */
    public void getSdCardStorage(){
        StatFs stat = new StatFs(sdCard);
        long available = stat.getAvailableBytes();
        long total = stat.getTotalBytes();

        Log.d("myExternalAvailable",Long.toString(available));
        Log.d("myExternalUsed",Long.toString(total-available));
    }

    /**
     * Scans the storage or directory
     * Directory is a File because at line 62 a File object has to be passed again
     */
    public static void scanStorage(File directory){
        Log.d("myDataPath", directory.getAbsolutePath());
        //lists the subdirectories and files in contents
        File [] contents = directory.listFiles();
        Log.d("myContents",String.valueOf(contents.length));
        //open recursively every folder and read every file
        for(File file:contents){
            if(file.isFile())
                Log.d("myFile",file.getName());
            else
                scanStorage(file);

        }
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
        //More to be tested on Samsung, OnePlus, Sony??? ,Huawei???
        String internalPath = "/" + internal[1] + "/" + internal[2] + "/" + internal[3];
        Log.d("InternalPath", internalPath);
        return internalPath;
    }

    /**
     * Getter for internal field
     */
    public String getInternal(){
        return internal;
    }

    /**
     *  Getter for sdCard field
     */
    public String getSdCard(){
        return sdCard;
    }

}
