package com.ddash.android_client.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class InternetSpeedTest extends AsyncTask<String, Void, String> {

    private static double speed;

//    long startTime;
//    long endTime;
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
                    Log.d(TAG,"Step 2 running: entered run method");
                    InternetSpeedTest in = new InternetSpeedTest();
                    Log.d(TAG,"Step 3: created internet speed test object");
                    String s = in.doInBackground();
                    Log.d(TAG,"Step 4 : calculating speed");
                    in.onPostExecute(s);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Log.d(TAG,"Step 1 start thread");
        thread.start();         //Start getting the download speed
        Log.d(TAG,"Step 4 finished thread");
        thread.join();          //wait for the thread to finish
        Log.d(TAG,"Step 5 returning data");
        return InternetSpeedTest.getSpeed();        //return the speed
    }
    public static double getSpeed(){return speed;}
    /*
     *AsyncTask method, cannot/should not touch any mainthread activities or fragments.
     */
    @Override
    protected String doInBackground(String... param) {

//        startTime = System.currentTimeMillis();                     //Fetches system time
//        Log.d(TAG, "doInBackground: StartTime" + startTime);

        Bitmap bmp = null;
        try {
            URL ulrn = new URL("https://upload.wikimedia.org/wikipedia/commons/3/31/Mandelbox_OpenCL_845484_32K.jpg"); // Roughly 158 Megabytes of data JPEG. THanks wikimedia.
            Log.d(TAG, "doInBackground: started downloading"+ System.currentTimeMillis());

            Download down = new Download(ulrn);
            Thread test = new Thread(down);
            test.start();
            test.join();



//            HttpsURLConnection con = (HttpsURLConnection) ulrn.openConnection();        //Open the connection to the URL
//            InputStream is = con.getInputStream();      //Read the data from the opened connection
            InputStream is = down.getIs();

            bmp = BitmapFactory.decodeStream(is);       //Create a Bitmap from the data read

            Bitmap bitmap = bmp;        //Unsure why the bitmap is reassigned to another Bitmap variable
            ByteArrayOutputStream stream = new ByteArrayOutputStream();         //ByteArray init
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, stream);        //Re-create the bitmap/image compressed into output stream
            byte[] imageInByte = stream.toByteArray();          //Get the byte-size of compressed image
            Log.d(TAG,"doInBackground: bytesize" +imageInByte);
            long lengthbmp = imageInByte.length;        //Get the number of bytes
            Log.d(TAG,"doInBackground: bytes" + lengthbmp);

            if (null != bmp) {
                takenTime = down.getTakenTime();
//                Log.d(TAG, "doInBackground: EndTIme" + endTime);
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
            double dataSize = (double) Integer.parseInt(result) / (1024*1024);
            Log.d(TAG,"onPostExecute: datasize "+dataSize);
            double s = (double) takenTime / 1000;
            Log.d(TAG,"onPostExecute: time elapsed "+s);
            speed = dataSize / s;
            Log.d(TAG,"onPostExecute: speed without formatting "+speed);
            //CALL A UTIL CLASS TO CONVERT THE VALUE IF EXCEEDING 1000 Kbps
            Log.d(TAG, "onPostExecute: " + "" + new DecimalFormat("##.##").format(speed) + "mb/second");
        }
    }

}
