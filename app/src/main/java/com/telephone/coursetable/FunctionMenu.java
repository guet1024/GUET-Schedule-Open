package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.telephone.coursetable.Database.PersonInfo;
import com.telephone.coursetable.Database.PersonInfoDao;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import java.util.LinkedList;
import java.util.List;

public class FunctionMenu extends AppCompatActivity {

    private PersonInfoDao pdao;
    private ExpandableListView menu_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_menu);

        pdao = MyApp.getCurrentAppDB().personInfoDao();
        menu_list = (ExpandableListView)findViewById(R.id.function_menu_list);

        final ExpandableListView menu_listf = menu_list;

        new Thread(new Runnable() {
            @Override
            public void run() {
                PersonInfo pinfo = pdao.selectAll().get(0);
                final List<Entry<String, List<List<String>>>> menus = new LinkedList<>();
                int i = 0;
                for (MenuAndSubmenu msm : MyApp.menuText) {
                    List<List<String>> children = new LinkedList<>();
                    for (String sm : msm.submenu){
                        List<String> child = new LinkedList<>();
                        child.add(sm);
                        switch (i){
                            case 0:
                                if (sm.equals("学号")){
                                    child.add(pinfo.stid);
                                }else if (sm.equals("姓名")){
                                    child.add(pinfo.name);
                                }
                                else if (sm.equals("年级")){
                                    child.add(pinfo.grade+"");
                                }
                                else if (sm.equals("班级")){
                                    child.add(pinfo.classno);
                                }
                                else if (sm.equals("专业代码")){
                                    child.add(pinfo.spno);
                                }
                                else if (sm.equals("状态")){
                                    child.add(pinfo.changetype);
                                }else if (sm.equals("身份证号码")){
                                    child.add(pinfo.idcard);
                                }else if (sm.equals("学生类型")){
                                    child.add(pinfo.stype);
                                }else if (sm.equals("民族")){
                                    child.add(pinfo.nation);
                                }else if (sm.equals("政治面貌")){
                                    child.add(pinfo.political);
                                }else if (sm.equals("籍贯")){
                                    child.add(pinfo.nativeplace);
                                }else if (sm.equals("入学日期")){
                                    child.add(pinfo.enrolldate);
                                }else if (sm.equals("离校日期")){
                                    child.add(pinfo.leavedate);
                                }else if (sm.equals("高考总分")){
                                    child.add(pinfo.total+"");
                                }else if (sm.equals("高考英语（或语文）")){
                                    child.add(pinfo.chinese+"");
                                }else if (sm.equals("高考数学")){
                                    child.add(pinfo.maths+"");
                                }else if (sm.equals("高考语文（或英语）")){
                                    child.add(pinfo.english+"");
                                }else if (sm.equals("高考综合")){
                                    child.add(pinfo.addscore1+"");
                                }else if (sm.equals("高考其他")){
                                    child.add(pinfo.addscore2+"");
                                }else if (sm.equals("备注")){
                                    child.add(pinfo.comment);
                                }else if (sm.equals("高考考生号")){
                                    child.add(pinfo.testnum);
                                }
                                break;
                        }
                        children.add(child);
                    }
                    menus.add(Map.entry(msm.menu, children));
                    i++;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        menu_listf.setAdapter(new FunctionMenuAdapter(FunctionMenu.this, menus, true, menu_listf));
                    }
                });
            }
        }).start();
    }
}