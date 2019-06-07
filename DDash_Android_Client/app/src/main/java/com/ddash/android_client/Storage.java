package com.ddash.android_client;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    public List<Double> getSdCardStorage(){
        StatFs stat = new StatFs(sdCard);
        long available = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            available = stat.getAvailableBytes();
        }
        else{
            available = stat.getAvailableBlocks() * stat.getBlockSize();
        }
        long total = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            total = stat.getTotalBytes();
        }
        else{
            total = stat.getBlockCount() * stat.getBlockSize();
        }

        List <Double> sdCard = new ArrayList<>();

        Log.d("mySdTotal", Double.toString(Utils.convertBytes(total)));
        sdCard.add(Utils.convertBytes(total));
        Log.d("mySdAvailable",Double.toString(Utils.convertBytes(available)));
        sdCard.add(Utils.convertBytes(available));
        Log.d("mySdUsed",Double.toString(Utils.convertBytes(total-available)));
        sdCard.add(Utils.convertBytes(total - available));
        return sdCard;
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

class ScanStorage extends Thread {

    private File directory;
    private Map<String,ArrayList> files = new HashMap<>();

    public ScanStorage(File directory){
        files.put("Documents", new ArrayList<>());
        files.put("Apks", new ArrayList<>());
        files.put("Pictures", new ArrayList<>());
        files.put("Music", new ArrayList<>());
        files.put("RARs", new ArrayList<>());
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            scanStorage(this.directory, this.files);
        }
        catch(NullPointerException e){
            Log.i("Exception", "noSdCard");
        }
    }

    /**
     * Scans the storage or directory and groups the files by type
     */
    public void scanStorage(File directory, Map files){
        Log.d("mySdcardPath", directory.getAbsolutePath());
        //lists the subdirectories and files in contents
        File [] contents = directory.listFiles();
        Log.d("mySdcardContents",String.valueOf(contents.length));
        //open recursively every folder and add it to the correct group in files
        for(File file:contents){
            if(file.isFile()) {
                Log.d("mySdCardFile", file.getName());
                String name = file.getName();
                //popular documents extensions
                List<String> docExtensions = Arrays.asList(".doc",".docx",".odt",".ppt",".pptx",".pdf");
                try {
                    //This if statement checks if the extension
                    //of the current file is in the array of documents extensions
                    if (docExtensions.contains(name.substring(name.lastIndexOf('.')))) {
                        Log.d("myDoc", name);
                        addFile(file, files, "doc");
                    }// if the file is apk
                    else if (name.endsWith(".apk")) {
                        Log.d("myApk", name);
                        addFile(file, files, "apk");
                    } // if the file is in DCIM, Bluetooth, Pictures and its an image
                    else if (file.getParent().contains("DCIM")  || file.getParent().contains("Bluetooth") || file.getParent().contains("Pictures") && (name.endsWith(".png") || name.endsWith(".jpg"))) {
                        Log.d("myPic", name);
                        addFile(file, files, "pic");
                    }// if the files is a mp3
                    else if(name.endsWith("mp3")){
                        Log.d("myMusic", name + " " + Utils.convertBytes(file.length()));
                        addFile(file, files, "music");
                    }//if the file is compressed file
                    else if(name.endsWith(".zip") || name.endsWith(".rar")){
                        Log.d("myRar", name);
                        addFile(file, files, "rar");
                    }
                }//in cases when a file doesnt have extension and lastIndexOf() is called in line 50
                catch (StringIndexOutOfBoundsException e){
                    Log.d("mySdCardFile", file.getName());
                }
            }
            else
                scanStorage(file, files);

        }
    }

    /**
     * Adds the given file to the given map in the correct ArrayList
     * @param file
     * @param map - hashmap of groups of files
     * @param fileType - doc, apk, pic, music, rar
     */
    public void addFile(File file, Map map, String fileType){
        ArrayList<File> list;
        switch(fileType){
            case ("doc"):
                Log.d("myCheck", "this is a document");
                list = (ArrayList)map.get("Documents");
                list.add(file);
                map.put("Documents", list);
                break;
            case ("apk"):
                list = (ArrayList)map.get("Apks");
                list.add(file);
                map.put("Apks", list);
                break;
            case ("pic"):
                list = (ArrayList)map.get("Pictures");
                list.add(file);
                map.put("Pictures", list);
                break;
            case ("music"):
                list = (ArrayList)map.get("Music");
                list.add(file);
                map.put("Music", list);
                break;
            case ("rar"):
                list = (ArrayList)map.get("RARs");
                list.add(file);
                map.put("RARs", list);

        }
    }

    public Map getFiles(){
        return this.files;
    }

    public void printFiles(){
        for (String key: files.keySet()){
            ArrayList <File> list = files.get(key);
            Log.d("myGroupFiles", list.size() + "");
            /*for(File file:list){
                Log.d("myDocuments", file.getName());
            }*/
        }
    }
}
