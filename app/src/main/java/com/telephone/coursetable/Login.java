package com.telephone.coursetable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.Locate;
import com.telephone.coursetable.Database.AppDatabase;
import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GraduationScore;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Gson.Hour;
import com.telephone.coursetable.Gson.Hours;
import com.telephone.coursetable.Gson.LoginResponse;
import com.telephone.coursetable.Gson.Person;
import com.telephone.coursetable.Gson.PersonInfo;
import com.telephone.coursetable.Gson.StudentInfo;
import com.telephone.coursetable.Gson.Table;
import com.telephone.coursetable.Gson.TableNode;
import com.telephone.coursetable.Gson.Term;
import com.telephone.coursetable.Gson.Terms;
import com.telephone.coursetable.Gson.ValidScoreQueryS;
import com.telephone.coursetable.Gson.ValidScoreQuery_Data;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Http.Post;
import com.telephone.coursetable.OCR.OCR;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Login extends AppCompatActivity {

    private boolean updating = false;

    private StringBuilder cookie_builder = null;

    //DAOs of the whole app's database instance
    private GoToClassDao gdao = null;
    private ClassInfoDao cdao = null;
    private TermInfoDao tdao = null;
    private UserDao  udao = null;
    private PersonInfoDao pdao = null;
    private GraduationScoreDao gsdao = null;

    /**
     * @ui
     * 1. clear old input, old image, old cookie
     * 2. get check-code, if success:
     *      1. update image, cookie
     *      2. auto recognize the image and fill in the check-code input box
     * @clear
     */
    public void changeCode(View view){
        final String NAME = "changeCode()";
        EditText et = (EditText)findViewById(R.id.checkcode_input);
        ImageView im = (ImageView)findViewById(R.id.imageView_checkcode);
        //clear old image
        im.setImageDrawable(getResources().getDrawable(R.drawable.network, getTheme()));
        //clear old input
        et.setText("");
        //clear old cookie
        cookie_builder = new StringBuilder();
        //set the new one
        new Thread(() -> {
            //get
            HttpConnectionAndCode res = LAN.checkcode(Login.this);
            Log.e(NAME + " " + "the code of get check code res", res.code+"");
            //if success, set
            if (res.obj != null){
                String ocr = OCR.getTextFromBitmap(Login.this, (Bitmap)res.obj, "telephone");
                cookie_builder.append(res.cookie);
                runOnUiThread(() -> {
                    im.setImageBitmap((Bitmap) (res.obj));
                    et.setText(ocr);
                });
            }
        }).start();
    }

    /**
     * @non-ui
     * 1. get all user names in the database
     * 2. make an ArrayAdapter with these user names, set it as the adapter of sid input box
     * 3. set OnDismissListener of sid input box:
     *      1. get the user with the sid in the sid input box in the database, if exist:
     *          1. fill its password in the password input box
     * @clear
     */
    private void updateUserNameAutoFill(){
        final ArrayAdapter<String> ada = new ArrayAdapter<>(Login.this, android.R.layout.simple_dropdown_item_1line, udao.selectAllUserName());
        runOnUiThread(() -> {
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setAdapter(ada);
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setOnDismissListener(() -> new Thread(() -> {
                final List<User> userSelected = udao.selectUser(((AutoCompleteTextView) findViewById(R.id.sid_input)).getText().toString());
                if (!userSelected.isEmpty()) {
                    runOnUiThread(() -> ((AutoCompleteTextView) findViewById(R.id.passwd_input)).setText(userSelected.get(0).password));
                }
            }).start());
        });
    }

    /**
     * @ui/non-ui
     * 1. build an AlertDialog in this activity:
     *      - Message : m(if not null)
     *      - PositiveButtonOnClickListener : yes
     *      - NegativeButtonOnClickListener : no
     *      - View : view(if not null)
     *      - Title : title(if not null)
     * 2. return this AlertDialog
     * @clear
     */
    private AlertDialog getAlertDialog(@Nullable final String m, @NonNull DialogInterface.OnClickListener yes, @NonNull DialogInterface.OnClickListener no, @Nullable View view, @Nullable String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (m != null) {
            builder.setMessage(m);
        }
        builder.setPositiveButton(getResources().getString(R.string.ok_btn_text_zhcn), yes)
                .setNegativeButton(getResources().getString(R.string.deny_btn_zhcn), no);
        if (view != null){
            builder.setView(view);
        }
        if (title != null){
            builder.setTitle(title);
        }
        return builder.create();
    }

    /**
     * @non-ui
     * 1. try to login credit system with:
     *      - student id : sid
     *      - password : pwd
     *      - check-code : ckcode
     *      - Cookie : cookie
     * 2. if login res's comment is not null and not empty, login res's comment will be replaced with msg
     * 3. if login success and builder is not null, append the server-set cookie to builder
     * 4. return login res
     * @return
     * - res.code == 0 : login success
     * - res.code != 0 : login fail
     * @clear
     */
    public static HttpConnectionAndCode login(Context c, String sid, String pwd, String ckcode, String cookie, @Nullable StringBuilder builder) {
        final String NAME = "login()";
        Resources r = c.getResources();
        String body = "us=" + sid + "&pwd=" + pwd + "&ck=" + ckcode;
        HttpConnectionAndCode login_res = Post.post(
                r.getString(R.string.lan_login_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_login_referer),
                body,
                cookie,
                "}",
                r.getString(R.string.cookie_delimiter),
                r.getString(R.string.lan_login_success_contain_response_text),
                null,
                null
        );
        if (login_res.comment != null && !login_res.comment.isEmpty()) {
            LoginResponse response = new Gson().fromJson(login_res.comment, LoginResponse.class);
            login_res.comment = response.getMsg();
        }
        if (login_res.code == 0 && builder != null) {
            if (!builder.toString().isEmpty()) {
                builder.append(r.getString(R.string.cookie_delimiter));
            }
            builder.append(login_res.cookie);
        }
        Log.e(NAME, "body: " + body + " code: " + login_res.code + " resp_code: " + login_res.resp_code + " comment/msg: " + login_res.comment);
        return login_res;
    }

    /**
     * @non-ui
     * 1. call and return {@link Login_vpn#vpn_login(Context, String, String)}
     * @return {@link Login_vpn#vpn_login(Context, String, String)}
     * @clear
     */
    public static String vpn_login_test(Context c, final String sid, final String pwd){
        return Login_vpn.vpn_login(c, sid, pwd);
    }

    /**
     * @non-ui
     * 1. try to login aaw with:
     *      - student id : sid
     *      - password : pwd
     * 2. change the code of login res to -6 if res.code == 0 but res.resp_code != 302
     * 3. return login res
     * @apiNote if login success, you can get cookie by res.cookie
     * @return
     * - res.code == 0 : login success
     * - res.code != 0 : login fail
     * @clear
     */
    public static HttpConnectionAndCode outside_login_test(Context c, final String sid, final String pwd){
        final String NAME = "outside_login_test()";
        Resources r = c.getResources();
        String body = "username=" + sid + "&passwd=" + pwd + "&login=%B5%C7%A1%A1%C2%BC";
        Log.e(NAME + " " + "body", body);
        HttpConnectionAndCode login_res = Post.post(
                r.getString(R.string.lan_outside_login_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_outside_login_referer),
                body,
                null,
                null,
                r.getString(R.string.cookie_delimiter),
                null,
                null,
                false
        );
        if (login_res.code == 0 && login_res.resp_code == 302){
            Log.e(NAME + " " + "login status", "success");
        }else {
            if (login_res.code == 0){
                login_res.code = -6;
            }
            Log.e(NAME + " " + "login status", "fail" + " code: " + login_res.code);
        }
        return login_res;
    }

    /**
     * @ui
     * 1. set content view
     * 2. enable buttons and check-code-image-view, hide progressbar, set focus to sid input box
     * 3. call {@link #changeCode(View)}
     * 4. call {@link #updateUserNameAutoFill()}
     * 5. get activated user from database, if exist:
     *      1. fill its username in sid input box
     *      2. fill its password in password input box
     *      3. set focus to check-code input box
     *      4. clear focus of check-code input box
     * @clear
     */
    private void initContentView(){
        setContentView(R.layout.activity_login);
        ((Button)findViewById(R.id.button)).setEnabled(true);
        ((Button)findViewById(R.id.button2)).setEnabled(true);
        ((ImageView)findViewById(R.id.imageView_checkcode)).setEnabled(true);
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
        ((AutoCompleteTextView)findViewById(R.id.sid_input)).requestFocus();
        changeCode(null);
        new Thread((Runnable) () -> {
            updateUserNameAutoFill();
            //if any user is activated, fill his sid and pwd in the input box
            List<User> ac_user = udao.getActivatedUser();
            if (!ac_user.isEmpty()){
                final User u = ac_user.get(0);
                runOnUiThread((Runnable) () -> {
                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText(u.username);
                    ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText(u.password);
                    ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).requestFocus();
                    ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).clearFocus();
                });
            }
        }).start();
    }

    /**
     * @ui
     * 1. super onCreate()
     * 2. initialize DAOs
     * 3. call {@link #initContentView()}
     * @clear
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = MyApp.getCurrentAppDB();
        gdao = db.goToClassDao();
        cdao = db.classInfoDao();
        tdao = db.termInfoDao();
        udao = db.userDao();
        pdao = db.personInfoDao();
        gsdao = db.graduationScoreDao();
        initContentView();
    }

    /**
     * @ui
     * 1. start {@link MainActivity}
     * @clear
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * @ui
     * 1. get the username in the sid input box
     * 2. show an AlertDialog to warn user:
     *      - if press yes, a new thread will be started:
     *          1. try to delete the user from the database with the username in the sid input box
     *          2. call {@link #updateUserNameAutoFill()}
     *          3. clear sid input box and password input box
     *          4. set focus to sid input box
     *          5. call {@link #changeCode(View)}
     *      - if press no, nothing will happen
     * @clear
     */
    public void deleteUser(View view){
        final String NAME = "deleteUser()";
        String sid = ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString();
        getAlertDialog("确定要取消记住用户" + " " + sid + " " + "的登录信息吗？",
                (DialogInterface.OnClickListener) (dialogInterface, i) -> new Thread((Runnable) () -> {
                    udao.deleteUser(sid);
                    Log.e(NAME + " " + "user deleted", sid);
                    updateUserNameAutoFill();
                    runOnUiThread((Runnable) () -> {
                        ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText("");
                        ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText("");
                        ((AutoCompleteTextView)findViewById(R.id.sid_input)).requestFocus();
                        changeCode(null);
                    });
                }).start(),
                (DialogInterface.OnClickListener) (dialogInterface, i) -> {},
                null, null).show();
    }

    /**
     * when user clicks the login btn, read the information in all the input boxes, then create a new
     * thread to do these things:
     *  1. use the information inputted and the cookie "cookie_builder" to login by login().
     *  2. if fail due to ck, toast @toast_login_fail_ck, clear the ck input box, stop here.
     *  3. if fail due to pwd, toast @toast_login_fail_pwd, clear the pwd input box, stop here.
     *  4. if fail due to other reason, toast @toast_login_fail + " - " + login_res.code, stop here.
     *  5. if success, toast @toast_login_success, and continue......
     */
    public void login_thread(final View view){
        final String sid = ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString();
        final String pwd = ((AutoCompleteTextView)findViewById(R.id.passwd_input)).getText().toString();
        final String ck = ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).getText().toString();
        final String cookie_before_login = cookie_builder.toString();
        View extra_pwd_dialog_layout = getLayoutInflater().inflate(R.layout.extra_password, null);
        AlertDialog extra_pwd_dialog = getAlertDialog("",
                (DialogInterface.OnClickListener) (dialogInterface, i) -> {
                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).clearFocus();
                    ((AutoCompleteTextView)findViewById(R.id.passwd_input)).clearFocus();
                    ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).clearFocus();
                    ((Button)findViewById(R.id.button)).setEnabled(false);
                    ((Button)findViewById(R.id.button2)).setEnabled(false);
                    ((ImageView)findViewById(R.id.imageView_checkcode)).setEnabled(false);
                    ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                    final String aaw_pwd = ((EditText)extra_pwd_dialog_layout.findViewById(R.id.aaw_passwd_input)).getText().toString();
                    final String vpn_pwd = ((EditText)extra_pwd_dialog_layout.findViewById(R.id.vpn_passwd_input)).getText().toString();
                    new Thread((Runnable) () -> {
                        HttpConnectionAndCode login_res = login(Login.this, sid, pwd, ck, cookie_before_login, cookie_builder);
                        if (login_res.code != 0){
                            String toast;
                            if (login_res.comment != null && login_res.comment.contains("验证码")){
                                toast = getResources().getString(R.string.snackbar_login_fail_ck);
                            }else if (login_res.comment != null && login_res.comment.contains("密码")){
                                toast = getResources().getString(R.string.snackbar_login_fail_pwd);
                            }else {
                                toast = getResources().getString(R.string.snackbar_login_fail) + " : " + login_res.comment + "(" + login_res.code + ")";
                            }
                            final String toast_f = toast;
                            runOnUiThread((Runnable) () -> {
                                Snackbar.make(view, toast_f, BaseTransientBottomBar.LENGTH_SHORT).show();
                                changeCode(null);
                                ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).requestFocus();
                                ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).clearFocus();
                                if (toast_f.equals(getResources().getString(R.string.snackbar_login_fail_pwd))) {
                                    ((AutoCompleteTextView) findViewById(R.id.passwd_input)).setText("");
                                    ((AutoCompleteTextView) findViewById(R.id.passwd_input)).requestFocus();
                                }
                                ((Button)findViewById(R.id.button)).setEnabled(true);
                                ((Button)findViewById(R.id.button2)).setEnabled(true);
                                ((ImageView)findViewById(R.id.imageView_checkcode)).setEnabled(true);
                                ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                            });
                            return;
                        }
                        String vpn_login_res = vpn_login_test(Login.this, sid, vpn_pwd);
                        if (vpn_login_res == null || vpn_login_res.equals(getResources().getString(R.string.vpn_ip_forbidden))){
                            runOnUiThread((Runnable) () -> {
                                if (vpn_login_res != null && vpn_login_res.equals(getResources().getString(R.string.vpn_ip_forbidden))){
                                    Snackbar.make(view, getResources().getString(R.string.snackbar_vpn_test_login_fail_ip), BaseTransientBottomBar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(view, getResources().getString(R.string.snackbar_vpn_test_login_fail), BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                                ((Button)findViewById(R.id.button)).setEnabled(true);
                                ((Button)findViewById(R.id.button2)).setEnabled(true);
                                ((ImageView)findViewById(R.id.imageView_checkcode)).setEnabled(true);
                                ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                            });
                            return;
                        }
                        HttpConnectionAndCode outside_login_res = outside_login_test(Login.this, sid, aaw_pwd);
                        if (outside_login_res.code != 0){
                            runOnUiThread((Runnable) () -> {
                                Snackbar.make(view, getResources().getString(R.string.snackbar_outside_test_login_fail), BaseTransientBottomBar.LENGTH_SHORT).show();
                                ((Button)findViewById(R.id.button)).setEnabled(true);
                                ((Button)findViewById(R.id.button2)).setEnabled(true);
                                ((ImageView)findViewById(R.id.imageView_checkcode)).setEnabled(true);
                                ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                            });
                            return;
                        }
                        String cookie_after_login = cookie_builder.toString();
                        SharedPreferences hours_pref = getSharedPreferences(getResources().getString(R.string.preference_file_name), MODE_PRIVATE);
                        SharedPreferences.Editor editor = hours_pref.edit();
                        udao.insert(new User(sid, pwd, aaw_pwd, vpn_pwd));
                        udao.disableAllUser();
                        editor.clear();
                        updating = true;
                        editor.putBoolean(getResources().getString(R.string.pref_user_updating_key), updating);
                        editor.commit();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                      Toast.makeText(Login.this, getResources().getString(R.string.toast_login_success), Toast.LENGTH_LONG).show();
                                Snackbar.make(view, getResources().getString(R.string.toast_login_success), BaseTransientBottomBar.LENGTH_LONG).show();
                                //make a tip to show data-update status
                                getSupportActionBar().setTitle(getResources().getString(R.string.title_login_updating));
                            }
                        });
                        cdao.deleteAll();
                        gdao.deleteAll();
                        tdao.deleteAll();
                        pdao.deleteAll();
                        gsdao.deleteAll();
                        /**
                         * ******************************* UPDATE DATA START *******************************
                         */
                        //update person info
                        HttpConnectionAndCode getPersonInfo_res = LAN.personInfo(Login.this, cookie_after_login);
                        Log.e("login_thread() get person info", getPersonInfo_res.code+"");
                        HttpConnectionAndCode getStudentInfo_res = LAN.studentInfo(Login.this, cookie_after_login);
                        Log.e("login_thread() get student info", getStudentInfo_res.code+"");
                        //if success, insert data into database
                        if (getPersonInfo_res.code == 0 && getStudentInfo_res.code == 0){
                            Person person = new Gson().fromJson(getPersonInfo_res.comment, Person.class);
                            PersonInfo p = person.getData();
                            StudentInfo studentInfo = new Gson().fromJson(getStudentInfo_res.comment, StudentInfo.class);
                            //extract information and then insert into database
                            pdao.insert(new com.telephone.coursetable.Database.PersonInfo(p.getStid(), p.getGrade(),p.getClassno(),p.getSpno(),p.getName(),p.getName1(),
                                    p.getEngname(),p.getSex(),p.getPass(),p.getDegree(),p.getDirection(),p.getChangetype(),p.getSecspno(),p.getClasstype(),p.getIdcard(),
                                    p.getStype(),p.getXjzt(),p.getChangestate(),p.getLqtype(),p.getZsjj(),p.getNation(),p.getPolitical(),p.getNativeplace(),
                                    p.getBirthday(),p.getEnrolldate(),p.getLeavedate(),p.getDossiercode(),p.getHostel(),p.getHostelphone(),p.getPostcode(),p.getAddress(),
                                    p.getPhoneno(),p.getFamilyheader(),p.getTotal(),p.getChinese(),p.getMaths(),p.getEnglish(),p.getAddscore1(),p.getAddscore2(),p.getComment(),
                                    p.getTestnum(),p.getFmxm1(),p.getFmzjlx1(),p.getFmzjhm1(),p.getFmxm2(),p.getFmzjlx2(),p.getFmzjhm2(),p.getDs(),p.getXq(),p.getRxfs(),p.getOldno(),
                                    studentInfo.getDptno(), studentInfo.getDptname(), studentInfo.getSpname()));
                        }
                        //update graduation score
                        HttpConnectionAndCode getGraduationScore_res = LAN.graduationScore(Login.this, cookie_after_login);
                        Log.e("login_thread() get graduation score", getGraduationScore_res.code+"");
                        //if success, insert data into database
                        if (getGraduationScore_res.code == 0){
                            ValidScoreQueryS vs = new Gson().fromJson(getGraduationScore_res.comment, ValidScoreQueryS.class);
                            List<ValidScoreQuery_Data> vd_list = vs.getData();
                            for (ValidScoreQuery_Data vd : vd_list){
                                //extract information and then insert into database
                                gsdao.insert(new GraduationScore(vd.getName(), vd.getCname(), vd.getEngname(), vd.getEngcj(), vd.getTname(), vd.getStid(),
                                        vd.getTerm(), vd.getCourseid(), vd.getPlanxf(), vd.getCredithour(), vd.getCoursetype(), vd.getLvl(), vd.getSterm(),
                                        vd.getCourseno(), vd.getScid(), vd.getScname(), vd.getScore(), vd.getZpxs(), vd.getXf(), vd.getStp()));
                            }
                        }
                        //update terms info
                        HttpConnectionAndCode getTerms_res = LAN.terms(Login.this, cookie_after_login);
                        Log.e("login_thread() get terms", getTerms_res.code+"");
                        //if success, insert data into database
                        if (getTerms_res.code == 0){
                            Terms terms = new Gson().fromJson(getTerms_res.comment, Terms.class);
                            List<Term> term_list = terms.getData();
                            for (Term t : term_list){
                                //extract information and then insert into database
                                DateTimeFormatter server_formatter = DateTimeFormatter.ofPattern(getResources().getString(R.string.server_terminfo_datetime_format));
                                DateTimeFormatter ts_formatter = DateTimeFormatter.ofPattern(getResources().getString(R.string.ts_datetime_format));
                                String sts_string = LocalDateTime.parse(t.getStartdate(), server_formatter).format(ts_formatter);
                                String ets_string = LocalDateTime.parse(t.getEnddate(), server_formatter).format(ts_formatter);
                                long sts = Timestamp.valueOf(sts_string).getTime();
                                long ets = Timestamp.valueOf(ets_string).getTime();
                                tdao.insert(new TermInfo(t.getTerm(), t.getStartdate(), t.getEnddate(), t.getWeeknum(), t.getTermname(), t.getSchoolyear(), t.getComm(), sts, ets));
                            }
                        }
                        /*
                        for each term stored in the database, try to get table for it. if the data list in
                        the response is not empty, extract information and then insert into "GoToClass" and
                        "ClassInfo"
                         */
                        List<TermInfo> term_info_list = tdao.selectAll();
                        String sterm = pdao.getGrade().get(0).toString();
                        for (TermInfo t : term_info_list){
                            //do not get table for before-enroll terms, remove them from database
                            if (t.term.substring(0, 4).compareTo(sterm) < 0) {
                                tdao.deleteTerm(t.term);
                                continue;
                            }
                            HttpConnectionAndCode getTable_res = LAN.table(Login.this, cookie_after_login, t.term);
                            Log.e("login_thread() get table " + t.term, getTable_res.code+"");
                            //if success, insert data into database
                            if (getTable_res.code == 0){
                                Table table = new Gson().fromJson(getTable_res.comment, Table.class);
                                List<TableNode> table_node_list = table.getData();
                                for (TableNode table_node : table_node_list){
                                    //extract information and then insert into "GoToClass"
                                    gdao.insert(new GoToClass(table_node.getTerm(), table_node.getWeek(), table_node.getSeq(), table_node.getCourseno(), table_node.getId(), table_node.getCroomno(), table_node.getStartweek(), table_node.getEndweek(), table_node.isOddweek(), table_node.getHours()));
                                    //extract information and then insert into "ClassInfo"
                                    cdao.insert(new ClassInfo(table_node.getCourseno(), table_node.getCtype(), table_node.getTname(), table_node.getExamt(), table_node.getDptname(), table_node.getDptno(), table_node.getSpname(), table_node.getSpno(), table_node.getGrade(), table_node.getCname(), table_node.getTeacherno(), table_node.getName(),
                                            table_node.getCourseid(), table_node.getComm(), table_node.getMaxcnt(), table_node.getXf(), table_node.getLlxs(), table_node.getSyxs(), table_node.getSjxs(), table_node.getQtxs(), table_node.getSctcnt()));
                                }
                            }
                        }
                        //update hours info
                        HttpConnectionAndCode getHours_res = LAN.hours(Login.this, cookie_after_login);
                        Log.e("login_thread() get hours", getHours_res.code+"");
                        //if success, insert data into SharedPreferences
                        if (getHours_res.code == 0){
                            Hours hours = new Gson().fromJson(getHours_res.comment, Hours.class);
                            List<Hour> hour_list = hours.getData();
                            for (Hour h : hour_list){
                                String memo = h.getMemo();
                                if (memo == null){
                                    continue;
                                }
                                String des = h.getNodename();
                                int index = memo.indexOf('-');
                                String stime = memo.substring(0, index);
                                String etime = memo.substring(index + 1);
                                editor.putString(h.getNodeno() + getResources().getString(R.string.pref_time_start_suffix), stime);
                                editor.putString(h.getNodeno() + getResources().getString(R.string.pref_time_end_suffix), etime);
                                editor.putString(h.getNodeno() + getResources().getString(R.string.pref_time_des_suffix), des);
                                editor.putString(h.getNodeno() + getResources().getString(R.string.pref_time_start_backup_suffix), stime);
                                editor.putString(h.getNodeno() + getResources().getString(R.string.pref_time_end_backup_suffix), etime);
                                editor.putString(h.getNodeno() + getResources().getString(R.string.pref_time_des_backup_suffix), des);
                            }
                            editor.commit();
                        }
                        //locate today
                        long nts = Clock.nowTimeStamp();
                        DateTimeFormatter server_hours_time_formatter = DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format));
                        Locate locate_res = Clock.locateNow(nts, tdao, hours_pref, MyApp.times, server_hours_time_formatter,
                                getResources().getString(R.string.pref_time_start_suffix),
                                getResources().getString(R.string.pref_time_end_suffix),
                                getResources().getString(R.string.pref_time_des_suffix));
                        if (locate_res.term != null){
                            Log.e("login_thread() which term", locate_res.term.term + " | " + locate_res.term.termname);
                        }else{
                            Log.e("login_thread() which term", getResources().getString(R.string.term_vacation));
                        }
                        Log.e("login_thread() which week", locate_res.week + "");
                        Log.e("login_thread() which weekday", locate_res.weekday + "");
                        Log.e("login_thread() which month", locate_res.month + "");
                        Log.e("login_thread() which day", locate_res.day + "");
                        Log.e("login_thread() which time", locate_res.time + " | " + locate_res.time_description);
                        Log.e("login_thread() now timestamp", nts + "");
                        /**
                         * ******************************** UPDATE DATA END ********************************
                         */
                        editor.commit();
                        udao.activateUser(sid);
                        updating = false;
                        editor.putBoolean(getResources().getString(R.string.pref_user_updating_key), updating);
                        editor.commit();
                        com.telephone.coursetable.Database.PersonInfo acuser = pdao.selectAll().get(0);
                        Log.e("login_thread() user activated", acuser.stid + " " + acuser.name);
                        runOnUiThread((Runnable) () -> {
                            ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, getResources().getString(R.string.toast_update_success), Toast.LENGTH_SHORT).show();
                            //make a tip to show data-update status
                            getSupportActionBar().setTitle(getResources().getString(R.string.title_login_updated));
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        });
                    }).start();
                },
                (DialogInterface.OnClickListener) (dialogInterface, i) -> {},
                extra_pwd_dialog_layout,
                getResources().getString(R.string.extra_password_title));
        new Thread(() -> {
            List<User> u = udao.selectUser(sid);
            String aaw_pwd = "";
            String vpn_pwd = "";
            if (!u.isEmpty()){
                aaw_pwd = u.get(0).aaw_password;
                vpn_pwd = u.get(0).vpn_password;
            }
            final String aaw_pwdf = aaw_pwd;
            final String vpn_pwdf = vpn_pwd;
            runOnUiThread(() -> {
                ((EditText)extra_pwd_dialog_layout.findViewById(R.id.aaw_passwd_input)).setText(aaw_pwdf);
                ((EditText)extra_pwd_dialog_layout.findViewById(R.id.vpn_passwd_input)).setText(vpn_pwdf);
                extra_pwd_dialog.show();
            });
        }).start();
    }
}