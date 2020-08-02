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
                String person_group = "个人信息";
                List<List<String>> person_children = new LinkedList<>();
                List<String> child = new LinkedList<>();
                child = new LinkedList<>();
                child.add("学号");
                child.add(pinfo.stid);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("姓名");
                child.add(pinfo.name);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("年级");
                child.add(pinfo.grade+"");
                person_children.add(child);
                child = new LinkedList<>();
                child.add("班级");
                child.add(pinfo.classno);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("学院");
                child.add(pinfo.dptno + "院 " + pinfo.dptname);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("专业");
                child.add(pinfo.spname);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("专业代码");
                child.add(pinfo.spno);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("状态");
                child.add(pinfo.changetype);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("身份证号码");
                child.add(pinfo.idcard);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("学生类型");
                child.add(pinfo.stype);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("民族");
                child.add(pinfo.nation);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("政治面貌");
                child.add(pinfo.political);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("籍贯");
                child.add(pinfo.nativeplace);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("入学日期");
                child.add(pinfo.enrolldate);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("离校日期");
                child.add(pinfo.leavedate);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("高考总分");
                child.add(pinfo.total+"");
                person_children.add(child);
                child = new LinkedList<>();
                child.add("高考英语（或语文）");
                child.add(pinfo.chinese+"");
                person_children.add(child);
                child = new LinkedList<>();
                child.add("高考数学");
                child.add(pinfo.maths+"");
                person_children.add(child);
                child = new LinkedList<>();
                child.add("高考语文（或英语）");
                child.add(pinfo.english+"");
                person_children.add(child);
                child = new LinkedList<>();
                child.add("高考综合");
                child.add(pinfo.addscore1+"");
                person_children.add(child);
                child = new LinkedList<>();
                child.add("高考其他");
                child.add(pinfo.addscore2+"");
                person_children.add(child);
                child = new LinkedList<>();
                child.add("备注");
                child.add(pinfo.comment);
                person_children.add(child);
                child = new LinkedList<>();
                child.add("高考考生号");
                child.add(pinfo.testnum);
                person_children.add(child);
                menus.add(Map.entry(person_group, person_children));
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