package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.telephone.coursetable.Gson.CourseCard.ACard;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;
import com.telephone.coursetable.LogMe.LogMe;

import java.util.LinkedList;
import java.util.List;

public class CourseCard extends AppCompatActivity {

    public static final String EXTRA = "card data";

    public static void startMe(@NonNull Context c, @NonNull CourseCardData data){
        Intent intent = new Intent(c, CourseCard.class);
        intent.putExtra(EXTRA, new Gson().toJson(data));
        c.startActivity(intent);
    }

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
        startActivity(new Intent(CourseCard.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.COURSE_CARD);
        MyApp.setRunning_activity_pointer(this);
        String data_string = getIntent().getStringExtra(EXTRA);
        CourseCardData data;
        if (data_string == null){
            return;
        }else {
            data = new Gson().fromJson(data_string, CourseCardData.class);
        }
        setContentView(R.layout.activity_course_card);
        ((TextView)findViewById(R.id.card_date_left_top)).setText(data.getTermname());
        String weekday = "";
        switch (data.getWeekday()){
            case 1:
                weekday = "星期一";
                break;
            case 2:
                weekday = "星期二";
                break;
            case 3:
                weekday = "星期三";
                break;
            case 4:
                weekday = "星期四";
                break;
            case 5:
                weekday = "星期五";
                break;
            case 6:
                weekday = "星期六";
                break;
            case 7:
                weekday = "星期日";
                break;
        }
        ((TextView)findViewById(R.id.card_date_left_bottom)).setText(weekday + ": " + data.getTime_des());
        ((TextView)findViewById(R.id.card_date_right_top)).setText("第 " + data.getWeek() + " 周");
        ((TextView)findViewById(R.id.card_date_right_bottom)).setText("共 " + data.getCards().size() + " 节课");
    }

    public void comment(View view){}

    public static final int TITLE_TAG_KEY = 1800301129;

    public void show_detail(View view){
        String msg = view.getTag().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(msg);
        Object title = view.getTag(TITLE_TAG_KEY);
        if (title != null){
            builder.setTitle(title.toString());
        }
        builder.create().show();
        Login.copyText(this, msg);
        Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show();
    }
    public void add_course(View view){
        final String NAME = "add_course()";
        LogMe.e(NAME, "called");
        CourseCardData no_card = (CourseCardData) view.getTag();
        EditCourse.start(CourseCard.this, true, no_card);
    }
    public void edit_course(View view){
        final String NAME = "edit_course()";
        LogMe.e(NAME, "called");
        CourseCardData with_a_card = (CourseCardData) view.getTag();
        EditCourse.start(CourseCard.this, false, with_a_card);
    }
    public void delete_course(View view){
        Login.getAlertDialog(
                CourseCard.this,
                "确定要删除这条记录吗？",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(() -> {
                            CourseCardData with_a_card = (CourseCardData) view.getTag();
                            MyApp.getCurrentAppDB().goToClassDao().deleteRecord(
                                    MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                                    with_a_card.getTerm(),
                                    with_a_card.getWeekday(),
                                    with_a_card.getTime_id(),
                                    with_a_card.getCards().get(0).getCno(),
                                    with_a_card.getCards().get(0).getStart_week(),
                                    with_a_card.getCards().get(0).getEnd_week(),
                                    with_a_card.getCards().get(0).isOdd_week()
                            );
                            runOnUiThread(() -> {
                                Toast.makeText(CourseCard.this, "删除成功", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CourseCard.this, MainActivity.class));
                            });
                        }).start();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                },
                null, "确认删除", "确定", "我再想想"
        ).show();
    }
}