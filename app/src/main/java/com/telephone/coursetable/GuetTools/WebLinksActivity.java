package com.telephone.coursetable.GuetTools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.util.LinkedList;
import java.util.Map;

public class WebLinksActivity extends AppCompatActivity {

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

    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.WEB_LINKS);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_web_links);
        ListView listView = findViewById(R.id.web_links_list);
        listView.setAdapter(new WebLinksAdapter(
                new LinkedList<Map.Entry<String, String>>() {
                    {
                        add(Map.entry("1.  学生选课", "http://172.16.1.99/student/public/login.asp"));
                        add(Map.entry("2.  学分制管理系统", "http://172.16.13.22/"));
                        add(Map.entry("3.  桂电官网 | 校内主页", "http://iw.guet.edu.cn/"));
                        add(Map.entry("4.  桂电WebVPN", "https://v.guet.edu.cn/login"));
                        add(Map.entry("5.  桂电官网 | 校外主页", "https://www.guet.edu.cn/"));
                        add(Map.entry("6.  一站式服务平台", "http://fwdt.guet.edu.cn/EIP/user/index.htm"));
                        add(Map.entry("7.  上网登录页", "http://10.32.254.11/"));
                        add(Map.entry("8.  财务处收费平台", "https://cwcx.guet.edu.cn/unifee/AlterPay/Login"));
                        add(Map.entry("9.  教材订购 | 高校教材云", "https://www.gxjcy.cn/"));
                        add(Map.entry("10. 桂电招生信息网", "https://www.guet.edu.cn/zs"));
                        add(Map.entry("11. 桂电图书馆", "http://www.gliet.edu.cn/lib"));
                        add(Map.entry("12. 桂电图书馆 - 馆藏查询", "http://202.193.70.139/index.aspx"));
                        add(Map.entry("13. 教学资源中心 (通识课)", "https://www.guet.edu.cn/jxzyzx"));
                        add(Map.entry("14. 桂电学生资助管理系统", "http://172.16.13.32:8088/zizhu/a/login"));
                    }
                },
                this));
    }
}