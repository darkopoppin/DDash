package com.ddash.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ddash.android_client.Data.SystemData;

import java.util.Map;

public class SystemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView placeholder = (TextView) findViewById(R.id.system_text_placeholder);

        //Displaying all the system data gathered
        Map<String, Object> systemData = SystemData.getSystemData(getApplicationContext());

        StringBuilder sb = new StringBuilder();
        for (String key: systemData.keySet()) {
            sb.append(key + " : ");
            sb.append(systemData.get(key).toString() + "\n");

        }
        placeholder.setText(sb);

    }
}
