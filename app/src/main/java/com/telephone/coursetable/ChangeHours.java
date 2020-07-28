package com.telephone.coursetable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.telephone.coursetable.Gson.Hour;
import com.telephone.coursetable.Gson.Hours;

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
            else break;
        }

            index = jiaoyan.indexOf(":", index);
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
    public void restoretime(int[] ids,int[] editids,List<Hour> hs){
        int i = -1;
        int j = -1;
        for (Hour h : hs){
            i++;
            ((TextView)findViewById(ids[i])).setText(h.getNodename() + "开始");
            int index = h.getMemo().indexOf('-');
            String stime= h.getMemo().substring(0, index);
            String etime= h.getMemo().substring(index+1);
            j++;
            ((EditText)findViewById(editids[j])).setText(stime);
            ((EditText)findViewById(editids[j])).setError(null);
            j++;
            ((EditText)findViewById(editids[j])).setText(etime);
            ((EditText)findViewById(editids[j])).setError(null);
            i++;
            ((TextView)findViewById(ids[i])).setText(h.getNodename() + "结束");
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
        originHList = new Gson().fromJson("{\n" +
                "    \"success\": true,\n" +
                "    \"total\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"1\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第1、2节\",\n" +
                "            \"memo\": \"8:25-10:05\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"2\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第3、4节\",\n" +
                "            \"memo\": \"10:25-12:00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"3\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第5、6节\",\n" +
                "            \"memo\": \"14:25-16:05\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"4\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第7、8节\",\n" +
                "            \"memo\": \"16:25-18:00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"5\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第9、10节\",\n" +
                "            \"memo\": \"19:25-21:05\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n", Hours.class);
        reoriginHList = new Gson().fromJson("{\n" +
                "    \"success\": true,\n" +
                "    \"total\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"1\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第1、2节\",\n" +
                "            \"memo\": \"8:25-10:05\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"2\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第3、4节\",\n" +
                "            \"memo\": \"10:25-12:00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"3\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第5、6节\",\n" +
                "            \"memo\": \"14:25-16:05\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"4\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第7、8节\",\n" +
                "            \"memo\": \"16:25-18:00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"term\": null,\n" +
                "            \"nodeno\": \"5\",\n" +
                "            \"xss\": 2,\n" +
                "            \"nodename\": \"第9、10节\",\n" +
                "            \"memo\": \"19:25-21:05\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n", Hours.class);
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
        List<Hour> hs = originHList.getData();

        restoretime(ids,editids,hs);//显示时间

        Button btn2 = (Button)findViewById(R.id.button3);//复原建
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Hour> hs = reoriginHList.getData();
                restoretime(ids,editids,hs);
                Toast toast =  Toast.makeText(ChangeHours.this,"已恢复默认值",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Button btn = (Button)findViewById(R.id.button4);//保存建
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int re = jianyan(editids); //进行数据校验
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeHours.this);
                alertDialog.setTitle("数据将会保存");
                alertDialog.setMessage("请确认数据无误");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(re==0){
                            Toast toast =  Toast.makeText(ChangeHours.this,"数据出错，不能保存！",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                        //保存操作
                        int j =-1;
                        List<Hour> hs = originHList.getData();
                        for(Hour hour:hs){
                            j++;
                            String firstpart = ((EditText) findViewById(editids[j])).getText()+"-";
                            j++;
                            String Memo = firstpart + ((EditText) findViewById(editids[j])).getText();
                            hour.setMemo(Memo);
                        }
                        Toast toast =  Toast.makeText(ChangeHours.this,"保存成功！",Toast.LENGTH_LONG);
                        toast.show();
                    }
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