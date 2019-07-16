package com.ddash.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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

        //Displaying all the system data gathered
        Map<String, Object> systemData = SystemData.getSystemData(getApplicationContext());

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout rela = findViewById(R.id.system_activity);

        for(String key: systemData.keySet()){



            View rowview = inflater.inflate(R.layout.label,null);
            TextView l1 = rowview.findViewById(R.id.label);
            l1.setText(key);
            TextView l2 = rowview.findViewById(R.id.contents);
            l2.setText(systemData.get(key).toString() +"\n\n");
            rela.addView(rowview);

        }

    }
}
