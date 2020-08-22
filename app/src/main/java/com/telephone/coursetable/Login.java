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
import com.telephone.coursetable.Gson.GoToClass_ClassInfo_s;
import com.telephone.coursetable.Gson.GoToClass_ClassInfo;
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
     *      1. call {@link #clearAllIMAndFocus()}
     *      2. get the user with the sid in the sid input box in the database, if exist:
     *          1. fill its password in the password input box
     *          2. set focus to check-code input box
     * @clear
     */
    private void updateUserNameAutoFill(){
        final ArrayAdapter<String> ada = new ArrayAdapter<>(Login.this, android.R.layout.simple_dropdown_item_1line, udao.selectAllUserName());
        runOnUiThread(() -> {
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setAdapter(ada);
            ((AutoCompleteTextView) findViewById(R.id.sid_input)).setOnDismissListener(() -> {
                clearAllIMAndFocus();
                new Thread(() -> {
                    final List<User> userSelected = udao.selectUser(((AutoCompleteTextView) findViewById(R.id.sid_input)).getText().toString());
                    if (!userSelected.isEmpty()) {
                        runOnUiThread(() -> {
                            ((AutoCompleteTextView) findViewById(R.id.passwd_input)).setText(userSelected.get(0).password);
                            setFocusToEditText((EditText)findViewById(R.id.checkcode_input));
                        });
                    }
                }).start();
            });
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
        EditText eta = (EditText)findViewById(R.id.aaw_passwd_input);
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
        if (eta != null) {
            eta.setEnabled(!eta.isEnabled());
            eta.setEnabled(!eta.isEnabled());
            eta.clearFocus();
        }
        if (etv != null) {
            etv.setEnabled(!etv.isEnabled());
            etv.setEnabled(!etv.isEnabled());
            etv.clearFocus();
        }
    }

    /**
     * @non-ui
     * 1. delete all user-related data from database(not including user login information)
     * @clear
     */
    private void deleteOldDataFromDatabase(){
        gdao.deleteAll();
        cdao.deleteAll();
        tdao.deleteAll();
        pdao.deleteAll();
        gsdao.deleteAll();
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
        getAlertDialog("确定要取消记住用户" + " " + sid + " " + "的登录信息吗？",
                (DialogInterface.OnClickListener) (dialogInterface, i) -> new Thread((Runnable) () -> {
                    udao.deleteUser(sid);
                    Log.e(NAME + " " + "user deleted", sid);
                    updateUserNameAutoFill();
                    runOnUiThread((Runnable) () -> {
                        ((AutoCompleteTextView)findViewById(R.id.sid_input)).setText("");
                        ((AutoCompleteTextView)findViewById(R.id.passwd_input)).setText("");
                        setFocusToEditText((EditText)findViewById(R.id.sid_input));
                        changeCode(null);
                    });
                }).start(),
                (DialogInterface.OnClickListener) (dialogInterface, i) -> {},
                null, null).show();
    }

    /**
     * @ui
     * 1. call {@link #clearAllIMAndFocus()}
     * 2. get student id, credit system password, credit system check-code from input box
     * 3. get cookie before logging in the credit system
     * 4. get a dialog View
     * 5. get an AlertDialog:
     *      - Message : null
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
     *              6. insert/replace new user into database
     *              ******************************* UPDATE DATA START *******************************
     *              7. deactivate all user in database
     *              8. clear shared preference, put <{@link R.string#pref_user_updating_key} : true> into shared preference
     *              9. commit shared preference
     *              10. show tip snack-bar, change title
     *              11. call {@link #deleteOldDataFromDatabase()}
     */
    public void login_thread(final View view){
        final String NAME = "login_thread()";
        /** call {@link #clearAllIMAndFocus()} */
        clearAllIMAndFocus();
        /** get student id, credit system password, credit system check-code from input box */
        final String sid = ((AutoCompleteTextView)findViewById(R.id.sid_input)).getText().toString();
        final String pwd = ((AutoCompleteTextView)findViewById(R.id.passwd_input)).getText().toString();
        final String ck = ((AutoCompleteTextView)findViewById(R.id.checkcode_input)).getText().toString();
        /** get cookie before logging in the credit system */
        final String cookie_before_login = cookie_builder.toString();
        /** get a dialog View */
        View extra_pwd_dialog_layout = getLayoutInflater().inflate(R.layout.extra_password, null);
        /** get an AlertDialog */
        AlertDialog extra_pwd_dialog = getAlertDialog(null,
                (DialogInterface.OnClickListener) (dialogInterface, i) -> {
                    /** call {@link #clearAllIMAndFocus()} */
                    clearAllIMAndFocus();
                    /** call {@link #lock()} */
                    lock();
                    /** get aaw password, vpn password from input box */
                    final String aaw_pwd = ((EditText)extra_pwd_dialog_layout.findViewById(R.id.aaw_passwd_input)).getText().toString();
                    final String vpn_pwd = ((EditText)extra_pwd_dialog_layout.findViewById(R.id.vpn_passwd_input)).getText().toString();
                    /** start a new thread */
                    new Thread((Runnable) () -> {
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
                                Snackbar.make(view, tip_f, BaseTransientBottomBar.LENGTH_SHORT).show();
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
                        String vpn_login_res = vpn_login_test(Login.this, sid, vpn_pwd);
                        /** if vpn login test fail */
                        if (vpn_login_res == null || vpn_login_res.equals(getResources().getString(R.string.wan_vpn_ip_forbidden))){
                            runOnUiThread((Runnable) () -> {
                                /** show tip snack-bar */
                                if (vpn_login_res != null && vpn_login_res.equals(getResources().getString(R.string.wan_vpn_ip_forbidden))){
                                    Snackbar.make(view, getResources().getString(R.string.lan_snackbar_vpn_test_login_fail_ip), BaseTransientBottomBar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(view, getResources().getString(R.string.lan_snackbar_vpn_test_login_fail), BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                                /** call {@link #unlock(boolean)} with true */
                                unlock(true);
                            });
                            /** end this thread */
                            return;
                        }
                        /** call {@link #outside_login_test(Context, String, String)} */
                        HttpConnectionAndCode outside_login_res = outside_login_test(Login.this, sid, aaw_pwd);
                        /** if outside login test fail */
                        if (outside_login_res.code != 0){
                            runOnUiThread((Runnable) () -> {
                                /** show tip snack-bar */
                                Snackbar.make(view, getResources().getString(R.string.lan_snackbar_outside_test_login_fail), BaseTransientBottomBar.LENGTH_SHORT).show();
                                /** call {@link #unlock(boolean)} with true */
                                unlock(true);
                            });
                            /** end this thread */
                            return;
                        }
                        /** get cookie after successfully logging in the credit system */
                        final String cookie_after_login = cookie_builder.toString();
                        /** get shared preference and its editor */
                        final SharedPreferences shared_pref = getSharedPreferences(getResources().getString(R.string.preference_file_name), MODE_PRIVATE);
                        final SharedPreferences.Editor editor = shared_pref.edit();
                        /** insert/replace new user into database */
                        udao.insert(new User(sid, pwd, aaw_pwd, vpn_pwd));
                        /**
                         * ******************************* UPDATE DATA START *******************************
                         */
                        /** deactivate all user in database */
                        udao.disableAllUser();
                        /** clear shared preference, put <{@link R.string#pref_user_updating_key} : true> into shared preference */
                        editor.clear();
                        editor.putBoolean(getResources().getString(R.string.pref_user_updating_key), true);
                        /** commit shared preference */
                        editor.commit();
                        /** show tip snack-bar, change title */
                        runOnUiThread(() -> {
                            Snackbar.make(view, getResources().getString(R.string.lan_snackbar_data_updating), BaseTransientBottomBar.LENGTH_LONG).show();
                            getSupportActionBar().setTitle(getResources().getString(R.string.lan_title_login_updating));
                        });
                        /** call {@link #deleteOldDataFromDatabase()} */
                        deleteOldDataFromDatabase();


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
                            HttpConnectionAndCode getTable_res = LAN.goToClass_ClassInfo(Login.this, cookie_after_login, t.term);
                            Log.e("login_thread() get table " + t.term, getTable_res.code+"");
                            //if success, insert data into database
                            if (getTable_res.code == 0){
                                GoToClass_ClassInfo_s table = new Gson().fromJson(getTable_res.comment, GoToClass_ClassInfo_s.class);
                                List<GoToClass_ClassInfo> table_node_list = table.getData();
                                for (GoToClass_ClassInfo table_node : table_node_list){
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
                        Locate locate_res = Clock.locateNow(nts, tdao, shared_pref, MyApp.times, server_hours_time_formatter,
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
                        editor.putBoolean(getResources().getString(R.string.pref_user_updating_key), false);
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
