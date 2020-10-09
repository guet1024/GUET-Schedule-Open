package com.telephone.coursetable.GuetTools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

public class ImageActivity extends Example {

    private volatile boolean visible = true;
    private volatile Intent outdated = null;

    synchronized public boolean setOutdated(){
        if (visible) return false;
        outdated = new Intent(this, MainActivity.class);
        return true;
    }

    synchronized public void hide(){
        visible = false;
    }

    synchronized public void show(){
        visible = true;
        if (outdated != null){
            startActivity(outdated);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    @Override
    protected void onPause() {
        hide();
        super.onPause();
    }

    public static final String actionbartitle="schoolname";
    public static final String map_id="school";

    private static int whichcample=0;
    private static String whichcampleName=null;

    public static void initmap(Context c, int id, String name){
        Intent intent = new Intent(c, ImageActivity.class);
        intent.putExtra(map_id,id);
        intent.putExtra(actionbartitle,name);
        c.startActivity(intent);
    }

    @Override
    protected int getImgResId() {
        int id = getIntent().getIntExtra(map_id,R.drawable.guet_map);
        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.IMAGE_MAP);
        MyApp.setRunning_activity_pointer(this);
        String s = getIntent().getStringExtra(actionbartitle);
        if(s!=null) getSupportActionBar().setTitle(s);
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }
}