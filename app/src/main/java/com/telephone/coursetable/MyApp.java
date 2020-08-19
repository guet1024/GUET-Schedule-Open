package com.telephone.coursetable;

import android.app.Application;

import androidx.room.Room;

import com.telephone.coursetable.Database.AppDatabase;

import java.io.IOException;
import java.net.InetAddress;

public class MyApp extends Application {
    private static MyApp app;
    private static AppDatabase db;
    public static String[] times = {"1","2","3","4","5"};
    public static int[] timetvIds = {
            R.id.textView_time1, //times[0]
            R.id.textView_time2, //times[1]
            R.id.textView_time3, //times[2]
            R.id.textView_time4, //times[3]
            R.id.textView_time5 //times[4]
    };
    public static int[] weekdaytvIds = {
            R.id.textView_wd1,
            R.id.textView_wd2,
            R.id.textView_wd3,
            R.id.textView_wd4,
            R.id.textView_wd5,
            R.id.textView_wd6,
            R.id.textView_wd7
    };
    public static int[][] nodeIds = {
            {R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4,R.id.textView5,R.id.textView6,R.id.textView7},//times[0]
            {R.id.textView8,R.id.textView9,R.id.textView10,R.id.textView11,R.id.textView12,R.id.textView13,R.id.textView14},//times[1]
            {R.id.textView15,R.id.textView16,R.id.textView17,R.id.textView18,R.id.textView19,R.id.textView20,R.id.textView21},//times[2]
            {R.id.textView22,R.id.textView23,R.id.textView24,R.id.textView25,R.id.textView26,R.id.textView27,R.id.textView28},//times[3]
            {R.id.textView29,R.id.textView30,R.id.textView31,R.id.textView32,R.id.textView33,R.id.textView34,R.id.textView35}//times[4]
    };

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        db = Room.databaseBuilder(this, AppDatabase.class, "telephone-db").build();
    }

    public static MyApp getCurrentApp(){
        return app;
    }

    public static AppDatabase getCurrentAppDB(){
        return db;
    }

    public static boolean isLAN(){
        try {
            return InetAddress.getByName("bkjw.guet.edu.cn").isReachable(300);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
