package com.bien.networkdataminer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;


public class MainActivity extends AppCompatActivity {
//    TextView txtWifiInfo;
//    Button btnInfo;

    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        txtWifiInfo = (TextView) findViewById(R.id.idTxt);
//        btnInfo = (Button) findViewById(R.id.idBtn);
        }

    public void getWifiInformation(View view) {
        /*
        Invoke the Network class.
         */
        Network network = new Network(getApplicationContext().getSystemService(WIFI_SERVICE));

        // Call to getAllWifiDetails method from the Network class to retrieve all network details. Returns a List.
        List n = network.getAllWifiDetails();

        Log.d(TAG,"Calling getAllWifiDetails: \n" + n);

    }

}
