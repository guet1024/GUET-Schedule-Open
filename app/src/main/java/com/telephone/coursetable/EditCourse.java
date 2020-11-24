package com.telephone.coursetable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.telephone.coursetable.Gson.CourseCard.ACard;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;
import com.telephone.coursetable.LogMe.LogMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.security.InvalidParameterException;

public class EditCourse extends AppCompatActivity {

    public static final String EXTRA_COURSE_CARD_DATA = "ccd";
    public static final String EXTRA_IF_ADD = "add";

    private View snack_bar_root_view;
    private boolean add_start = false;
    private CourseCardData intent_extra_CourseCardData = null;

    private static final int[] et_ids_add_yes = {
            R.id.edit_course_startweek,
            R.id.edit_course_endweek,
            R.id.edit_course_weekday,
            R.id.edit_course_time,
            R.id.edit_course_cname,
            R.id.edit_course_tname,
            R.id.edit_course_croom,
            R.id.edit_course_grade_point,
            R.id.edit_course_ctype,
            R.id.edit_course_examt,
            R.id.edit_course_cno
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
        snack_bar_root_view = findViewById(R.id.edit_course_termname);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit_course_save:
                save(snack_bar_root_view);
                break;
        }
        return true;
    }

    public static void start(Context c, boolean add, @NonNull CourseCardData courseCardData){
        Intent intent = new Intent(c, EditCourse.class);
        intent.putExtra(EXTRA_IF_ADD, add);
        intent.putExtra(EXTRA_COURSE_CARD_DATA, new Gson().toJson(courseCardData));
        c.startActivity(intent);
    }

    private void initContentView(){
        View content = getLayoutInflater().inflate(R.layout.activity_edit_course, null);
        add_start = getIntent().getBooleanExtra(EXTRA_IF_ADD, false);
        intent_extra_CourseCardData = new Gson().fromJson(getIntent().getStringExtra(EXTRA_COURSE_CARD_DATA), CourseCardData.class);
        ((EditText)content.findViewById(R.id.edit_course_termname)).setText(intent_extra_CourseCardData.getTermname());
        ((EditText)content.findViewById(R.id.edit_course_weekday)).setText(intent_extra_CourseCardData.getWeekday()+"");
        ((EditText)content.findViewById(R.id.edit_course_time)).setText(intent_extra_CourseCardData.getTime_id());
        if (!intent_extra_CourseCardData.getCards().isEmpty()){ // if have a card, fill blank with the data on the card
            ACard card = intent_extra_CourseCardData.getCards().get(0);
            ((EditText)content.findViewById(R.id.edit_course_startweek)).setText(card.getStart_week()+"");
            ((EditText)content.findViewById(R.id.edit_course_endweek)).setText(card.getEnd_week()+"");
            ((EditText)content.findViewById(R.id.edit_course_cno)).setText(card.getCno());
            ((EditText)content.findViewById(R.id.edit_course_cname)).setText(card.getCname());
            ((EditText)content.findViewById(R.id.edit_course_tname)).setText(card.getTname());
            ((EditText)content.findViewById(R.id.edit_course_croom)).setText(card.getCroom());
            ((EditText)content.findViewById(R.id.edit_course_system_comment)).setText(card.getSys_comm());
            ((EditText)content.findViewById(R.id.edit_course_my_comment)).setText(card.getMy_comm());
            ((EditText)content.findViewById(R.id.edit_course_grade_point)).setText(card.getGrade_point()+"");
            ((EditText)content.findViewById(R.id.edit_course_ctype)).setText(card.getCtype());
            ((EditText)content.findViewById(R.id.edit_course_examt)).setText(card.getExamt());
        }
        if (add_start) {
            // enable customized fields
            for (int id : et_ids_add_yes){
                content.findViewById(id).setEnabled(true);
            }
            // set title
            ((CollapsingToolbarLayout)content.findViewById(R.id.edit_course_toolbar_layout)).setTitle(getString(R.string.title_activity_edit_course_add));
        }else {
            // set title
            ((CollapsingToolbarLayout)content.findViewById(R.id.edit_course_toolbar_layout)).setTitle(getString(R.string.title_activity_edit_course_edit));
        }
        setContentView(content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private int int_check(int et_id){
        EditText et = (EditText)findViewById(et_id);
        String wrong_tip = "不正确的格式：" + et.getHint().toString();
        String text = et.getText().toString();
        try {
            return Integer.parseInt(text);
        }catch (Exception e){
            e.printStackTrace();
            Snackbar.make(et, wrong_tip, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            throw new InvalidParameterException();
        }
    }

    private double double_check(int et_id){
        EditText et = (EditText)findViewById(et_id);
        String wrong_tip = "不正确的格式：" + et.getHint().toString();
        String text = et.getText().toString();
        try {
            return Double.parseDouble(text);
        }catch (Exception e){
            e.printStackTrace();
            Snackbar.make(et, wrong_tip, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            throw new InvalidParameterException();
        }
    }

    private String string_empty_check(int et_id){
        EditText et = (EditText)findViewById(et_id);
        String wrong_tip = "不能为空：" + et.getHint().toString();
        String text = et.getText().toString();
        if (!text.isEmpty()){
            return text;
        }else {
            Snackbar.make(et, wrong_tip, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            throw new InvalidParameterException();
        }
    }

    private boolean format_check(){
        try {
            int_check(R.id.edit_course_startweek);
            int_check(R.id.edit_course_endweek);
            int_check(R.id.edit_course_weekday);
            int_check(R.id.edit_course_time);
            double_check(R.id.edit_course_grade_point);
            string_empty_check(R.id.edit_course_cno);
            string_empty_check(R.id.edit_course_cname);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private boolean logic_check(@NonNull View view){
        int sw = int_check(R.id.edit_course_startweek);
        int ew = int_check(R.id.edit_course_endweek);
        int weekday = int_check(R.id.edit_course_weekday);
        int time = int_check(R.id.edit_course_time);
        if (sw <= 0 || ew <= 0 || ew < sw){
            Snackbar.make(view, "不合法的起始周和结束周", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            return false;
        }
        if (weekday < 1 || weekday > 7){
            Snackbar.make(view, "不合法的星期", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            return false;
        }
        if (time < 1 || time > MyApp.times.length){
            Snackbar.make(view, "不合法的节次（大节数）", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            return false;
        }
        return true;
    }

    private boolean check(@NonNull View view){
        return format_check() && logic_check(view);
    }

    public void save(@NonNull View view) {
        final String NAME = "save()";
        clearFocus();
        new Thread(() -> {
            boolean check_res = check(view);
            LogMe.e(NAME, "check res: " + check_res);
            if (check_res){
                if (add_start){

                }else {
                    MyApp.getCurrentAppDB().goToClassDao().setMyComment(
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                            intent_extra_CourseCardData.getTerm(),
                            intent_extra_CourseCardData.getWeekday(),
                            intent_extra_CourseCardData.getTime_id(),
                            intent_extra_CourseCardData.getCards().get(0).getCno(),
                            intent_extra_CourseCardData.getCards().get(0).getStart_week(),
                            intent_extra_CourseCardData.getCards().get(0).getEnd_week(),
                            intent_extra_CourseCardData.getCards().get(0).isOdd_week(),
                            ((EditText)findViewById(R.id.edit_course_my_comment)).getText().toString()
                    );
                    Snackbar.make(view, "保存成功", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                }
            }
        }).start();
    }

    private void clearFocus(){
        runOnUiThread(()->{
            // Check if no view has focus:
            View view = EditCourse.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            findViewById(R.id.edit_course_background_of_all_input).clearFocus();
        });
    }
}