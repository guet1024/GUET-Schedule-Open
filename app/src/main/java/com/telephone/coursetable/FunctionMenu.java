package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Database.ExamInfo;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.Database.Grades;
import com.telephone.coursetable.Database.GradesDao;
import com.telephone.coursetable.Database.GraduationScore;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.PersonInfo;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfoDao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.LinkedList;
import java.util.List;

/**
 * @clear
 */
public class FunctionMenu extends AppCompatActivity {

    public static Map<String, Integer> colors = new HashMap<String, Integer>(){
        {
            put("0", 0xFFDEF2EF);
            put("1", 0xFFDEF2EF);
        }
    };

    private PersonInfoDao pdao;
    private GraduationScoreDao gsdao;
    private GradesDao grdao;
    private ExamInfoDao edao;
    private TermInfoDao tdao;
    private ExpandableListView menu_list;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.FUNCTION_MENU);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_function_menu);

        pdao = MyApp.getCurrentAppDB().personInfoDao();
        gsdao = MyApp.getCurrentAppDB().graduationScoreDao();
        grdao = MyApp.getCurrentAppDB().gradesDao();
        edao = MyApp.getCurrentAppDB().examInfoDao();
        tdao = MyApp.getCurrentAppDB().termInfoDao();
        menu_list = (ExpandableListView)findViewById(R.id.function_menu_list);

        final ExpandableListView menu_listf = menu_list;

        new Thread(() -> {

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
            child.add(null);
            children.add(child);
            double credit_hour_total = 0;
            String last_sterm = null;
            boolean color = false;
            for (GraduationScore gs : graduation_score_list){
                child = new LinkedList<>();
                child.add(gs.scname);
                child.add(gs.score+"");
                child.add(gs.xf+"");
                if (gs.credithour == 0){
                    child.add(" ");
                }else {
                    credit_hour_total += gs.credithour;
                    child.add("√");
                }
                if (gs.planxf == 0){
                    child.add(" ");
                }else {
                    child.add("√");
                }
                if (!gs.sterm.equals(last_sterm)){
                    color = !color;
                }
                last_sterm = gs.sterm;
                if (color){
                    child.add("0");
                }else {
                    child.add(null);
                }
                children.add(child);
            }
            child = new LinkedList<>();
            child.add("有效学分");
            child.add("");
            child.add("");
            child.add(credit_hour_total+"");
            child.add("");
            color = !color;
            if (color){
                child.add("0");
            }else {
                child.add(null);
            }
            children.add(child);
            menus.add(Map.entry(graduation_score_group, children));

            String grades_group = "成绩单";
            List<Grades> grades_list = grdao.selectAll();
            children = new LinkedList<>();
            child = new LinkedList<>();
            child.add("课程名称");
            child.add("成绩");
            child.add("平时");
            child.add("实验");
            child.add("考核");
            child.add(null);
            children.add(child);
            last_sterm = null;
            color = false;
            for (Grades gr : grades_list){
                child = new LinkedList<>();
                child.add(gr.cname);
                child.add(gr.zpxs);
                child.add(gr.pscj+"");
                child.add(gr.sycj+"");
                child.add(gr.khcj+"");
                if (!gr.term.equals(last_sterm)){
                    color = !color;
                }
                last_sterm = gr.term;
                if (color){
                    child.add("0");
                }else {
                    child.add(null);
                }
                children.add(child);
            }
            menus.add(Map.entry(grades_group, children));

            String library_group = "图书馆藏";
            children = new LinkedList<>();
            child = new LinkedList<>();
            child.add("图书馆藏查询");
            children.add(child);
            menus.add(Map.entry(library_group, children));

            String change_term_group = "学期调整";
            children = new LinkedList<>();
            child = new LinkedList<>();
            child.add("调整学期时间");
            children.add(child);
            menus.add(Map.entry(change_term_group, children));

            String exams_group = "考试安排";
            List<ExamInfo> exam_list = edao.selectAll();
            children = new LinkedList<>();
            String cno = "";
            String edate = "";
            String etime = "";
            List<ExamInfo> filter_elist = new LinkedList<>();
            for (ExamInfo e : exam_list){
                if (e.courseno.equals(cno) && e.examdate.equals(edate) && e.kssj.equals(etime)){
                    filter_elist.get(filter_elist.size() - 1).croomno += ", " + e.croomno;
                }else {
                    filter_elist.add(e);
                }
                cno = e.courseno;
                edate = e.examdate;
                etime = e.kssj;
            }
            exam_list = filter_elist;
            for (ExamInfo e : exam_list){
                child = new LinkedList<>();
                child.add("学期: " + tdao.select(e.term).get(0).termname);
                child.add("课程名称: " + e.cname);
                child.add("课号: " + e.courseno);
                child.add("日期: " + e.examdate);
                child.add("时间: " + e.kssj);
                child.add("教室: " + e.croomno);
                if (e.ets >= Clock.nowTimeStamp()){
                    child.add("1");
                }else {
                    child.add(null);
                }
                children.add(child);
            }
            menus.add(Map.entry(exams_group, children));

            runOnUiThread(() -> menu_listf.setAdapter(new FunctionMenuAdapter(FunctionMenu.this, menus, true, menu_listf, FunctionMenu.this)));
        }).start();
    }
}