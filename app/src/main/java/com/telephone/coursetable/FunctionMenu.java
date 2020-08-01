package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.Map;
import java.util.Map.Entry;

import java.util.LinkedList;
import java.util.List;

public class FunctionMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_menu);
        List<Entry<String, List<String>>> menus = new LinkedList<>();
        for (int i = 0; i < 5; i++){
            List<String> children = new LinkedList<>();
            for (int j = 0; j < 2; j++){
                children.add("");
            }
            menus.add(Map.entry("", children));
        }
        ExpandableListView menu_list = (ExpandableListView)findViewById(R.id.function_menu_list);
        menu_list.setAdapter(new FunctionMenuAdapter(this, menus, true, menu_list));
    }
}