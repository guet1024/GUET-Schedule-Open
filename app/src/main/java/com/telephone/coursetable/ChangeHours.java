package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @clear
 */
public class ChangeHours extends AppCompatActivity {

    private int[] tvids = {
            R.id.change_hour_textView1,
            R.id.change_hour_textView2,
            R.id.change_hour_textView3,
            R.id.change_hour_textView4,
            R.id.change_hour_textView5,
            R.id.change_hour_textView6,
            R.id.change_hour_textView7,
            R.id.change_hour_textView8,
            R.id.change_hour_textView9,
            R.id.change_hour_textView10
    };
    private int[] etids = {
            R.id.change_hour_editTextTime1,
            R.id.change_hour_editTextTime2,
            R.id.change_hour_editTextTime3,
            R.id.change_hour_editTextTime4,
            R.id.change_hour_editTextTime5,
            R.id.change_hour_editTextTime6,
            R.id.change_hour_editTextTime7,
            R.id.change_hour_editTextTime8,
            R.id.change_hour_editTextTime9,
            R.id.change_hour_editTextTime10
    };

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View snack_view;
    private List<String> default_time_list;

    private boolean isIllegal(String text){
        if (!text.contains(":") || text.indexOf(":") == 0 || text.indexOf(":") == text.length() - 1){
            return true;
        }
        int index = text.indexOf(":");
        String first = text.substring(0, index);
        String second = text.substring(index + 1);
        int firstNum;
        int secondNum;
        try {
            firstNum = Integer.parseInt(first);
            secondNum = Integer.parseInt(second);
        }catch (Exception e){
            return true;
        }
        if (firstNum < 0 || firstNum > 23 || secondNum < 0 || secondNum > 59){
            return true;
        }
        return false;
    }

    private boolean isNotAscending(String text1, String text2){
        int firstNum1 = Integer.parseInt(text1.substring(0, text1.indexOf(":")));
        int firstNum2 = Integer.parseInt(text2.substring(0, text2.indexOf(":")));
        int secondNum1 = Integer.parseInt(text1.substring(text1.indexOf(":") + 1));
        int secondNum2 = Integer.parseInt(text2.substring(text2.indexOf(":") + 1));
        if (firstNum1 > firstNum2){
            return true;
        }else if (firstNum1 == firstNum2 && secondNum1 >= secondNum2){
            return true;
        }
        return false;
    }

    private String format(String text){
        int firstNum = Integer.parseInt(text.substring(0, text.indexOf(":")));
        int secondNum = Integer.parseInt(text.substring(text.indexOf(":") + 1));
        return String.format("%d:%02d", firstNum, secondNum);
    }

    private List<String> getInputtedTimeStringList(){
        List<String> list = new LinkedList<>();
        for (int etid : etids) {
            String s = ((EditText) findViewById(etid)).getText().toString();
            list.add(format(s));
        }
        return list;
    }

    private void showDescription(List<String> des_list){
        for (int i = 0; i < tvids.length; i++){
            ((TextView)findViewById(tvids[i])).setText(des_list.get(i));
        }
    }

    private void showTime(List<String> time_list){
        for (int i = 0; i < etids.length; i++){
            ((EditText)findViewById(etids[i])).setText(time_list.get(i));
        }
    }

    private void clearAllIMAndFocusAndError(){
        for (int id : etids){
            EditText et = (EditText)findViewById(id);
            et.setEnabled(!et.isEnabled());
            et.setEnabled(!et.isEnabled());
            et.clearFocus();
            et.setError(null);
        }
    }

    private void save(List<String> time_list){
        for (int i = 0; i < MyApp.times.length; i++){
            String time = MyApp.times[i];
            editor.putString(time + getResources().getString(R.string.pref_hour_start_suffix), time_list.get(i * 2));
            editor.putString(time + getResources().getString(R.string.pref_hour_end_suffix), time_list.get(i * 2 + 1));
        }
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.CHANGE_HOURS)) MyApp.setRunning_activity(MyApp.RunningActivity.NULL);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.CHANGE_HOURS);
        setContentView(R.layout.activity_change_hours);

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        snack_view = findViewById(R.id.change_hour_textView10);
        default_time_list = new LinkedList<>();

        List<String> des_list = new LinkedList<>();
        List<String> time_list = new LinkedList<>();
        for (int i = 0; i < MyApp.times.length; i++){
            String des = sharedPreferences.getString(MyApp.times[i] + getResources().getString(R.string.pref_hour_des_suffix), "null");
            String stime = sharedPreferences.getString(MyApp.times[i] + getResources().getString(R.string.pref_hour_start_suffix), "null");
            String sbtime = sharedPreferences.getString(MyApp.times[i] + getResources().getString(R.string.pref_hour_start_backup_suffix), "null");
            String etime = sharedPreferences.getString(MyApp.times[i] + getResources().getString(R.string.pref_hour_end_suffix), "null");
            String ebtime = sharedPreferences.getString(MyApp.times[i] + getResources().getString(R.string.pref_hour_end_backup_suffix), "null");
            des_list.add(des + "开始");
            des_list.add(des + "结束");
            time_list.add(stime);
            time_list.add(etime);
            default_time_list.add(sbtime);
            default_time_list.add(ebtime);
        }
        showDescription(des_list);
        showTime(time_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//add menu to action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.changehour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        clearAllIMAndFocusAndError();
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ChangeHours.this, MainActivity.class));
                return true;
            case R.id.change_hour_menu1:
                save(default_time_list);
                showTime(default_time_list);
                Snackbar.make(snack_view, "已恢复默认值", BaseTransientBottomBar.LENGTH_SHORT).show();
                break;
            case R.id.change_hour_menu2:
                for (int etid : etids) {
                    String text = ((EditText) findViewById(etid)).getText().toString();
                    if (isIllegal(text)) {
                        ((EditText) findViewById(etid)).setError("时间格式错误");
                        return true;
                    }
                }
                for (int i = 0; i < etids.length - 1; i++){
                    String text1 = ((EditText)findViewById(etids[i])).getText().toString();
                    String text2 = ((EditText)findViewById(etids[i + 1])).getText().toString();
                    if (isNotAscending(text1, text2)){
                        ((EditText) findViewById(etids[i])).setError("上面的时间点必须小于下面的时间点");
                        ((EditText) findViewById(etids[i + 1])).setError("下面的时间点必须大于上面的时间点");
                        return true;
                    }
                }
                List<String> formatted_inputted_time_list = getInputtedTimeStringList();
                save(formatted_inputted_time_list);
                showTime(formatted_inputted_time_list);
                Snackbar.make(snack_view, "保存成功", BaseTransientBottomBar.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}