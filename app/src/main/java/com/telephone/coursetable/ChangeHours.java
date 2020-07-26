package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.telephone.coursetable.Gson.Hour;
import com.telephone.coursetable.Gson.Hours;

import java.util.List;

public class ChangeHours extends AppCompatActivity {

    private Hours originHList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_hours);
        originHList = new Gson().fromJson("{\n" +
                "    \"success\": true,\n" +
                "    \"total\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"1\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第1、2节\",\n" +
                "            \"memo\": \"8:25-10:05\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"2\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第3、4节\",\n" +
                "            \"memo\": \"10:25-12:00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"3\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第5、6节\",\n" +
                "            \"memo\": \"14:25-16:05\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"4\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第7、8节\",\n" +
                "            \"memo\": \"16:25-18:00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"5\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第9、10节\",\n" +
                "            \"memo\": \"19:25-21:05\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"6\",\n" +
                "            \"xss\": 1,\n" +
                "            \"nodename\": \"第11节\",\n" +
                "            \"memo\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"7\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"中午\",\n" +
                "            \"memo\": null\n" +
                "        }\n" +
                "    ]\n" +
                "}\n", Hours.class);
        Log.e("b","v");
    }
}