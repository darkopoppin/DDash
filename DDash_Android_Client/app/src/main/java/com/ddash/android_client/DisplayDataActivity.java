package com.ddash.android_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DisplayDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        Intent intent = getIntent();
        String jsonIn = intent.getStringExtra(MainActivity.FETCHED_DATA);
        Gson gson = new Gson();
        Type typeOfMap = new TypeToken<TreeMap<String, Object>>(){}.getType();
        Map<String, Object> data = gson.fromJson(jsonIn, typeOfMap);
//        String[] dataArr = {"test", "works?", "hello 3"};
        String[] dataArr = toArray(data, "=");
//        Arrays.sort(dataArr);

        ArrayAdapter arrAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dataArr);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(arrAdapter);

//        TextView textView = findViewById(R.id.textView);
//        textView.setText(data.toString());
    }

    public static String[] toArray(Map<String, Object> map, String sep) {
        String[] arr = new String[map.size()];
        int i = 0;
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            arr[i] = entry.getKey() + sep + entry.getValue().toString();
            i += 1;
        }
        return arr;
    }
}
