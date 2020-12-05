package com.telephone.coursetable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.telephone.coursetable.Database.CETDao;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GradesDao;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.Key.GoToClassKey;
import com.telephone.coursetable.Database.KeyAndValue.GoToClassKeyAndValue;
import com.telephone.coursetable.Database.LABDao;
import com.telephone.coursetable.Database.Methods.Methods;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Gson.LoginResponse;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Http.Post;
import com.telephone.coursetable.Library.LibraryActivity;
import com.telephone.coursetable.LogMe.LogMe;
import com.telephone.coursetable.Merge.Merge;
import com.telephone.coursetable.MyException.ExceptionIpForbidden;
import com.telephone.coursetable.MyException.ExceptionNetworkError;
import com.telephone.coursetable.MyException.ExceptionUnknown;
import com.telephone.coursetable.MyException.ExceptionWrongUserOrPassword;
import com.telephone.coursetable.OCR.OCR;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class Login extends AppCompatActivity {

    private StringBuilder cookie_builder = null;
    private volatile String help_cookie = null; //the cookie used for help, volatile to maintain multi-thread synchronization
    private String ck_cookie_backup = ""; //store the check code cookie, restore cookie string builder to the check code cookie before each login

    //DAOs of the whole app's database instance
    private GoToClassDao gdao = null;
    private ClassInfoDao cdao = null;
    private TermInfoDao tdao = null;
    private UserDao  udao = null;
    private PersonInfoDao pdao = null;
    private GraduationScoreDao gsdao = null;
    private GradesDao grdao = null;
    private ExamInfoDao edao = null;
    private CETDao cetDao = null;
    private LABDao labDao = null;
    private HashMap<GoToClassKey, String> my_comment_map = null;

    private boolean isMenuEnabled = true;

    private String aaw_p = "";
    private String vpn_p = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(Login.this, MainActivity.class));
                return true;
            case R.id.login_menu_switch_login_mode:
                startActivity(new Intent(Login.this, Login_vpn.class));
                return true;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.login_menu_switch_login_mode);
        item.setEnabled(isMenuEnabled);
        return true;
    }

    /**
     * @ui
     * 1. clear old input, old image, old cookie
     * 2. get check-code, if success:
     *      1. update image, cookie
     *      2. auto recognize the image and fill in the check-code input box
     *      3. clear focus of the check-code input box
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
        ck_cookie_backup = "";
        //set the new one
        new Thread(() -> {
            //get
            HttpConnectionAndCode res = LAN.checkcode(Login.this);
            com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "the code of get check code res", res.code+"");
            //if success, set
            if (res.obj != null){
                String ocr = OCR.getTextFromBitmap(Login.this, (Bitmap)res.obj, MyApp.ocr_lang_code);
                cookie_builder.append(res.cookie);
                ck_cookie_backup = res.cookie;
                runOnUiThread(() -> {
                    im.setImageBitmap((Bitmap) (res.obj));
                    et.setText(ocr);
                    et.clearFocus();
                });
            }
        }).start();
    }

    /**
     * @non-ui
     * 1. get all user names in the database
     * 2. make an ArrayAdapter with these user names, set it as the adapter of sid input box
     * 3. set OnDismissListener of sid input box:
     *      1. call {@link #clearAllIMAndFocus()}
     *      2. get the user with the sid in the sid input box in the database, if exist:
     *          1. fill its password in the password input box
     *          2. set focus to check-code input box
     * @clear
     */
    private void updateUserNameAutoFill(){
        final String NAME = "updateUserNameAutoFill()";
        final ArrayAdapter<String> ada = new ArrayAdapter<>(Login.this, android.R.layout.simple_dropdown_item_1line, udao.selectAllUserName());
        runOnUiThread(() -> {
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setAdapter(ada);
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                clearAllIMAndFocus();
                String selected_sid = (String) parent.getAdapter().getItem(position);
                new Thread(() -> {
                    final List<User> userSelected = udao.selectUser(selected_sid);
                    if (!userSelected.isEmpty()) {
                        runOnUiThread(() -> {
                            ((AutoCompleteTextView) findViewById(R.id.passwd_input)).setText(userSelected.get(0).password);
                            aaw_p = userSelected.get(0).aaw_password;
                            vpn_p = userSelected.get(0).vpn_password;
                            setFocusToEditText((EditText)findViewById(R.id.checkcode_input));
                        });
                    }
                }).start();
            });
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        LogMe.e(NAME, "the sid input box lost focus");
                        String read_sid = ((AutoCompleteTextView)v).getText().toString();
                        new Thread(()->{
                            List<User> get_users = udao.selectUser(read_sid);
                            User get_user = new User(read_sid, "", "", "");
                            if (!get_users.isEmpty()){
                                get_user = get_users.get(0);
                            }
                            User u_f = get_user;
                            runOnUiThread(()->{
                                ((EditText)findViewById(R.id.passwd_input)).setText(u_f.password);
                                aaw_p = u_f.aaw_password;
                                vpn_p = u_f.vpn_password;
                            });
                        }).start();
                    }
                }
            });
        });
    }

    /**
     * @ui/non-ui
     * 1. build an AlertDialog in this activity:
     *      - Message : m(if not null)
     *      - PositiveButtonOnClickListener : yes
     *      - NegativeButtonOnClickListener : no
     *      - PositiveButtonText : yes_text(if null, use default value)
     *      - NegativeButtonText : no_text(if null, use default value)
     *      - View : view(if not null)
     *      - Title : title(if not null)
     * 2. return this AlertDialog
     * @clear
     */
    public static AlertDialog getAlertDialog(@NonNull Context c, @Nullable final String m, @NonNull DialogInterface.OnClickListener yes, @NonNull DialogInterface.OnClickListener no, @Nullable View view, @Nullable String title, @Nullable String yes_text, @Nullable String no_text){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        if (m != null) {
            builder.setMessage(m);
        }
        if (yes_text != null){
            builder.setPositiveButton(yes_text, yes);
        }else {
            builder.setPositiveButton(c.getResources().getString(R.string.ok_btn_text_zhcn), yes);
        }
        if (no_text != null){
            builder.setNegativeButton(no_text, no);
        }else {
            builder.setNegativeButton(c.getResources().getString(R.string.deny_btn_zhcn), no);
        }
        if (view != null){
            builder.setView(view);
        }
        if (title != null){
            builder.setTitle(title);
        }
        return builder.create();
    }

    /**
     * @ui
     * 1. disable buttons, check-code-image-view
     * 2. show progress-bar
     * @clear
     */
    private void lock(){
        ((Button)findViewById(R.id.button)).setEnabled(false);
        ((Button)findViewById(R.id.button2)).setEnabled(false);
        ((ImageView)findViewById(R.id.imageView_checkcode)).setEnabled(false);
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        isMenuEnabled = false;
        invalidateOptionsMenu();
    }

    /**
     * @ui
     * @param clickable if enable buttons and check-code-image-view or not
     * 1. enable/disable(according to clickable) buttons and check-code-image-view
     * 2. hide progress-bar
     * @clear
     */
    private void unlock(boolean clickable){
        ((Button)findViewById(R.id.button)).setEnabled(clickable);
        ((Button)findViewById(R.id.button2)).setEnabled(clickable);
        ((ImageView)findViewById(R.id.imageView_checkcode)).setEnabled(clickable);
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
        isMenuEnabled = clickable;
        invalidateOptionsMenu();
    }

    /**
     * copy a text to system clipboard
     * @clear
     */
    public static void copyText(Context c, String text){
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", text);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    /**
     * help test
     * @clear
     */
    public void help(View view){
        if (help_cookie == null){
            Toast.makeText(this, "请在登录成功后使用此功能", Toast.LENGTH_SHORT).show();
        }else {
            Login.getAlertDialog(this, "请将此一次性凭据提供给测试人员：\n" + help_cookie,
                    (dialog, which) -> {
                        Login.copyText(Login.this, help_cookie);
                        Toast.makeText(Login.this, "复制成功", Toast.LENGTH_SHORT).show();
                    },
                    (dialog, which) -> {
                        //nothing
                    },
                    null,
                    "协助测试", "点击复制", "取消").show();
            Login.copyText(Login.this, help_cookie);
            Toast.makeText(Login.this, "一次性凭据复制成功", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @ui
     * @param et the EditText
     * 1. if the EditText is not null:
     *      1. set focus to the EditText
     *      2. if the EditText is not empty:
     *           1. clear focus of the EditText
     * @clear
     */
    private void setFocusToEditText(EditText et){
        if (et != null) {
            et.requestFocus();
            if (!et.getText().toString().isEmpty()) {
                et.clearFocus();
            }
        }
    }

    /**
     * @ui
     * @param et the EditText
     * 1. if the EditText is not null:
     *      1. set focus to the EditText
     * @clear
     */
    private void setFocusToEditText_Force(EditText et){
        if (et != null) {
            et.requestFocus();
        }
    }

    /**
     * @ui
     * 1. for each input box, if it is not null:
     *      1. clear IM on it
     *      2. clear focus of it
     * @clear
     */
    private void clearAllIMAndFocus(){
        EditText ets = (EditText)findViewById(R.id.sid_input);
        EditText etp = (EditText)findViewById(R.id.passwd_input);
        EditText etc = (EditText)findViewById(R.id.checkcode_input);
//        EditText eta = (EditText)findViewById(R.id.aaw_passwd_input);
        EditText etv = (EditText)findViewById(R.id.vpn_passwd_input);
        if (ets != null) {
            ets.setEnabled(!ets.isEnabled());
            ets.setEnabled(!ets.isEnabled());
            ets.clearFocus();
        }
        if (etp != null) {
            etp.setEnabled(!etp.isEnabled());
            etp.setEnabled(!etp.isEnabled());
            etp.clearFocus();
        }
        if (etc != null) {
            etc.setEnabled(!etc.isEnabled());
            etc.setEnabled(!etc.isEnabled());
            etc.clearFocus();
        }
//        if (eta != null) {
//            eta.setEnabled(!eta.isEnabled());
//            eta.setEnabled(!eta.isEnabled());
//            eta.clearFocus();
//        }
        if (etv != null) {
            etv.setEnabled(!etv.isEnabled());
            etv.setEnabled(!etv.isEnabled());
            etv.clearFocus();
        }
    }

    /**
     * @ui
     * @clear
     */
    private void setHintForEditText(String hint, int size, EditText et){
        SpannableString h = new SpannableString(hint);
        AbsoluteSizeSpan s = new AbsoluteSizeSpan(size,true);//true means "sp"
        h.setSpan(s, 0, h.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et.setHint(new SpannedString(h));
    }

    /**
     * @non-ui
     * 1. delete all user-related data from database(not including user login information)
     * @param username can be null if no currently activated user
     * @clear
     */
    public static HashMap<GoToClassKey, String> deleteOldDataFromDatabase( @Nullable String username, GoToClassDao gdao, ClassInfoDao cdao, TermInfoDao tdao, PersonInfoDao pdao, GraduationScoreDao gsdao, GradesDao grdao, ExamInfoDao edao, CETDao cetDao, LABDao labDao){
        /** prepare */
        HashMap<GoToClassKey, String> my_comm_map = Methods.getMyCommentMap(gdao, cdao);
        cdao.resetAllCustomRef(username);
        List<String> customCnoList = gdao.selectCustomCno(username);
        for(String cno : customCnoList){
            cdao.increaseCustomRef(username, cno);
        }
        gdao.clearAllSysComment(username);
        /** delete */
        gdao.deleteAllNotCustomized(username);
        cdao.deleteAllNotReferredByCustom(username);
        tdao.deleteAll();
        pdao.deleteAll();
        gsdao.deleteAll();
        grdao.deleteAll();
        edao.deleteAll();
        cetDao.deleteAll();
        labDao.deleteAll();
        return my_comm_map;
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
                null,
                null
        );
        if ( login_res.code == 0 ) {
            LoginResponse response = MyApp.gson.fromJson(login_res.comment, LoginResponse.class);
            login_res.comment = response.getMsg();
        }
        if (login_res.code == 0 && builder != null) {
            if (!builder.toString().isEmpty()) {
                builder.append(r.getString(R.string.cookie_delimiter));
            }
            builder.append(login_res.cookie);
        }
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "body: " + body + " code: " + login_res.code + " resp_code: " + login_res.resp_code + " comment/msg: " + login_res.comment);
        return login_res;
    }

    /**
     * @non-ui
     * 1. call and return {@link Login_vpn#vpn_login(Context, String, String)}
     * @return {@link Login_vpn#vpn_login(Context, String, String)}
     * @clear
     */
    public static String vpn_login_test(Context c, final String sid, final String pwd) throws ExceptionWrongUserOrPassword, ExceptionUnknown, ExceptionIpForbidden, ExceptionNetworkError {
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
//        final String NAME = "outside_login_test()";
//        Resources r = c.getResources();
//        String body = "username=" + sid + "&passwd=" + pwd + "&login=%B5%C7%A1%A1%C2%BC";
//        com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "body", body);
//        HttpConnectionAndCode login_res = Post.post(
//                r.getString(R.string.lan_outside_login_url),
//                null,
//                r.getString(R.string.user_agent),
//                r.getString(R.string.lan_outside_login_referer),
//                body,
//                null,
//                null,
//                r.getString(R.string.cookie_delimiter),
//                null,
//                null,
//                false,
//                null
//        );
//        if (login_res.code == -7){
//            login_res.code = 0;
//            com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "login status", "success");
//        }else {
//            if (login_res.code == 0){
//                login_res.code = -6;
//            }
//            com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "login status", "fail" + " code: " + login_res.code);
//        }
//        return login_res;

        return new HttpConnectionAndCode(0);
    }

    /**
     * @ui
     * 1. set content view
     * 2. call {@link #unlock(boolean)} with true
     * 3. set focus to sid input box
     * 4. call {@link #changeCode(View)}
     * 5. call {@link #updateUserNameAutoFill()}
     * 6. get activated user from database, if exist:
     *      1. fill its username in sid input box
     *      2. fill its password in password input box
     *      3. set focus to check-code input box
     * @clear
     */
    private void initContentView(){
        setContentView(R.layout.activity_login);
        unlock(true);
        setFocusToEditText((EditText)findViewById(R.id.sid_input));
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
                    setFocusToEditText((EditText)findViewById(R.id.checkcode_input));
                });
            }
        }).start();
    }

    /**
     * @ui
     * 1. super onCreate()
     * 2. set {@link MyApp#setRunning_activity(MyApp.RunningActivity)} to {@link MyApp.RunningActivity#LOGIN}
     * 3. initialize DAOs
     * 4. call {@link #initContentView()}
     * @clear
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.LOGIN);
        MyApp.setRunning_activity_pointer(this);
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
        new Thread(()->my_comment_map = Methods.getMyCommentMap(gdao, cdao)).start();
        initContentView();
    }

    /**
     * @ui
     * 1. if {@link MyApp#getRunning_activity()} is {@link MyApp.RunningActivity#LOGIN}:
     *      1. set {@link MyApp#setRunning_activity(MyApp.RunningActivity)} to {@link MyApp.RunningActivity#NULL}
     * 2. super onDestroy()
     * @clear
     */
    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
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
     * 1. call {@link #clearAllIMAndFocus()}
     * 2. get the username in the sid input box
     * 3. show an AlertDialog to warn user:
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
        clearAllIMAndFocus();
        String sid = ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString();
        getAlertDialog(this, "确定要取消记住用户" + " " + sid + " " + "的登录信息吗？",
                (DialogInterface.OnClickListener) (dialogInterface, i) -> new Thread((Runnable) () -> {
                    udao.deleteUser(sid);
                    com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "user deleted", sid);
                    updateUserNameAutoFill();
                    runOnUiThread((Runnable) () -> {
                        ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText("");
                        ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText("");
                        aaw_p = "";
                        vpn_p = "";
                        setFocusToEditText((EditText)findViewById(R.id.sid_input));
                        changeCode(null);
                    });
                }).start(),
                (DialogInterface.OnClickListener) (dialogInterface, i) -> {},
                null, null, null, null).show();
    }

    /**
     * @non-ui
     * 1. pull all user-related data from internet
     * 2. save the pulled data to database and shared preference
     * @return
     * - true : everything is ok
     * - false : something went wrong
     * @clear
     */
    public static boolean fetch_merge(boolean formal, Context c, String cookie, String username, HashMap<GoToClassKey, String> my_comm_map, PersonInfoDao pdao, TermInfoDao tdao, GoToClassDao gdao, ClassInfoDao cdao, GraduationScoreDao gsdao, SharedPreferences.Editor editor, GradesDao grdao, ExamInfoDao edao, CETDao cetDao, LABDao labDao){
        final String NAME = "fetch_merge()";
        HttpConnectionAndCode res;

        LogMe.e(NAME, "fetching person info and student info");
        res = LAN.personInfo(c, cookie);
        HttpConnectionAndCode res_add = LAN.studentInfo(c, cookie);
        if (res.code != 0 || res_add.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch person info and student info success, merging...");
        Merge.personInfo(res.comment, res_add.comment, pdao);

        LogMe.e(NAME, "fetching term info");
        res = LAN.termInfo(c, cookie);
        if (res.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch term info success, merging...");
        Merge.termInfo(c, res.comment, tdao);

        List<String> terms = tdao.getTermsSince(
                pdao.getGrade().get(0) + "-" + (pdao.getGrade().get(0) + 1) + "_1"
        );
        List<TermInfo> term_list = tdao.selectAll();
        for (TermInfo term : term_list){
            if (terms.contains(term.term))continue;
            tdao.deleteTerm(term.term);
        }
        LogMe.e(NAME, "fetching go-to-class and class info");
        res = LAN.goToClass_ClassInfo(c, cookie);
        if (res.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch go-to-class and class info success, merging...");
        Merge.goToClass_ClassInfo(res.comment, gdao, cdao, my_comm_map, username);

        LogMe.e(NAME, "fetching graduation courses");
        res = LAN.graduationScore(c, cookie);
        res_add = LAN.graduationScore2(c, cookie);
        if (res.code != 0 || res_add.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch graduation courses success, merging...");
        Merge.graduationScore(res.comment, res_add.comment, gsdao);

        LogMe.e(NAME, "fetching hour info");
        res = LAN.hour(c, cookie);
        if (res.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch hour info success, merging...");
        Merge.hour(c, res.comment, editor);

        LogMe.e(NAME, "fetching grades");
        res = LAN.grades(c, cookie);
        if (res.code != 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch grades success, merging...");
        Merge.grades(res.comment, grdao, formal, !formal);

        LogMe.e(NAME, "fetching exam info");
        res = LAN.examInfo(c, cookie);
        if (res.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch exam info success, merging...");
        Merge.examInfo(res.comment, edao, tdao, c, formal, !formal, username);

        LogMe.e(NAME, "fetching cet");
        res = LAN.cet(c, cookie);
        if (res.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch cet success, merging...");
        Merge.cet(res.comment, cetDao);

        term_list = tdao.selectAll();
        Locate locate = Clock.locateNow_low_api(Clock.nowTimeStamp(), tdao, MyApp.getCurrentSharedPreference(),
                MyApp.times,
                Clock.getDateTimeFormatterFor_locateNow_low_api(c),
                Clock.getDefaultDelimiterFor_whichTime(),
                Clock.getSSFor_locateNow(c),
                Clock.getESFor_locateNow(c),
                Clock.getDSFor_locateNow(c)
        );
        if (locate.term != null) {
            for (TermInfo term : term_list) {
                if (!term.term.equals(locate.term.term)) {
                    LogMe.e(NAME, "skip lab-fetch: " + term.term);
                    continue;
                }
                LogMe.e(NAME, "fetching lab");
                res.code = -1;
                for (int i = 0; i < 2 && res.code != 0 && res.code != -6 && res.code != -7; i++) {
                    LogMe.e(NAME, "fetching lab time: " + (i + 1));
                    res = LAN.lab(c, cookie, term.term);
                }
                if (res.code != 0) {
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "fetch lab fail: " + term.term);
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
                    return false;
                }
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "fetch lab success: " + term.term);
                LogMe.e(NAME, "fetch lab success, merging...");
                Merge.lab(res.comment, labDao, gdao, cdao, my_comm_map, username);
            }
        }

        com.telephone.coursetable.LogMe.LogMe.e(NAME, "success");
        return true;
    }

    /**
     * @ui
     * 1. call {@link #clearAllIMAndFocus()}
     * 2. get student id, credit system password, credit system check-code from input box
     * 3. get cookie before logging in the credit system
     * 4. get a dialog View
     * 5. get an AlertDialog:
     *      - Message : ""
     *      - View : the View obtained before
     *      - Title : the String specified in resources file
     *      - Press-yes :
     *          1. call {@link #clearAllIMAndFocus()}
     *          2. call {@link #lock()}
     *          3. get aaw password, vpn password from input box
     *          4. start a new thread:
     *              1. call {@link #login(Context, String, String, String, String, StringBuilder)} , passing {@link #cookie_builder}
     *                  - if credit system login fail:
     *                      1. show tip snack-bar
     *                      2. clear corresponding input box(except check-code input box), set focus to it(including check-code input box)
     *                      3. call {@link #unlock(boolean)} with true
     *                      4. end this thread
     *              2. call {@link #vpn_login_test(Context, String, String)}
     *                  - if vpn login test fail:
     *                      1. show tip snack-bar
     *                      2. call {@link #unlock(boolean)} with true
     *                      3. end this thread
     *              3. call {@link #outside_login_test(Context, String, String)}
     *                  - if outside login test fail:
     *                      1. show tip snack-bar
     *                      2. call {@link #unlock(boolean)} with true
     *                      3. end this thread
     *              4. get cookie after successfully logging in the credit system
     *              5. get shared preference and its editor
     *              6. detect new activity || skip no activity
     *              7. insert/replace new user into database
     *              ******************************* UPDATE DATA START *******************************
     *              8. deactivate all user in database
     *              9. set {@link MyApp#setRunning_login_thread(boolean)} to true
     *              10. clear shared preference
     *              11. commit shared preference
     *              12. show tip snack-bar, change title
     *              13. call {@link #deleteOldDataFromDatabase(String, GoToClassDao, ClassInfoDao, TermInfoDao, PersonInfoDao, GraduationScoreDao, GradesDao, ExamInfoDao, CETDao, LABDao)}
     *              14. call {@link #fetch_merge(boolean, Context, String, String, HashMap, PersonInfoDao, TermInfoDao, GoToClassDao, ClassInfoDao, GraduationScoreDao, SharedPreferences.Editor, GradesDao, ExamInfoDao, CETDao, LABDao)}
     *              15. commit shared preference
     *              16. the result of {@link #fetch_merge(boolean, Context, String, String, HashMap, PersonInfoDao, TermInfoDao, GoToClassDao, ClassInfoDao, GraduationScoreDao, SharedPreferences.Editor, GradesDao, ExamInfoDao, CETDao, LABDao)}:
     *                  - if everything is ok:
     *                      1. locate now, print the locate-result to log
     *                      2. activate the user
     *                      3. print the user's student id and name to log
     *                      4. set {@link MyApp#setRunning_login_thread(boolean)} to false
     *                      5. call {@link #unlock(boolean)} with false
     *                      6. show tip toast, change title
     *                      7. if some activity is running:
     *                          1. start a new {@link MainActivity}
     *                  - if something went wrong:
     *                      1. set {@link MyApp#setRunning_login_thread(boolean)} to false
     *                      2. if login activity is current running activity:
     *                              1. call {@link #unlock(boolean)} with true
     *                              2. show tip snack-bar, change title
     *                         else:
     *                              1. show tip toast
     *                              2. if main activity is current running activity:
     *                                  1. call {@link MainActivity#refresh()}
     *              ******************************* UPDATE DATA END *******************************
     *      - Press-no : nothing will happen
     * 6. start a new thread:
     *      1. try to get the user who has the inputted student id from the database, if exist:
     *          1. fill the user's aaw-password and his vpn-password in the AlertDialog's input box
     *      2. show the AlertDialog obtained before
     * @clear
     */
    public void login_thread(final View view){
        final String NAME = "login_thread()";
        /** call {@link #clearAllIMAndFocus()} */
        clearAllIMAndFocus();
        /** get student id, credit system password, credit system check-code from input box */
        final String sid = ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString();
        final String pwd = ((AutoCompleteTextView)findViewById(R.id.passwd_input)).getText().toString();
        final String ck = ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).getText().toString();

        // restore the cookie builder to the check code cookie before a new login
        cookie_builder = new StringBuilder();
        cookie_builder.append(ck_cookie_backup);

        /** get cookie before logging in the credit system */
        final String cookie_before_login = cookie_builder.toString();
        /** get a dialog View */
        View extra_pwd_dialog_layout = getLayoutInflater().inflate(R.layout.extra_password, null);
        /** get an AlertDialog */
        AlertDialog extra_pwd_dialog = getAlertDialog(this, "",
                (dialogInterface, i) -> {
                    /** call {@link #clearAllIMAndFocus()} */
                    clearAllIMAndFocus();
                    /** call {@link #lock()} */
                    lock();
                    /** get aaw password, vpn password from input box */
//                    final String aaw_pwd = ((EditText)extra_pwd_dialog_layout.findViewById(R.id.aaw_passwd_input)).getText().toString();
                    final String aaw_pwd = "";
                    final String vpn_pwd = ((EditText)extra_pwd_dialog_layout.findViewById(R.id.vpn_passwd_input)).getText().toString();
                    aaw_p = aaw_pwd;
                    vpn_p = vpn_pwd;
                    /** start a new thread */
                    new Thread(() -> {
                        /** call {@link #login(Context, String, String, String, String, StringBuilder)} , passing {@link #cookie_builder} */
                        HttpConnectionAndCode login_res = login(Login.this, sid, pwd, ck, cookie_before_login, cookie_builder);
                        /** if credit system login fail */
                        if (login_res.code != 0){
                            String tip;
                            if (login_res.comment != null && login_res.comment.contains("验证码")){
                                tip = getResources().getString(R.string.lan_snackbar_login_fail_ck);
                            }else if (login_res.comment != null && login_res.comment.contains("密码")){
                                tip = getResources().getString(R.string.lan_snackbar_login_fail_pwd);
                            }else {
                                tip = getResources().getString(R.string.lan_snackbar_login_fail) + " : " + login_res.comment + " (" + login_res.code + ")";
                            }
                            final String tip_f = tip;
                            runOnUiThread((Runnable) () -> {
                                /** show tip snack-bar */
                                Snackbar.make(view, tip_f, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                /** clear corresponding input box(except check-code input box), set focus to it(including check-code input box) */
                                if (tip_f.equals(getResources().getString(R.string.lan_snackbar_login_fail_pwd))) {
                                    ((EditText)findViewById(R.id.passwd_input)).setText("");
                                    setFocusToEditText((EditText)findViewById(R.id.passwd_input));
                                }else if (tip_f.equals(getResources().getString(R.string.lan_snackbar_login_fail_ck))){
                                    setFocusToEditText_Force((EditText)findViewById(R.id.checkcode_input));
                                }
                                /** call {@link #unlock(boolean)} with true */
                                unlock(true);
                            });
                            /** end this thread */
                            return;
                        }
                        /** call {@link #vpn_login_test(Context, String, String)} */
                        String vpn_login_res = null;
                        try {
                            vpn_login_res = vpn_login_test(Login.this, sid, vpn_pwd);
                        } catch (ExceptionWrongUserOrPassword exceptionWrongUserOrPassword) {
                            runOnUiThread(()->{
                                Snackbar.make(view, getResources().getString(R.string.lan_snackbar_vpn_test_login_fail_wrong_pwd), BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                unlock(true);
                            });
                            return;
                        } catch (ExceptionUnknown exceptionUnknown) {
                            runOnUiThread(()->{
                                Snackbar.make(view, getResources().getString(R.string.lan_snackbar_vpn_test_login_fail), BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                unlock(true);
                            });
                            return;
                        } catch (ExceptionIpForbidden exceptionIpForbidden) {
                            runOnUiThread(()->{
                                Snackbar.make(view, getResources().getString(R.string.lan_snackbar_vpn_test_login_fail_ip), BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                unlock(true);
                            });
                            return;
                        } catch (ExceptionNetworkError exceptionNetworkError) {
                            runOnUiThread(()->{
                                Snackbar.make(view, getResources().getString(R.string.lan_snackbar_vpn_test_login_fail_net_error), BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                unlock(true);
                            });
                            return;
                        }
                        /** call {@link #outside_login_test(Context, String, String)} */
                        HttpConnectionAndCode outside_login_res = outside_login_test(Login.this, sid, aaw_pwd);
                        /** if outside login test fail */
                        if (outside_login_res.code != 0){
                            runOnUiThread((Runnable) () -> {
                                /** show tip snack-bar */
                                Snackbar.make(view, getResources().getString(R.string.lan_snackbar_outside_test_login_fail), BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                /** call {@link #unlock(boolean)} with true */
                                unlock(true);
                            });
                            /** end this thread */
                            return;
                        }
                        /** get cookie after successfully logging in the credit system */
                        final String cookie_after_login = cookie_builder.toString();
                        /** get shared preference and its editor */
                        final SharedPreferences shared_pref = MyApp.getCurrentSharedPreference();
                        final SharedPreferences.Editor editor = MyApp.getCurrentSharedPreferenceEditor();
                        /** detect new activity || skip no activity */
                        if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.NULL)){
                            com.telephone.coursetable.LogMe.LogMe.e(NAME, "no activity is running, login = " + Login.this.toString() + " canceled");
                            runOnUiThread(()->Toast.makeText(Login.this, "登录取消", Toast.LENGTH_SHORT).show());
                            return;
                        }
                        com.telephone.coursetable.LogMe.LogMe.e(NAME, "login activity pointer = " + Login.this.toString());
                        com.telephone.coursetable.LogMe.LogMe.e(NAME, "running activity pointer = " + MyApp.getRunning_activity_pointer().toString());
                        if (!Login.this.toString().equals(MyApp.getRunning_activity_pointer().toString())){
                            com.telephone.coursetable.LogMe.LogMe.e(NAME, "new running activity detected = " + MyApp.getRunning_activity_pointer().toString() + ", login = " + Login.this.toString() + " canceled");
                            runOnUiThread(()->Toast.makeText(Login.this, "登录取消", Toast.LENGTH_SHORT).show());
                            return;
                        }
                        // edit by Telephone 2020/11/23 09:46, get currently activated username
                        String username = null;
                        if (!udao.getActivatedUser().isEmpty()){
                            username = udao.getActivatedUser().get(0).username;
                        }
                        /** insert/replace new user into database */
                        udao.insert(new User(sid, pwd, aaw_pwd, vpn_pwd));
                        /**
                         * ******************************* UPDATE DATA START *******************************
                         */
                        /** deactivate all user in database */
                        udao.disableAllUser();
                        /** set {@link MyApp#running_login_thread} to true */
                        MyApp.setRunning_login_thread(true);
                        /** clear shared preference */
                        editor.clear();
                        /** commit shared preference */
                        editor.commit();
                        /** show tip snack-bar, change title */
                        runOnUiThread(() -> {
                            Snackbar.make(view, getResources().getString(R.string.lan_snackbar_data_updating), BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.WHITE).show();
                            getSupportActionBar().setTitle(getResources().getString(R.string.lan_title_login_updating));
                        });

                        help_cookie = cookie_after_login; //set help-cookie after login success

                        try { // this is an Accident Prone Area
                            /** call {@link #deleteOldDataFromDatabase()} */
                            deleteOldDataFromDatabase(username, gdao, cdao, tdao, pdao, gsdao, grdao, edao, cetDao, labDao);
                            /** call {@link #fetch_merge(Context, String, PersonInfoDao, TermInfoDao, GoToClassDao, ClassInfoDao, GraduationScoreDao, SharedPreferences.Editor)} */
                            boolean fetch_merge_res = fetch_merge(true, Login.this, cookie_after_login, sid, my_comment_map, pdao, tdao, gdao, cdao, gsdao, editor, grdao, edao, cetDao, labDao);
                            /** commit shared preference */
                            editor.commit();
                            if (fetch_merge_res) {
                                /** locate now, print the locate-result to log */
                                com.telephone.coursetable.LogMe.LogMe.e(
                                        NAME + " " + "locate now",
                                        Clock.locateNow_low_api(
                                                Clock.nowTimeStamp(), tdao, shared_pref, MyApp.times,
                                                Clock.getDateTimeFormatterFor_locateNow_low_api(Login.this),
                                                Clock.getDefaultDelimiterFor_whichTime(),
                                                getResources().getString(R.string.pref_hour_start_suffix),
                                                getResources().getString(R.string.pref_hour_end_suffix),
                                                getResources().getString(R.string.pref_hour_des_suffix)
                                        ) + ""
                                );
                                /** activate the user */
                                udao.activateUser(sid);
                                /** print the user's student id and name to log */
                                com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "user activated", pdao.selectAll().get(0).stid + " " + pdao.selectAll().get(0).name);
                                /** set {@link MyApp#running_login_thread} to false */
                                MyApp.setRunning_login_thread(false);
                                runOnUiThread(() -> {
                                    /** call {@link #unlock(boolean)} with false */
                                    unlock(false);
                                    /** show tip toast, change title */
                                    Toast.makeText(Login.this, getResources().getString(R.string.lan_toast_update_success), Toast.LENGTH_SHORT).show();
                                    getSupportActionBar().setTitle(getResources().getString(R.string.lan_title_login_updated));
                                    /** if some activity is running */
                                    if (!MyApp.getRunning_activity().equals(MyApp.RunningActivity.NULL)) {
                                        com.telephone.coursetable.LogMe.LogMe.e(NAME, "start a new Main Activity...");
                                        /** start a new {@link MainActivity} */
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    } else {
                                        com.telephone.coursetable.LogMe.LogMe.e(NAME, "update success but no activity is running, NOT start new Main Activity");
                                    }
                                });
                            } else {
                                /** set {@link MyApp#running_login_thread} to false */
                                MyApp.setRunning_login_thread(false);
                                /** if login activity is current running activity */
                                if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN)) {
                                    runOnUiThread(() -> {
                                        /** call {@link #unlock(boolean)} with true */
                                        unlock(true);
                                        /** show tip snack-bar, change title */
                                        Snackbar.make(view, getResources().getString(R.string.lan_toast_update_fail), BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.WHITE).show();
                                        getSupportActionBar().setTitle(getResources().getString(R.string.lan_title_login_updated_fail));
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        /** show tip toast */
                                        Toast.makeText(Login.this, getResources().getString(R.string.lan_toast_update_fail), Toast.LENGTH_SHORT).show();
                                        /** if main activity is current running activity */
                                        if (MyApp.getRunning_main() != null) {
                                            com.telephone.coursetable.LogMe.LogMe.e(NAME, "refresh the Main Activity...");
                                            /** call {@link MainActivity#refresh()} */
                                            MyApp.getRunning_main().refresh();
                                        }
                                    });
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(()->Toast.makeText(Login.this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show());
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                        /**
                         * ******************************** UPDATE DATA END ********************************
                         */
                    }).start();
                },
                (dialogInterface, i) -> {},
                extra_pwd_dialog_layout,
                getResources().getString(R.string.lan_extra_password_title), null, null);
        new Thread(() -> {
            final String aaw_pwdf = aaw_p;
            final String vpn_pwdf = vpn_p;
            runOnUiThread(() -> {
//                ((EditText)extra_pwd_dialog_layout.findViewById(R.id.aaw_passwd_input)).setText(aaw_pwdf);
                ((EditText)extra_pwd_dialog_layout.findViewById(R.id.vpn_passwd_input)).setText(vpn_pwdf);
//                setHintForEditText("默认为身份证后6位", 10, (EditText)extra_pwd_dialog_layout.findViewById(R.id.aaw_passwd_input));
                setHintForEditText("上网登录页密码，默认为身份证后6位", 8, (EditText)extra_pwd_dialog_layout.findViewById(R.id.vpn_passwd_input));
                extra_pwd_dialog.show();
            });
        }).start();
    }
}
