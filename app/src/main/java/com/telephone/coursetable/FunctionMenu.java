package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.telephone.coursetable.Database.GraduationScore;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.PersonInfo;
import com.telephone.coursetable.Database.PersonInfoDao;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import java.util.LinkedList;
import java.util.List;

public class FunctionMenu extends AppCompatActivity {

    private PersonInfoDao pdao;
    private GraduationScoreDao gsdao;
    private ExpandableListView menu_list;

    @Override
    protected void onDestroy() {
        MyApp.running_function_menu = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.running_function_menu = this;
        setContentView(R.layout.activity_function_menu);

        pdao = MyApp.getCurrentAppDB().personInfoDao();
        gsdao = MyApp.getCurrentAppDB().graduationScoreDao();
        menu_list = (ExpandableListView)findViewById(R.id.function_menu_list);

        final ExpandableListView menu_listf = menu_list;

        new Thread(new Runnable() {
            @Override
            public void run() {

                final List<Entry<String, List<List<String>>>> menus = new LinkedList<>();
                List<List<String>> children = new LinkedList<>();
                List<String> child = new LinkedList<>();

                PersonInfo pinfo = pdao.selectAll().get(0);
                String person_group = "个人信息";
                children = new LinkedList<>();
                child = new LinkedList<>();
                child.add("学号");
                child.add(pinfo.stid);
                children.add(child);
                child = new LinkedList<>();
                child.add("姓名");
                child.add(pinfo.name);
                children.add(child);
                child = new LinkedList<>();
                child.add("年级");
                child.add(pinfo.grade+"");
                children.add(child);
                child = new LinkedList<>();
                child.add("班级");
                child.add(pinfo.classno);
                children.add(child);
                child = new LinkedList<>();
                child.add("学院");
                child.add(pinfo.dptno + "院 " + pinfo.dptname);
                children.add(child);
                child = new LinkedList<>();
                child.add("专业");
                child.add(pinfo.spname);
                children.add(child);
                child = new LinkedList<>();
                child.add("专业代码");
                child.add(pinfo.spno);
                children.add(child);
                child = new LinkedList<>();
                child.add("状态");
                child.add(pinfo.changetype);
                children.add(child);
                child = new LinkedList<>();
                child.add("身份证号码");
                child.add(pinfo.idcard);
                children.add(child);
                child = new LinkedList<>();
                child.add("学生类型");
                child.add(pinfo.stype);
                children.add(child);
                child = new LinkedList<>();
                child.add("民族");
                child.add(pinfo.nation);
                children.add(child);
                child = new LinkedList<>();
                child.add("政治面貌");
                child.add(pinfo.political);
                children.add(child);
                child = new LinkedList<>();
                child.add("籍贯");
                child.add(pinfo.nativeplace);
                children.add(child);
                child = new LinkedList<>();
                child.add("入学日期");
                child.add(pinfo.enrolldate);
                children.add(child);
                child = new LinkedList<>();
                child.add("离校日期");
                child.add(pinfo.leavedate);
                children.add(child);
                child = new LinkedList<>();
                child.add("高考总分");
                child.add(pinfo.total+"");
                children.add(child);
                child = new LinkedList<>();
                child.add("高考英语（或语文）");
                child.add(pinfo.chinese+"");
                children.add(child);
                child = new LinkedList<>();
                child.add("高考数学");
                child.add(pinfo.maths+"");
                children.add(child);
                child = new LinkedList<>();
                child.add("高考语文（或英语）");
                child.add(pinfo.english+"");
                children.add(child);
                child = new LinkedList<>();
                child.add("高考综合");
                child.add(pinfo.addscore1+"");
                children.add(child);
                child = new LinkedList<>();
                child.add("高考其他");
                child.add(pinfo.addscore2+"");
                children.add(child);
                child = new LinkedList<>();
                child.add("备注");
                child.add(pinfo.comment);
                children.add(child);
                child = new LinkedList<>();
                child.add("高考考生号");
                child.add(pinfo.testnum);
                children.add(child);
                menus.add(Map.entry(person_group, children));

                String graduation_score_group = "毕业学分";
                List<GraduationScore> graduation_score_list = gsdao.selectAll();
                children = new LinkedList<>();
                child = new LinkedList<>();
                child.add("课程名称");
                child.add("成绩");
                child.add("学分");
                child.add("有效");
                child.add("计划");
                children.add(child);
                double credit_hour_total = 0;
                for (GraduationScore gs : graduation_score_list){
                    child = new LinkedList<>();
                    child.add(gs.scname);
                    child.add(gs.score+"");
                    child.add(gs.xf+"");
                    if (gs.credithour == 0){
                        child.add("×");
                    }else {
                        credit_hour_total += gs.credithour;
                        child.add("√");
                    }
                    if (gs.planxf == 0){
                        child.add("×");
                    }else {
                        child.add("√");
                    }
                    children.add(child);
                }
                child = new LinkedList<>();
                child.add("");
                child.add("");
                child.add("");
                child.add(credit_hour_total+"");
                child.add("");
                children.add(child);
                menus.add(Map.entry(graduation_score_group, children));

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