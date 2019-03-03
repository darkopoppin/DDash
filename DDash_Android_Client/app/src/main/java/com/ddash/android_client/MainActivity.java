package com.ddash.android_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ddash.android_client.DataFetcher;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DATA = "com.ddash.android_client.EXTRA_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getData(View view) {
        String build = DataFetcher.getBuild(this).toString();
        String versions = DataFetcher.getAndroidVersionCodes(this).toString();
        String memory = Arrays.toString(DataFetcher.getMemory(this));
        String data = build + "\n\n" + versions + "\n\n" + memory;

        Intent intent = new Intent(this, DisplayDataActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        startActivity(intent);
    }
}
