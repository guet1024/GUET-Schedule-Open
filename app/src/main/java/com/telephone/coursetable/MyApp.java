package com.telephone.coursetable;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.telephone.coursetable.Database.AppDatabase;
import com.telephone.coursetable.Database.AppTestDatabase;
import com.telephone.coursetable.Http.Get;

public class MyApp extends Application {
    private static MyApp app;
    private static AppDatabase db;
    private static AppTestDatabase db_test;
    private static SharedPreferences sp;
    private static SharedPreferences sp_test;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences.Editor editor_test;

    volatile public static MainActivity running_main = null;
    volatile public static Login running_login = null;
    volatile public static boolean running_login_thread = false;
    volatile public static boolean running_fetch_service = false;
    volatile public static FunctionMenu running_function_menu = null;
    volatile public static ChangeHours running_change_hours = null;

    final public static String ocr_lang_code = "telephone";
    final public static String notification_channel_id_normal = "normal";
    final public static String notification_channel_name_normal = "普通通知";
    final public static String notification_channel_des_normal = "常规通知";
    final public static int notification_id_fetch_service_foreground = 1800301129;
    final public static int notification_id_fetch_service_lan_password_wrong = 1800301127;
    final public static long service_fetch_interval = 60000;   // 60s
    final public static String[] times = {"1","2","3","4","5"};
    final public static int[] timetvIds = {
            R.id.textView_time1, //times[0]
            R.id.textView_time2, //times[1]
            R.id.textView_time3, //times[2]
            R.id.textView_time4, //times[3]
            R.id.textView_time5 //times[4]
    };
    final public static int[] weekdaytvIds = {
            R.id.textView_wd1,
            R.id.textView_wd2,
            R.id.textView_wd3,
            R.id.textView_wd4,
            R.id.textView_wd5,
            R.id.textView_wd6,
            R.id.textView_wd7
    };
    final public static int[][] nodeIds = {
            {R.id.change_hour_textView1,R.id.change_hour_textView2,R.id.change_hour_textView3,R.id.change_hour_textView4,R.id.change_hour_textView5,R.id.change_hour_textView6,R.id.change_hour_textView7},//times[0]
            {R.id.change_hour_textView8,R.id.change_hour_textView9,R.id.change_hour_textView10,R.id.textView11,R.id.textView12,R.id.textView13,R.id.textView14},//times[1]
            {R.id.textView15,R.id.textView16,R.id.textView17,R.id.textView18,R.id.textView19,R.id.textView20,R.id.textView21},//times[2]
            {R.id.textView22,R.id.textView23,R.id.textView24,R.id.textView25,R.id.textView26,R.id.textView27,R.id.textView28},//times[3]
            {R.id.textView29,R.id.textView30,R.id.textView31,R.id.textView32,R.id.textView33,R.id.textView34,R.id.textView35}//times[4]
    };

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        db = Room.databaseBuilder(this, AppDatabase.class, "telephone-db").build();
        db_test = Room.databaseBuilder(this, AppTestDatabase.class, "telephone-db-test").build();
        sp = getSharedPreferences(getResources().getString(R.string.preference_file_name), MODE_PRIVATE);
        sp_test = getSharedPreferences(getResources().getString(R.string.preference_file_name_test), MODE_PRIVATE);
        editor = sp.edit();
        editor_test = sp_test.edit();

        NotificationChannel channel = new NotificationChannel(notification_channel_id_normal, notification_channel_name_normal, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(notification_channel_des_normal);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        FetchService.startAction_START_FETCH_DATA(this, service_fetch_interval);
    }

    public static MyApp getCurrentApp(){
        return app;
    }

    public static AppDatabase getCurrentAppDB(){
        return db;
    }

    public static AppTestDatabase getCurrentAppDB_Test(){
        return db_test;
    }

    public static SharedPreferences getCurrentSharedPreference(){
        return sp;
    }

    public static SharedPreferences getCurrentSharedPreference_Test(){
        return sp_test;
    }

    public static SharedPreferences.Editor getCurrentSharedPreferenceEditor(){
        return editor;
    }

    public static SharedPreferences.Editor getCurrentSharedPreferenceEditor_Test(){
        return editor_test;
    }

    public static boolean isLAN(){
        return Get.get(
                "http://bkjw.guet.edu.cn/",
                null,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36",
                "",
                null,
                null,
                null,
                null,
                null,
                null
        ).resp_code == 200;
    }
}
