package darko.storage_location;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Files {
    //files contained in internal storage
    private Map<String, ArrayList> filesInternal = new HashMap<>();
    // files contained in sdCard
    private Map<String, ArrayList> filesSdCard = new HashMap<>();

    public Files (File internal, File sdCard){
        filesInternal.put("Documents", new ArrayList<>());
        filesSdCard.put("Documents", new ArrayList<>());

        filesInternal.put("Apks", new ArrayList<>());
        filesSdCard.put("Apks", new ArrayList<>());

        filesInternal.put("Pictures", new ArrayList<>());
        filesSdCard.put("Pictures", new ArrayList<>());

        filesInternal.put("Music", new ArrayList<>());
        filesSdCard.put("Music", new ArrayList<>());

        filesInternal.put("RARs", new ArrayList<>());
        filesInternal.put("RARs", new ArrayList<>());

        scanStorage(internal, filesInternal);
        if (sdCard != null)
            scanStorage(sdCard, filesSdCard);
    }
    /**
     * Scans the storage or directory
     * Directory is a File because at line 62 a File object has to be passed again
     */
    public void scanStorage(File directory, Map map){
        Log.d("mySdcardPath", directory.getAbsolutePath());
        //lists the subdirectories and files in contents
        File [] contents = directory.listFiles();
        Log.d("mySdcardContents",String.valueOf(contents.length));
        //open recursively every folder and read every file
        for(File file:contents){
            if(file.isFile()) {
                Log.d("mySdCardFile", file.getName());
                String name = file.getName();
                List <String> docExtensions = Arrays.asList(".doc",".docx",".odt",".ppt",".pptx",".pdf");
                try {
                    if (docExtensions.contains(name.substring(name.lastIndexOf('.')))) {
                        Log.d("myDoc", file.getName());
                        addFile(file, map, "doc");
                    } else if (name.endsWith(".apk")) {
                        Log.d("myApk", name);
                        addFile(file, map, "apk");
                    } else if (file.getParent().contains("DCIM")  || file.getParent().contains("Bluetooth") || file.getParent().contains("Pictures") && (name.endsWith(".png") || name.endsWith(".jpg"))) {
                        Log.d("myPic", name);
                        addFile(file, map, "pic");
                    }
                    else if(name.endsWith("mp3")){
                        Log.d("myMusic", name);
                        addFile(file, map, "music");
                    }
                    else if(name.endsWith(".zip") || name.endsWith(".rar")){
                        Log.d("myRar", name);
                        //addFile(file, map, "rar");
                    }
                }
                catch (StringIndexOutOfBoundsException e){
                    Log.d("mySdCardFile", file.getName());
                }
            }
            else
                scanStorage(file, map);

        }
    }

    public void addFile(File file, Map map, String fileType){
        ArrayList<File> list;
        switch(fileType){
            case ("doc"):
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
                map.put("Pictures", list);
                break;
            case ("rar"):
                Log.d("myCheck", file.getPath());
                list = (ArrayList)map.get("RARs");
                list.add(file);
                map.put("RARs", list);

        }
    }

    public void getSdCardFiles(){
        for (String key: filesSdCard.keySet()){
            ArrayList <File> list = filesSdCard.get(key);
            for(File file:list){
                Log.d("myDocuments", file.getName());
            }
        }
    }
}
