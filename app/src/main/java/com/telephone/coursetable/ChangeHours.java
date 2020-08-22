package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class ChangeHours extends AppCompatActivity {

    private int[] tvids;
    private int[] etids;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View snack_view;

    public int islogical(String jiaoyan, EditText editText,String jiaoyan1,EditText editText1) {
        int index = 0;
        int count = 0;
        index = jiaoyan.indexOf(":");
        switch (index){
            case 1:if(jiaoyan.indexOf(":",2)!=-1){ editText.setError("格式错误！");return 0;}break;
            case 2:if(jiaoyan.indexOf(":",3)!=-1) { editText.setError("格式错误！");return 0;}break;
            case 3:if(jiaoyan.indexOf(":",4)!=-1) { editText.setError("格式错误！");return 0;}break;
            default:editText.setError("格式错误！");return 0;
        }
        index = jiaoyan1.indexOf(":");
        switch (index){
            case 1:if(jiaoyan1.indexOf(":",2)!=-1){return 0;}break;
            case 2:if(jiaoyan1.indexOf(":",3)!=-1) {return 0;}break;
            case 3:if(jiaoyan1.indexOf(":",4)!=-1) { return 0;}break;
            default:return 0;
        }

            index = jiaoyan.indexOf(":");
            String stime = jiaoyan.substring(0, index);
            String etime = jiaoyan.substring(index + 1);
            if(etime.equals("")){
                editText.setError("？？？？？");
                return 0;
            }
            if(Integer.parseInt(stime)>24 || Integer.parseInt(etime)>60){
                editText.setError("时间输入错误");
                return 0;
            }
        index = jiaoyan1.indexOf(":");
        String stime1 = jiaoyan1.substring(0, index);
            String etime1= jiaoyan1.substring(index + 1);
            if(etime1.equals("")){
                editText1.setError("？？？？？");
                return 0;
            }

        if(Integer.parseInt(stime1)>=24 || Integer.parseInt(etime1)>=60){
            editText1.setError("时间输入错误");
            return 0;
        }
        if(Integer.parseInt(stime)>Integer.parseInt(stime1)){
            editText.setError("前面时间大于后面时间");
            return 0;
        }
        else if(Integer.parseInt(stime)==Integer.parseInt(stime1) && Integer.parseInt(etime)>Integer.parseInt(etime1)){
            editText.setError("前面时间大于后面时间");
            return 0;
        }
            count = Integer.parseInt(stime+etime);
            return count;

    }

    public void restoretime(int[] ids,int[] editids,List<String> starttime,List<String> endtime,List<String> des){
        int i = -1;
        int j = -1;
        for (String d :des){
            i++;
            ((TextView)findViewById(ids[i])).setText(d + "开始");
            i++;
            ((TextView)findViewById(ids[i])).setText(d + "结束");
        }
         j = 0;
        for(String s:starttime){
            ((EditText)findViewById(editids[j])).setText(s);
            j=j+2;
        }
         j = 1;
        for(String e:endtime){
            ((EditText)findViewById(editids[j])).setText(e);
            j=j+2;
        }

    }

    public int jianyan(int[] editids){
        String jiaoyan=null;
        String jiaoyan1=null;
        int a=0;
        int b=0;
        int flag=0;
        int n=0;//判断循环次数
        for(int e:editids) {
            if(n==editids.length-1)
            break;
            EditText editText = (EditText) findViewById(e);
            jiaoyan = editText.getText().toString();
                EditText editText1 = (EditText) findViewById(e + 1);
                jiaoyan1 = editText1.getText().toString();
                a = islogical(jiaoyan,editText,jiaoyan1,editText1);
                if(a==0){
                    flag++;
                }
            if(n%2!=0){
                b=a;
            }
            n++;
            if(b!=0 && a!=0 && n%2!=0 &&a==b){
                editText.setError("大节时间不能相等");
                ((EditText)findViewById(e-1)).setError("大节时间不能相等");
                flag++;
            }
            if(n%2!=0){
                b=0;
            }
            }

        if(flag==0){
            return 1;
        }

        else
            return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_hours);

        tvids = new int[]{
                R.id.textView1,
                R.id.textView2,
                R.id.textView3,
                R.id.textView4,
                R.id.textView5,
                R.id.textView6,
                R.id.textView7,
                R.id.textView8,
                R.id.textView9,
                R.id.textView10
        };
        etids = new int[]{
                R.id.editTextTime,
                R.id.editTextTime10,
                R.id.editTextTime2,
                R.id.editTextTime3,
                R.id.editTextTime4,
                R.id.editTextTime5,
                R.id.editTextTime6,
                R.id.editTextTime7,
                R.id.editTextTime8,
                R.id.editTextTime9
        };
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        snack_view = (TextView)findViewById(R.id.textView10);

        List<String> starttime=new ArrayList<String>();
        List<String> endtime=new ArrayList<String>();
        List<String> des=new ArrayList<String>();
        int i =0;
        for(String time: MyApp.times){
            String skey = time + getResources().getString(R.string.pref_hour_start_suffix);
            String ekey = time + getResources().getString(R.string.pref_hour_end_suffix);
            String dkey = time + getResources().getString(R.string.pref_hour_des_suffix);
            starttime.add( sharedPreferences.getString(skey,null));
            endtime.add( sharedPreferences.getString(ekey,null));
            des.add( sharedPreferences.getString(dkey,null));
        }
        restoretime(tvids,etids,starttime,endtime,des);//显示时间
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//add menu to action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.changehour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                ((EditText)findViewById(etids[0])).requestFocus();
                ((EditText)findViewById(etids[0])).setEnabled(false);
                ((EditText)findViewById(etids[0])).setEnabled(true);
                List<String> starttime=new ArrayList<String>();
                List<String> endtime=new ArrayList<String>();
                List<String> des=new ArrayList<String>();
                int i=0;
                for(String time: MyApp.times){
                    String sbkey = time + getResources().getString(R.string.pref_hour_start_backup_suffix);
                    String ebkey = time + getResources().getString(R.string.pref_hour_end_backup_suffix);
                    String skey = time + getResources().getString(R.string.pref_hour_start_suffix);
                    String ekey = time + getResources().getString(R.string.pref_hour_end_suffix);
                    String sbtime = sharedPreferences.getString(sbkey, null);
                    String ebtime = sharedPreferences.getString(ebkey, null);
                    editor.putString(skey, sbtime);
                    editor.putString(ekey, ebtime);
                    ((EditText)findViewById(etids[i])).setText(sbtime);
                    ((EditText)findViewById(etids[i+1])).setText(ebtime);
                    i += 2;
                }
                editor.apply();
                for(int e:etids){
                    ((EditText)findViewById(e)).setError(null);
                }
                Snackbar.make(snack_view, "已恢复默认值", BaseTransientBottomBar.LENGTH_SHORT).show();
                break;
            case R.id.menu2:
                ((EditText)findViewById(etids[0])).requestFocus();
                ((EditText)findViewById(etids[0])).setEnabled(false);
                ((EditText)findViewById(etids[0])).setEnabled(true);
                final int re = jianyan(etids); //进行数据校验
                if(re==0){
                    Snackbar.make(snack_view, "数据出错，不能保存！", BaseTransientBottomBar.LENGTH_LONG).show();
                    break;
                }
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeHours.this);
                for(int e:etids){
                    ((EditText)findViewById(e)).setError(null);
                }
                alertDialog.setTitle("数据将会保存");
                alertDialog.setMessage("请确认数据无误");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int var) {
                        List<String> starttime=new ArrayList<String>();
                        List<String> endtime=new ArrayList<String>();
                        int n=0;
                        int index=0;
                        for(int i = 0; i < etids.length; i+=2){
                            String sadd = ((EditText)findViewById(etids[i])).getText().toString();
                            String eadd = ((EditText)findViewById(etids[i+1])).getText().toString();

                            index = sadd.indexOf(":");
                            String sad = sadd.substring(0, index);
                            String sad1 =sadd.substring(index+1);
                            switch (index) {
                                case 1:if(sad1.length()==3){
                                            sad1=sad1.substring(1);
                                    }
                                    sad = sad +":"+ sad1;
                                break;
                                case 2:
                                    if (sad.length() == 2 && sad.substring(0, 1).equals("0")) {
                                        sad = sad.substring(1);
                                    }
                                    if (sad1.length() == 1 && Integer.parseInt(sad) < 10) {
                                        sad1 = "0" + sad1;
                                    }
                                    sad = sad +":"+ sad1;
                                    break;
                                case 3:
                                    if (sad.length() == 3 && sad.substring(0, 1).equals("0") && Integer.parseInt(sad) < 10) {
                                        sad = sad.substring(2);
                                    }
                                    if (sad.length() == 3 && Integer.parseInt(sad) >= 10) {
                                        sad = sad.substring(1);
                                    }
                                    if (sad1.length() == 1) {
                                        sad1 = "0" + sad1;
                                    }
                                    sad = sad +":"+ sad1;
                                    break;
                            }
                            starttime.add(sad);
                            index = eadd.indexOf(":");
                            String ead = eadd.substring(0, index);
                            String ead1 =eadd.substring(index+1);
                            switch (index) {
                                case 1:if(ead1.length()==3){
                                    ead1=ead1.substring(1);
                                }
                                    ead = ead +":"+ ead1;
                                    break;
                                case 2:
                                    if (ead.length() == 2 && ead.substring(0, 1).equals("0")) {
                                        ead = ead.substring(1);
                                    }
                                    if (ead1.length() == 1 && Integer.parseInt(ead) < 10) {
                                        ead1 = "0" + ead1;
                                    }
                                    ead = ead +":"+ ead1;
                                    break;
                                case 3:
                                    if (ead.length() == 3 && ead.substring(0, 1).equals("0") && Integer.parseInt(ead) < 10) {
                                        ead = ead.substring(2);
                                    }
                                    if (ead.length() == 3 && Integer.parseInt(ead) >= 10) {
                                        ead = ead.substring(1);
                                    }
                                    if (ead1.length() == 1) {
                                        ead1 = "0" + ead1;
                                    }
                                    ead = ead +":"+ ead1;
                                    break;
                            }

                            endtime.add(ead);
                        }
                        int j =0;
                        for(String time: MyApp.times){
                            String skey = time + getResources().getString(R.string.pref_hour_start_suffix);
                            String ekey = time + getResources().getString(R.string.pref_hour_end_suffix);
                            String dkey = time + getResources().getString(R.string.pref_hour_des_suffix);
                            editor.putString(skey,starttime.get(j));
                            editor.putString(ekey,endtime.get(j));
                            j++;
                        }
                        editor.apply();
                        Snackbar.make(snack_view, "保存成功！", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                    //保存操作
                });
                alertDialog.setNegativeButton("重新编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}