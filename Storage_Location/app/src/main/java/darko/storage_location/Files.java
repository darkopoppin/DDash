package darko.storage_location;

import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Files {
    //files contained in internal storage
    private Map<String, Array> filesInternal = new HashMap<>();
    // files contained in sdCard
    private Map<String, Array> filesSdCard = new HashMap<>();

    public Files (File internal, File sdCard){
        scanInternal(internal);
        if (sdCard != null)
            scanSdCard(sdCard);
    }
    /**
     * Scans the storage or directory
     * Directory is a File because at line 62 a File object has to be passed again
     */
    public void scanInternal(File directory){
        Log.d("myDataPath", directory.getAbsolutePath());
        //lists the subdirectories and files in contents
        File [] contents = directory.listFiles();
        Log.d("myContents",String.valueOf(contents.length));
        //open recursively every folder and read every file
        for(File file:contents){
            if(file.isFile())
                Log.d("myFile",file.getName());
            else
                scanInternal(file);

        }
    }

    public void scanSdCard(File directory){
        Log.d("myDataPath", directory.getAbsolutePath());
        //lists the subdirectories and files in contents
        File [] contents = directory.listFiles();
        Log.d("myContents",String.valueOf(contents.length));
        //open recursively every folder and read every file
        for(File file:contents){
            if(file.isFile())
                Log.d("myFile",file.getName());
            else
                scanSdCard(file);

        }
    }
}
