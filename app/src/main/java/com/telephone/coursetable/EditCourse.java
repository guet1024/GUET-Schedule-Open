package com.telephone.coursetable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.Key.GoToClassKey;
import com.telephone.coursetable.Database.Methods.Methods;
import com.telephone.coursetable.Database.MyComment;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Gson.CourseCard.ACard;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;
import com.telephone.coursetable.LogMe.LogMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

public class EditCourse extends AppCompatActivity {

    public static final String EXTRA_COURSE_CARD_DATA = "ccd";
    public static final String EXTRA_IF_ADD = "add";

    private View snack_bar_root_view;
    private boolean add_start = false;
    private CourseCardData intent_extra_CourseCardData = null;

    private static final int[] et_ids_add_yes = {
            R.id.edit_course_startweek,
            R.id.edit_course_endweek,
            R.id.edit_course_weekday,
            R.id.edit_course_time,
            R.id.edit_course_cname,
            R.id.edit_course_tname,
            R.id.edit_course_croom,
            R.id.edit_course_grade_point,
            R.id.edit_course_ctype,
            R.id.edit_course_examt,
            R.id.edit_course_cno
    };

    private volatile boolean visible = true;
    private volatile Intent outdated = null;

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
        go_back();
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.EDIT_COURSE);
        MyApp.setRunning_activity_pointer(this);
        initContentView();
        snack_bar_root_view = findViewById(R.id.edit_course_termname);
        new Thread(()-> Methods.refreshAndDeleteNotReferredClassInfo(
                MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                MyApp.getCurrentAppDB().goToClassDao(),
                MyApp.getCurrentAppDB().classInfoDao()
        )).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                go_back();
                break;
            case R.id.menu_edit_course_save:
                save(snack_bar_root_view);
                break;
        }
        return true;
    }

    public static void start(Context c, boolean add, @NonNull CourseCardData courseCardData){
        Intent intent = new Intent(c, EditCourse.class);
        intent.putExtra(EXTRA_IF_ADD, add);
        intent.putExtra(EXTRA_COURSE_CARD_DATA, MyApp.gson.toJson(courseCardData));
        c.startActivity(intent);
    }

    private void initContentView(){
        View content = getLayoutInflater().inflate(R.layout.activity_edit_course, null);
        add_start = getIntent().getBooleanExtra(EXTRA_IF_ADD, false);
        intent_extra_CourseCardData = MyApp.gson.fromJson(getIntent().getStringExtra(EXTRA_COURSE_CARD_DATA), CourseCardData.class);
        ((EditText)content.findViewById(R.id.edit_course_termname)).setText(intent_extra_CourseCardData.getTermname());
        ((EditText)content.findViewById(R.id.edit_course_weekday)).setText(intent_extra_CourseCardData.getWeekday()+"");
        ((EditText)content.findViewById(R.id.edit_course_time)).setText(intent_extra_CourseCardData.getTime_id());
        if (!intent_extra_CourseCardData.getCards().isEmpty()){ // if have a card, fill blank with the data on the card
            ACard card = intent_extra_CourseCardData.getCards().get(0);
            ((EditText)content.findViewById(R.id.edit_course_startweek)).setText(card.getStart_week()+"");
            ((EditText)content.findViewById(R.id.edit_course_endweek)).setText(card.getEnd_week()+"");
            ((EditText)content.findViewById(R.id.edit_course_cno)).setText(card.getCno());
            ((EditText)content.findViewById(R.id.edit_course_cname)).setText(card.getCname());
            ((EditText)content.findViewById(R.id.edit_course_tname)).setText(card.getTname());
            ((EditText)content.findViewById(R.id.edit_course_croom)).setText(card.getCroom());
            ((EditText)content.findViewById(R.id.edit_course_system_comment)).setText(card.getSys_comm());
            ((EditText)content.findViewById(R.id.edit_course_my_comment)).setText(card.getMy_comm());
            ((EditText)content.findViewById(R.id.edit_course_grade_point)).setText(card.getGrade_point()+"");
            ((EditText)content.findViewById(R.id.edit_course_ctype)).setText(card.getCtype());
            ((EditText)content.findViewById(R.id.edit_course_examt)).setText(card.getExamt());
        }
        if (add_start) {
            // enable customized fields
            for (int id : et_ids_add_yes){
                content.findViewById(id).setEnabled(true);
            }
            // set title
            ((CollapsingToolbarLayout)content.findViewById(R.id.edit_course_toolbar_layout)).setTitle(getString(R.string.title_activity_edit_course_add));
        }else {
            // set title
            ((CollapsingToolbarLayout)content.findViewById(R.id.edit_course_toolbar_layout)).setTitle(getString(R.string.title_activity_edit_course_edit));
        }
        setContentView(content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private int int_check(int et_id){
        EditText et = (EditText)findViewById(et_id);
        String wrong_tip = "不正确的格式：" + et.getHint().toString();
        String text = et.getText().toString();
        try {
            return Integer.parseInt(text);
        }catch (Exception e){
            e.printStackTrace();
            Snackbar.make(et, wrong_tip, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            throw new InvalidParameterException();
        }
    }

    private double double_check(int et_id){
        EditText et = (EditText)findViewById(et_id);
        String wrong_tip = "不正确的格式：" + et.getHint().toString();
        String text = et.getText().toString();
        try {
            return Double.parseDouble(text);
        }catch (Exception e){
            e.printStackTrace();
            Snackbar.make(et, wrong_tip, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            throw new InvalidParameterException();
        }
    }

    private String string_empty_check(int et_id){
        EditText et = (EditText)findViewById(et_id);
        String wrong_tip = "不能为空：" + et.getHint().toString();
        String text = et.getText().toString();
        if (!text.isEmpty()){
            return text;
        }else {
            Snackbar.make(et, wrong_tip, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            throw new InvalidParameterException();
        }
    }

    private boolean format_check(){
        try {
            int_check(R.id.edit_course_startweek);
            int_check(R.id.edit_course_endweek);
            int_check(R.id.edit_course_weekday);
            int_check(R.id.edit_course_time);
            double_check(R.id.edit_course_grade_point);
            string_empty_check(R.id.edit_course_cno);
            string_empty_check(R.id.edit_course_cname);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private boolean logic_check(@NonNull View view){
        int sw = int_check(R.id.edit_course_startweek);
        int ew = int_check(R.id.edit_course_endweek);
        int weekday = int_check(R.id.edit_course_weekday);
        int time = int_check(R.id.edit_course_time);
        if (sw <= 0 || ew <= 0 || ew < sw){
            Snackbar.make(view, "不合法的起始周和结束周", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            return false;
        }
        if (weekday < 1 || weekday > 7){
            Snackbar.make(view, "不合法的星期", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            return false;
        }
        if (time < 1 || time > MyApp.times.length){
            Snackbar.make(view, "不合法的节次（大节数）", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            return false;
        }
        return true;
    }

    private boolean check(@NonNull View view){
        return format_check() && logic_check(view);
    }

    public void save(@NonNull View view) {
        final String NAME = "save()";
        clearFocus();
        new Thread(() -> {
            boolean check_res = check(view);
            LogMe.e(NAME, "check res: " + check_res);
            if (check_res){
                String ac_username = MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username;
                if (add_start){
                    // ----------------------- class info
                    String cno = ((EditText)findViewById(R.id.edit_course_cno)).getText().toString();
                    String cname = ((EditText)findViewById(R.id.edit_course_cname)).getText().toString();
                    String teacher_name = ((EditText)findViewById(R.id.edit_course_tname)).getText().toString();
                    double grade_point = Double.parseDouble(((EditText)findViewById(R.id.edit_course_grade_point)).getText().toString());
                    String ctype = ((EditText)findViewById(R.id.edit_course_ctype)).getText().toString();
                    String examt = ((EditText)findViewById(R.id.edit_course_examt)).getText().toString();
                    // ----------------------- go to class
                    String term = intent_extra_CourseCardData.getTerm();
                    String sys_comment = "";
                    int startweek = int_check(R.id.edit_course_startweek);
                    int endweek = int_check(R.id.edit_course_endweek);
                    int weekday = int_check(R.id.edit_course_weekday);
                    String seq = int_check(R.id.edit_course_time)+"";
                    String croom = ((EditText)findViewById(R.id.edit_course_croom)).getText().toString();
                    String my_comment = ((EditText)findViewById(R.id.edit_course_my_comment)).getText().toString();
                    // -----------------------------------------
                    List<ClassInfo> from_data_base_list = MyApp.getCurrentAppDB().classInfoDao().selectOne(ac_username, cno);
                    if (!from_data_base_list.isEmpty()) {
                        ClassInfo ci = from_data_base_list.get(0);
                        // ---------------- replace
                        cname = ci.cname;
                        teacher_name = ci.name;
                        grade_point = ci.xf;
                        ctype = ci.tname;
                        examt = ci.examt;
                        // ---------------- final
                        String cname_f = cname;
                        String teacher_name_f = teacher_name;
                        double grade_point_f = grade_point;
                        String ctype_f = ctype;
                        String examt_f = examt;
                        // -----------------------------
                        runOnUiThread(() ->
                                Login.getAlertDialog(
                                        EditCourse.this,
                                        "数据库中已经存在相同课号的课程信息，确定要使用数据库中的已有数据吗？",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new Thread(() -> {
                                                    // ----------------- read from database / customize if not found in database
                                                    String fake_ctype = ci.ctype;
                                                    String dpt_name = ci.dptname;
                                                    String dpt_no = ci.dptno;
                                                    String sp_name = ci.spname;
                                                    String sp_no = ci.spno;
                                                    String grade = ci.grade;
                                                    String teacherno = ci.teacherno;
                                                    String courseid = ci.courseid;
                                                    long maxcnt = ci.maxcnt;
                                                    double llxs = ci.llxs;
                                                    double syxs = ci.syxs;
                                                    double sjxs = ci.sjxs;
                                                    double qtxs = ci.qtxs;
                                                    long sctcnt = ci.sctcnt;
                                                    int custom_ref = ci.custom_ref + 1;
                                                    // ----------------------------------------------
                                                    MyApp.getCurrentAppDB().goToClassDao().insert(new GoToClass(
                                                            ac_username, term, weekday, seq, cno, startweek,
                                                            endweek, false, 0, croom, 0,
                                                            sys_comment, my_comment, true
                                                    ));
                                                    MyApp.getCurrentAppDB().classInfoDao().insert(new ClassInfo(
                                                            ac_username, cno, fake_ctype, ctype_f, examt_f,
                                                            dpt_name, dpt_no, sp_name, sp_no, grade,
                                                            cname_f, teacherno, teacher_name_f, courseid,
                                                            maxcnt, grade_point_f, llxs, syxs, sjxs,
                                                            qtxs, sctcnt, custom_ref
                                                    ));
                                                    MyApp.getCurrentAppDB().myCommentDao().insert(new MyComment(
                                                            MyApp.gson.toJson(new GoToClassKey(
                                                                    ac_username, term, weekday, seq,
                                                                    cno, startweek, endweek, false
                                                            )),
                                                            my_comment
                                                    ));
                                                    runOnUiThread(() -> {
                                                        Toast.makeText(EditCourse.this, "保存成功", Toast.LENGTH_SHORT).show();
                                                        go_back();
                                                    });
                                                }).start();
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        },
                                        null,
                                        "重复的数据", "确定", "我再想想"
                                ).show());
                    }else {
                        // ----------------- read from database / customize if not found in database
                        String fake_ctype = "";
                        String dpt_name = "";
                        String dpt_no = "";
                        String sp_name = "";
                        String sp_no = "";
                        String grade = "";
                        String teacherno = "";
                        String courseid = "";
                        long maxcnt = 0;
                        double llxs = 0;
                        double syxs = 0;
                        double sjxs = 0;
                        double qtxs = 0;
                        long sctcnt = 0;
                        int custom_ref = 1;
                        // ----------------------------------------------
                        MyApp.getCurrentAppDB().goToClassDao().insert(new GoToClass(
                                ac_username, term, weekday, seq, cno, startweek,
                                endweek, false, 0, croom, 0,
                                sys_comment, my_comment, true
                        ));
                        MyApp.getCurrentAppDB().classInfoDao().insert(new ClassInfo(
                                ac_username, cno, fake_ctype, ctype, examt,
                                dpt_name, dpt_no, sp_name, sp_no, grade,
                                cname, teacherno, teacher_name, courseid,
                                maxcnt, grade_point, llxs, syxs, sjxs,
                                qtxs, sctcnt, custom_ref
                        ));
                        MyApp.getCurrentAppDB().myCommentDao().insert(new MyComment(
                                MyApp.gson.toJson(new GoToClassKey(
                                        ac_username, term, weekday, seq, cno, startweek,
                                        endweek, false
                                )),
                                my_comment
                        ));
                        runOnUiThread(() -> {
                            Toast.makeText(EditCourse.this, "保存成功", Toast.LENGTH_SHORT).show();
                            go_back();
                        });
                    }
                }else {
                    MyApp.getCurrentAppDB().goToClassDao().setMyComment(
                            ac_username,
                            intent_extra_CourseCardData.getTerm(),
                            intent_extra_CourseCardData.getWeekday(),
                            intent_extra_CourseCardData.getTime_id(),
                            intent_extra_CourseCardData.getCards().get(0).getCno(),
                            intent_extra_CourseCardData.getCards().get(0).getStart_week(),
                            intent_extra_CourseCardData.getCards().get(0).getEnd_week(),
                            intent_extra_CourseCardData.getCards().get(0).isOdd_week(),
                            ((EditText)findViewById(R.id.edit_course_my_comment)).getText().toString()
                    );
                    MyApp.getCurrentAppDB().myCommentDao().insert(new MyComment(
                            MyApp.gson.toJson(new GoToClassKey(
                                    ac_username,
                                    intent_extra_CourseCardData.getTerm(),
                                    intent_extra_CourseCardData.getWeekday(),
                                    intent_extra_CourseCardData.getTime_id(),
                                    intent_extra_CourseCardData.getCards().get(0).getCno(),
                                    intent_extra_CourseCardData.getCards().get(0).getStart_week(),
                                    intent_extra_CourseCardData.getCards().get(0).getEnd_week(),
                                    intent_extra_CourseCardData.getCards().get(0).isOdd_week()
                            )),
                            ((EditText)findViewById(R.id.edit_course_my_comment)).getText().toString()
                    ));
                    Snackbar.make(view, "保存成功", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                }
            }
        }).start();
    }

    private void clearFocus(){
        runOnUiThread(()->{
            // Check if no view has focus:
            View view = EditCourse.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            findViewById(R.id.edit_course_background_of_all_input).clearFocus();
        });
    }

    private void go_back(){
        new Thread(()->{
            List<ShowTableNode> nodes = MyApp.getCurrentAppDB().goToClassDao().getNode(
                    MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                    intent_extra_CourseCardData.getTerm(),
                    intent_extra_CourseCardData.getWeek(),
                    intent_extra_CourseCardData.getWeekday(),
                    intent_extra_CourseCardData.getTime_id()
            );
            CourseCardData data_to_go_back;
            try {
                data_to_go_back = intent_extra_CourseCardData.deepClone();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            data_to_go_back.setCards(new LinkedList<>());
            for (ShowTableNode node : nodes){
                data_to_go_back.getCards().add(new ACard(
                        (node.courseno == null)?(""):(node.courseno),
                        (node.cname == null)?(""):(node.cname),
                        (int)node.start_week,
                        (int)node.end_week,
                        (node.name == null)?(""):(node.name),
                        (node.tno == null)?(""):(node.tno),
                        (node.croomno == null)?(""):(node.croomno),
                        node.grade_point,
                        (node.ctype == null)?(""):(node.ctype),
                        (node.examt == null)?(""):(node.examt),
                        (node.sys_comm == null)?(""):(node.sys_comm),
                        (node.my_comm == null)?(""):(node.my_comm),
                        node.oddweek,
                        node.customized
                ));
            }
            CourseCard.startMe(EditCourse.this, data_to_go_back);
        }).start();
    }
}
