package com.telephone.coursetable.GuetTools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;
import com.telephone.coursetable.Webinfo.Webinfo;

import java.util.LinkedList;
import java.util.Map;

public class WebLinksActivity extends AppCompatActivity {

    public static final String isQQ = "isQQ";

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
    public void onBackPressed() {
        startActivity(new Intent(this, Webinfo.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    public static void start(Context c, boolean isQQ){
        Intent intent = new Intent(c, WebLinksActivity.class);
        intent.putExtra(WebLinksActivity.isQQ, isQQ);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.WEB_LINKS);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_web_links);
        ListView listView = findViewById(R.id.web_links_list);
        if (getIntent().getBooleanExtra(isQQ, true)){//if is qq
            getSupportActionBar().setTitle("公共平台");
            listView.setAdapter(new WebLinksAdapter(
                    new LinkedList<Map.Entry<String, String>>() {
                        {
                            add(Map.entry("1.  GUET课程表交流群", "Jm2emUYqOfaVWX3WL17GY0nN2wOBN1wG" + " | " + R.drawable.qqcode_guet_coursetable));
                            add(Map.entry("2.  桂电二手交易群", "9eKaKJbPZKs7u3VO7zHb5vsEc0ApK6U_" + " | " + R.drawable.qqcode_second_hand_trading_group));
                            add(Map.entry("3.  桂电表白墙", "2091507780" + " | " + R.drawable.qqcode_bbq));
                            add(Map.entry("4.  桂林电子科技大学公众号", ""+R.drawable.qrcode_guet));
                            add(Map.entry("5.  桂林电子科技大学智慧校园公众号", ""+R.drawable.qrcode_guet_campus));
                            add(Map.entry("6.  桂林电子科技大学图书馆公众号", ""+R.drawable.qrcode_guet_lib));
                            add(Map.entry("7.  桂林电子科技大学财务处公众号", ""+R.drawable.qrcode_guet_cwc));
                        }
                    },
                    this,
                    true));
        }else {
            getSupportActionBar().setTitle("常用链接");
            listView.setAdapter(new WebLinksAdapter(
                    new LinkedList<Map.Entry<String, String>>() {
                        {
                            add(Map.entry("1.  教务网站/教学管理系统（校园网）", "http://172.16.1.99/student/public/login.asp"));
                            add(Map.entry("2.  教务网站/教学管理系统（外网）", "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/public/login.asp"));
                            add(Map.entry("3.  学分制管理系统（校园网）", "http://172.16.13.22/"));
                            add(Map.entry("4.  学分制管理系统（外网）", "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626012d46dbfe/"));
                            add(Map.entry("5.  桂电官网 | 校内主页", "http://iw.guet.edu.cn/"));
                            add(Map.entry("6.  桂电WebVPN", "https://v.guet.edu.cn/login"));
                            add(Map.entry("7.  桂电官网 | 校外主页", "https://www.guet.edu.cn/"));
                            add(Map.entry("8.  一站式服务平台", "http://fwdt.guet.edu.cn/EIP/user/index.htm"));
                            add(Map.entry("9.  上网登录页（校园网）", "http://10.32.254.11/"));
                            add(Map.entry("10. 财务处收费平台", "https://cwcx.guet.edu.cn/unifee/AlterPay/Login"));
                            add(Map.entry("11. 教材订购 | 高校教材云", "https://www.gxjcy.cn/"));
                            add(Map.entry("12. 桂电图书馆", "http://www.gliet.edu.cn/lib"));
                            add(Map.entry("13. 桂电图书馆 - 馆藏查询1（校园网）", "http://202.193.70.139/index.aspx"));
                            add(Map.entry("14. 桂电图书馆 - 馆藏查询1（外网）", "https://v.guet.edu.cn/http/77726476706e69737468656265737421a2a713d276693b1e2958c7fdcb0c/index.aspx"));
                            add(Map.entry("15. 桂电图书馆 - 馆藏查询2", "https://mobilelib.guet.edu.cn/sms/opac/search/showiphoneSearch.action"));
                            add(Map.entry("16. 桂电图书馆 - 研讨室预约（校园网）", "http://202.193.70.162:8089/"));
                            add(Map.entry("17. 桂电图书馆 - 研讨室预约（外网）", "https://v.guet.edu.cn/http-8089/77726476706e69737468656265737421a2a713d276693b1e2958c7fdce07/"));
                            add(Map.entry("18. 教学资源中心 | 通识课", "https://www.guet.edu.cn/jxzyzx"));
                            add(Map.entry("19. 全国大学英语四六级官网", "http://cet.neea.edu.cn/"));
                            add(Map.entry("20. 桂电学生资助管理系统（校园网）", "http://172.16.13.32:8088/zizhu/a/login"));
                            add(Map.entry("21. 桂电学生资助管理系统（外网）", "https://v.guet.edu.cn/http-8088/77726476706e69737468656265737421a1a013d2766626012d46dafe/zizhu/a/login"));
                            add(Map.entry("22. 桂电招生信息网", "https://www.guet.edu.cn/zs"));
                            add(Map.entry("23. 教学日历（校园网）", "http://utsc.guet.edu.cn/calendar.aspx?mCode=0010"));
                            add(Map.entry("24. 教学日历（外网）", "https://v.guet.edu.cn/http/77726476706e69737468656265737421e5e3529f69377d556a468ca88d1b203b/calendar.aspx?mCode=0010"));
                        }
                    },
                    this,
                    false));
        }
    }

    public static void openWeChatScan(Context context, View view) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            context.startActivity(intent);
        } catch (Exception e) {
            Snackbar.make(view, "未安装微信或安装的版本不支持", BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.WHITE).show();
        }
    }
}