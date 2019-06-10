package com.ddash.android_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
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
//        Gson gson = new Gson();
//        Type targetType = new TypeToken<List<?>>(){}.getType();
//        List<Object> data = gson.fromJson(jsonIn, List.class);

        TextView textView = findViewById(R.id.textView);
        textView.setText(jsonIn);


//        String[] dataArr = {"test", "works?", "hello 3"};
//        String[] dataArr = toArray(data, "=");
//        Arrays.sort(dataArr);
//        String[] dataArr = data.toFlatStringArray(" = ");
//        ArrayAdapter arrAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dataArr);
//        ListView listView = findViewById(R.id.list_view);
//        listView.setAdapter(arrAdapter);


//        SimpleExpandableListAdapter groupAdapter = new SimpleExpandableListAdapter(this, groups,
//                android.R.layout.simple_expandable_list_item_1
//        );
//        ExpandableListView groupAdapterView = findViewById(R.id.groupView);
//        groupAdapterView.setAdapter(groupAdapter);
    }

}
