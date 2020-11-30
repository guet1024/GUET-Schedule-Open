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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.telephone.coursetable.AppWidgetProvider.ListAdapter;
import com.telephone.coursetable.AppWidgetProvider.ListRemoteViewsService;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.FindClassOfCurrentOrNextTimeRes;
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
import com.telephone.coursetable.Database.LAB;
import com.telephone.coursetable.Database.LABDao;
import com.telephone.coursetable.Database.Methods.Methods;
import com.telephone.coursetable.Database.PersonInfo;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.GradePoint.GradePointActivity;
import com.telephone.coursetable.GuetTools.ImageActivity;
import com.telephone.coursetable.GuetTools.WebLinksActivity;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Library.LibraryActivity;
import com.telephone.coursetable.OCR.OCR;
import com.telephone.coursetable.Update.Update;
import com.telephone.coursetable.Webinfo.GUET_Music;
import com.telephone.coursetable.Webinfo.Guetphonenums;
import com.telephone.coursetable.Webinfo.Webinfo;

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
    private static final String ACTION_STOP_SERVICE = "com.telephone.coursetable.action.STOP_SERVICE";

    public static final String SIGN = "telephone";

    // TODO: Rename parameters
    private static final String EXTRA_interval_milliseconds = "com.telephone.coursetable.extra.interval_milliseconds";
    private static final String EXTRA_start_toast = "com.telephone.coursetable.extra.start_toast";
    public static final String EXTRA_sign = "com.telephone.coursetable.extra.sign";

    private PowerManager.WakeLock wakeLock = null;

    private boolean started = false;
    private boolean notify = false;
    private boolean stop = false;
    private int dog = 20;

    public FetchService() {
        super("MyIntentService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        final String NAME = "onStartCommand()";
        if (stop || (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_STOP_SERVICE))){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "the fetch service has stopped!!");
            stop = true;
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        if (started){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "the fetch service has started");
            return START_STICKY;
        }
        started = true;
        if (!notify) {
            Intent stopIntent = new Intent(this, FetchService.class);
            stopIntent.setAction(ACTION_STOP_SERVICE);
            PendingIntent stopPendingIntent =
                    PendingIntent.getService(this, 0, stopIntent, 0);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification =
                    new NotificationCompat.Builder(this, MyApp.notification_channel_id_running)
                            .setContentTitle("加油~今天也要打起精神来")
                            .setSmallIcon(R.drawable.feather_pen_trans)
                            .setContentIntent(pendingIntent)
                            .setTicker("加油~今天也要打起精神来")
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .addAction(0, "强行停止", stopPendingIntent)
                            .build();
            startForeground(MyApp.notification_id_fetch_service_foreground, notification);
            notify = true;
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void update_foreground_notification(@NonNull String text){
        Intent stopIntent = new Intent(this, FetchService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent =
                PendingIntent.getService(this, 0, stopIntent, 0);
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
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .addAction(0, "强行停止", stopPendingIntent)
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
        intent.putExtra(EXTRA_sign, SIGN);
        if (tip != null){
            intent.putExtra(EXTRA_start_toast, tip);
        }
        context.startForegroundService(intent);
    }

    /**
     * Call me to stop the service
     */
    public static void startAction_STOP_SERVICE(Context context) {
        Intent intent = new Intent(context, FetchService.class);
        intent.setAction(ACTION_STOP_SERVICE);
        context.startService(intent);
    }

    /**
     * Handle action {@link #ACTION_START_FETCH_DATA} in the provided background thread with the provided
     * parameters.
     */
    private void handleAction_START_FETCH_DATA(long milliseconds) {
        final String NAME = "handleAction_START_FETCH_DATA()";
        if (wakeLock == null && (BRAND.toLowerCase().equals("huawei") || BRAND.toLowerCase().equals("honor"))){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationManagerService");
            wakeLock.acquire();
        }
        running_showLessons();
        updateListAppWidgets();
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "dog = " + dog);
        if (dog == 20){
            if (MyApp.isLAN()){
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "LAN");
                service_fetch_lan();
            }else {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "WAN");
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
        started = false;
        if (!stop) {
            startAction_START_FETCH_DATA(this, milliseconds, null);
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
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "the intent to remind the app-widget-provider to update all list app-widgets has been sent");
        }
    }

    private void running_showLessons() {
        final String NAME = "running_showLessons()";
        if (
                MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN_VPN) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.TEST) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.CHANGE_HOURS) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.CHANGE_TERMS) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.COURSE_CARD) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.EDIT_COURSE) ||
                        MyApp.isRunning_login_thread()
        ){
            com.telephone.coursetable.LogMe.LogMe.e(NAME,"data is being change, NOT update shown lessons");
            return;
        }
        AppDatabase appDatabase = MyApp.getCurrentAppDB();
        UserDao udao = appDatabase.userDao();
        TermInfoDao tdao = appDatabase.termInfoDao();
        SharedPreferences preferences = MyApp.getCurrentSharedPreference();
        long today = Clock.nowTimeStamp();
        if (udao.getActivatedUser().isEmpty()){
            update_foreground_notification("未登录");
            return;
        }
        FindClassOfCurrentOrNextTimeRes currentOrNextTime = Clock.findClassOfCurrentOrNextTime(
                udao.getActivatedUser().get(0).username,
                today,
                tdao,
                preferences,
                DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                getResources().getString(R.string.pref_hour_start_suffix),
                getResources().getString(R.string.pref_hour_end_suffix),
                getResources().getString(R.string.pref_hour_des_suffix)
        );
        if (currentOrNextTime!=null) {
            if (currentOrNextTime.isNow) {
                StringBuilder text = new StringBuilder("正在上课：");
                for (int i = 0; i < currentOrNextTime.list.size(); i++) {
                    ShowTableNode nt = currentOrNextTime.list.get(i);
                    if (i > 0) {
                        if (nt.croomno != null) {
                            text.append(" ( ").append(nt.croomno).append(" ").append(nt.cname).append(" )");
                        } else text.append(" (").append(" *　").append(nt.cname).append(" )");
                    } else {
                        if (nt.croomno != null) {
                            text.append(nt.croomno).append(" ").append(nt.cname);
                        } else text.append(" *　").append(nt.cname);
                    }
                }
                for (int i = 0; i < currentOrNextTime.list.size(); i++) {
                    text.append("\n\n");
                    ShowTableNode nt = currentOrNextTime.list.get(i);
                    text.append(nt.cname).append("🔎").append("系统备注：").append(((nt.sys_comm == null)?(""):(nt.sys_comm))).append("\n");
                    text.append(nt.cname).append("📝").append("自定义备注：").append(((nt.my_comm == null)?(""):(nt.my_comm)));
                }
                update_foreground_notification(text.toString());
            }
            else {
                if (currentOrNextTime.list.isEmpty()) {
                    update_foreground_notification("今日课程已全部完成");
                }
                else {
                    StringBuilder text = new StringBuilder("下一节课：");
                    if ( currentOrNextTime.start!=null ) text = new StringBuilder("下一节课(" + currentOrNextTime.start + ")：");
                    for (int i = 0; i < currentOrNextTime.list.size(); i++) {
                        ShowTableNode nt = currentOrNextTime.list.get(i);
                        if (i > 0) {
                            if (nt.croomno != null) {
                                text.append(" ( ").append(nt.croomno).append(" ").append(nt.cname).append(" )");
                            } else text.append(" (").append(" *　").append(nt.cname).append(" )");
                        } else {
                            if (nt.croomno != null) {
                                text.append(nt.croomno).append(" ").append(nt.cname);
                            } else text.append(" *　").append(nt.cname);
                        }
                    }
                    for (int i = 0; i < currentOrNextTime.list.size(); i++) {
                        text.append("\n\n");
                        ShowTableNode nt = currentOrNextTime.list.get(i);
                        text.append(nt.cname).append("🔎").append("系统备注：").append(((nt.sys_comm == null)?(""):(nt.sys_comm))).append("\n");
                        text.append(nt.cname).append("📝").append("自定义备注：").append(((nt.my_comm == null)?(""):(nt.my_comm)));
                    }
                    update_foreground_notification(text.toString());
                }
            }
        }
        else {
            update_foreground_notification("今日无课");
        }
    }

    private ArrayList<String> getListForListAppWidgets(){
        final String NAME = "getListForListAppWidgets()";
        if (
                MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.LOGIN_VPN) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.TEST) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.CHANGE_HOURS) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.CHANGE_TERMS) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.COURSE_CARD) ||
                        MyApp.getRunning_activity().equals(MyApp.RunningActivity.EDIT_COURSE) ||
                        MyApp.isRunning_login_thread()
        ){
            com.telephone.coursetable.LogMe.LogMe.e(NAME,"data is being change, list app-widgets NOT updated");
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
            com.telephone.coursetable.LogMe.LogMe.e(NAME,"no user, set all list app-widgets to default");
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
                List<ShowTableNode> courses_list = gdao.getNode(udao.getActivatedUser().get(0).username, today_locate.term.term, today_locate.week, today_locate.weekday, corresponding_time);
                for (ShowTableNode course : courses_list){
                    res.add(null);

                    StringBuilder stringBuilder = new StringBuilder();

                    if (now_time != null && now_time.equals(corresponding_time)){
                        stringBuilder.append("➤ ");
                    }

                    String cname = "";
                    if (course.cname != null){
                        cname = course.cname;
                    }
                    if (cname.length() > 8){
                        cname = cname.substring(0, 7) + "...";
                    }
                    String t_name = "";
                    if (course.name != null){
                        t_name = course.name;
                    }
                    String place = "";
                    if (course.croomno != null){
                        place = course.croomno;
                    }

                    stringBuilder.append(cname).append("@").append(t_name).append("#").append(place);

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
                List<ShowTableNode> courses_list = gdao.getNode(udao.getActivatedUser().get(0).username, tomorrow_locate.term.term, tomorrow_locate.week, tomorrow_locate.weekday, corresponding_time);
                for (ShowTableNode course : courses_list){
                    res.add(null);

                    StringBuilder stringBuilder = new StringBuilder();

                    String cname = "";
                    if (course.cname != null){
                        cname = course.cname;
                    }
                    if (cname.length() > 8){
                        cname = cname.substring(0, 7) + "...";
                    }
                    String t_name = "";
                    if (course.name != null){
                        t_name = course.name;
                    }
                    String place = "";
                    if (course.croomno != null){
                        place = course.croomno;
                    }

                    stringBuilder.append(cname).append("@").append(t_name).append("#").append(place);

                    res.add(stringBuilder.toString());
                }
            }
        }
        com.telephone.coursetable.LogMe.LogMe.e(NAME,"the new data-list for all list app-widgets has been made");
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
            case COMMENT:
                return ((Comments)activity).setOutdated();
            case TEACHER_EVALUATION_PANEL:
                return ((TeacherEvaluationPanel)activity).setOutdated();
            case WEB_LINKS:
                return ((WebLinksActivity)activity).setOutdated();
            case GUET_MUSIC:
                return ((GUET_Music)activity).setOutdated();
            case GUET_PHONE:
                return ((Guetphonenums)activity).setOutdated();
            case WEB_INFO:
                return ((Webinfo)activity).setOutdated();
            case IMAGE_MAP:
                return ((ImageActivity)activity).setOutdated();
            case GRADE_POINTS:
                return ((GradePointActivity)activity).setOutdated();
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
                        ra.equals(MyApp.RunningActivity.COURSE_CARD) ||
                        ra.equals(MyApp.RunningActivity.EDIT_COURSE) ||
                        ra.equals(MyApp.RunningActivity.TEST) ||
                        ra.equals(MyApp.RunningActivity.LOGIN) ||
                        ra.equals(MyApp.RunningActivity.LOGIN_VPN) ||
                        (rap != null && !tryOutdated(ra, rap))
        ){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "skip | some activity is active or data is being updated");
            return;
        }
        lan_start();
        List<User> ac_users = MyApp.getCurrentAppDB().userDao().getActivatedUser();
        if (ac_users.isEmpty()){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "skip | no activated user");
            lan_end();
            return;
        }
        User user = ac_users.get(0);
        StringBuilder cookie_builder;
        for (int i = 0; true; i++){
            cookie_builder = new StringBuilder();
            HttpConnectionAndCode get_checkcode_res = LAN.checkcode(FetchService.this);
            if (get_checkcode_res.obj == null){
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | get check-code fail");
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
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | login fail");
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
        LABDao labDao_test = MyApp.getCurrentAppDB_Test().labDao();
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
        LABDao labDao = MyApp.getCurrentAppDB().labDao();
        UserDao udao = MyApp.getCurrentAppDB().userDao();
        Login.deleteOldDataFromDatabase(
            user.username, gdao_test, cdao_test, tdao_test, pdao_test, gsdao_test, grdao_test, edao_test, cetDao_test, labDao_test
        );
        editor_test.clear().commit();
        // edit by Telephone 2020/11/23 18:15, use the comment map from formal database, NOT test database
        boolean fetch_merge_res = Login.fetch_merge(
                FetchService.this,
                cookie_builder.toString(),
                user.username,
                Methods.getMyCommentMap(gdao, cdao),
                pdao_test,
                tdao_test,
                gdao_test,
                cdao_test,
                gsdao_test,
                editor_test,
                grdao_test,
                edao_test,
                cetDao_test,
                labDao_test
        );
        if (!fetch_merge_res){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | fetch fail");
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
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "deactivate all user...");
        udao.disableAllUser();
        /** migrate the pulled data to the database */
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "migrate the pulled data to the database...");
        lan_merge(
                user.username,
                pdao, pdao_test,
                tdao, tdao_test,
                gdao, gdao_test,
                cdao, cdao_test,
                gsdao, gsdao_test,
                editor, pref_test,
                grdao, grdao_test,
                delay_week_to_apply,
                edao, edao_test,
                cetDao, cetDao_test,
                labDao, labDao_test
        );
        /** re-insert user */
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "re-insert user...");
        udao.insert(new User(user.username, user.password, user.aaw_password, user.vpn_password));
        /** activate user */
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "activate user...");
        udao.activateUser(user.username);
        List<User> ac_list = udao.getActivatedUser();
        if (!ac_list.isEmpty()){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "success | user activated: " + ac_list.get(0).username + " " + pdao.selectAll().get(0).name);
        }else {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "fail | no user activated");
        }
        lan_end();
    }

    public void wan_merge(){}

    public void lan_merge(
            String username,
            PersonInfoDao p, PersonInfoDao p_t, TermInfoDao t, TermInfoDao t_t, GoToClassDao g, GoToClassDao g_t,
            ClassInfoDao c, ClassInfoDao c_t, GraduationScoreDao gs, GraduationScoreDao gs_t,
            SharedPreferences.Editor editor, SharedPreferences pref_t,
            GradesDao gr, GradesDao gr_t, List<Integer> delay_week_to_apply,
            ExamInfoDao e, ExamInfoDao e_t,
            CETDao cet, CETDao cet_t,
            LABDao lab, LABDao lab_t
    ){
        Login.deleteOldDataFromDatabase(username, g, c, t, p, gs, gr, e, cet, lab);
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
        List<GoToClass> g_t_all = g_t.selectAll(username);
        for (GoToClass g_a : g_t_all){
            g.insert(g_a);
        }
        List<ClassInfo> c_t_all = c_t.selectAll(username);
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
        List<LAB> lab_t_all = lab_t.selectAll();
        for (LAB lab_a : lab_t_all){
            lab.insert(lab_a);
        }
    }

    private void lan_start(){
        final String NAME = "lan_start()";
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "start...");
        MyApp.setRunning_fetch_service(true);
        if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.MAIN) && MyApp.getRunning_main() != null && MyApp.getRunning_main().isVisible()){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "refresh main activity...");
            MyApp.getRunning_main().refresh();
        }
    }

    private void lan_end(){
        final String NAME = "lan_end()";
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "end...");
        MyApp.setRunning_fetch_service(false);
        if (MyApp.getRunning_activity().equals(MyApp.RunningActivity.MAIN) && MyApp.getRunning_main() != null && MyApp.getRunning_main().isVisible()){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "refresh main activity...");
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
        com.telephone.coursetable.LogMe.LogMe.e(NAME,"it fail...");
        if (res.comment != null && res.comment.contains("密码")) {
            lan_notify_login_wrong_password();
        }
    }

    /**
     * Handle action STOP_SERVICE in the provided background thread with the provided
     * parameters.
     */
    private void handleAction_STOP_SERVICE() {
        final String NAME = "handleAction_STOP_SERVICE()";
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "stop signal received");
        stop = true;
        stopForeground(true);
        stopSelf();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String NAME = "onHandleIntent()";
        if (intent != null && !stop) {
            final String action = intent.getAction();
            if (ACTION_START_FETCH_DATA.equals(action)) {
                final long ms = intent.getLongExtra(EXTRA_interval_milliseconds, MyApp.service_fetch_interval);
                final String tip = intent.getStringExtra(EXTRA_start_toast);
                String sign = intent.getStringExtra(EXTRA_sign);
                if (sign == null || !sign.equals(SIGN)){
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "invalidated start");
                    return;
                }else {
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "validated start");
                }
                if (tip != null && !tip.isEmpty()){
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "i have received a tip..........................");
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, tip);
//                    Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
                }
                handleAction_START_FETCH_DATA(ms);
            } else if (ACTION_STOP_SERVICE.equals(action)) {
                handleAction_STOP_SERVICE();
            }
        }
    }
}
