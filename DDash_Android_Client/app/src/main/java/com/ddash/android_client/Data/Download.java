package com.ddash.android_client.Data;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class Download implements Runnable{
    private URL urls;
    private InputStream is;
    private long startTime;
    private long takenTime;
    private long endTime;


    public Download(URL urls){

        this.urls = urls;
        this.is = is;


    }
    @Override
    public void run() {

        HttpsURLConnection con = null;        //Open the connection to the URL

        try {
            Log.d(TAG, "run: started downloading "+ System.currentTimeMillis());
            startTime = System.currentTimeMillis();
            con = (HttpsURLConnection) urls.openConnection();
            InputStream is = con.getInputStream();      //Read the data from the opened connection
            setIs(is);
            Log.d(TAG, "run: finished downloading"+ System.currentTimeMillis());
            endTime = System.currentTimeMillis();
            takenTime = (endTime - startTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public InputStream getIs() {
        return is;
    }

    public long getTakenTime() {
        return takenTime;
    }
}