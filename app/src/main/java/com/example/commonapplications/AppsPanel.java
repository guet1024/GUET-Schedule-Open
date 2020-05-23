package com.example.commonapplications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static android.app.usage.UsageStatsManager.INTERVAL_BEST;
import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.Intent.ACTION_TIME_TICK;
import static android.content.Intent.ACTION_USER_PRESENT;

public class AppsPanel extends AppWidgetProvider {

    public static final String PACKAGE_NAME = "com.example.commonapplications";
    public static final String CLASS_NAME = "com.example.commonapplications.AppsPanel";
    public static final String ACTION_NAME_UPDATE_ALL_WIDGET = "com.example.commonapplications.UPDATE_ALL";
    public static final String ACTION_NAME_CHANGE_COLOR = "com.example.commonapplications.CHANGE_COLOR";
    public static final int REQUEST_CODE_COMMON_APPS_START_OTHER_APP = 55532;
    public static final int REQUEST_CODE_COMMON_APPS_CHANGE_COLOR = 55531;

    private static final int SORT_LAUNCH_TIME = 0;
    private static final int SORT_TOTAL_VISIBLE_TIME = 1;
    private static final int DEDUPLICATION_LABEL_ACCEPT_FIRST = -1;
    private static final int DEDUPLICATION_INTENT_ACCEPT_NOT_NULL = -2;

    /**
     * When the first widget is added to screen, onEnabled() followed by onUpdate() is automatically
     * called, even if not handling the "APPWIDGET_ENABLED" in onReceive(), because these two calls
     * are made by super.onReceive() and super.onReceive() is called in the first line of onReceive().
     * <p>
     * Obviously, the service should be start every time the onEnabled() is called, because every time
     * onDisabled() is called, it stops the running service, and the service won't be run automatically.
     * <p>
     * The only question is : Could it succeed starting the service by calling startForegroundService()
     * at any time?
     * ==========
     * Answer: Yes, Double-startForegroundService() is safe.
     * If you call start when start, the create will be executed only once but the start will be executed
     * twice.
     * If you call start when destroy, after the destroy, the start will be executed.
     * ==========
     * So I have changed my mind, because I found it easier to make a undying-service than extremely
     * carefully and painstakingly seek ways to prevent my service from being killed by mistake.
     * <p>
     * Every time onEnabled() is called, I will use startForegroundService() to start my service.
     * <p>
     * I will no longer take the initiative to stop my service. Instead, I will make a self-loop in
     * the service. As a fallback, I will allow users to click a button to manually start the service.
     * ==========
     * The new approach brings another problem. Through my observation, I found that if the service keep
     * itself in a loop, the whole application will be stuck. Fortunately, I also found that if I specify
     * in the Manifest.xml that the service runs in a separate process, the problem will be solved.
     * ==========
     * Because in my new approach, my service runs in a separate process from the MainActivity, and
     * my service is not always running(it gets killed by system), I make a decision to ignore the color
     * code from the extra of intent, instead, I provide two buttons to users, one is used to change
     * the text color of widgets between black and white, another one is used to wake up my service.
     * ==========
     * I have turned to Android Alarms. I canceled the button used to start the service because I found
     * that Android Alarm is quite effective. So now, if you click on the title or the clock icon, the
     * widget will change into a new random color(black or white).
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.e("the first Widget added", "^^^^^^^^^^^^^^^^^^^^ onEnabled() called! ^^^^^^^^^^^^^^^^^^^^");

//        Intent intent = new Intent(CommonAppsService.ACTION_NAME_START_OR_STOP_SERVICE);
//        intent.setPackage(CommonAppsService.PACKAGE_NAME);
//        context.startForegroundService(intent);

    }

    /**
     * When the last widget is deleted from the screen, onDisabled() is automatically called, even if
     * not handling the "APPWIDGET_DISABLED" in onReceive(), because this call is made by super.onReceive()
     * and super.onReceive() is called in the first line of onReceive().
     * <p>
     * Obviously, the service should be stopped because we don't need any service more if no widget
     * is being used.
     * <p>
     * The only question is : Could it succeed stopping the service by calling stopService() at any
     * time?
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
//        Intent intent = new Intent(CommonAppsService.ACTION_NAME_START_OR_STOP_SERVICE);
//        intent.setPackage(CommonAppsService.PACKAGE_NAME);
//        context.stopService(intent);
        Log.e("the last Widget deleted", "^^^^^^^^^^^^^^^^^^^^ onDisabled() called! ^^^^^^^^^^^^^^^^^^^^");
    }

    /**
     * I override this method just because I want to handle the update of the widgets only by myself,
     * nothing else.
     * <p>
     * So I must call super.onReceive() to handle other broadcasts.
     * <p>
     * I want to handle the update, but I still want the system to update the widgets so that the widgets
     * can still be updated at a certain rate if my service is killed. So I set "updatePeriodMillis"
     * to a certain but large value(low rate) in the XML file and override the onUpdate() with a unchanged-color call of
     * myUpdate().
     * <p>
     * I use my special intent with action "UPDATE_ALL" and a color code to call my own update-method
     * myUpdate() periodically.
     * <p>
     * Although "ACTION_APPWIDGET_OPTIONS_CHANGED" is already handled by super.onReceive(), I still
     * want to make a Toast when "ACTION_APPWIDGET_OPTIONS_CHANGED" is received.
     * <p>
     * I want to receive some system-intents so that I can continuously wake my service up, but actually
     * that doesn't work.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        int[] widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context.getPackageName(), CLASS_NAME));
        Log.e("action received", "******************** Action: " + intent.getAction() + " ********************");
        if (action.equals(ACTION_NAME_UPDATE_ALL_WIDGET)) {
            int color = intent.getIntExtra(CommonAppsService.EXTRA_NAME_APP_TEXT_COLOR, MyApplication.COLOR_STAY);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int ms_later = context.getResources().getInteger(R.integer.period_ms);
            long triggerAtTime = System.currentTimeMillis() + ms_later;
            Intent intentWake = new Intent(ACTION_NAME_UPDATE_ALL_WIDGET);
            intentWake.setComponent(new ComponentName(PACKAGE_NAME, CLASS_NAME));
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, PendingIntent.getBroadcast(context, 0, intentWake, PendingIntent.FLAG_UPDATE_CURRENT));
            Log.e("Alarm set", "<<<<<<<<<<<<<<<<<<<< Alarm continue <<<<<<<<<<<<<<<<<<<<");

            Log.e("color code received", "#################### ColorCode: " + MyApplication.getColorName(color) + " ####################");
            if (widgetIds.length > 0)
//                myUpdate(context, AppWidgetManager.getInstance(context), widgetIds, color);
                myUpdate(context, AppWidgetManager.getInstance(context), widgetIds, MyApplication.COLOR_STAY);
            else
                Log.e("update canceled", "@@@@@@@@@@ No widget @@@@@@@@@@");
        } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {
            Toast.makeText(context, R.string.wait_app, Toast.LENGTH_LONG).show();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int ms_later = context.getResources().getInteger(R.integer.period_ms);
            long triggerAtTime = System.currentTimeMillis();
            Intent intentWake = new Intent(ACTION_NAME_UPDATE_ALL_WIDGET);
            intentWake.setComponent(new ComponentName(PACKAGE_NAME, CLASS_NAME));
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, PendingIntent.getBroadcast(context, 0, intentWake, PendingIntent.FLAG_UPDATE_CURRENT));
            Log.e("Alarm set", ">>>>>>>>>>>>>>>>>>>> Alarm set >>>>>>>>>>>>>>>>>>>>");
        } else if (action.equals(ACTION_TIME_TICK) || action.equals(ACTION_USER_PRESENT) || action.equals(ACTION_SCREEN_ON)) {
            Intent intentStart = new Intent(CommonAppsService.ACTION_NAME_START_OR_STOP_SERVICE);
            intentStart.setPackage(CommonAppsService.PACKAGE_NAME);
            context.startForegroundService(intentStart);
            Log.e("wake the service", "~~~~~~~~~~~~~~~~~~~~ wake up, don't die ~~~~~~~~~~~~~~~~~~~~");
        } else if (action.equals(ACTION_NAME_CHANGE_COLOR)) {
            if (widgetIds.length > 0)
                changeColor(context, AppWidgetManager.getInstance(context), widgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        if (appWidgetIds.length > 0)
//            myUpdate(context, appWidgetManager, appWidgetIds, MyApplication.COLOR_STAY);
//        else
//            Log.e("update canceled", "@@@@@@@@@@ No widget @@@@@@@@@@");
    }

    public void changeColor(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (new Random().nextBoolean()) {
            Log.e("change color", "(((((((((((((((((((( random color : black ))))))))))))))))))))");
            TypedArray tr = context.getResources().obtainTypedArray(R.array.button_texts);
            int lent = tr.length();
            int[] buttonTextIds = new int[lent];
            for (int i = 0; i < lent; i++) {
                buttonTextIds[i] = tr.getResourceId(i, 0);
            }
            for (int appWidgetId : appWidgetIds) {
                RemoteViews rv = new RemoteViews(PACKAGE_NAME, R.layout.apps_panel_layout);
                for (int i = 0; i < buttonTextIds.length; i++) {
                    rv.setTextColor(buttonTextIds[i], Color.BLACK);
                    rv.setTextColor(R.id.widget_name, Color.BLACK);
                    rv.setTextColor(R.id.update_button, Color.BLACK);
                }
                appWidgetManager.updateAppWidget(appWidgetId, rv);
            }
//            Toast.makeText(context, R.string.random_color_black, Toast.LENGTH_SHORT).show();
            tr.recycle();
        } else {
            Log.e("change color", "(((((((((((((((((((( random color : white ))))))))))))))))))))");
            TypedArray tr = context.getResources().obtainTypedArray(R.array.button_texts);
            int lent = tr.length();
            int[] buttonTextIds = new int[lent];
            for (int i = 0; i < lent; i++) {
                buttonTextIds[i] = tr.getResourceId(i, 0);
            }
            for (int appWidgetId : appWidgetIds) {
                RemoteViews rv = new RemoteViews(PACKAGE_NAME, R.layout.apps_panel_layout);
                for (int i = 0; i < buttonTextIds.length; i++) {
                    rv.setTextColor(buttonTextIds[i], Color.WHITE);
                    rv.setTextColor(R.id.widget_name, Color.WHITE);
                    rv.setTextColor(R.id.update_button, Color.WHITE);
                }
                appWidgetManager.updateAppWidget(appWidgetId, rv);
            }
//            Toast.makeText(context, R.string.random_color_white, Toast.LENGTH_SHORT).show();
            tr.recycle();
        }
    }

    public void myUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, int color) {
        Log.e("update widgets", "==================== myUpdate() called with " + MyApplication.getColorName(color) + " ====================");
        List<UsageStats> list = getUsageStatsList(context, SORT_TOTAL_VISIBLE_TIME);
        if (list == null) {
            Toast.makeText(context, R.string.null_stats, Toast.LENGTH_LONG).show();
            return;
        }
        int rank = 1;
        for (UsageStats app :
                list) {
            try {
                Log.e(rank + " ", context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(app.getPackageName(), PackageManager.GET_META_DATA)).toString());
            } catch (PackageManager.NameNotFoundException e) {
            }
            rank++;
        }
        TypedArray ar = context.getResources().obtainTypedArray(R.array.buttons);
        TypedArray tr = context.getResources().obtainTypedArray(R.array.button_texts);
        int len = ar.length(), lent = tr.length();
        int[] buttonIds = new int[len], buttonTextIds = new int[lent];
        for (int i = 0; i < len; i++) {
            buttonIds[i] = ar.getResourceId(i, 0);
        }
        for (int i = 0; i < lent; i++) {
            buttonTextIds[i] = tr.getResourceId(i, 0);
        }
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(PACKAGE_NAME, R.layout.apps_panel_layout);
            for (int i = 0; i < buttonIds.length; i++) {
                /*change the icon and the link*/
                rv.setImageViewBitmap(buttonIds[i], drawableToBitmap(context, getDrawable(context, list.get(i))));
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(list.get(i).getPackageName());
                PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE_COMMON_APPS_START_OTHER_APP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setOnClickPendingIntent(buttonIds[i], pendingIntent);
                Intent intentChange = new Intent(ACTION_NAME_CHANGE_COLOR);
                intentChange.setComponent(new ComponentName(PACKAGE_NAME, CLASS_NAME));
                rv.setOnClickPendingIntent(R.id.widget_name, PendingIntent.getBroadcast(context, REQUEST_CODE_COMMON_APPS_CHANGE_COLOR, intentChange, PendingIntent.FLAG_UPDATE_CURRENT));
                rv.setOnClickPendingIntent(R.id.widget_name_blank, PendingIntent.getBroadcast(context, REQUEST_CODE_COMMON_APPS_CHANGE_COLOR, intentChange, PendingIntent.FLAG_UPDATE_CURRENT));
                rv.setOnClickPendingIntent(R.id.update_button, PendingIntent.getBroadcast(context, REQUEST_CODE_COMMON_APPS_CHANGE_COLOR, intentChange, PendingIntent.FLAG_UPDATE_CURRENT));
                rv.setOnClickPendingIntent(R.id.update_button_blank, PendingIntent.getBroadcast(context, REQUEST_CODE_COMMON_APPS_CHANGE_COLOR, intentChange, PendingIntent.FLAG_UPDATE_CURRENT));
                try {
                    rv.setTextViewText(buttonTextIds[i], context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(list.get(i).getPackageName(), PackageManager.GET_META_DATA)));
                } catch (PackageManager.NameNotFoundException e) {
                }
                if (color == MyApplication.COLOR_BLACK) {
                    rv.setTextColor(buttonTextIds[i], Color.BLACK);
                    rv.setTextColor(R.id.widget_name, Color.BLACK);
                    rv.setTextColor(R.id.update_button, Color.BLACK);
                } else if (color == MyApplication.COLOR_WHITE) {
                    rv.setTextColor(buttonTextIds[i], Color.WHITE);
                    rv.setTextColor(R.id.widget_name, Color.WHITE);
                    rv.setTextColor(R.id.update_button, Color.WHITE);
                }
                rv.setTextViewText(R.id.widget_name, "常用应用页 | Last Update at " + new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
            }
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        ar.recycle();
        tr.recycle();
    }

    private long getLaunchCount(UsageStats usageStats) throws NoSuchFieldException, IllegalAccessException {
        Field field = null;
        field = usageStats.getClass().getDeclaredField("mLaunchCount");
        return (long) (int) (field.get(usageStats));
    }

    private List<UsageStats> getUsageStatsList(Context context, int mode) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> list = usageStatsManager.queryUsageStats(INTERVAL_BEST, 0, System.currentTimeMillis());
        List<UsageStats> nameFoundList = new ArrayList<UsageStats>();
        for (UsageStats stats :
                list) {
            try {
                context.getPackageManager().getApplicationInfo(stats.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            nameFoundList.add(stats);
        }
        list = nameFoundList;
        if (list.size() > 0) {
            try {
                if (mode == SORT_LAUNCH_TIME) {
                    list.sort(new Comparator<UsageStats>() {
                        @Override
                        public int compare(UsageStats o1, UsageStats o2) {
                            try {
                                long o1Launch = getLaunchCount(o1), o2Launch = getLaunchCount(o2);
                                if (o1Launch > o2Launch) {
                                    return -1;
                                } else if (o1Launch == o2Launch) {
                                    return 0;
                                } else if (o1Launch < o2Launch) {
                                    return 1;
                                }
                            } catch (Exception e) {
                            }
                            return 0;
                        }
                    });
                } else if (mode == SORT_TOTAL_VISIBLE_TIME) {
                    list.sort(new Comparator<UsageStats>() {
                        @Override
                        public int compare(UsageStats o1, UsageStats o2) {
                            try {
                                long o1TotalVisTime = getTotalUsedTime(o1), o2TotalVisTime = getTotalUsedTime(o2);
                                if (o1TotalVisTime > o2TotalVisTime) {
                                    return -1;
                                } else if (o1TotalVisTime == o2TotalVisTime) {
                                    return 0;
                                } else if (o1TotalVisTime < o2TotalVisTime) {
                                    return 1;
                                }
                            } catch (Exception e) {
                            }
                            return 0;
                        }
                    });
                }
                List<UsageStats> deduplicationList = deduplicate(context, list, DEDUPLICATION_INTENT_ACCEPT_NOT_NULL);
                Collections.reverse(deduplicationList);
                deduplicationList = deduplicate(context, deduplicationList, DEDUPLICATION_LABEL_ACCEPT_FIRST);
                Collections.reverse(deduplicationList);
                int end = 23;   //****WARNING: the number of apps on the phone must be at least 24****
                //now, end point at the
                if (mode == SORT_LAUNCH_TIME)
                    while (end + 1 < deduplicationList.size() && getLaunchCount(deduplicationList.get(end + 1)) == getLaunchCount(deduplicationList.get(end))) {
                        end++;
                    }
                else if (mode == SORT_TOTAL_VISIBLE_TIME)
                    while (end + 1 < deduplicationList.size() && getTotalUsedTime(deduplicationList.get(end + 1)) == getTotalUsedTime(deduplicationList.get(end))) {
                        end++;
                    }
                //now, end point at the last app in the tail
                List<UsageStats> firstList = deduplicationList.subList(0, end + 1);
                if (mode == SORT_LAUNCH_TIME)
                    while (end - 1 >= 0 && getLaunchCount(firstList.get(end - 1)) == getLaunchCount(firstList.get(end))) {
                        end--;
                    }
                else if (mode == SORT_TOTAL_VISIBLE_TIME)
                    while (end - 1 >= 0 && getTotalUsedTime(firstList.get(end - 1)) == getTotalUsedTime(firstList.get(end))) {
                        end--;
                    }
                //now, end point at the first app in the tail
                List<UsageStats> headList = firstList.subList(0, end);
                List<UsageStats> tailList = firstList.subList(end, firstList.size());
                tailList.sort(new Comparator<UsageStats>() {
                    @Override
                    public int compare(UsageStats o1, UsageStats o2) {
                        try {

                            if (getLastUsedTime(o1) > getLastUsedTime(o2)) {
                                return -1;
                            } else if (getLastUsedTime(o1) == getLastUsedTime(o2)) {
                                return 0;
                            } else if (getLastUsedTime(o1) < getLastUsedTime(o2)) {
                                return 1;
                            }
                        } catch (Exception e) {
                        }
                        return 0;
                    }
                });
                int num = 24 - headList.size();
                if (num > 0) {
                    headList.addAll(tailList.subList(0, num));
                }
                List<UsageStats> list24 = headList;
                List<UsageStats> list121 = list24.subList(0, 12);
                List<UsageStats> list122 = list24.subList(12, 24);
                list122.sort(new Comparator<UsageStats>() {
                    @Override
                    public int compare(UsageStats o1, UsageStats o2) {
                        try {

                            if (getLastUsedTime(o1) > getLastUsedTime(o2)) {
                                return 1;
                            } else if (getLastUsedTime(o1) == getLastUsedTime(o2)) {
                                return 0;
                            } else if (getLastUsedTime(o1) < getLastUsedTime(o2)) {
                                return -1;
                            }
                        } catch (Exception e) {
                        }
                        return 0;
                    }
                });
                list121.sort(new Comparator<UsageStats>() {
                    @Override
                    public int compare(UsageStats o1, UsageStats o2) {
                        try {

                            if (getLastUsedTime(o1) > getLastUsedTime(o2)) {
                                return -1;
                            } else if (getLastUsedTime(o1) == getLastUsedTime(o2)) {
                                return 0;
                            } else if (getLastUsedTime(o1) < getLastUsedTime(o2)) {
                                return 1;
                            }
                        } catch (Exception e) {
                        }
                        return 0;
                    }
                });
                list121.addAll(list122);
                List<UsageStats> listRes = list121;
                return listRes;
            } catch (Exception e) {
                Log.e("exception caused in getUsageStatsList()", "EEEEEEEEEEEEEEEEEEEE " + e.toString() + " EEEEEEEEEEEEEEEEEEEE");

            }
        }
        return null;
    }

    private List<UsageStats> deduplicate(Context context, List<UsageStats> list, int mode) {
        List<UsageStats> deduplicationList = new ArrayList<UsageStats>();
        if (mode == DEDUPLICATION_INTENT_ACCEPT_NOT_NULL) {
            for (UsageStats stats : list) {
                if (context.getPackageManager().getLaunchIntentForPackage(stats.getPackageName()) != null) {
                    deduplicationList.add(stats);
                }
            }
        } else if (mode == DEDUPLICATION_LABEL_ACCEPT_FIRST) {
            HashSet<String> statsHashSet = new HashSet<String>();
            for (UsageStats stats : list) {
                String labelName;
                try {
                    labelName = context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(stats.getPackageName(), PackageManager.GET_META_DATA)).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    continue;
                }
                if (statsHashSet.add(labelName)) {
                    deduplicationList.add(stats);
                }
            }
        }
        return deduplicationList;
    }

    private long getTotalUsedTime(UsageStats stats) {
        long res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            res = stats.getTotalTimeVisible();
        else
            res = stats.getTotalTimeInForeground();
        return res;
    }

    private long getLastUsedTime(UsageStats stats) {
        long res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            res = stats.getLastTimeVisible();
        else
            res = stats.getLastTimeUsed();
        return res;
    }

    private Bitmap drawableToBitmap(Context context, Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width, height);
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        Log.e("original icon width", "-------------------- " + bitmap.getWidth() + " --------------------");
        Matrix matrix = new Matrix();
        int new_width = context.getResources().getInteger(R.integer.bitmap_width_default);
        String brand = Build.BRAND;
        Log.e("brand", "BRAND BRAND BEAND     " + brand + "     BRAND BRAND BRAND");
        if (brand.equalsIgnoreCase("huawei")) {
            new_width = context.getResources().getInteger(R.integer.bitmap_width_huawei);
        } else if (brand.equalsIgnoreCase("xiaomi")) {
            new_width = context.getResources().getInteger(R.integer.bitmap_width_xiaomi);
        } else if (brand.equalsIgnoreCase("honor")) {
            new_width = context.getResources().getInteger(R.integer.bitmap_width_honor);
        }
        matrix.postScale(new_width / (float) bitmap.getWidth(), new_width / (float) bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Drawable getDrawable(Context context, UsageStats usage) {
        PackageManager pm = context.getPackageManager();
        Drawable drawable;
        try {
            drawable = pm.getApplicationIcon(pm.getApplicationInfo(usage.getPackageName(), PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            drawable = null;
        }
        return drawable;
    }
}
