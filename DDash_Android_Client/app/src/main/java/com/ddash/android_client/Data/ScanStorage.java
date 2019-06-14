package com.ddash.android_client.Data;

import android.util.Log;

import com.ddash.android_client.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanStorage extends Thread {

    private File directory;
    private Map<String, ArrayList> files = new HashMap<>();

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
