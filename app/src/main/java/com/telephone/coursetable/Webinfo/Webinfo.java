package com.telephone.coursetable.Webinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.telephone.coursetable.FunctionMenu;
import com.telephone.coursetable.GuetTools.ImageActivity;
import com.telephone.coursetable.GuetTools.WebLinksActivity;
import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

public class Webinfo extends AppCompatActivity implements View.OnClickListener {


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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.guet_music_icon||id==R.id.guet_music){
            startActivity(new Intent(Webinfo.this,GUET_Music.class));
        }
        if(id==R.id.guet_map_icon||id==R.id.guet_map){
            ImageActivity.initmap(Webinfo.this,R.drawable.guet_map,"花江校区");
            //转到地图
        }
        if(id==R.id.jjl_map||id==R.id.jjl_maptext){
            ImageActivity.initmap(Webinfo.this,R.drawable.guet_jjl_map,"金鸡岭校区");
            //转到地图
        }
        if(id==R.id.guet_calendar||id==R.id.guet_calendartext){
            ImageActivity.initmap(Webinfo.this,R.drawable.calendar_2020_2021,"教学日历");
            //转到日历
        }
        if(id==R.id.guet_phone_icon||id==R.id.guet_phone){
            startActivity(new Intent(Webinfo.this,Guetphonenums.class));
        }
        if(id==R.id.chat_group || id==R.id.chat_grouptext){
            //转到公共平台
            WebLinksActivity.start(Webinfo.this,true);
        }
        if(id==R.id.linktext||id==R.id.link){
            //转到常用链接
            WebLinksActivity.start(Webinfo.this,false);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, FunctionMenu.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.setRunning_activity(MyApp.RunningActivity.WEB_INFO);
        MyApp.setRunning_activity_pointer(this);

        setContentView(R.layout.activity_webinfo);
        ImageView guetmusicicon = (ImageView)findViewById(R.id.guet_music_icon);
        ImageView guetmapicon = (ImageView)findViewById(R.id.guet_map_icon);
        ImageView guetphoneicon = (ImageView)findViewById(R.id.guet_phone_icon);
        TextView  guetmusic = (TextView)findViewById(R.id.guet_music);
        TextView  guetmap   = (TextView)findViewById(R.id.guet_map);
        TextView  guetphone = (TextView)findViewById(R.id.guet_phone);
        ImageView chat_group=(ImageView)findViewById(R.id.chat_group);
        TextView  chat_group_text=(TextView)findViewById(R.id.chat_grouptext);
        TextView  linktext=(TextView)findViewById(R.id.linktext);
        ImageView link=(ImageView)findViewById(R.id.link);
        TextView jjlmaptext= (TextView)findViewById(R.id.jjl_maptext);
        ImageView jjmap= (ImageView)findViewById(R.id.jjl_map);
        TextView calendartext = (TextView)findViewById(R.id.guet_calendartext);
        ImageView calendar = (ImageView)findViewById(R.id.guet_calendar);
        calendartext.setOnClickListener(this);
        calendar.setOnClickListener(this);
        jjlmaptext.setOnClickListener(this);
        jjmap.setOnClickListener(this);
        chat_group.setOnClickListener(this);
        chat_group_text.setOnClickListener(this);
        linktext.setOnClickListener(this);
        link.setOnClickListener(this);
        guetmusicicon.setOnClickListener(this);
        guetphoneicon.setOnClickListener(this);
        guetmapicon.setOnClickListener(this);
        guetmap.setOnClickListener(this);
        guetmusic.setOnClickListener(this);
        guetphone.setOnClickListener(this);
    }
}