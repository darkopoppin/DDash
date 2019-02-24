package com.ddash.android_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        Intent intent = getIntent();
        String data = intent.getStringExtra(MainActivity.EXTRA_DATA);

        TextView textView = findViewById(R.id.textView);
        textView.setText(data);
    }
}
