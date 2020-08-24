package com.telephone.coursetable;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GraduationScore;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.PersonInfo;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.OCR.OCR;

import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Set;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_START_FETCH_DATA = "com.telephone.coursetable.action.START_FETCH_DATA";
    private static final String ACTION_BAZ = "com.telephone.coursetable.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.telephone.coursetable.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.telephone.coursetable.extra.PARAM2";
    private static final String EXTRA_interval_milliseconds = "com.telephone.coursetable.extra.interval_milliseconds";

    public FetchService() {
        super("MyIntentService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new Notification.Builder(this, MyApp.notification_channel_id_normal)
                        .setContentTitle("加油~今天也要打起精神来")
                        .setSmallIcon(R.drawable.guet_logo_white)
                        .setContentIntent(pendingIntent)
                        .setTicker("加油~今天也要打起精神来")
                        .build();
        startForeground(MyApp.notification_id_fetch_service_foreground, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Starts this service to perform action {@link #ACTION_START_FETCH_DATA} with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startAction_START_FETCH_DATA(Context context, long milliseconds) {
        Intent intent = new Intent(context, FetchService.class);
        intent.setAction(ACTION_START_FETCH_DATA);
        intent.putExtra(EXTRA_interval_milliseconds, milliseconds);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, FetchService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Handle action {@link #ACTION_START_FETCH_DATA} in the provided background thread with the provided
     * parameters.
     */
    private void handleAction_START_FETCH_DATA(long milliseconds) {
        final String NAME = "handleAction_START_FETCH_DATA()";
        while (true){
            if (MyApp.isLAN()){
                Log.e(NAME, "LAN");
                service_fetch_lan();
            }else {
                Log.e(NAME, "WAN");
                service_fetch_wan();
            }
            try {
                Thread.sleep(milliseconds + 1);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
        }
    }

    private void service_fetch_wan(){
        lan_notify_login_wrong_password();
    }

    private void service_fetch_lan(){
        final String NAME = "service_fetch_lan()";
        Resources r = getResources();
        if (
                MyApp.running_main != null ||
                        MyApp.running_change_hours != null ||
                        MyApp.running_function_menu != null ||
                        MyApp.running_login != null ||
                        MyApp.running_login_thread
        ){
            Log.e(NAME, "skip | some activity is active or data is being updated");
            return;
        }
        lan_start();
        List<User> ac_users = MyApp.getCurrentAppDB().userDao().getActivatedUser();
        if (ac_users.isEmpty()){
            Log.e(NAME, "skip | no activated user");
            lan_end();
            return;
        }
        User user = ac_users.get(0);
        StringBuilder cookie_builder;
        for (int i = 0; true; i++){
            cookie_builder = new StringBuilder();
            HttpConnectionAndCode get_checkcode_res = LAN.checkcode(FetchService.this);
            if (get_checkcode_res.obj == null){
                Log.e(NAME, "fail | get check-code fail");
                lan_end();
                return;
            }
            cookie_builder.append(get_checkcode_res.cookie);
            HttpConnectionAndCode login_res = Login.login(
                    FetchService.this,
                    user.username,
                    user.password,
                    OCR.getTextFromBitmap(FetchService.this, (Bitmap) get_checkcode_res.obj, MyApp.ocr_lang_code),
                    cookie_builder.toString(),
                    cookie_builder
            );
            if (login_res.code == 0){
                break;
            }else if(login_res.comment != null && login_res.comment.contains("验证码")){
                continue;
            }else {
                Log.e(NAME, "fail | login fail");
                lan_fetch_service_loginFail(login_res);
                lan_end();
                return;
            }
        }
        PersonInfoDao pdao_test = MyApp.getCurrentAppDB_Test().personInfoDao();
        TermInfoDao tdao_test = MyApp.getCurrentAppDB_Test().termInfoDao();
        GoToClassDao gdao_test = MyApp.getCurrentAppDB_Test().goToClassDao();
        ClassInfoDao cdao_test = MyApp.getCurrentAppDB_Test().classInfoDao();
        GraduationScoreDao gsdao_test = MyApp.getCurrentAppDB_Test().graduationScoreDao();
        SharedPreferences pref_test = MyApp.getCurrentSharedPreference_Test();
        SharedPreferences.Editor editor_test = MyApp.getCurrentSharedPreferenceEditor_Test();
        PersonInfoDao pdao = MyApp.getCurrentAppDB().personInfoDao();
        TermInfoDao tdao = MyApp.getCurrentAppDB().termInfoDao();
        GoToClassDao gdao = MyApp.getCurrentAppDB().goToClassDao();
        ClassInfoDao cdao = MyApp.getCurrentAppDB().classInfoDao();
        GraduationScoreDao gsdao = MyApp.getCurrentAppDB().graduationScoreDao();
        SharedPreferences.Editor editor = MyApp.getCurrentSharedPreferenceEditor();
        UserDao udao = MyApp.getCurrentAppDB().userDao();
        Login.deleteOldDataFromDatabase(
            gdao_test, cdao_test, tdao_test, pdao_test, gsdao_test
        );
        editor_test.clear().commit();
        boolean fetch_merge_res = Login.fetch_merge(
                FetchService.this,
                cookie_builder.toString(),
                pdao_test,
                tdao_test,
                gdao_test,
                cdao_test,
                gsdao_test,
                editor_test
        );
        if (!fetch_merge_res){
            Log.e(NAME, "fail | fetch fail");
            lan_end();
            return;
        }
        /** deactivate all user */
        Log.e(NAME, "deactivate all user...");
        udao.disableAllUser();
        /** migrate the pulled data to the database */
        Log.e(NAME, "migrate the pulled data to the database...");
        lan_merge(pdao, pdao_test, tdao, tdao_test, gdao, gdao_test, cdao, cdao_test, gsdao, gsdao_test, editor, pref_test);
        /** re-insert user */
        Log.e(NAME, "re-insert user...");
        udao.insert(new User(user.username, user.password, user.aaw_password, user.vpn_password));
        /** activate user */
        Log.e(NAME, "activate user...");
        udao.activateUser(user.username);
        List<User> ac_list = udao.getActivatedUser();
        if (!ac_list.isEmpty()){
            Log.e(NAME, "success | user activated: " + ac_list.get(0).username + " " + pdao.selectAll().get(0).name);
        }else {
            Log.e(NAME, "fail | no user activated");
        }
        lan_end();
    }

    private void lan_merge(PersonInfoDao p, PersonInfoDao p_t, TermInfoDao t, TermInfoDao t_t, GoToClassDao g, GoToClassDao g_t,
                           ClassInfoDao c, ClassInfoDao c_t, GraduationScoreDao gs, GraduationScoreDao gs_t,
                           SharedPreferences.Editor editor, SharedPreferences pref_t){
        Login.deleteOldDataFromDatabase(g, c, t, p, gs);
        editor.clear();
        editor.putBoolean(getResources().getString(R.string.pref_user_updating_key), false);
        editor.putBoolean(getResources().getString(R.string.pref_logging_in_key), false);
        editor.putBoolean(getResources().getString(R.string.pref_service_updating_key), true);
        editor.commit();
        List<PersonInfo> p_t_all = p_t.selectAll();
        for (PersonInfo p_a : p_t_all){
            p.insert(p_a);
        }
        List<TermInfo> t_t_all = t_t.selectAll();
        for (TermInfo t_a : t_t_all){
            t.insert(t_a);
        }
        List<GoToClass> g_t_all = g_t.selectAll();
        for (GoToClass g_a : g_t_all){
            g.insert(g_a);
        }
        List<ClassInfo> c_t_all = c_t.selectAll();
        for (ClassInfo c_a : c_t_all){
            c.insert(c_a);
        }
        List<GraduationScore> gs_t_all = gs_t.selectAll();
        for (GraduationScore gs_a : gs_t_all){
            gs.insert(gs_a);
        }
        Set<String> keys = pref_t.getAll().keySet();
        for (String key : keys){
            String value = pref_t.getString(key, null);
            editor.putString(key, value);
        }
        editor.commit();
    }

    private void lan_start(){
        final String NAME = "lan_start()";
        Log.e(NAME, "start...");
        MyApp.running_fetch_service = true;
        if (MyApp.running_main != null){
            Log.e(NAME, "refresh main activity...");
            MyApp.running_main.refresh();
        }
    }

    private void lan_end(){
        final String NAME = "lan_end()";
        Log.e(NAME, "end...");
        MyApp.running_fetch_service = false;
        if (MyApp.running_main != null){
            Log.e(NAME, "refresh main activity...");
            MyApp.running_main.refresh();
        }
    }

    private void lan_notify_login_wrong_password(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new Notification.Builder(this, MyApp.notification_channel_id_normal)
                        .setContentTitle("同步失败了 ಥ_ಥ")
                        .setContentText("您的学分制系统密码是否已更改? 请再次登录以更新您的密码 >>")
                        .setSmallIcon(R.drawable.guet_logo_white)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setTicker("同步失败了 ಥ_ಥ")
                        .build();
        NotificationManagerCompat.from(this).notify(MyApp.notification_id_fetch_service_lan_password_wrong, notification);
    }

    private void lan_fetch_service_loginFail(HttpConnectionAndCode res){
        final String NAME = "lan_fetch_service_loginFail()";
        Log.e(NAME,"it fail...");
        if (res.comment != null && res.comment.contains("密码")) {
            lan_notify_login_wrong_password();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_FETCH_DATA.equals(action)) {
                final long ms = intent.getLongExtra(EXTRA_interval_milliseconds, MyApp.service_fetch_interval);
                handleAction_START_FETCH_DATA(ms);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }
}
