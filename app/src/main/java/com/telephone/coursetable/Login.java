package com.telephone.coursetable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.telephone.coursetable.Database.AppDatabase;
import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Login extends AppCompatActivity {

    private StringBuilder cookie_builder;
    private HttpConnectionAndCode ck_res;

    private AppDatabase db = null;
    private GoToClassDao gdao = null;
    private ClassInfoDao cdao = null;
    private TermInfoDao tdao = null;
    private UserDao  udao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ck_res = new HttpConnectionAndCode(null, 1);
        cookie_builder = new StringBuilder();
        CheckCode.ShowCheckCode_thread(this, (ImageView)findViewById(R.id.imageView_checkcode), getResources().getString(R.string.checkcode_url), cookie_builder, getResources().getString(R.string.cookie_delimiter), ck_res);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "telephone_db").build();
        gdao = db.goToClassDao();
        cdao = db.classInfoDao();
        tdao = db.termInfoDao();
        udao = db.userDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                updataUserNameAutoFill();
                List<User> userList = udao.selectAll();
                if (!userList.isEmpty()){
                    final User userFill = userList.get(userList.size() - 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText(userFill.username);
                            ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText(userFill.password);
                        }
                    });
                }
            }
        }).start();

        //set OnDismissListener of username input box to auto-fill password corresponding to the username inputted
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

    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private AlertDialog getAlertDialog(final String m, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(m)
                .setPositiveButton(getResources().getString(R.string.ok_btn_text_zhcn), yes)
                .setNegativeButton(getResources().getString(R.string.deny_btn_zhcn), no);
        return builder.create();
    }

    //delete user in database
    //update auto-fill list of username input box
    //clear all input box
    public void deleteUser(View view){
        getAlertDialog("确定要取消记住用户" + " " + ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString() + " " + "的登录信息吗？",
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            udao.deleteUser(((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString());
                            updataUserNameAutoFill();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText("");
                                    ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText("");
                                    ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).setText("");
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

    //update the auto-fill list of the username input box in the UI thread of Login Activity, must be called in a non-UI thread
    private void updataUserNameAutoFill(){
        final ArrayAdapter<String> ada = new ArrayAdapter<String>(Login.this, android.R.layout.simple_dropdown_item_1line, udao.selectAllUserName());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((AutoCompleteTextView)findViewById(R.id.sid_input)).setAdapter(ada);
            }
        });
    }

    /**
     *
     * @return
     * - 0 login success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -3 cannot get data output stream
     * - -4 error when post
     * - -5 cannot get response
     * - -6 login fail
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
            return new HttpConnectionAndCode(cnt, -6);
        }

        //if login success, update cookie
        CookieManager cookieman = new CookieManager();
        //getHeaderFields() returns the header fields of response
        List<String> cookies = cnt.getHeaderFields().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie_resp : cookies) {
                cookieman.getCookieStore().add(null, HttpCookie.parse(cookie_resp).get(0));
            }
        }
        if (cookieman.getCookieStore().getCookies().size() > 0) {
            cookie_builder = new StringBuilder();
            cookie_builder.append(cookie).append(getResources().getString(R.string.cookie_delimiter));
            cookie_builder.append(TextUtils.join(getResources().getString(R.string.cookie_delimiter), cookieman.getCookieStore().getCookies()));
        }
        //if login success, insert the username and password into database
        udao.insert(new User(sid, pwd));
        //then update the auto-fill list of username input
        updataUserNameAutoFill();
        //do not disconnect, keep alive
        return new HttpConnectionAndCode(cnt, 0);
    }

    public void login_thread(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cookie_last = cookie_builder.toString();
                String ck_res_last = "c="+ck_res.c.toString()+" code="+ck_res.code;
                Log.e("login_thread() last cookie", cookie_last);
                Log.e("login_thread() last get-check-code return", ck_res_last);
                int login_res = login(
                        ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString(),
                        ((AutoCompleteTextView)findViewById(R.id.passwd_input)).getText().toString(),
                        ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).getText().toString(),
                        cookie_last
                ).code;
                if (login_res == 0){
//                    Toast.makeText(Login.this, R.string.toast_login_success, Toast.LENGTH_LONG).show();
                    Log.e("login_thread() login status", "success");
                }else {
//                    Toast.makeText(Login.this, R.string.toast_login_fail, Toast.LENGTH_LONG).show();
                    Log.e("login_thread() login status", "fail");
                }
                cookie_last = cookie_builder.toString();
                Log.e("login_thread() cookie after login", cookie_last);

                List<TermInfo> test = tdao.selectAll();
                List<ClassInfo> test2 = cdao.selectAll();
                List<GoToClass> test3 = gdao.selectAll();
                List<User> test4 = udao.selectAll();

                HttpConnectionAndCode getTerms_res = getTerms(cookie_last);
                if (getTerms_res.code == 0){
                    Terms terms = new Gson().fromJson(getTerms_res.comment, Terms.class);
                    List<Term> term_data = terms.getData();
                    tdao.deleteAll();
                    for (Term t : term_data){
                        tdao.insert(new TermInfo(t.getTerm(), t.getStartdate(), t.getEnddate(), t.getWeeknum(), t.getTermname(), t.getSchoolyear(), t.getComm()));
                    }
                }
                HttpConnectionAndCode getTable_res = getTable(cookie_last, "2020-2021_1");
                if (getTable_res.code == 0){
                    Table table = new Gson().fromJson(getTable_res.comment, Table.class);
                    List<TableNode> table_data = table.getData();
                    gdao.deleteAll();
                    cdao.deleteAll();
                    for (TableNode t : table_data){
                        gdao.insert(new GoToClass(t.getTerm(), t.getWeek(), t.getSeq(), t.getCourseno(), t.getId(), t.getCroomno(), t.getStartweek(), t.getEndweek(), t.isOddweek(), t.getHours()));
                        cdao.insert(new ClassInfo(t.getCourseno(), t.getCtype(), t.getTname(), t.getExamt(), t.getDptname(), t.getDptno(), t.getSpname(), t.getSpno(), t.getGrade(), t.getCname(), t.getTeacherno(), t.getName(),
                                t.getCourseid(), t.getComm(), t.getMaxcnt(), t.getXf(), t.getLlxs(), t.getSyxs(), t.getSjxs(), t.getQtxs(), t.getSctcnt()));
                    }
                }
            }
        }).start();
    }

    public void changeCode(View view){
        ck_res = new HttpConnectionAndCode(null, 1);
        cookie_builder = new StringBuilder();
        CheckCode.ShowCheckCode_thread(this, (ImageView)findViewById(R.id.imageView_checkcode), getResources().getString(R.string.checkcode_url), cookie_builder, getResources().getString(R.string.cookie_delimiter), ck_res);
    }

    /**
     *
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
     *
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
}