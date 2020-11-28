package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.TeachersEvaluation.TeachersEvaluation;
import com.telephone.coursetable.TeachersEvaluation.TextBookEvaluation;

public class TeacherEvaluationPanel extends AppCompatActivity {

    public static final String EXTRA_MENU_LAYOUT_ID = "which_menu";
    public static final String EXTRA_TITLE = "what_title";
    public static final String EXTRA_END_TIP = "what_tip";

    private String end_tip = "======== 评教/评学结束 ========";

    private TextView textView;
    private NestedScrollView scrollView;
    private ProgressBar progressBar;

    private volatile boolean visible = true;
    private volatile Intent outdated = null;

    synchronized public boolean isVisible(){
        return visible;
    }

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
        startActivity(new Intent(TeacherEvaluationPanel.this, FunctionMenu.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.TEACHER_EVALUATION_PANEL);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_teacher_evaluation_panel);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title == null){
            title = "评教评学";
        }
        getSupportActionBar().setTitle(title);
        String etip = getIntent().getStringExtra(EXTRA_END_TIP);
        if (etip != null){
            end_tip = etip;
        }
        textView = (TextView) findViewById(R.id.teachers_evaluation_panel_textview);
        scrollView = (NestedScrollView) findViewById(R.id.teachers_evaluation_panel_scroll);
        progressBar = (ProgressBar) findViewById(R.id.teachers_evaluation_panel_progressbar);
    }

    public static void start(Context c, @NonNull String title, @NonNull String end_text, int menu_layout_id){
        Intent intent = new Intent(c, TeacherEvaluationPanel.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_END_TIP, end_text);
        intent.putExtra(EXTRA_MENU_LAYOUT_ID, menu_layout_id);
        c.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getIntent().getIntExtra(EXTRA_MENU_LAYOUT_ID, R.menu.teacher_evaluation_menu), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(TeacherEvaluationPanel.this, FunctionMenu.class));
                break;
            case R.id.teacher_evaluation_start:
                new Thread(() -> {
                    if (!TeachersEvaluation.evaluation(
                            TeacherEvaluationPanel.this,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).password,
                            MyApp.getCurrentAppDB().termInfoDao()
                    )) {
                        Snackbar.make(textView,
                                "上次评教还未结束，只有上次评教结束后才能开始新的评教哦~",
                                BaseTransientBottomBar.LENGTH_SHORT
                        ).setTextColor(Color.WHITE).show();
                    }
                }).start();
                break;
            case R.id.book_evaluation_start:
                new Thread(() -> {
                    if (!TextBookEvaluation.evaluation(
                            TeacherEvaluationPanel.this,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).password
                    )) {
                        Snackbar.make(textView,
                                "上次教材评价还未结束，只有上次教材评价结束后才能开始新的教材评价哦~",
                                BaseTransientBottomBar.LENGTH_SHORT
                        ).setTextColor(Color.WHITE).show();
                    }
                }).start();
                break;
            case R.id.go_to_query_graduation_degree:
                new Thread(() -> {
                    if (!Byxw.Byxw_Query(TeacherEvaluationPanel.this)) {
                        Snackbar.make(textView,
                                "上次查询还未结束，只有上次查询结束后才能开始新的查询哦~",
                                BaseTransientBottomBar.LENGTH_SHORT
                        ).setTextColor(Color.WHITE).show();
                    }
                }).start();
                break;
        }
        return true;
    }

    public void print(String text){
        runOnUiThread(()->{
            textView.setText(textView.getText() + text + "\n");
            scrollView.postDelayed(()->scrollView.fullScroll(View.FOCUS_DOWN), 200);
        });
    }

    public void print_end_symbol(){
        print("");
        print(end_tip);
        print("");
    }

    public void prepare_start(){
        runOnUiThread(()->progressBar.setVisibility(View.VISIBLE));
    }

    public void cleanup_end(){
        print_end_symbol();
        runOnUiThread(()->progressBar.setVisibility(View.INVISIBLE));
    }
}