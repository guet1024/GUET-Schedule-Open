package com.telephone.coursetable.AppWidgetProvider;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.FetchService;
import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.spec.OAEPParameterSpec;

public class List extends AppWidgetProvider {
    public static final String CLASS_NAME = MyApp.PACKAGE_NAME + ".AppWidgetProvider.List";

    /**
     * initialize new app-widget
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final String NAME = "onUpdate()";
        RemoteViews remoteViews = new RemoteViews(MyApp.PACKAGE_NAME, R.layout.appwidget_layout_list);
        Intent open_intent = new Intent(context, MainActivity.class);
        open_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_list_title_text, PendingIntent.getActivity(context, 0, open_intent, PendingIntent.FLAG_UPDATE_CURRENT));
        Intent data_intent = new Intent(context, ListRemoteViewsService.class);
        // simply point to the remote views service, no need to add extra, nothing to add
        remoteViews.setRemoteAdapter(R.id.appwidget_list_listview, data_intent);
        // notice: 1. the onDataSetChanged() will be called 2. the adapter is set to default data-list 3. the remote view will be refresh except the list-view
        // ▲ the app-widget will try to use the old list-view cached by other existing widgets
        // ▲ only when no cached found the widget will get a new list-view
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);// 1. no date 2. display default data-list(or the cached list-view)
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Toast.makeText(context, "课程表小部件稍后将自动刷新", Toast.LENGTH_LONG).show();
        Log.e(NAME, "new list app-widget(s) added");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String NAME = "onReceive()";
        if (intent.getAction().equals("com.telephone.coursetable.action.UPDATE_THE_DATASET_OF_ALL_LIST_APPWIDGET")){
            AppWidgetManager m = AppWidgetManager.getInstance(context);
            // get ids of all existing app-widgets
            int[] w_ids = m.getAppWidgetIds(new ComponentName(MyApp.PACKAGE_NAME, CLASS_NAME));
            if (w_ids != null && w_ids.length > 0) {
                RemoteViews remoteViews = new RemoteViews(MyApp.PACKAGE_NAME, R.layout.appwidget_layout_list);
                // open button
                Intent open_intent = new Intent(context, MainActivity.class);
                open_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                remoteViews.setOnClickPendingIntent(R.id.appwidget_list_title_text, PendingIntent.getActivity(context, 0, open_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                // refresh date
                LocalDateTime now = LocalDateTime.now();
                remoteViews.setTextViewText(R.id.appwidget_list_title_date,
                        now.getMonthValue() + "月" + now.getDayOfMonth() + "日"
                                + new String[]{
                                "",
                                "星期一",
                                "星期二",
                                "星期三",
                                "星期四",
                                "星期五",
                                "星期六",
                                "星期日"}[now.getDayOfWeek().getValue()] + "    ");
                // declare the remote views service, so that the app-widget can get its old remote adapter
                Intent data_intent = new Intent(context, ListRemoteViewsService.class);
                // no need to add extra, because even if you send some extra, the extra won't be sent to the old remote adapter
                remoteViews.setRemoteAdapter(R.id.appwidget_list_listview, data_intent);
                // update the app-widget
                // notice: the old adapter won't neither be create again nor refresh the list-view, the onDataSetChanged() won't be called neither
                m.updateAppWidget(w_ids, remoteViews);
                super.onUpdate(context, m, w_ids);

                // so that, we put the new data-list in MyAPP's static variable
                MyApp.setData_list(intent.getStringArrayListExtra(ListRemoteViewsService.EXTRA_ARRAY_LIST_OF_STRING_TO_GET_A_NEW_REMOTE_ADAPTER));
                // then notify all app-widgets's remote adapter to get new data-list and refresh view
                m.notifyAppWidgetViewDataChanged(w_ids, R.id.appwidget_list_listview);

                Log.e(NAME, "all list app-widgets updated: " + w_ids.length + " total");
            }else {
                Log.e(NAME, "no existing list app-widgets to update");
            }
        }else if (intent.getAction().equals("com.telephone.coursetable.action.START_FETCH_SERVIE")){
            FetchService.startAction_START_FETCH_DATA(context, MyApp.service_fetch_interval);
        }
        super.onReceive(context, intent);
    }
}
