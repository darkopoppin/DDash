package com.ddash.android_client;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


@Deprecated
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
