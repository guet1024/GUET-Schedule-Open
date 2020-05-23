package com.example.commonapplications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;


public class CommonAppsService extends Service {

    public static final String PACKAGE_NAME = "com.example.commonapplications";
    public static final String CLASS_NAME = "com.example.commonapplications.CommonAppsService";
    public static final String ACTION_NAME_START_OR_STOP_SERVICE = "android.appwidget.action.COMMON_APPS_SERVICE";
    public static final String EXTRA_NAME_APP_TEXT_COLOR = "com.example.commonapplications.text.color";
    public static final String NOTIFICATION_CHANNEL_ID_IS_RUNNING_NOTIFICATION = "is_running";
    public static final int REQUEST_CODE_IS_RUNNING_NOTIFICATION = 55533;
    public static final int NOTIFICATION_ID_IS_RUNNING_NOTIFICATION = 55534;

    /**
     * The service has a reference of the being-used Timer. Every time I want to cancel the running
     * task and run a new task, I will also make a new Timer and replace this reference with the new
     * Timer.
     */
    private Timer timer;
    /**
     * The service has a reference of the running TimerTask, if a new TimerTask need to be run, the
     * old one will be canceled and this reference will be replaced with the new one.
     */
    private TimerTask task;

    /**
     * The task periodically sends the "UPDATE_ALL" intent with a color code.
     */
    public class Task extends TimerTask {

        @Override
        public void run() {
            Intent intent = new Intent(AppsPanel.ACTION_NAME_UPDATE_ALL_WIDGET);
            intent.setPackage(AppsPanel.PACKAGE_NAME);
            intent.setComponent(new ComponentName(AppsPanel.PACKAGE_NAME, AppsPanel.CLASS_NAME));
            intent.putExtra(EXTRA_NAME_APP_TEXT_COLOR, ((MyApplication) CommonAppsService.this.getApplication()).color);
            CommonAppsService.this.sendBroadcast(intent);
            Log.e("Task executed", ".................... task = " + Task.this.toString() + " ....................");
        }
    }

    /**
     * When the service is created, it make a new task and starts it.
     * ==========
     * I have changed my mind, no task and timer are needed. The service will live forever and sent
     * the "UPDATE_ALL" intent periodically by itself.
     * <p>
     * Every time onStartCommand() called, stopService() is called within onStartCommand() to make
     * onDestroy() called. In onDestroy(), I use a for-loop to send the "UPDATE_ALL" intent
     * periodically. After the loop, startForegroundService() is called to make the service
     * reborn.
     */
    @Override
    public void onCreate() {
        super.onCreate();
//        task = new Task();
//        timer = new Timer();
//        timer.scheduleAtFixedRate(task, 0, R.integer.period_ms);
        Log.e("service created", "SSSSSSSSSSSSSSSSSSSS onCreate() called SSSSSSSSSSSSSSSSSSSS");
    }

    /**
     * Every time the service is started, it cancels the running task and makes a new task doing the
     * same job and then starts it.
     * <p>
     * Double-cancel is safe.
     * <p>
     * Every time the service is started, it turns into foreground service by calling startForeground().
     * This is to make service itself stay alive.
     * <p>
     * By the way, I set the id of notification to 0 in hope that the notification won't show.
     * <p>
     * This method returns "START_STICKY" so that the service will be restarted automatically after
     * it get killed accidentally.
     * <p>
     * The only questions are :
     * <p>
     * Is 0-id safe and does it work?
     * ==========
     * Answer: No, it is not safe. Don't do this or your service will definitely die.
     * <p>
     * Is Double-startForeground() safe?
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        task.cancel();
//        Log.e("Task canceled", ".................... task = " + task.toString() + " ....................");
//        task = new Task();
//        timer = new Timer();
//        timer.scheduleAtFixedRate(task, 0, R.integer.period_ms);
        startForeground(NOTIFICATION_ID_IS_RUNNING_NOTIFICATION, getNotification());
        Log.e("service started", "SSSSSSSSSSSSSSSSSSSS onStartCommand() called SSSSSSSSSSSSSSSSSSSS");
        Intent intentStop = new Intent(ACTION_NAME_START_OR_STOP_SERVICE);
        intentStop.setPackage(PACKAGE_NAME);
        stopService(intentStop);
//        return Service.START_STICKY;
        return Service.START_NOT_STICKY;
    }

    /**
     * Before the service is destroyed, whether killed accidentally or stopped by user, it cancels
     * the running task.
     * <p>
     * Double-cancel is safe.
     * <p>
     * When the service is destroyed, it cancels its own foreground-service state by calling stopForeground(true).
     * <p>
     * The only question is : Is Double-stopForeground() safe?
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
//        task.cancel();
//        Log.e("Task canceled", ".................... task = " + task.toString() + " ....................");
        Log.e("service destroyed", "SSSSSSSSSSSSSSSSSSSS onDestroy() called SSSSSSSSSSSSSSSSSSSS");
        Log.e("ghost time!", "GGGGGGGGGGGGGGGGGGGG sending...... GGGGGGGGGGGGGGGGGGGG");
        int times = getResources().getInteger(R.integer.period_loop_time);
        int period = getResources().getInteger(R.integer.period_ms);
        for (int i = 0; i < times; i++) {
            Log.e("The " + (i + 1) + "th", "Loop ......");
            Intent intent = new Intent(AppsPanel.ACTION_NAME_UPDATE_ALL_WIDGET);
            intent.setPackage(AppsPanel.PACKAGE_NAME);
            intent.setComponent(new ComponentName(AppsPanel.PACKAGE_NAME, AppsPanel.CLASS_NAME));
            intent.putExtra(EXTRA_NAME_APP_TEXT_COLOR, ((MyApplication) getApplication()).color);
            sendBroadcast(intent);
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
            }
        }
        Intent intent = new Intent(ACTION_NAME_START_OR_STOP_SERVICE);
        intent.setPackage(PACKAGE_NAME);
        startForegroundService(intent);
        Log.e("Reborn", "restart-intent sent!");
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification getNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_IS_RUNNING_NOTIFICATION, getResources().getString(R.string.is_running_notification_channel_name), NotificationManager.IMPORTANCE_MIN);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification.Builder builder = new Notification.Builder(this, notificationChannel.getId());
        Intent notificationIntent = new Intent(this, this.getClass());
        builder.setContentIntent(PendingIntent.getActivity(this, REQUEST_CODE_IS_RUNNING_NOTIFICATION, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_complex_purple));
        builder.setContentTitle(getResources().getString(R.string.is_running_notification_title));
        builder.setSmallIcon(R.mipmap.ic_launcher_complex_purple_round);
        builder.setContentText(getResources().getString(R.string.is_running_notification_content_text));
        builder.setWhen(System.currentTimeMillis());
        return builder.build();
    }
}