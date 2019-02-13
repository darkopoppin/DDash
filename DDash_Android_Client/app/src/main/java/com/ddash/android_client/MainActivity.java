package com.ddash.android_client;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView keyField = findViewById(R.id.textView);
        keyField.setText("Device model: ");

        String devModel = Build.MODEL;
        TextView valueField = findViewById(R.id.textView1);
        valueField.setText(devModel);
    }
}
