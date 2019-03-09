package com.ddash.android_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DATA = "com.ddash.android_client.EXTRA_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getData(View view) {
        Map<String, Object> build = DataFetcher.getBuild(this);
        Map<String, Object> versions = DataFetcher.getAndroidVersionCodes(this);
        Map<String, Object> memory = DataFetcher.getMemory(this);
        Data data = new Data();
        data.putAll(build);
        data.putAll(versions);
        data.putAll(memory);
        String data_str = data.toString();

        Intent intent = new Intent(this, DisplayDataActivity.class);
        intent.putExtra(EXTRA_DATA, data_str);
        startActivity(intent);
    }
}
