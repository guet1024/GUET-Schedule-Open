package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.telephone.coursetable.Database.AppDatabase;
import com.telephone.coursetable.Database.CETDao;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GradesDao;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.LABDao;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.LogMe.LogMe;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    //DAOs of the database of the whole app
    private GoToClassDao gdao = null;
    private ClassInfoDao cdao = null;
    private TermInfoDao tdao = null;
    private UserDao udao = null;
    private PersonInfoDao pdao = null;
    private GraduationScoreDao gsdao = null;
    private GradesDao grdao = null;
    private ExamInfoDao edao = null;
    private CETDao cetDao = null;
    private LABDao labDao = null;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    private EditText editText;
    private TextView textView;
    private Button button1;
    private Button button2;
    private Button buttonClearOutput;
    private Button buttonClearInput;
    private ScrollView scrollView;

    private boolean fetch_lan_if_true = true;

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
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.TEST);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_test);

        AppDatabase db = MyApp.getCurrentAppDB();

        gdao = db.goToClassDao();
        cdao = db.classInfoDao();
        tdao = db.termInfoDao();
        udao = db.userDao();
        pdao = db.personInfoDao();
        gsdao = db.graduationScoreDao();
        grdao = db.gradesDao();
        edao = db.examInfoDao();
        cetDao = db.cetDao();
        labDao = db.labDao();

        pref = MyApp.getCurrentSharedPreference();
        editor = pref.edit();

        editText = findViewById(R.id.test_activity_et);
        textView = findViewById(R.id.test_activity_tv);
        button1 = findViewById(R.id.test_activity_btn1);
        button2 = findViewById(R.id.test_activity_btn2);
        buttonClearOutput = findViewById(R.id.test_activity_btn_clear_output);
        buttonClearInput = findViewById(R.id.test_activity_btn_clear_input);
        scrollView = findViewById(R.id.test_activity_sv);

        LogMe.LogRunnable logRunnable = new LogMe.LogRunnable() {
            @Override
            public void log(String tag, String msg) {
                print(tag + ": " + msg);
            }
        };

        LogMe.setAll(logRunnable);

        test_button2(null);
    }

    public void test_button1(View view){
        final String NAME = "test_button1()";
        String input = editText.getText().toString();
        print("输入：\n" + input + "\n================================");
        new Thread(()->{
            List<User> activatedUsers = udao.getActivatedUser();
            User current_user = null;
            String name = "";
            if (!activatedUsers.isEmpty()) {
                current_user = udao.getActivatedUser().get(0);
                name = pdao.selectAll().get(0).name;
                print("当前用户：" + current_user.username + " " + name);
            }else {
                print("当前用户：无");
            }
            udao.disableAllUser();
            print("已取消激活所有用户");
            Login.deleteOldDataFromDatabase(gdao, cdao, tdao, pdao, gsdao, grdao, edao, cetDao, labDao);
            print("数据库已清空（除用户数据库外）");
            print("拉取数据中...");
            if (fetch_merge(TestActivity.this, input, pdao, tdao, gdao, cdao, gsdao, editor, grdao, edao, cetDao, labDao)){
                print("拉取成功");
                if (current_user != null) {
                    udao.activateUser(current_user.username);
                    print("激活用户：" + current_user.username + " " + name);
                }
            }else {
                print("拉取失败");
            }
        }).start();
    }

    public void test_button2(View view){
        final String NAME = "test_button2()";
        fetch_lan_if_true = !fetch_lan_if_true;
        if (fetch_lan_if_true){
            button1.setText("拉取数据（内网）");
            button2.setText("内网（点击切换）");
        }else {
            button1.setText("拉取数据（外网）");
            button2.setText("外网（点击切换）");
        }
    }

    private boolean fetch_merge(Context c, String cookie, PersonInfoDao pdao, TermInfoDao tdao, GoToClassDao gdao, ClassInfoDao cdao, GraduationScoreDao gsdao, SharedPreferences.Editor editor, GradesDao grdao, ExamInfoDao edao, CETDao cetDao, LABDao labDao){
        if (fetch_lan_if_true){
            return Login.fetch_merge(c, cookie, pdao, tdao, gdao, cdao, gsdao, editor, grdao, edao, cetDao, labDao);
        }else {
            return Login_vpn.fetch_merge(c, cookie, pdao, tdao, gdao, cdao, gsdao, grdao, edao, cetDao, labDao, editor);
        }
    }

    public void clearOutput(View view){
        final String NAME = "clearOutput()";
        textView.setText("");
    }

    public void clearInput(View view){
        final String NAME = "clearInput()";
        editText.setText("");
    }

    public void print(String text){
        runOnUiThread(()->{
            textView.setText(textView.getText() + text + "\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}