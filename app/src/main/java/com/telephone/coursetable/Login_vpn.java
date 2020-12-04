package com.telephone.coursetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.telephone.coursetable.Database.LABDao;
import com.telephone.coursetable.Database.Methods.Methods;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Fetch.WAN;
import com.telephone.coursetable.Gson.LoginResponse;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Https.Post;
import com.telephone.coursetable.LogMe.LogMe;
import com.telephone.coursetable.Merge.Merge;
import com.telephone.coursetable.MyException.Exception302;
import com.telephone.coursetable.MyException.ExceptionIpForbidden;
import com.telephone.coursetable.MyException.ExceptionNetworkError;
import com.telephone.coursetable.MyException.ExceptionUnknown;
import com.telephone.coursetable.MyException.ExceptionWrongCheckCode;
import com.telephone.coursetable.MyException.ExceptionWrongUserOrPassword;
import com.telephone.coursetable.MyException.MyException;
import com.telephone.coursetable.OCR.OCR;


import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Login_vpn extends AppCompatActivity {

    public final static String EXTRA_USERNAME = "com.telephone.coursetable.loginvpn.username";
    public final static String EXTRA_VPN_PASSWORD = "com.telephone.coursetable.loginvpn.password.vpn";
    public final static String EXTRA_AAW_PASSWORD = "com.telephone.coursetable.loginvpn.password.aaw";
    public final static String EXTRA_SYS_PASSWORD = "com.telephone.coursetable.loginvpn.password.sys";

    private boolean updating = false;
    //private AppDatabase db = null;

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
    private SharedPreferences.Editor editor = MyApp.getCurrentSharedPreferenceEditor();
    private HashMap<GoToClassKey, String> my_comment_map = null;

    private String sid = "";
//    private String aaw_pwd = "";//教务处密码
    private String sys_pwd = "";//学分系统密码
    private String vpn_pwd = "";
    private String cookie = "";
    private String ck = "";
    private volatile String help_cookie = null; //the cookie used for help, volatile to maintain multi-thread synchronization

    private HttpConnectionAndCode login_res;
    private boolean outside_login_res;

    private boolean isMenuEnabled = true;

    private String title;

    private int vpn_login_fail_times = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_vpn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(Login_vpn.this, MainActivity.class));
                return true;
            case R.id.login_vpn_menu_switch_login_mode:
                startActivity(new Intent(Login_vpn.this, Login.class));
                return true;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.login_vpn_menu_switch_login_mode);
        item.setEnabled(isMenuEnabled);
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener(Button bt, TextView textView) {
        bt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        textView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        bt.setBackground(getDrawable(R.drawable.eye_open));
                        clearIMAndFocus();
                        break;
                    case MotionEvent.ACTION_UP:
                        textView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        bt.setBackground(getDrawable(R.drawable.eye_close));
                        clearIMAndFocus();
                        break;
                }
                return false;
            }
        });
    }

    //clear
    private void first_login() {
        setContentView(R.layout.activity_login_vpn_no_checkcode);
        setHintForEditText("上网登录页密码，默认为身份证后6位", 10, (EditText)findViewById(R.id.passwd_input));
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);

        ((EditText)findViewById(R.id.passwd_input)).setInputType(((EditText)findViewById(R.id.passwd_input)).getInputType());

        Button btn_pwd = (Button)findViewById(R.id.show_pwd);
        setOnTouchListener(btn_pwd, (AutoCompleteTextView)findViewById(R.id.passwd_input));

        new Thread((Runnable) () -> {
            updateUserNameAutoFill();
            //if any user is activated, fill his sid and pwd in the input box
            List<User> ac_user = udao.getActivatedUser();
            if (!ac_user.isEmpty()){
                final User u = ac_user.get(0);
                runOnUiThread(() -> {

                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText(u.username);
                    ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText(u.vpn_password);

//                    aaw_pwd = u.aaw_password;
                    sys_pwd = u.password;

                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).clearFocus();
                    fillStringExtra();
                });
            }else {
                runOnUiThread( ()->{
                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).requestFocus();
                    fillStringExtra();
                });
            }
        }).start();
   }



    //clear
    private void system_login(String sid) {
        isMenuEnabled = true;
        invalidateOptionsMenu();

        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);

        setContentView(R.layout.activity_login_vpn);
//        setHintForEditText("默认为身份证后6位", 10, (EditText)findViewById(R.id.aaw_pwd_input));
//        ((EditText)findViewById(R.id.aaw_pwd_input)).setInputType(((EditText)findViewById(R.id.aaw_pwd_input)).getInputType());
        ((EditText)findViewById(R.id.sys_pwd_input)).setInputType(((EditText)findViewById(R.id.sys_pwd_input)).getInputType());
        ((TextView) findViewById(R.id.sid_input2)).setText(sid);
        ((TextView) findViewById(R.id.sid_input2)).setEnabled(false);

        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);

//        ((TextView)findViewById(R.id.aaw_pwd_input)).setText(aaw_pwd);
        ((TextView)findViewById(R.id.sys_pwd_input)).setText(sys_pwd);

        if ( sys_pwd.isEmpty() ) {
            setFocusToEditText( (EditText) findViewById(R.id.sys_pwd_input) );
        }
//        if ( aaw_pwd.isEmpty() ) {
//            setFocusToEditText( (EditText) findViewById(R.id.aaw_pwd_input) );
//        }

//        Button btn_pwd_21 = (Button)findViewById(R.id.show_pwd_21);
//        setOnTouchListener(btn_pwd_21, (AutoCompleteTextView)findViewById(R.id.aaw_pwd_input));


        Button btn_pwd_22 = (Button)findViewById(R.id.show_pwd_22);
        setOnTouchListener(btn_pwd_22, (AutoCompleteTextView)findViewById(R.id.sys_pwd_input));
    }


    private void updateUserNameAutoFill(){
        final String NAME = "updateUserNameAutoFill()";
        final ArrayAdapter<String> ada = new ArrayAdapter<>(Login_vpn.this, android.R.layout.simple_dropdown_item_1line, udao.selectAllUserName());
        runOnUiThread(() -> {
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setAdapter(ada);
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                clearIMAndFocus();
                String selected_sid = (String) parent.getAdapter().getItem(position);
                new Thread(() -> {
                    final List<User> userSelected = udao.selectUser(selected_sid);
                    if (!userSelected.isEmpty()) {
                        runOnUiThread(() -> {
//                            aaw_pwd = userSelected.get(0).aaw_password;
                            sys_pwd = userSelected.get(0).password;
                            ((AutoCompleteTextView) findViewById(R.id.passwd_input)).setText(userSelected.get(0).vpn_password);
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
                                ((EditText)findViewById(R.id.passwd_input)).setText(u_f.vpn_password);
//                                aaw_pwd = u_f.aaw_password;
                                sys_pwd = u_f.password;
                            });
                        }).start();
                    }
                }
            });
        });
    }

    /**
     * @ui
     * @clear
     */
    private void lock(){
        int[] disable_ids = {
                R.id.sid_input,
                R.id.passwd_input,
                R.id.sys_pwd_input,
//                R.id.aaw_pwd_input,
                R.id.button,
                R.id.button2
        };
        int[] visible_ids = {
                R.id.progressBar
        };
        for (int id : disable_ids){
            View view = findViewById(id);
            if (view != null) {
                view.setEnabled(false);
            }
        }
        for (int id : visible_ids){
            View view = findViewById(id);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
        isMenuEnabled = false;
        invalidateOptionsMenu();
    }

    /**
     * @ui
     * @clear
     */
    private void unlock(boolean clickable){
        int[] enable_disable_ids = {
                R.id.sid_input,
                R.id.passwd_input,
                R.id.sys_pwd_input,
//                R.id.aaw_pwd_input,
                R.id.button,
                R.id.button2
        };
        int[] invisible_ids = {
                R.id.progressBar,
                R.id.login_vpn_patient
        };
        for (int id : enable_disable_ids){
            View view = findViewById(id);
            if (view != null) {
                view.setEnabled(clickable);
            }
        }
        for (int id : invisible_ids){
            View view = findViewById(id);
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }
        isMenuEnabled = clickable;
        invalidateOptionsMenu();
    }

    /**
     * @ui
     * @clear
     */
    private void try_to_show_patient(){
        View patient = findViewById(R.id.login_vpn_patient);
        View pbar = findViewById(R.id.progressBar);
        if (pbar != null) {
            patient.setVisibility(pbar.getVisibility());
        }else {
            patient.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @ui
     * @clear
     */
    private void retry(@NonNull View snack_bar_bind_to_view, @NonNull String tip){
        Snackbar.make(snack_bar_bind_to_view, tip, BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
        unlock(true);
    }

    /**
     * @ui
     * @clear
     */
    private void jump(@Nullable String tip, @NonNull Class<?> jump_to_class, @Nullable Map<String, String> string_extra){
        if (tip != null) {
            Toast.makeText(Login_vpn.this, tip, Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(Login_vpn.this, jump_to_class);
        if (string_extra != null) {
            for (String key : string_extra.keySet()){
                intent.putExtra(key, string_extra.get(key));
            }
        }
        startActivity(intent);
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
                        Login.copyText(Login_vpn.this, help_cookie);
                        Toast.makeText(Login_vpn.this, "复制成功", Toast.LENGTH_SHORT).show();
                    },
                    (dialog, which) -> {
                        //nothing
                    },
                    null,
                    "协助测试", "点击复制", "取消").show();
            Login.copyText(Login_vpn.this, help_cookie);
            Toast.makeText(Login_vpn.this, "一次性凭据复制成功", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @ui
     * @clear
     */
    private void fillStringExtra(){
        Intent intent = getIntent();
        String sid = intent.getStringExtra(EXTRA_USERNAME);
        String vpn_pwd = intent.getStringExtra(EXTRA_VPN_PASSWORD);
        String sys_pwd = intent.getStringExtra(EXTRA_SYS_PASSWORD);
        String aaw_pwd = intent.getStringExtra(EXTRA_AAW_PASSWORD);
        EditText sid_input = findViewById(R.id.sid_input);
        EditText vpn_pwd_input = findViewById(R.id.passwd_input);
        EditText sys_pwd_input = findViewById(R.id.sys_pwd_input);
//        EditText aaw_pwd_input = findViewById(R.id.aaw_pwd_input);
        if (sid != null) {
            if (sid_input != null) {
                sid_input.setText(sid);
            }
        }
        if (vpn_pwd != null) {
            if (vpn_pwd_input != null) {
                vpn_pwd_input.setText(vpn_pwd);
            }
        }
        if (sys_pwd != null) {
            this.sys_pwd = sys_pwd;
            if (sys_pwd_input != null) {
                sys_pwd_input.setText(sys_pwd);
            }
        }
        if (aaw_pwd != null) {
//            this.aaw_pwd = aaw_pwd;
//            if (aaw_pwd_input != null) {
//                aaw_pwd_input.setText(aaw_pwd);
//            }
        }
    }

    /**
     * @clear
     */
    private Map<String, String> getSidPasswordExtraMap(){
        return new HashMap<String, String>() {
            {
                put(EXTRA_USERNAME, sid);
//                put(EXTRA_AAW_PASSWORD, aaw_pwd);
                put(EXTRA_SYS_PASSWORD, sys_pwd);
                put(EXTRA_VPN_PASSWORD, vpn_pwd);
            }
        };
    }

    public static String wan_vpn_login_text(Context context, final String sid, final String pwd){
        final String NAME = "wan_vpn_login_test()";
        int times = 0;
        Resources resources = context.getResources();
        do {
            try {
                return Login_vpn.vpn_login(context, sid, pwd);

            } catch (ExceptionWrongUserOrPassword exceptionWrongUserOrPassword) {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "ExceptionWrongUserOrPassword");
                return resources.getString(R.string.login_fail_pwd_text_exception);

            } catch (ExceptionNetworkError exceptionNetworkError) {
                if ( times++<MyApp.check_code_regain_times ) continue;;
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "ExceptionNetworkError");
                return resources.getString(R.string.wan_login_vpn_network_error_exception);

            } catch (ExceptionIpForbidden exceptionIpForbidden) {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "ExceptionIpForbidden");
                return resources.getString(R.string.wan_login_vpn_ip_forbidden_exception);

            } catch (ExceptionUnknown exceptionUnknown) {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "ExceptionUnknown");
                return resources.getString(R.string.wan_snackbar_unknown_fail_exception);

            }
        }while ( true );
    }

    /**
     * @non-ui
     * @throws com.telephone.coursetable.MyException.MyException
     * @return return web vpn ticket if success, otherwise throw exceptions
     * @trustme
     * @clear
     */
    public static String regainVPNTicket(@NonNull Context c, @NonNull String sid, @NonNull String pwd) throws ExceptionWrongUserOrPassword, ExceptionNetworkError, ExceptionIpForbidden, ExceptionUnknown {
        int times = 0;
        String ticket = null;
        while (times < MyApp.web_vpn_ticket_regain_times){
            try {
                ticket = vpn_login(c, sid, pwd);
                break;
            } catch (ExceptionWrongUserOrPassword e) {
                throw e;
            } catch (ExceptionNetworkError | ExceptionIpForbidden | ExceptionUnknown e) {
                if (times == MyApp.web_vpn_ticket_regain_times - 1){
                    throw e;
                }
            }
            times++;
        }
        return ticket;
    }

    /**
     * @non-ui
     * @throws com.telephone.coursetable.MyException.MyException
     * @return return check-code bitmap if success, otherwise throw exceptions
     * @trustme
     * @clear
     */
    public static Bitmap try_to_get_check_code(@NonNull Context c, @NonNull StringBuilder cookie, @NonNull String sid, @NonNull String vpn_pwd) throws Exception302, ExceptionWrongUserOrPassword, ExceptionUnknown, ExceptionIpForbidden, ExceptionNetworkError {
        Bitmap ck_bitmap = null;
        int times = 0;
        while (times < MyApp.check_code_regain_times){
            HttpConnectionAndCode res = WAN.checkcode(c, cookie.toString());
            ck_bitmap = (Bitmap) res.obj;
            times++;
            if (ck_bitmap != null){//success
                break;
            }else if (res.code == -7){//jump to other page
                throw new Exception302();
            }else {//else, network error
                if (times >= MyApp.check_code_regain_times/3) {
                    if (res.c != null) {
                        res.c.disconnect();
                    }
                }
                cookie.setLength(0);
                cookie.append(regainVPNTicket(c, sid, vpn_pwd));
            }
        }
        if (ck_bitmap == null){
            throw new ExceptionNetworkError();
        }
        return ck_bitmap;
    }

    //clear
    private void setFocusToEditText(EditText et) {
        if (et != null) {
            et.requestFocus();
            if (!et.getText().toString().isEmpty()) {
                et.clearFocus();
            }
        }
    }


    //clear
    private void clearIMAndFocus() {
        EditText ets = (EditText) findViewById(R.id.sid_input);
        EditText etp = (EditText) findViewById(R.id.passwd_input);
//        EditText etw = (EditText) findViewById(R.id.aaw_pwd_input);
        EditText ety = (EditText)findViewById(R.id.sys_pwd_input);


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
//        if (etw != null) {
//            etw.setEnabled(!etw.isEnabled());
//            etw.setEnabled(!etw.isEnabled());
//            etw.clearFocus();
//        }
        if (ety != null) {
            ety.setEnabled(!ety.isEnabled());
            ety.setEnabled(!ety.isEnabled());
            ety.clearFocus();
        }

    }

    /**
     * @non-ui
     * @throws com.telephone.coursetable.MyException.MyException
     * @return return the res if success, otherwise throw exceptions
     * @trustme
     * @clear
     */
    public static HttpConnectionAndCode login(@NonNull Context c, @NonNull String sid, @NonNull String pwd, @NonNull String ckcode, @NonNull String cookie) throws Exception302, ExceptionWrongCheckCode, ExceptionWrongUserOrPassword, ExceptionNetworkError, ExceptionUnknown {
        final String NAME = "login()";
        Resources r = c.getResources();
        String body = "us=" + sid + "&pwd=" + pwd + "&ck=" + ckcode;
        HttpConnectionAndCode login_res = Post.post(
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626012d46dbfe/Login/SubmitLogin",
                null,
                r.getString(R.string.user_agent),
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626012d46dbfe/",
                body,
                cookie,
                "}",
                null,
                r.getString(R.string.lan_login_success_contain_response_text),
                null,
                false,
                null
        );
        if (login_res.code == 0){
            LoginResponse response = MyApp.gson.fromJson(login_res.comment, LoginResponse.class);
            login_res.comment = response.getMsg();
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "body: " + body + " code: " + login_res.code + " resp_code: " + login_res.resp_code + " comment/msg: " + login_res.comment);
            return login_res;
        }else if (login_res.code == -7){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "meet 302 -> " + login_res.c.getHeaderFields().get("location").get(0));
            throw new Exception302();
        }else if (login_res.comment != null && login_res.comment.contains("验证码")){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "body: " + body + " code: " + login_res.code + " resp_code: " + login_res.resp_code + " comment/msg: " + login_res.comment);
            throw new ExceptionWrongCheckCode();
        }else if (login_res.comment != null && login_res.comment.contains("密码")){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "body: " + body + " code: " + login_res.code + " resp_code: " + login_res.resp_code + " comment/msg: " + login_res.comment);
            throw new ExceptionWrongUserOrPassword();
        }else if (login_res.comment == null){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "network error");
            throw new ExceptionNetworkError();
        }else {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "unknown error");
            throw new ExceptionUnknown();
        }
    }

    /**
     * @non-ui
     * @throws com.telephone.coursetable.MyException.MyException
     * @return return true if success, otherwise throw exceptions
     * @trustme
     * @clear
     */
    public static boolean aaw_login(@NonNull Context c, @NonNull final String sid, @NonNull final String pwd, @NonNull String cookie) throws ExceptionNetworkError, ExceptionWrongUserOrPassword, Exception302, ExceptionUnknown {
        final String NAME = "aaw_login()";
        Resources r = c.getResources();
        String body = "username=" + sid + "&passwd=" + pwd + "&login=%B5%C7%A1%A1%C2%BC";
        com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "body", body);
        HttpConnectionAndCode login_res = com.telephone.coursetable.Https.Post.post(
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/public/login.asp",
                null,
                r.getString(R.string.user_agent),
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/",
                body,
                cookie,
                null,
                null,
                null,
                null,
                false,
                null
        );
        if (login_res.code == -7 && login_res.c.getHeaderFields().get("location").get(0).contains("menu.asp?menu=mnall.asp")){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "success -> " + login_res.c.getHeaderFields().get("location").get(0));
            return true;
        }else if (login_res.comment == null){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | network error");
            throw new ExceptionNetworkError();
        }else if (login_res.comment.contains("77726476706e69737468656265737421a1a013d2766626013051d0")){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | wrong user or password | " + login_res.comment);
            throw new ExceptionWrongUserOrPassword();
        }else if (login_res.code == -7){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | meet 302 -> " + login_res.c.getHeaderFields().get("location").get(0));
            throw new Exception302();
        }else {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | unknown error");
            throw new ExceptionUnknown();
        }
    }


    /**
     * @param pwd origin password
     * @return - String : the encrypted password
     * - null : fail
     * @ui/non-ui get encrypted password
     * @clear
     */
    public static String encrypt(String pwd) {
        int[] key = {134, 8, 187, 0, 251, 59, 238, 74, 176, 180, 24, 67, 227, 252, 205, 80};
        //for good, pwd's length should not be 0
        int pwd_len = pwd.length();
        try {
            if (pwd.length() % 16 != 0) {
                int need_num = 16 - pwd.length() % 16;
                StringBuilder pwd_builder = new StringBuilder();
                pwd_builder.append(pwd);
                for (int i = 0; i < need_num; i++) {
                    pwd_builder.append("0");
                }
                pwd = pwd_builder.toString();
            }
            byte[] pwd_bytes = pwd.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < pwd_bytes.length; i++) {
                pwd_bytes[i] ^= key[i % 16];
            }
            StringBuilder encrypt_builder = new StringBuilder();
            encrypt_builder.append("77726476706e6973617765736f6d6521");
            for (int i = 0; i < pwd_len; i++) {
                byte b = pwd_bytes[i];
                encrypt_builder.append(String.format("%02x", b));
            }
            return encrypt_builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @non-ui
     * @throws com.telephone.coursetable.MyException.MyException
     * @return return web vpn ticket if success, otherwise throw exceptions
     * @trustme
     * @clear
     */
    public static String vpn_login(@NonNull Context c, @NonNull String id, @NonNull String pwd) throws ExceptionWrongUserOrPassword, ExceptionNetworkError, ExceptionIpForbidden, ExceptionUnknown {
        final String NAME = "vpn_login()";
        Resources r = c.getResources();
        String body = "auth_type=local&username=" + id + "&sms_code=&password=" + pwd;
        com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "body", body);
        if (pwd.length() <= 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "empty password");
            throw new ExceptionWrongUserOrPassword();
        }
        pwd = encrypt(pwd);
        body = "auth_type=local&username=" + id + "&sms_code=&password=" + pwd;
        com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "encrypted body", body);
        HttpConnectionAndCode get_ticket_res = com.telephone.coursetable.Https.Get.get(
                r.getString(R.string.wan_vpn_get_ticket_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.wan_vpn_get_ticket_referer),
                null,
                null,
                r.getString(R.string.cookie_delimiter),
                null,
                null,
                null,
                null,
                null
        );
        String cookie = get_ticket_res.cookie;
        if (cookie == null || cookie.isEmpty()) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | can not get init vpn ticket");
            throw new ExceptionNetworkError();
        }
        cookie = cookie.substring(cookie.indexOf("wengine_vpn_ticket"));
        cookie = cookie.substring(0, cookie.indexOf(r.getString(R.string.cookie_delimiter)));
        cookie += r.getString(R.string.cookie_delimiter) + "show_vpn=1" + r.getString(R.string.cookie_delimiter) + "refresh=1";
        com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "ticket cookie", cookie);
        HttpConnectionAndCode try_to_login_res = com.telephone.coursetable.Https.Post.post(
                r.getString(R.string.wan_vpn_login_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.wan_vpn_login_referer),
                body,
                cookie,
                "}",
                null,
                r.getString(R.string.wan_vpn_login_success_contain_response_text),
                null,
                null,
                null
        );
        if (try_to_login_res.code == 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "success | " + try_to_login_res.comment);
            return cookie;
        } else if (try_to_login_res.comment != null && try_to_login_res.comment.contains("\"error\": \"INVALID_ACCOUNT\"")) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | " + try_to_login_res.comment);
            throw new ExceptionWrongUserOrPassword();
        } else if (try_to_login_res.comment != null && try_to_login_res.comment.contains("\"error\": \"NEED_CONFIRM\"")) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "need confirm", "confirm...");
            HttpConnectionAndCode confirm_login_res = com.telephone.coursetable.Https.Post.post(
                    r.getString(R.string.wan_vpn_confirm_login_url),
                    null,
                    r.getString(R.string.user_agent),
                    r.getString(R.string.wan_vpn_confirm_login_referer),
                    null,
                    cookie,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (confirm_login_res.code == 0) {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "success | confirm success");
                return cookie;
            }else {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | confirm fail");
                throw new ExceptionNetworkError();
            }
        } else if (try_to_login_res.comment != null && try_to_login_res.comment.contains("\"error\": \"IP_FORBIDDEN\"")) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | " + try_to_login_res.comment);
            throw new ExceptionIpForbidden();
        } else if (try_to_login_res.comment == null){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | network error");
            throw new ExceptionNetworkError();
        } else {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | unknown error");
            throw new ExceptionUnknown();
        }
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.LOGIN_VPN);
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
        title = getSupportActionBar().getTitle().toString();

        new Thread(()->my_comment_map = Methods.getMyCommentMap(gdao, cdao)).start();

        first_login();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * @ui 1. call {@link #clearIMAndFocus()}
     * 2. get the username in the sid input box
     * 3. show an AlertDialog to warn user:
     * - if press yes, a new thread will be started:
     * 1. try to delete the user from the database with the username in the sid input box
     * 2. call {@link #updateUserNameAutoFill()}
     * 3. clear sid input box and password input box
     * 4. set focus to sid input box
     * 5. call
     * - if press no, nothing will happen
     * @clear
     */
    public void deleteUser(View view) {

        final String NAME = "deleteUser()";
        clearIMAndFocus();
        String sid = ((AutoCompleteTextView) findViewById(R.id.sid_input)).getText().toString();
        Login.getAlertDialog(this, "确定要取消记住用户" + " " + sid + " " + "的登录信息吗？",
                (DialogInterface.OnClickListener) (dialogInterface, i) -> new Thread((Runnable) () -> {
                    udao.deleteUser(sid);
                    com.telephone.coursetable.LogMe.LogMe.e(NAME + " " + "user deleted", sid);
                    updateUserNameAutoFill();
                    runOnUiThread((Runnable) () -> {
//                        aaw_pwd = "";
                        sys_pwd = "";
                        ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText("");
                        ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText("");
                        setFocusToEditText((EditText)findViewById(R.id.sid_input));
                    });
                }).start(),
                (DialogInterface.OnClickListener) (dialogInterface, i) -> {},
                null, null, null, null).show();

    }

    /**
     * @return - true : everything is ok
     * - false : something went wrong
     * @non-ui 1. pull all user-related data from internet
     * 2. save the pulled data to database and shared preference
     * @clear
     */
    public static boolean fetch_merge(boolean formal, Context c, String cookie,
                                      HashMap<GoToClassKey, String> my_comm_map, String username,
                                      PersonInfoDao pdao, TermInfoDao tdao,
                                      GoToClassDao gdao, ClassInfoDao cdao, GraduationScoreDao gsdao,
                                      GradesDao grdao, ExamInfoDao edao , CETDao cetDao, LABDao labDao,
                                      SharedPreferences.Editor editor) {
        final String NAME = "fetch_merge()";
        HttpConnectionAndCode res;
        HttpConnectionAndCode res_add;

        LogMe.e(NAME, "fetching person info and student info");
        res = WAN.personInfo(c, cookie);
        res_add = WAN.studentInfo(c, cookie);
        if (res.code != 0 || res_add.code != 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch person info and student info success, merging...");
        Merge.personInfo(res.comment, res_add.comment, pdao);

        LogMe.e(NAME, "fetching term info");
        res = WAN.termInfo(c, cookie);
        if (res.code != 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch term info success, merging...");
        Merge.termInfo(c, res.comment, tdao);

        List<String> terms = tdao.getTermsSince(
                pdao.getGrade().get(0) + "-" + (pdao.getGrade().get(0) + 1) + "_1"
        );
        List<TermInfo> term_list = tdao.selectAll();
        for (TermInfo term : term_list) {
            if (terms.contains(term.term)) continue;
            tdao.deleteTerm(term.term);
        }
        LogMe.e(NAME, "fetching go-to-class and class info");
        res = WAN.goToClass_ClassInfo(c, cookie);
        if (res.code != 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch go-to-class and class info success, merging...");
        Merge.goToClass_ClassInfo(res.comment, gdao, cdao, my_comm_map, username);

        LogMe.e(NAME, "fetching graduation courses");
        res = WAN.graduationScore(c, cookie);
        res_add = WAN.graduationScore2(c,cookie);
        if (res.code != 0 || res_add.code != 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch graduation courses success, merging...");
        Merge.graduationScore(res.comment,res_add.comment,gsdao);

        LogMe.e(NAME, "fetching grades");
        res = WAN.grades(c, cookie);
        if (res.code != 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch grades success, merging...");
        Merge.grades(res.comment, grdao, formal, !formal);

        LogMe.e(NAME, "fetching exam info");
        res = WAN.examInfo(c, cookie);
        if (res.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch exam info success, merging...");
        Merge.examInfo(res.comment, edao, tdao, c, formal, !formal);

        LogMe.e(NAME, "fetching cet");
        res = WAN.cet(c, cookie);
        if (res.code != 0){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch cet success, merging...");
        Merge.cet(res.comment, cetDao);

        LogMe.e(NAME, "fetching hour info");
        res = WAN.hour(c, cookie);
        if (res.code != 0) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail");
            return false;
        }
        LogMe.e(NAME, "fetch hour info success, merging...");
        Merge.hour(c, res.comment, editor);

        term_list = tdao.selectAll();
        Locate locate = Clock.locateNow_low_api(Clock.nowTimeStamp(), tdao, MyApp.getCurrentSharedPreference(),
                MyApp.times,
                Clock.getDateTimeFormatterFor_locateNow_low_api(c),
                Clock.getSSFor_locateNow(c),
                Clock.getESFor_locateNow(c),
                Clock.getDSFor_locateNow(c)
        );
        if (locate.term != null) {
            for (TermInfo term : term_list) {
                if (!term.term.equals(locate.term.term)){
                    LogMe.e(NAME, "skip lab-fetch: " + term.term);
                    continue;
                }
                LogMe.e(NAME, "fetching lab");
                res.code = -1;
                for (int i = 0; i < 2 && res.code != 0 && res.code != -6 && res.code != -7; i++) {
                    LogMe.e(NAME, "fetching lab time: " + (i + 1));
                    res = WAN.lab(c, cookie, term.term);
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


    //clear
    public void login_thread_1(View view) {
        //after click button login , it will go to login_thread

        lock();
        clearIMAndFocus();

        sid = ((TextView) findViewById(R.id.sid_input)).getText().toString();
        vpn_pwd = ((TextView) findViewById(R.id.passwd_input)).getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {

                new Thread(()->{
                    try {
                        sleep(MyApp.patient_time);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    runOnUiThread(Login_vpn.this::try_to_show_patient);
                }).start();

                //get cookie
                cookie = wan_vpn_login_text(Login_vpn.this, sid, vpn_pwd);

                final String NAME = "login_thread_1()";

                /** detect new activity || skip no activity */
                if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.NULL)){
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "no activity is running, login = " + Login_vpn.this.toString() + " canceled");
                    runOnUiThread(()->Toast.makeText(Login_vpn.this, getResources().getString(R.string.wan_login_vpn_cancel_tip), Toast.LENGTH_SHORT).show());
                    return;
                }
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "login activity pointer = " + Login_vpn.this.toString());
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "running activity pointer = " + MyApp.getRunning_activity_pointer().toString());
                if (!Login_vpn.this.toString().equals(MyApp.getRunning_activity_pointer().toString())){
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "new running activity detected = " + MyApp.getRunning_activity_pointer().toString() + ", login = " + Login_vpn.this.toString() + " canceled");
                    runOnUiThread(()->Toast.makeText(Login_vpn.this, getResources().getString(R.string.wan_login_vpn_cancel_tip), Toast.LENGTH_SHORT).show());
                    return;
                }

                runOnUiThread(() -> {
                    if ( cookie.contains("fail:") ) {
                        Snackbar.make(view, cookie.substring(5), BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.WHITE).show();
                        unlock(true);
                        if ( cookie.equals(getResources().getString(R.string.login_fail_pwd_text_exception)) ) {
                            ((EditText) findViewById(R.id.passwd_input)).setText("");
                            setFocusToEditText((EditText) findViewById(R.id.passwd_input));
                        }

                        vpn_login_fail_times++;
                        if (vpn_login_fail_times >= 3){
                            jump(getResources().getString(R.string.wan_login_vpn_relogin_tip), Login_vpn.class, getSidPasswordExtraMap());
                        }
                    }else {
                        system_login(sid);
                    }
                });
            }
        }).start();
    }



    public void login_thread_2(View view){
        lock();
        clearIMAndFocus();

        if (getSupportActionBar().getTitle().toString().equals(getResources().getString(R.string.lan_title_login_updated_fail))){
            getSupportActionBar().setTitle(title);
        }

//        aaw_pwd = ((TextView) findViewById(R.id.aaw_pwd_input)).getText().toString();
//        aaw_pwd = "";
        sys_pwd = ((TextView) findViewById(R.id.sys_pwd_input)).getText().toString();

//        if( aaw_pwd.isEmpty() ){
//            retry(view, getResources().getString(R.string.wan_snackbar_outside_test_login_fail));
//            setFocusToEditText((EditText)findViewById(R.id.aaw_pwd_input));
//            return;
//        }

        if( sys_pwd.isEmpty() ){
            retry(view, getResources().getString(R.string.wan_snackbar_sys_pwd_login_fail));
            setFocusToEditText((EditText)findViewById(R.id.sys_pwd_input));
            return;
        }

        new Thread(()->{
            try {
                sleep(MyApp.patient_time);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            runOnUiThread(Login_vpn.this::try_to_show_patient);
        }).start();

        new Thread(()-> {

            int loop_syslogin_times = 0;
            do {
                /** -------------------------------------------------------------------------*/
                try {
                    StringBuilder cookie_temp = new StringBuilder().append(cookie);
                    ck = OCR.getTextFromBitmap(Login_vpn.this, try_to_get_check_code(Login_vpn.this, cookie_temp, sid, vpn_pwd), MyApp.ocr_lang_code);
                    cookie = cookie_temp.toString();
                } catch (Exception302 | ExceptionUnknown exception302) {
                    runOnUiThread(() -> jump(getResources().getString(R.string.wan_login_vpn_relogin_tip), Login_vpn.class, getSidPasswordExtraMap()));
                    return;
                } catch (ExceptionWrongUserOrPassword exceptionWrongUserOrPassword) {
                    runOnUiThread(() -> jump(getResources().getString(R.string.wan_snackbar_vpn_pwd_login_fail), Login_vpn.class, getSidPasswordExtraMap()));
                    return;
                } catch (ExceptionIpForbidden exceptionIpForbidden) {
                    runOnUiThread(() -> retry(view, getResources().getString(R.string.wan_login_vpn_ip_forbidden_tip)));
                    return;
                } catch (ExceptionNetworkError exceptionNetworkError) {
                    runOnUiThread(() -> retry(view, getResources().getString(R.string.wan_login_vpn_network_error_tip)));
                    return;
                }
                /** -------------------------------------------------------------------------*/

                int loop_getres_times = 0;
                boolean wck = false;
                do {
                    try {
                        login_res = login(Login_vpn.this, sid, sys_pwd, ck, cookie);
                        break;
                    } catch (Exception302 | ExceptionUnknown exception302) {
                        runOnUiThread(() -> jump(getResources().getString(R.string.wan_login_vpn_relogin_tip), Login_vpn.class, getSidPasswordExtraMap()));
                        return;
                    } catch (ExceptionWrongCheckCode exceptionWrongCheckCode) {
                        wck = true;
                        break;
                    } catch (ExceptionWrongUserOrPassword exceptionWrongUserOrPassword) {
                        runOnUiThread(() -> {
                            retry(view, getResources().getString(R.string.wan_snackbar_sys_pwd_login_fail));
                            ((EditText) findViewById(R.id.sys_pwd_input)).setText("");
                            setFocusToEditText((EditText) findViewById(R.id.sys_pwd_input));
                        });
                        return;
                    } catch (ExceptionNetworkError exceptionNetworkError) {
                        if (loop_getres_times++ < MyApp.web_vpn_relogin_times) continue;
                        runOnUiThread(() -> retry(view, getResources().getString(R.string.wan_login_vpn_network_error_tip)));
                        return;
                    }
                } while (true);

                if (!wck) {
                    break;
                } else if (loop_syslogin_times++ >= MyApp.web_vpn_wck_times) {
                    runOnUiThread(() -> {
                        retry(view, getResources().getString(R.string.wan_snackbar_unknown_fail));
                    });
                    return;
                }

            } while (true);

//            int loop_aawlogin_times = 0;
//            do {
//                try {
//                    outside_login_res = aaw_login(Login_vpn.this, sid, aaw_pwd, cookie);
//                    break;
//                } catch (ExceptionNetworkError exceptionNetworkError) {
//                    if (loop_aawlogin_times++ < MyApp.web_vpn_relogin_times) continue;
//                    runOnUiThread(() -> retry(view, getResources().getString(R.string.wan_login_vpn_network_error_tip)));
//                    return;
//                } catch (ExceptionWrongUserOrPassword exceptionWrongUserOrPassword) {
//                    runOnUiThread(() -> {
//                        retry(view, getResources().getString(R.string.wan_snackbar_outside_test_login_fail));
//                        ((EditText) findViewById(R.id.aaw_pwd_input)).setText("");
//                        setFocusToEditText((EditText) findViewById(R.id.aaw_pwd_input));
//                    });
//                    return;
//                } catch (Exception302 | ExceptionUnknown exception302) {
//                    runOnUiThread(() -> jump(getResources().getString(R.string.wan_login_vpn_relogin_tip), Login_vpn.class, getSidPasswordExtraMap()));
//                    return;
//                }
//            } while (true);

            /** get shared preference and its editor */
            final SharedPreferences shared_pref = MyApp.getCurrentSharedPreference();
            final SharedPreferences.Editor editor = MyApp.getCurrentSharedPreferenceEditor();

            final String NAME = "login_thread_2()";

            /** detect new activity || skip no activity */
            if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.NULL)) {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "no activity is running, login = " + Login_vpn.this.toString() + " canceled");
                runOnUiThread(() -> Toast.makeText(Login_vpn.this, getResources().getString(R.string.wan_login_vpn_cancel_tip), Toast.LENGTH_SHORT).show());
                return;
            }
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "login activity pointer = " + Login_vpn.this.toString());
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "running activity pointer = " + MyApp.getRunning_activity_pointer().toString());
            if (!Login_vpn.this.toString().equals(MyApp.getRunning_activity_pointer().toString())) {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "new running activity detected = " + MyApp.getRunning_activity_pointer().toString() + ", login = " + Login_vpn.this.toString() + " canceled");
                runOnUiThread(() -> Toast.makeText(Login_vpn.this, getResources().getString(R.string.wan_login_vpn_cancel_tip), Toast.LENGTH_SHORT).show());
                return;
            }

            // edit by Telephone 2020/11/23 09:46, get currently activated username
            String username = null;
            if (!udao.getActivatedUser().isEmpty()){
                username = udao.getActivatedUser().get(0).username;
            }
            /** insert/replace new user into database */
//            udao.insert(new User(sid, aaw_pwd, sys_pwd, vpn_pwd));
            udao.insert(new User(sid, sys_pwd, "", vpn_pwd));
            /** deactivate all user in database */
            udao.disableAllUser();
            /** set {@link MyApp#running_login_thread} to true */
            MyApp.setRunning_login_thread(true);
            /** show tip snack-bar, change title */
            runOnUiThread(() -> {
                Snackbar.make(view, getResources().getString(R.string.lan_snackbar_data_updating), BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.WHITE).show();
                getSupportActionBar().setTitle(getResources().getString(R.string.lan_title_login_updating));
            });

            help_cookie = cookie; //set help-cookie after login success

            try { // this is an Accident Prone Area
                int times = 0;
                boolean fetch_merge_res = false;
                while (times < MyApp.web_vpn_refetch_times && !fetch_merge_res) {
                    // it seems to be useless
//                    if (times >= MyApp.web_vpn_refetch_times / 3) {
//                        if (login_res.c != null) {
//                            login_res.c.disconnect();
//                        }
//                    }
                    /** clear shared preference */
                    editor.clear();
                    /** commit shared preference */
                    editor.commit();
                    /** call {@link #deleteOldDataFromDatabase()} */
                    Login.deleteOldDataFromDatabase(username, gdao, cdao, tdao, pdao, gsdao, grdao, edao, cetDao, labDao);
                    fetch_merge_res = fetch_merge(true, Login_vpn.this, cookie, my_comment_map, sid, pdao, tdao, gdao, cdao, gsdao, grdao, edao, cetDao, labDao, editor);
                    times++;
                }

                /** commit shared preference */
                editor.commit();

                if (fetch_merge_res) {

                    /** locate now, print the locate-result to log */
                    com.telephone.coursetable.LogMe.LogMe.e(
                            NAME + " " + "locate now",
                            Clock.locateNow_low_api(
                                    Clock.nowTimeStamp(), tdao, shared_pref, MyApp.times,
                                    Clock.getDateTimeFormatterFor_locateNow_low_api(Login_vpn.this),
                                    getResources().getString(R.string.pref_hour_start_suffix),
                                    getResources().getString(R.string.pref_hour_end_suffix),
                                    getResources().getString(R.string.pref_hour_des_suffix)
                            ) + ""
                    );

                    udao.activateUser(sid);

                    MyApp.setRunning_login_thread(false);

                    runOnUiThread(() -> {
                        unlock(false);
                        Toast.makeText(Login_vpn.this, getResources().getString(R.string.lan_toast_update_success), Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle(getResources().getString(R.string.lan_title_login_updated));
                        if (!MyApp.getRunning_activity().equals(MyApp.RunningActivity.NULL)) {
                            com.telephone.coursetable.LogMe.LogMe.e(NAME, "start a new Main Activity...");
                            /** start a new {@link MainActivity} */
                            startActivity(new Intent(Login_vpn.this, MainActivity.class));
                        } else {
                            com.telephone.coursetable.LogMe.LogMe.e(NAME, "update success but no activity is running, NOT start new Main Activity");
                        }
                    });

                } else {
                    /** set {@link MyApp#running_login_thread} to false */
                    MyApp.setRunning_login_thread(false);
                    /** if login activity is current running activity */
                    if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN_VPN)) {
                        runOnUiThread(() -> {
                            unlock(true);
                            /** show tip snack-bar, change title */
                            Snackbar.make(view, getResources().getString(R.string.lan_toast_update_fail), BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.WHITE).show();
                            getSupportActionBar().setTitle(getResources().getString(R.string.lan_title_login_updated_fail));
                        });
                    } else {
                        runOnUiThread(() -> {
                            /** show tip toast */
                            Toast.makeText(Login_vpn.this, getResources().getString(R.string.lan_toast_update_fail), Toast.LENGTH_SHORT).show();
                            /** if main activity is current running activity */
                            if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.MAIN) && MyApp.getRunning_main() != null) {
                                com.telephone.coursetable.LogMe.LogMe.e(NAME, "refresh the Main Activity...");
                                /** call {@link MainActivity#refresh()} */
                                MyApp.getRunning_main().refresh();
                            }
                        });
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                runOnUiThread(()->Toast.makeText(Login_vpn.this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show());
                startActivity(new Intent(Login_vpn.this, MainActivity.class));
            }
        }).start();
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


}


