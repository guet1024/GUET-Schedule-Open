package com.telephone.coursetable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.telephone.coursetable.Gson.CourseCard.ACard;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditCourse extends AppCompatActivity {

    public static final String EXTRA_COURSE_CARD_DATA = "ccd";
    public static final String EXTRA_IF_ADD = "add";

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
        boolean add_start = getIntent().getBooleanExtra(EXTRA_IF_ADD, false);
        CourseCardData data = new Gson().fromJson(getIntent().getStringExtra(EXTRA_COURSE_CARD_DATA), CourseCardData.class);
        ((EditText)content.findViewById(R.id.edit_course_termname)).setText(data.getTermname());
        ((EditText)content.findViewById(R.id.edit_course_weekday)).setText(data.getWeekday()+"");
        ((EditText)content.findViewById(R.id.edit_course_time)).setText(data.getTime_id());
        if (!data.getCards().isEmpty()){ // if have a card, fill blank with the data on the card
            ACard card = data.getCards().get(0);
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

    private boolean check(){
        return true;
    }

    public void save(View view){

    }
}