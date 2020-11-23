package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}