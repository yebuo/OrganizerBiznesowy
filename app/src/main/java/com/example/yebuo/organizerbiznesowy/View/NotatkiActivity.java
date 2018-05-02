package com.example.yebuo.organizerbiznesowy.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yebuo.organizerbiznesowy.Model.Resource;
import com.example.yebuo.organizerbiznesowy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yebuo on 02.05.2018.
 */

public class NotatkiActivity extends AppCompatActivity {

    private ArrayAdapter adapter;

    private List<Resource> lResources;
    private List<String> lResourcesNames;
    ListView listView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notatki);

        lResources = new ArrayList<>();
        lResourcesNames = new ArrayList<>();
        listView = findViewById(R.id.itemsListView);
        lResources = getIntent().getExtras().getParcelableArrayList("notatki");

//        lResourcesNames.add("x");
//        lResourcesNames.add("y");
//        lResourcesNames.add("x");
//
//        adapter = new ArrayAdapter<>(this, R.layout.item, R.id.listItem, lResourcesNames);
//        listView.setAdapter(adapter);

        if (!(lResources != null && lResources.isEmpty())) {
            for (int i = 0; i <lResources.size(); i++){
                lResourcesNames.add(lResources.get(i).getNazwa());
            }
            adapter = new ArrayAdapter<>(this, R.layout.item, R.id.listItem, lResourcesNames);
            listView.setAdapter(adapter);
        }

    }
}
