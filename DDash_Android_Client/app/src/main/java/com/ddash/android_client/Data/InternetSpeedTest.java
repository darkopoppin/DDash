package com.ddash.android_client.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class InternetSpeedTest extends AsyncTask<String, Void, String> {

    private static double speed;

    long startTime;
    long endTime;
    private long takenTime;



    public InternetSpeedTest(){
        this.speed = speed;
    }
    public static double run() throws InterruptedException {
        //Run the following on a child thread
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Create and instance of the InternetSpeedTest and call the class methods
                    InternetSpeedTest in = new InternetSpeedTest();
                    String s = in.doInBackground();
                    in.onPostExecute(s);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();         //Start getting the download speed
        thread.join();          //wait for the thread to finish
        return InternetSpeedTest.getSpeed();        //return the speed
    }
    public static double getSpeed(){return speed;}
    /*
     *AsyncTask method, cannot/should not touch any mainthread activities or fragments.
     */
    @Override
    protected String doInBackground(String... param) {

        startTime = System.currentTimeMillis();                     //Fetches system time
        Log.d(TAG, "doInBackground: StartTime" + startTime);

        Bitmap bmp = null;
        try {
            URL ulrn = new URL("https://upload.wikimedia.org/wikipedia/commons/6/67/CARTA_DELLE_RETI_ECOLOGICHE_E_AMBIENTALI.jpg"); // Roughly 49 Megabytes of data JPEG. THanks wikimedia.
            HttpsURLConnection con = (HttpsURLConnection) ulrn.openConnection();        //Open the connection to the URL
            InputStream is = con.getInputStream();      //Read the data from the opened connection
            bmp = BitmapFactory.decodeStream(is);       //Create a Bitmap from the data read

            Bitmap bitmap = bmp;        //Unsure why the bitmap is reassigned to another Bitmap variable
            ByteArrayOutputStream stream = new ByteArrayOutputStream();         //ByteArray init
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, stream);        //Re-create the bitmap/image compressed into output stream
            byte[] imageInByte = stream.toByteArray();          //Get the byte-size of compressed image
            long lengthbmp = imageInByte.length;        //Get the number of bytes

            if (null != bmp) {
                endTime = System.currentTimeMillis();
                Log.d(TAG, "doInBackground: EndTIme" + endTime);
                return lengthbmp + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    /*
     *In this method the kb/second is calculated
     */
    protected void onPostExecute(String result) {

        if (result != null) {
            long dataSize = Integer.parseInt(result) / 1024;
            takenTime = endTime - startTime;
            double s = (double) takenTime / 1000;
            speed = dataSize / s;
            //CALL A UTIL CLASS TO CONVERT THE VALUE IF EXCEEDING 1000 Kbps
            Log.d(TAG, "onPostExecute: " + "" + new DecimalFormat("##.##").format(speed) + "kb/second");
        }
    }
}