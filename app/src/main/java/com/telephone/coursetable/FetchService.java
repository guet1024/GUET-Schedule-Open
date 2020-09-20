package com.telephone.coursetable;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.telephone.coursetable.AppWidgetProvider.ListAdapter;
import com.telephone.coursetable.AppWidgetProvider.ListRemoteViewsService;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.Locate;
import com.telephone.coursetable.Database.AppDatabase;
import com.telephone.coursetable.Database.CET;
import com.telephone.coursetable.Database.CETDao;
import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.ExamInfo;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.Grades;
import com.telephone.coursetable.Database.GradesDao;
import com.telephone.coursetable.Database.GraduationScore;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.PersonInfo;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Library.LibraryActivity;
import com.telephone.coursetable.OCR.OCR;
import com.telephone.coursetable.Update.Update;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.os.Build.BRAND;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 *
 * @clear
 *
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
    private static final String EXTRA_start_toast = "com.telephone.coursetable.extra.start_toast";

    private PowerManager.WakeLock wakeLock;

    private boolean started = false;
    private int dog = 20;

    public FetchService() {
        super("MyIntentService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        final String NAME = "onStartCommand()";
        if (started){
            Log.e(NAME, "the fetch service has started");
            return START_STICKY;
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new Notification.Builder(this, MyApp.notification_channel_id_running)
                        .setContentTitle("加油~今天也要打起精神来")
                        .setSmallIcon(R.drawable.feather_pen_trans)
                        .setContentIntent(pendingIntent)
                        .setTicker("加油~今天也要打起精神来")
                        .build();
        startForeground(MyApp.notification_id_fetch_service_foreground, notification);
        started = true;
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void update_foreground_notification(@NonNull String text){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new NotificationCompat.Builder(this, MyApp.notification_channel_id_running)
                        .setContentTitle("加油~今天也要打起精神来")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .setSmallIcon(R.drawable.feather_pen_trans)
                        .setContentIntent(pendingIntent)
                        .setTicker("加油~今天也要打起精神来")
                        .build();
        NotificationManagerCompat.from(this).notify(MyApp.notification_id_fetch_service_foreground, notification);
    }

    /**
     * Starts this service to perform action {@link #ACTION_START_FETCH_DATA} with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startAction_START_FETCH_DATA(Context context, long milliseconds, @Nullable String tip) {
        Intent intent = new Intent(context, FetchService.class);
        intent.setAction(ACTION_START_FETCH_DATA);
        intent.putExtra(EXTRA_interval_milliseconds, milliseconds);
        if (tip != null){
            intent.putExtra(EXTRA_start_toast, tip);
        }
        context.startForegroundService(intent);
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
        if (BRAND.toLowerCase().equals("huawei") || BRAND.toLowerCase().equals("honor")){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationManagerService");
            wakeLock.acquire();
        }
        while (true){
            updateListAppWidgets();
            Log.e(NAME, "dog = " + dog);
            if (dog == 20){
                if (MyApp.isLAN()){
                    Log.e(NAME, "LAN");
                    service_fetch_lan();
                }else {
                    Log.e(NAME, "WAN");
                    service_fetch_wan();
                }
                Update.whatIsNew(FetchService.this, null, null, null, null, null, null, null, MyApp.getCurrentApp().new_version);
            }else if (dog == 0){
                dog = 21;
            }
            dog--;
            try {
                Thread.sleep(milliseconds + 1);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateListAppWidgets(){
        final String NAME = "updateListAppWidgets()";
        ArrayList<String> data_list = getListForListAppWidgets();
        if (data_list != null){
            Intent intent = new Intent("com.telephone.coursetable.action.UPDATE_THE_DATASET_OF_ALL_LIST_APPWIDGET");
            intent.setComponent(new ComponentName(MyApp.PACKAGE_NAME, com.telephone.coursetable.AppWidgetProvider.List.CLASS_NAME));
            intent.putStringArrayListExtra(ListRemoteViewsService.EXTRA_ARRAY_LIST_OF_STRING_TO_GET_A_NEW_REMOTE_ADAPTER, data_list);
            sendBroadcast(intent);
            Log.e(NAME, "the intent to remind the app-widget-provider to update all list app-widgets has been sent");
        }
    }

    private ArrayList<String> getListForListAppWidgets(){
        final String NAME = "getListForListAppWidgets()";
        if (
                MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN_VPN) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.CHANGE_HOURS) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.CHANGE_TERMS) ||
                        MyApp.isRunning_login_thread()
        ){
            Log.e(NAME,"data is being change, list app-widgets NOT updated");
            return null;
        }
        AppDatabase appDatabase = MyApp.getCurrentAppDB();
        UserDao udao = appDatabase.userDao();
        TermInfoDao tdao = appDatabase.termInfoDao();
        GoToClassDao gdao = appDatabase.goToClassDao();
        SharedPreferences preferences = MyApp.getCurrentSharedPreference();
        if (udao.getActivatedUser().isEmpty()){
            ArrayList<String > data = new ArrayList<>();
            for (String des : MyApp.appwidget_list_today_time_descriptions) {
                data.add(ListAdapter.TODAY);
                data.add(des);
            }
            for (String des : MyApp.appwidget_list_tomorrow_time_descriptions){
                data.add(ListAdapter.TOMORROW);
                data.add(des);
            }
            Log.e(NAME,"no user, set all list app-widgets to default");
            return data;
        }
        long today = Clock.nowTimeStamp();
        long tomorrow = today + 86400000;
        Locate today_locate = Clock.locateNow(today,
                tdao,
                preferences,
                MyApp.times,
                DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                getResources().getString(R.string.pref_hour_start_suffix),
                getResources().getString(R.string.pref_hour_end_suffix),
                getResources().getString(R.string.pref_hour_des_suffix)
        );
        Locate tomorrow_locate = Clock.locateNow(tomorrow,
                tdao,
                preferences,
                MyApp.times,
                DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                getResources().getString(R.string.pref_hour_start_suffix),
                getResources().getString(R.string.pref_hour_end_suffix),
                getResources().getString(R.string.pref_hour_des_suffix)
        );
        ArrayList<String> res = new ArrayList<>();
        for (String des : MyApp.appwidget_list_today_time_descriptions){
            res.add(ListAdapter.TODAY);
            res.add(des);
            if (today_locate.term == null){
                continue;
            }else {
                String corresponding_time = MyApp.times[Arrays.asList(MyApp.appwidget_list_today_time_descriptions).indexOf(des)];
                String now_time = today_locate.time;
                List<ShowTableNode> courses_list = gdao.getNode(today_locate.term.term, today_locate.week, today_locate.weekday, corresponding_time);
                for (ShowTableNode course : courses_list){
                    res.add(null);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (now_time != null && now_time.equals(corresponding_time)){
                        stringBuilder.append("➤ ");
                    }
                    if (course.croomno != null){
                        stringBuilder.append(course.croomno).append("    ");
                    }else {
                        stringBuilder.append("--------").append("    ");
                    }
                    if (course.cname != null){
                        String cname = course.cname;
                        if (cname.length() > 10){
                            cname = cname.substring(0, 10) + "...";
                        }
                        stringBuilder.append(cname).append(" @");
                    }else {
                        stringBuilder.append(" ").append(" @");
                    }
                    if (course.name != null){
                        stringBuilder.append(course.name);
                    }
                    res.add(stringBuilder.toString());
                }
            }
        }
        for (String des : MyApp.appwidget_list_tomorrow_time_descriptions){
            res.add(ListAdapter.TOMORROW);
            res.add(des);
            if (tomorrow_locate.term == null){
                continue;
            }else {
                String corresponding_time = MyApp.times[Arrays.asList(MyApp.appwidget_list_tomorrow_time_descriptions).indexOf(des)];
                List<ShowTableNode> courses_list = gdao.getNode(tomorrow_locate.term.term, tomorrow_locate.week, tomorrow_locate.weekday, corresponding_time);
                for (ShowTableNode course : courses_list){
                    res.add(null);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (course.croomno != null){
                        stringBuilder.append(course.croomno).append("    ");
                    }else {
                        stringBuilder.append("--------").append("    ");
                    }
                    if (course.cname != null){
                        String cname = course.cname;
                        if (cname.length() > 10){
                            cname = cname.substring(0, 10) + "...";
                        }
                        stringBuilder.append(cname).append(" @");
                    }else {
                        stringBuilder.append(" ").append(" @");
                    }
                    if (course.name != null){
                        stringBuilder.append(course.name);
                    }
                    res.add(stringBuilder.toString());
                }
            }
        }
        Log.e(NAME,"the new data-list for all list app-widgets has been made");
        return res;
    }

    private void service_fetch_wan(){
//        lan_notify_login_wrong_password();
    }

    private boolean tryOutdated(MyApp.RunningActivity type, AppCompatActivity activity){
        switch (type){
            case MAIN:
                return ((MainActivity)activity).setOutdated();
            case USAGE:
                return ((UsageActivity)activity).setOutdated();
            case FUNCTION_MENU:
                return ((FunctionMenu)activity).setOutdated();
            case ABOUT:
                return ((AboutActivity)activity).setOutdated();
            case LIBRARY:
                return ((LibraryActivity)activity).setOutdated();
            default: return false;
        }
    }

    private void service_fetch_lan(){
        final String NAME = "service_fetch_lan()";
        Resources r = getResources();
        MyApp.RunningActivity ra = MyApp.getRunning_activity();
        AppCompatActivity rap = MyApp.getRunning_activity_pointer();
        if (
                MyApp.isRunning_login_thread() ||
                        ra.equals(MyApp.RunningActivity.CHANGE_HOURS) ||
                        ra.equals(MyApp.RunningActivity.CHANGE_TERMS) ||
                        ra.equals(MyApp.RunningActivity.LOGIN) ||
                        ra.equals(MyApp.RunningActivity.LOGIN_VPN) ||
                        (rap != null && !tryOutdated(ra, rap))
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
        GradesDao grdao_test = MyApp.getCurrentAppDB_Test().gradesDao();
        ExamInfoDao edao_test = MyApp.getCurrentAppDB_Test().examInfoDao();
        CETDao cetDao_test = MyApp.getCurrentAppDB_Test().cetDao();
        SharedPreferences.Editor editor_test = MyApp.getCurrentSharedPreferenceEditor_Test();
        PersonInfoDao pdao = MyApp.getCurrentAppDB().personInfoDao();
        TermInfoDao tdao = MyApp.getCurrentAppDB().termInfoDao();
        GoToClassDao gdao = MyApp.getCurrentAppDB().goToClassDao();
        ClassInfoDao cdao = MyApp.getCurrentAppDB().classInfoDao();
        GraduationScoreDao gsdao = MyApp.getCurrentAppDB().graduationScoreDao();
        SharedPreferences.Editor editor = MyApp.getCurrentSharedPreferenceEditor();
        GradesDao grdao = MyApp.getCurrentAppDB().gradesDao();
        ExamInfoDao edao = MyApp.getCurrentAppDB().examInfoDao();
        CETDao cetDao = MyApp.getCurrentAppDB().cetDao();
        UserDao udao = MyApp.getCurrentAppDB().userDao();
        Login.deleteOldDataFromDatabase(
            gdao_test, cdao_test, tdao_test, pdao_test, gsdao_test, grdao_test, edao_test, cetDao_test
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
                editor_test,
                grdao_test,
                edao_test,
                cetDao_test
        );
        if (!fetch_merge_res){
            Log.e(NAME, "fail | fetch fail");
            lan_end();
            return;
        }
        /** get old delay_week */
        List<Integer> delay_week_to_apply = new LinkedList<>();
        List<TermInfo> new_terms = tdao_test.selectAll();
        for (TermInfo new_term : new_terms){
            List<Integer> old_delay_list = tdao.getDelayWeekNum(new_term.term);
            if (old_delay_list.isEmpty()){
                delay_week_to_apply.add(0);
            }else {
                delay_week_to_apply.add(old_delay_list.get(0));
            }
        }
        /** deactivate all user */
        Log.e(NAME, "deactivate all user...");
        udao.disableAllUser();
        /** migrate the pulled data to the database */
        Log.e(NAME, "migrate the pulled data to the database...");
        lan_merge(pdao, pdao_test, tdao, tdao_test, gdao, gdao_test, cdao, cdao_test, gsdao, gsdao_test, editor, pref_test, grdao, grdao_test, delay_week_to_apply, edao, edao_test, cetDao, cetDao_test);
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

    public void lan_merge(PersonInfoDao p, PersonInfoDao p_t, TermInfoDao t, TermInfoDao t_t, GoToClassDao g, GoToClassDao g_t,
                          ClassInfoDao c, ClassInfoDao c_t, GraduationScoreDao gs, GraduationScoreDao gs_t,
                          SharedPreferences.Editor editor, SharedPreferences pref_t,
                          GradesDao gr, GradesDao gr_t, List<Integer> delay_week_to_apply,
                          ExamInfoDao e, ExamInfoDao e_t,
                          CETDao cet, CETDao cet_t){
        Login.deleteOldDataFromDatabase(g, c, t, p, gs, gr, e, cet);
//        editor.clear().commit();
        List<PersonInfo> p_t_all = p_t.selectAll();
        for (PersonInfo p_a : p_t_all){
            p.insert(p_a);
        }
        List<TermInfo> t_t_all = t_t.selectAll();
        for (int i = 0; i < t_t_all.size(); i++){
            TermInfo t_a = t_t_all.get(i);
            t_a.setDelay(delay_week_to_apply.get(i));
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
//        Set<String> keys = pref_t.getAll().keySet();
//        for (String key : keys){
//            String value = pref_t.getString(key, null);
//            editor.putString(key, value);
//        }
//        editor.commit();
        List<Grades> gr_t_all = gr_t.selectAll();
        for (Grades gr_a : gr_t_all){
            gr.insert(gr_a);
        }
        List<ExamInfo> e_t_all = e_t.selectAll();
        for (ExamInfo e_a : e_t_all){
            e.insert(e_a);
        }
        List<CET> cet_t_all = cet_t.selectAll();
        for (CET cet_a : cet_t_all){
            cet.insert(cet_a);
        }
    }

    private void lan_start(){
        final String NAME = "lan_start()";
        Log.e(NAME, "start...");
        MyApp.setRunning_fetch_service(true);
        if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.MAIN) && MyApp.getRunning_main() != null && MyApp.getRunning_main().isVisible()){
            Log.e(NAME, "refresh main activity...");
            MyApp.getRunning_main().refresh();
        }
    }

    private void lan_end(){
        final String NAME = "lan_end()";
        Log.e(NAME, "end...");
        MyApp.setRunning_fetch_service(false);
        if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.MAIN) && MyApp.getRunning_main() != null && MyApp.getRunning_main().isVisible()){
            Log.e(NAME, "refresh main activity...");
            MyApp.getRunning_main().refresh();
        }
    }

    private void lan_notify_login_wrong_password(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new NotificationCompat.Builder(this, MyApp.notification_channel_id_fetch_fail)
                        .setContentTitle("同步失败了 ಥ_ಥ")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("您的学分制系统密码是否已更改? 请再次登录以更新您的密码 >>"))
                        .setSmallIcon(R.drawable.feather_pen_trans)
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
        final String NAME = "onHandleIntent()";
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_FETCH_DATA.equals(action)) {
                final long ms = intent.getLongExtra(EXTRA_interval_milliseconds, MyApp.service_fetch_interval);
                final String tip = intent.getStringExtra(EXTRA_start_toast);
                if (tip != null && !tip.isEmpty()){
                    Log.e(NAME, "i have received a tip..........................");
                    Log.e(NAME, tip);
//                    Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
                }
                handleAction_START_FETCH_DATA(ms);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }
}
