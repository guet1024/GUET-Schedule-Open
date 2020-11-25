package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

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
        textView = (TextView) findViewById(R.id.teachers_evaluation_panel_textview);
        scrollView = (NestedScrollView) findViewById(R.id.teachers_evaluation_panel_scroll);
        progressBar = (ProgressBar) findViewById(R.id.teachers_evaluation_panel_progressbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_evaluation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(TeacherEvaluationPanel.this, FunctionMenu.class));
                break;
            case R.id.teacher_evaluation_start:
                new Thread(()->{
                    if (!TeachersEvaluation.evaluation(
                            TeacherEvaluationPanel.this,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).password,
                            MyApp.getCurrentAppDB().termInfoDao()
                    )){
                        Snackbar.make(textView,
                                "上次评教还未结束，只有上次评教结束后才能开始新的评教哦~",
                                BaseTransientBottomBar.LENGTH_SHORT
                        ).setTextColor(Color.WHITE).show();
                    }
                }).start();
                break;
            case R.id.book_evaluation_start:
                new Thread(()->{
                    if (!TextBookEvaluation.evaluation(
                            TeacherEvaluationPanel.this,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                            MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).password
                    )){
                        Snackbar.make(textView,
                                "上次教材评价还未结束，只有上次教材评价结束后才能开始新的教材评价哦~",
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
        print("======== 评教/评学结束 ========");
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