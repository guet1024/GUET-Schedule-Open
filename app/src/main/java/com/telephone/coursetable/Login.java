package com.telephone.coursetable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.telephone.coursetable.Database.AppDatabase;
import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Gson.Hour;
import com.telephone.coursetable.Gson.Hours;
import com.telephone.coursetable.Gson.Person;
import com.telephone.coursetable.Gson.PersonInfo;
import com.telephone.coursetable.Gson.Table;
import com.telephone.coursetable.Gson.TableNode;
import com.telephone.coursetable.Gson.Term;
import com.telephone.coursetable.Gson.Terms;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Login extends AppCompatActivity {

    //the StringBuilder storing the latest cookie
    //will be update in login() when login success and in changeCode() when changeCode() called
    private StringBuilder cookie_builder;

    //the res of the latest get-check-code
    private HttpConnectionAndCode ck_res;

    //database of the whole app
    private AppDatabase db = null;

    //DAOs of the database of the whole app
    private GoToClassDao gdao = null;
    private ClassInfoDao cdao = null;
    private TermInfoDao tdao = null;
    private UserDao  udao = null;
    private PersonInfoDao pdao = null;

    /**
     * @ui
     * @Login-ui
     * @any
     * call this method in UI thread of Login Activity, it get-check-code and update the check-code-ImageView.
     * success or not, the old result and the old cookie will be cleared anyway.
     */
    public void changeCode(View view){
        //clear old result
        ck_res = new HttpConnectionAndCode(null, 1);
        //clear old cookie
        cookie_builder = new StringBuilder();

        CheckCode.ShowCheckCode_thread(this, (ImageView)findViewById(R.id.imageView_checkcode), getResources().getString(R.string.checkcode_url), cookie_builder, getResources().getString(R.string.cookie_delimiter), ck_res);
    }

    /**
     * @non-ui
     * @Login-ui
     * @any
     * select all user names from database and then use these names to make an ArrayAdapter<String>.
     * update the auto-fill list of Login Activity's username input box with this ArrayAdapter<String>
     * in the UI thread of Login Activity.
     * this method must be called in a non-UI thread because it has database query operation.
     */
    private void updateUserNameAutoFill(){
        final ArrayAdapter<String> ada = new ArrayAdapter<String>(Login.this, android.R.layout.simple_dropdown_item_1line, udao.selectAllUserName());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((AutoCompleteTextView)findViewById(R.id.sid_input)).setAdapter(ada);
            }
        });
    }

    /**
     * return an AlertDialog with specified message and specified OnClickListener
     */
    private AlertDialog getAlertDialog(final String m, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(m)
                .setPositiveButton(getResources().getString(R.string.ok_btn_text_zhcn), yes)
                .setNegativeButton(getResources().getString(R.string.deny_btn_zhcn), no);
        return builder.create();
    }

    /**
     * @non-ui
     * try to login with specified student id, password, check-code and cookie.
     * if login success, append the new cookie to old cookie.
     * @return
     * - 0 login success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -3 cannot get data output stream
     * - -4 error when post
     * - -5 cannot get response
     * - -6 login fail due to wrong check-code
     * - -7 login fail due to wrong password
     * - -8 login fail due to other reason
     */
    private HttpConnectionAndCode login(final String sid, final String pwd, final String code, final String cookie){
        URL url = null;
        HttpURLConnection cnt = null;
        DataOutputStream dos = null;
        InputStreamReader in = null;
        String response = null;
        int resp_code = 0;
        try {
            url = new URL(getResources().getString(R.string.login_url));
            cnt = (HttpURLConnection) url.openConnection();
            cnt.setDoOutput(true);
            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", getResources().getString(R.string.user_agent));
            cnt.setRequestProperty("Content-Length", String.valueOf(sid.length() + pwd.length() + code.length() + 12));
            if (cookie.length() > 0){
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("POST");
            cnt.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -1);
        }
        String body = "";
        body += "us=" + sid;
        body += "&pwd=" + pwd;
        body += "&ck=" + code;
        try {
            dos = new DataOutputStream(cnt.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -3);
        }
        try {
            dos.writeBytes(body);
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -4);
        }
        Log.e("login() POST body", body);
        try {
            resp_code = cnt.getResponseCode();
            Log.e("login() POST response code", ""+resp_code);
            in = new InputStreamReader(cnt.getInputStream());
            //getContentLength() returns the "Content-Length" value in the response header
            int content_len = cnt.getContentLength();
            StringBuilder response_builder = new StringBuilder();
            for (int i = 0; i < content_len; i++){
                //the conversion to char is necessary
                response_builder.append((char)in.read());
            }
            response = response_builder.toString();
            if (response.contains("}")){
                response = response.substring(0, response.indexOf("}") + 1);
            }
            Log.e("login() POST response", response);
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -5);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -2);
        }
        if (!response.contains(getResources().getString(R.string.login_success_contain_response_text))){
            if (response.contains(getResources().getString(R.string.login_fail_ck_text))) {
                return new HttpConnectionAndCode(cnt, -6);
            }else if (response.contains(getResources().getString(R.string.login_fail_pwd_text))){
                return new HttpConnectionAndCode(cnt, -7);
            }else{
                return new HttpConnectionAndCode(cnt, -8);
            }
        }

        //if login success, append new cookie to old cookie
        CookieManager cookieman = new CookieManager();
        //getHeaderFields() returns the header fields of response
        List<String> cookies = cnt.getHeaderFields().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie_resp : cookies) {
                cookieman.getCookieStore().add(null, HttpCookie.parse(cookie_resp).get(0));
            }
        }
        if (cookieman.getCookieStore().getCookies().size() > 0) {
            String cookie_old = cookie_builder.toString();
            cookie_builder = new StringBuilder();
            cookie_builder.append(cookie_old).append(getResources().getString(R.string.cookie_delimiter));
            cookie_builder.append(TextUtils.join(getResources().getString(R.string.cookie_delimiter), cookieman.getCookieStore().getCookies()));
        }

        //do not disconnect, keep alive
        return new HttpConnectionAndCode(cnt, 0);
    }

    /**
     * @non-ui
     * try to personal information information with specified cookie.
     * if success, put the personal information(json) in return value's "comment" field.
     * @return
     * - 0 get personal info success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -5 cannot get response
     * - -6 get personal info fail
     */
    private HttpConnectionAndCode getPersonInfo(final String cookie){
        URL url = null;
        HttpURLConnection cnt = null;
        InputStreamReader in = null;
        String response = null;
        int resp_code = 0;
        try {
            url = new URL(getResources().getString(R.string.get_person_url));
            cnt = (HttpURLConnection) url.openConnection();
            cnt.setDoOutput(true);
            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", getResources().getString(R.string.user_agent));
            cnt.setRequestProperty("Referer", getResources().getString(R.string.get_person_referer));
            if (cookie.length() > 0){
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("GET");
            cnt.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -1);
        }
        try {
            resp_code = cnt.getResponseCode();
            Log.e("getPersonInfo() GET response code", ""+resp_code);
            in = new InputStreamReader(cnt.getInputStream());
            //getContentLength() returns the "Content-Length" value in the response header
            int content_len = cnt.getContentLength();
            StringBuilder response_builder = new StringBuilder();
            for (int i = 0; i < content_len; i++){
                //the conversion to char is necessary
                response_builder.append((char)in.read());
            }
            response = response_builder.toString();
            if (response.contains("}}")){
                response = response.substring(0, response.indexOf("}}") + 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -5);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -2);
        }
        if (!response.contains(getResources().getString(R.string.get_person_success_contain_response_text))){
            return new HttpConnectionAndCode(cnt, -6);
        }
        //do not disconnect, keep alive
        return new HttpConnectionAndCode(cnt, 0, response);
    }

    /**
     * @non-ui
     * try to get terms information with specified cookie.
     * if success, put the terms information(json) in return value's "comment" field.
     * @return
     * - 0 get terms success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -5 cannot get response
     * - -6 get terms fail
     */
    private HttpConnectionAndCode getTerms(final String cookie){
        URL url = null;
        HttpURLConnection cnt = null;
        InputStreamReader in = null;
        String response = null;
        int resp_code = 0;
        try {
            url = new URL(getResources().getString(R.string.get_term_url));
            cnt = (HttpURLConnection) url.openConnection();
            cnt.setDoOutput(true);
            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", getResources().getString(R.string.user_agent));
            cnt.setRequestProperty("Referer", getResources().getString(R.string.get_term_referer));
            if (cookie.length() > 0){
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("GET");
            cnt.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -1);
        }
        try {
            resp_code = cnt.getResponseCode();
            Log.e("getTerms() GET response code", ""+resp_code);
            in = new InputStreamReader(cnt.getInputStream());
            //getContentLength() returns the "Content-Length" value in the response header
            int content_len = cnt.getContentLength();
            StringBuilder response_builder = new StringBuilder();
            for (int i = 0; i < content_len; i++){
                //the conversion to char is necessary
                response_builder.append((char)in.read());
            }
            response = response_builder.toString();
            if (response.contains("]}")){
                response = response.substring(0, response.indexOf("]}") + 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -5);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -2);
        }
        if (!response.contains(getResources().getString(R.string.get_term_success_contain_response_text))){
            return new HttpConnectionAndCode(cnt, -6);
        }
        //do not disconnect, keep alive
        return new HttpConnectionAndCode(cnt, 0, response);
    }

    /**
     * @non-ui
     * try to get table information of specified term with specified cookie.
     * if success, put the table information(json) in return value's "comment" field.
     * @return
     * - 0 get table success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -5 cannot get response
     * - -6 get table fail
     */
    private HttpConnectionAndCode getTable(final String cookie, final String term){
        URL url = null;
        HttpURLConnection cnt = null;
        InputStreamReader in = null;
        String response = null;
        int resp_code = 0;
        try {
            url = new URL(getResources().getString(R.string.get_table_url) + "?term=" + term);
            cnt = (HttpURLConnection) url.openConnection();
            cnt.setDoOutput(true);
            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", getResources().getString(R.string.user_agent));
            cnt.setRequestProperty("Referer", getResources().getString(R.string.get_table_referer));
            if (cookie.length() > 0){
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("GET");
            cnt.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -1);
        }
        try {
            resp_code = cnt.getResponseCode();
            Log.e("getTable() GET response code", ""+resp_code);
            in = new InputStreamReader(cnt.getInputStream());
            //getContentLength() returns the "Content-Length" value in the response header
            int content_len = cnt.getContentLength();
            StringBuilder response_builder = new StringBuilder();
            for (int i = 0; i < content_len; i++){
                //the conversion to char is necessary
                response_builder.append((char)in.read());
            }
            response = response_builder.toString();
            if (response.contains("]}")){
                response = response.substring(0, response.indexOf("]}") + 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -5);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -2);
        }
        if (!response.contains(getResources().getString(R.string.get_table_success_contain_response_text))){
            return new HttpConnectionAndCode(cnt, -6);
        }
        //do not disconnect, keep alive
        return new HttpConnectionAndCode(cnt, 0, response);
    }

    /**
     * @non-ui
     * try to get hours information with specified cookie.
     * if success, put the hours information(json) in return value's "comment" field.
     * @return
     * - 0 get hours success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -5 cannot get response
     * - -6 get hours fail
     */
    private HttpConnectionAndCode getHours(final String cookie){
        URL url = null;
        HttpURLConnection cnt = null;
        InputStreamReader in = null;
        String response = null;
        int resp_code = 0;
        try {
            url = new URL(getResources().getString(R.string.get_hours_url));
            cnt = (HttpURLConnection) url.openConnection();
            cnt.setDoOutput(true);
            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", getResources().getString(R.string.user_agent));
            cnt.setRequestProperty("Referer", getResources().getString(R.string.get_hours_referer));
            if (cookie.length() > 0){
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("GET");
            cnt.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -1);
        }
        try {
            resp_code = cnt.getResponseCode();
            Log.e("getHours() GET response code", ""+resp_code);
            in = new InputStreamReader(cnt.getInputStream());
            //getContentLength() returns the "Content-Length" value in the response header
            int content_len = cnt.getContentLength();
            StringBuilder response_builder = new StringBuilder();
            for (int i = 0; i < content_len; i++){
                //the conversion to char is necessary
                response_builder.append((char)in.read());
            }
            response = response_builder.toString();
            if (response.contains("]}")){
                response = response.substring(0, response.indexOf("]}") + 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -5);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(null, -2);
        }
        if (!response.contains(getResources().getString(R.string.get_hours_success_contain_response_text))){
            return new HttpConnectionAndCode(cnt, -6);
        }
        //do not disconnect, keep alive
        return new HttpConnectionAndCode(cnt, 0, response);
    }

    /**
     * return 0 means that sts > nts
     */
    public static long whichWeek(final long sts, final long nts){
        long res = 0;
        if (nts >= sts){
            res = 1;
            res += (nts - sts) / 604800000;
        }
        return res;
    }

    /**
     * @non-ui
     * key = timeno + suffix
     * return null means not found
     */
    public static TImeAndDescription whichTime(SharedPreferences pref, String[] timenos, DateTimeFormatter formatter, String s_suffix, String e_suffix, String d_suffix){
        TImeAndDescription res = null;
        LocalTime n =  LocalTime.now();
        for (String timeno : timenos) {
            String sj = pref.getString(timeno + s_suffix, null);
            String ej = pref.getString(timeno + e_suffix, null);
            String des = pref.getString(timeno + d_suffix, null);
            if (sj != null && ej != null && des != null) {
                LocalTime sl = LocalTime.parse(sj, formatter);
                LocalTime el = LocalTime.parse(ej, formatter);
                if (sl.isBefore(n) || sl.equals(n)) {
                    if (el.isAfter(n) || el.equals(n)) {
                        res = new TImeAndDescription(timeno, des);
                        break;
                    }
                }
            }
        }
        return res;
    }

    /**
     * @non-ui
     */
    public static Locate locateNow(long nts, TermInfoDao tdao, SharedPreferences pref, String[] times, DateTimeFormatter server_hours_time_format, String pref_s_suffix, String pref_e_suffix, String pref_d_suffix){
        Locate res = new Locate(null, 0, 0, 0, 0, null, null);
        List<TermInfo> which_term_res = tdao.whichTerm(nts);
        if (!which_term_res.isEmpty()){
            res.term = which_term_res.get(0);
            res.week = whichWeek(res.term.sts, nts);
        }
        res.weekday = LocalDateTime.now().getDayOfWeek().getValue();
        res.month = LocalDateTime.now().getMonthValue();
        res.day = LocalDateTime.now().getDayOfMonth();
        TImeAndDescription which_time_res = whichTime(pref, times, server_hours_time_format, pref_s_suffix, pref_e_suffix, pref_d_suffix);
        if (which_time_res != null && (!which_term_res.isEmpty())){
            res.time = which_time_res.time;
            res.time_description = which_time_res.des;
        }
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
        //get-check-code on create
        changeCode(null);
        db = MyApp.getCurrentAppDB();
        gdao = db.goToClassDao();
        cdao = db.classInfoDao();
        tdao = db.termInfoDao();
        udao = db.userDao();
        pdao = db.personInfoDao();
        //init auto-fill list of username input box on create
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateUserNameAutoFill();
            }
        }).start();
        //set OnDismissListener of username input box's auto-fill list to auto-fill password corresponding to the username inputted
        ((AutoCompleteTextView)findViewById(R.id.sid_input)).setOnDismissListener(new AutoCompleteTextView.OnDismissListener(){
            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<User> userSelected = udao.selectUser(((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString());
                        if (!userSelected.isEmpty()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText(userSelected.get(0).password);
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        //if any user is activated, fill his sid and pwd in the input box
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> ac_user = udao.getActivatedUser();
                if (!ac_user.isEmpty()){
                    final User u = ac_user.get(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText(u.username);
                            ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText(u.password);
                            ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).setText("");
                            ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).requestFocus();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        getSharedPreferences(getResources().getString(R.string.hours_preference_file_name), MODE_PRIVATE).edit().putBoolean(getResources().getString(R.string.pref_user_updating_key), false).commit();
        super.onPause();
    }

    /**
     * when user clicks the delete btn, show a AlertDialog to ask user to confirm to forget the information
     * of the user specified by the username which is in the username input box.
     *
     * if user click no, nothing will happen
     * if user click yes, start a new thread to do these things:
     *  1. delete user information from database
     *  2. use the data in the database to update the auto-fill list of Login Activity's username input box
     *  3. clear all the input boxes of Login Activity in Login Activity UI thread
     */
    public void deleteUser(View view){
        getAlertDialog("确定要取消记住用户" + " " + ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString() + " " + "的登录信息吗？",
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            udao.deleteUser(((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString());
                            updateUserNameAutoFill();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText("");
                                    ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText("");
                                    ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).setText("");
                                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).requestFocus();
                                }
                            });
                        }
                    }).start();
                }
            },
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
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
        ((AutoCompleteTextView)findViewById(R.id.sid_input)).setEnabled(false);
        ((AutoCompleteTextView)findViewById(R.id.sid_input)).setEnabled(true);
        ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setEnabled(false);
        ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setEnabled(true);
        ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).setEnabled(false);
        ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).setEnabled(true);
        final String sid = ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString();
        final String pwd = ((AutoCompleteTextView)findViewById(R.id.passwd_input)).getText().toString();
        final String ck = ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).getText().toString();
        final String cookie = cookie_builder.toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpConnectionAndCode login_res = login(sid, pwd, ck, cookie);
                if (login_res.code != 0){
                    String toast = null;
                    final int type = login_res.code;
                    if (type == -6){
                        toast = getResources().getString(R.string.toast_login_fail_ck);
                    }else if (type == -7){
                        toast = getResources().getString(R.string.toast_login_fail_pwd);
                    }else {
                        toast = getResources().getString(R.string.toast_login_fail) + " - " + type;
                    }
                    final String toast_f = toast;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            getSupportActionBar().setTitle(title_f);
//                            Toast.makeText(Login.this, toast_f, Toast.LENGTH_LONG).show();
                            Snackbar.make(view, toast_f, BaseTransientBottomBar.LENGTH_SHORT).show();
                            changeCode(null);
                            ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).setText("");
                            ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).requestFocus();
                            if (type == -7) {
                                ((AutoCompleteTextView) findViewById(R.id.passwd_input)).setText("");
                                ((AutoCompleteTextView) findViewById(R.id.passwd_input)).requestFocus();
                            }

                        }
                    });
                    return;
                }
                SharedPreferences hours_pref = getSharedPreferences(getResources().getString(R.string.hours_preference_file_name), MODE_PRIVATE);
                SharedPreferences.Editor editor = hours_pref.edit();
                udao.insert(new User(sid, pwd));
                udao.disableAllUser();
                editor.clear();
                editor.putBoolean(getResources().getString(R.string.pref_user_updating_key), true);
                editor.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(Login.this, getResources().getString(R.string.toast_login_success), Toast.LENGTH_LONG).show();
                        Snackbar.make(view, getResources().getString(R.string.toast_login_success), BaseTransientBottomBar.LENGTH_LONG).show();
                        //make a tip to show data-update status
                        getSupportActionBar().setTitle(getResources().getString(R.string.title_login_updating));
                        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                    }
                });
                cdao.deleteAll();
                gdao.deleteAll();
                tdao.deleteAll();
                pdao.deleteAll();
                /**
                 * ******************************* UPDATE DATA START *******************************
                 */
                //update person info
                HttpConnectionAndCode getPersonInfo_res = getPersonInfo(cookie_builder.toString());
                //if success, insert data into database
                if (getPersonInfo_res.code == 0){
                    Person person = new Gson().fromJson(getPersonInfo_res.comment, Person.class);
                    PersonInfo p = person.getData();
                    //extract information and then insert into database
                    pdao.insert(new com.telephone.coursetable.Database.PersonInfo(p.getStid(), p.getGrade(),p.getClassno(),p.getSpno(),p.getName(),p.getName1(),
                            p.getEngname(),p.getSex(),p.getPass(),p.getDegree(),p.getDirection(),p.getChangetype(),p.getSecspno(),p.getClasstype(),p.getIdcard(),
                            p.getStype(),p.getXjzt(),p.getChangestate(),p.getLqtype(),p.getZsjj(),p.getNation(),p.getPolitical(),p.getNativeplace(),
                            p.getBirthday(),p.getEnrolldate(),p.getLeavedate(),p.getDossiercode(),p.getHostel(),p.getHostelphone(),p.getPostcode(),p.getAddress(),
                            p.getPhoneno(),p.getFamilyheader(),p.getTotal(),p.getChinese(),p.getMaths(),p.getEnglish(),p.getAddscore1(),p.getAddscore2(),p.getComment(),
                            p.getTestnum(),p.getFmxm1(),p.getFmzjlx1(),p.getFmzjhm1(),p.getFmxm2(),p.getFmzjlx2(),p.getFmzjhm2(),p.getDs(),p.getXq(),p.getRxfs(),p.getOldno()));
                }
                //update terms info
                HttpConnectionAndCode getTerms_res = getTerms(cookie_builder.toString());
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
                    HttpConnectionAndCode getTable_res = getTable(cookie_builder.toString(), t.term);
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
                HttpConnectionAndCode getHours_res = getHours(cookie_builder.toString());
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
                        editor.putString(h.getNodeno() + getResources().getString(R.string.hours_pref_time_start_suffix), stime);
                        editor.putString(h.getNodeno() + getResources().getString(R.string.hours_pref_time_end_suffix), etime);
                        editor.putString(h.getNodeno() + getResources().getString(R.string.hours_pref_time_des_suffix), des);
                        editor.putString(h.getNodeno() + getResources().getString(R.string.hours_pref_time_start_backup_suffix), stime);
                        editor.putString(h.getNodeno() + getResources().getString(R.string.hours_pref_time_end_backup_suffix), etime);
                        editor.putString(h.getNodeno() + getResources().getString(R.string.hours_pref_time_des_backup_suffix), des);
                    }
                    editor.commit();
                }
                //locate today
                long nts = Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern(getResources().getString(R.string.ts_datetime_format)))).getTime();
                DateTimeFormatter server_hours_time_formatter = DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format));
                Locate locate_res = locateNow(nts, tdao, hours_pref, MyApp.times, server_hours_time_formatter,
                        getResources().getString(R.string.hours_pref_time_start_suffix),
                        getResources().getString(R.string.hours_pref_time_end_suffix),
                        getResources().getString(R.string.hours_pref_time_des_suffix));
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
                editor.putBoolean(getResources().getString(R.string.pref_user_updating_key), false);
                editor.commit();
                com.telephone.coursetable.Database.PersonInfo acuser = pdao.selectAll().get(0);
                Log.e("login_thread() user activated", acuser.stid + " " + acuser.name);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                        Toast.makeText(Login.this, getResources().getString(R.string.toast_update_success), Toast.LENGTH_SHORT).show();
//                        Snackbar.make(view, getResources().getString(R.string.toast_update_success), BaseTransientBottomBar.LENGTH_SHORT).show();
                        //make a tip to show data-update status
                        getSupportActionBar().setTitle(getResources().getString(R.string.title_login_updated));
                    }
                });
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        }).start();
    }
}