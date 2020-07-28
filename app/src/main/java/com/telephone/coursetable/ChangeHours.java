package com.telephone.coursetable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.telephone.coursetable.Gson.Hour;
import com.telephone.coursetable.Gson.Hours;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChangeHours extends AppCompatActivity {

    private Hours originHList;
    private Hours reoriginHList;

    public int islogical(String jiaoyan, EditText editText) {
        int index = 0;
        int count = 0;
        for (int n = 0; n < 5; n++) {
            index = jiaoyan.indexOf(":", index);
            if (index != -1) {
                if (index == 0 || index == 3 || index == 4) {
                    editText.setError("格式错误！");
                    return 0;
                }
                else if(index==1){
                    index++;
                    index = jiaoyan.indexOf(":", index);
                    if (index==2){
                        editText.setError("格式错误！");
                        return 0;
                    }
                }
            }
            else
            {
                editText.setError("格式错误！");
                return 0;
            }
        }

            index = jiaoyan.indexOf(":");
            String stime = jiaoyan.substring(0, index);
            if(Integer.parseInt(stime)>24){
                editText.setError("大于24时");
                return 0;
            }
            String etime = jiaoyan.substring(index + 1);
            if(Integer.parseInt(etime)>60){
                editText.setError("大于60分");
                return 0;
            }
            etime = stime + etime;
            int a = Integer.parseInt(etime);
            return a;

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
            a = islogical(jiaoyan, editText);
            if(n<editids.length-1){
                EditText editText1 = (EditText) findViewById(e + 1);
                jiaoyan1 = editText1.getText().toString();
                b = islogical(jiaoyan1, editText1);
            }
            n++;
            if(a==0 || b==0){
                flag++;
                break;
            }
            if(a>b){
                editText.setError("时间出错");
                flag++;
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
        List<String> starttime=new ArrayList<String>();
        List<String> endtime=new ArrayList<String>();
        List<String> des=new ArrayList<String>();
        int i =0;
        final SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.hours_preference_file_name),MODE_PRIVATE);
        for(String time: MyApp.times){
            String skey = time + getResources().getString(R.string.hours_pref_time_start_suffix);
            String ekey = time + getResources().getString(R.string.hours_pref_time_end_suffix);
            String dkey = time + getResources().getString(R.string.hours_pref_time_des_suffix);
            starttime.add( sharedPreferences.getString(skey,null));
            endtime.add( sharedPreferences.getString(ekey,null));
            des.add( sharedPreferences.getString(dkey,null));
        }

        final int[] ids = {
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
        final int[] editids = {
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
        restoretime(ids,editids,starttime,endtime,des);//显示时间

        Button btn2 = (Button)findViewById(R.id.button3);//复原建
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> starttime=new ArrayList<String>();
                List<String> endtime=new ArrayList<String>();
                List<String> des=new ArrayList<String>();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int i=0;
                for(String time: MyApp.times){
                    String sbkey = time + getResources().getString(R.string.hours_pref_time_start_backup_suffix);
                    String ebkey = time + getResources().getString(R.string.hours_pref_time_end_backup_suffix);
                    String skey = time + getResources().getString(R.string.hours_pref_time_start_suffix);
                    String ekey = time + getResources().getString(R.string.hours_pref_time_end_suffix);
                    String sbtime = sharedPreferences.getString(sbkey, null);
                    String ebtime = sharedPreferences.getString(ebkey, null);
                    editor.putString(skey, sbtime);
                    editor.putString(ekey, ebtime);
                    ((EditText)findViewById(editids[i])).setText(sbtime);
                    ((EditText)findViewById(editids[i+1])).setText(ebtime);
                    i += 2;
                }
                editor.apply();
                Toast.makeText(ChangeHours.this,"已恢复默认值",Toast.LENGTH_SHORT).show();
            }
        });

        Button btn = (Button)findViewById(R.id.button4);//保存建
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int re = jianyan(editids); //进行数据校验
                if(re==0){
                    Toast toast =  Toast.makeText(ChangeHours.this,"数据出错，不能保存！",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeHours.this);
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
                        for(int i = 0; i < editids.length; i+=2){
                            String sadd = ((EditText)findViewById(editids[i])).getText().toString();
                            String eadd = ((EditText)findViewById(editids[i+1])).getText().toString();

                            index = sadd.indexOf(":");
                            String sad = sadd.substring(0, index);
                            if(sad.length()==2 && sad.substring(0, 1).equals("0")){
                                sad = sad.substring(1);
                            }
                            String sad1 =sadd.substring(index+1);
                            if(sad1.length()==1){
                                sad1 = '0'+sad1;
                            }
                            sadd = sad+ ":"+ sad1;

                            index = eadd.indexOf(":");
                            String ead = eadd.substring(0, index);
                            if(ead.length()==2 && ead.substring(0, 1).equals("0")){
                                ead = ead.substring(1);
                            }
                            String ead1 =eadd.substring(index+1);
                            if(ead1.length()==1){
                                ead1 = '0'+ead1;
                            }
                            eadd = ead+ ":"+ ead1;


                            starttime.add(sadd);
                            endtime.add(eadd);
                        }
                        int j =0;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        for(String time: MyApp.times){
                            String skey = time + getResources().getString(R.string.hours_pref_time_start_suffix);
                            String ekey = time + getResources().getString(R.string.hours_pref_time_end_suffix);
                            String dkey = time + getResources().getString(R.string.hours_pref_time_des_suffix);
                            editor.putString(skey,starttime.get(j));
                            editor.putString(ekey,endtime.get(j));
                            j++;
                        }
                        editor.apply();
                        Toast toast =  Toast.makeText(ChangeHours.this,"保存成功！",Toast.LENGTH_LONG);
                        toast.show();
                        }
                        //保存操作
                });
                alertDialog.setNegativeButton("重新编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.show();
            }
       });

    }

}