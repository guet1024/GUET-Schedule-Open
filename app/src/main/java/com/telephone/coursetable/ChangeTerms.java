package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.LogMe.LogMe;

import java.util.LinkedList;
import java.util.List;

public class ChangeTerms extends AppCompatActivity {

    private boolean isChangingTerm = false;
    private boolean isScrolling = false;
    private List<TermInfo> terms_data = null;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, FunctionMenu.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.CHANGE_TERMS);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_change_terms);
        initTerms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//add menu to action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.changeterms, menu);
        return true;
    }

    /**
     * @ui
     */
    private void clearAllIMAndFocusAndError(){
        EditText et = (EditText)findViewById(R.id.delay_week);
        et.setEnabled(!et.isEnabled());
        et.setEnabled(!et.isEnabled());
        et.clearFocus();
        et.setError(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        clearAllIMAndFocusAndError();
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ChangeTerms.this, FunctionMenu.class));
                return true;
            case R.id.reset:
                onReset();
                break;
            case R.id.save:
                onSave();
                break;
        }
        return true;
    }

    /**
     * @ui
     */
    private void onReset(){
        final String NAME = "onReset()";
        if (isChangingTerm || isScrolling) return;
        //禁用输入框和滚轮
        //输入框变零
        //遍历列表设置0
        //写回数据库
        //启用输入框和滚轮
        ((EditText)findViewById(R.id.delay_week)).setEnabled(false);
        ((NumberPicker)findViewById(R.id.term)).setEnabled(false);
        ((EditText)findViewById(R.id.delay_week)).setText(0+"");
        for (int i = 0; i < terms_data.size(); i++){
            terms_data.get(i).setDelay(0);
        }
        new Thread(()->{
            MyApp.getCurrentAppDB().termInfoDao().deleteAll();
            for (TermInfo term : terms_data){
                LogMe.e(NAME, "save term: " + term.toString());
                MyApp.getCurrentAppDB().termInfoDao().insert(term);
            }
            runOnUiThread(()->{
                ((EditText)findViewById(R.id.delay_week)).setEnabled(true);
                ((NumberPicker)findViewById(R.id.term)).setEnabled(true);
                Snackbar.make(findViewById(R.id.term), "已重置所有学期", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            });
        }).start();
    }

    /**
     * @ui
     */
    private void onSave(){
        final String NAME = "onSave()";
        if (isChangingTerm || isScrolling) return;
        //禁用输入框和滚轮
        //保存当前输入框到列表
        //如果错误，不保存，直接将列表数据还原到输入框
        //写回数据库
        //启动输入框和滚轮
        ((EditText)findViewById(R.id.delay_week)).setEnabled(false);
        ((NumberPicker)findViewById(R.id.term)).setEnabled(false);
        try {
            int week = Integer.parseInt(((EditText)findViewById(R.id.delay_week)).getText().toString());
            terms_data.get(((NumberPicker)findViewById(R.id.term)).getValue()).setDelay(week);
        }catch (NumberFormatException e){
            e.printStackTrace();
            int rollback = terms_data.get(((NumberPicker)findViewById(R.id.term)).getValue()).delay_week;
            ((EditText)findViewById(R.id.delay_week)).setText(rollback+"");
        }
        new Thread(()->{
            MyApp.getCurrentAppDB().termInfoDao().deleteAll();
            for (TermInfo term : terms_data){
                LogMe.e(NAME, "save term: " + term.toString());
                MyApp.getCurrentAppDB().termInfoDao().insert(term);
            }
            runOnUiThread(()->{
                ((EditText)findViewById(R.id.delay_week)).setEnabled(true);
                ((NumberPicker)findViewById(R.id.term)).setEnabled(true);
                Snackbar.make(findViewById(R.id.term), "学期调整成功", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            });
        }).start();
    }

    /**
     * @ui
     */
    private void initTerms(){
        new Thread(()->{
            terms_data = MyApp.getCurrentAppDB().termInfoDao().selectAll();
            List<String> values = new LinkedList<>();
            for (TermInfo term : terms_data){
                values.add(term.termname);
            }
            runOnUiThread(()->{
                ((NumberPicker)findViewById(R.id.term)).setWrapSelectorWheel(false);
                ((NumberPicker)findViewById(R.id.term)).setOnScrollListener((numberPicker, i) -> {
                    switch (i){
                        case NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: default:
                            isScrolling = true;
                            clearAllIMAndFocusAndError();
                            ((EditText)findViewById(R.id.delay_week)).setEnabled(!isScrolling && !isChangingTerm);
                            break;
                        case NumberPicker.OnScrollListener.SCROLL_STATE_IDLE:
                            isScrolling = false;
                            ((EditText)findViewById(R.id.delay_week)).setEnabled(!isScrolling && !isChangingTerm);
                            break;
                    }
                });
                ((NumberPicker)findViewById(R.id.term)).setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
                    isChangingTerm = true;
                    clearAllIMAndFocusAndError();
                    ((EditText)findViewById(R.id.delay_week)).setEnabled(!isScrolling && !isChangingTerm);
                    //保存旧数据，显示新数据，出错不还原输出，直接跳过保存
                    try {
                        int week = Integer.parseInt(((EditText)findViewById(R.id.delay_week)).getText().toString());
                        terms_data.get(oldValue).setDelay(week);
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                    ((EditText)findViewById(R.id.delay_week)).setText(terms_data.get(newValue).delay_week+"");
                    isChangingTerm = false;
                    ((EditText)findViewById(R.id.delay_week)).setEnabled(!isScrolling && !isChangingTerm);
                });
                ((NumberPicker)findViewById(R.id.term)).setDisplayedValues(values.toArray(new String[0]));
                ((NumberPicker)findViewById(R.id.term)).setMinValue(0);
                ((NumberPicker)findViewById(R.id.term)).setMaxValue(values.size() - 1);
                ((NumberPicker)findViewById(R.id.term)).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                ((EditText)findViewById(R.id.delay_week)).setText(terms_data.get(((NumberPicker)findViewById(R.id.term)).getValue()).delay_week+"");
            });
        }).start();
    }
}
