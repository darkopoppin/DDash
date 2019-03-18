package com.ddash.android_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String FETCHED_DATA = "com.ddash.android_client.FETCHED_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getData(View view) {
        GroupMapList data = DataFetcher.get(this);
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        Intent intent = new Intent(this, DisplayDataActivity.class);
        intent.putExtra(FETCHED_DATA, jsonData);
        startActivity(intent);
    }
}
