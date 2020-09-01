package com.telephone.coursetable.AppWidgetProvider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class List extends AppWidgetProvider {
    public static final String CLASS_NAME = MyApp.PACKAGE_NAME + ".AppWidgetProvider.List";

    /**
     * only for test
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        java.util.ArrayList<String> data = new ArrayList<>();
        data.add(ListAdapter.TODAY);
        data.add(MyApp.appwidget_list_time_descriptions[0]);
        data.add(null);
        data.add("5203" + "    " + "高等数学A1" + " " + "@" + "王会勇");
        data.add(null);
        data.add("17401" + "    " + "线性代数" + " " + "@" + "李露");
        data.add(ListAdapter.TODAY);
        data.add(MyApp.appwidget_list_time_descriptions[1]);
        data.add(null);
//        data.add("17309" + "    " + "毛泽东思想和中国特色社会主义理论体系概论" + " " + "@" + "骆方金");
        data.add("▶ " + "17309" + "    " + "毛泽东思想和中国特色..." + " " + "@" + "骆方金");
        data.add(ListAdapter.TODAY);
        data.add(MyApp.appwidget_list_time_descriptions[2]);
        data.add(null);
        data.add("球类馆" + "    " + "体育二" + " " + "@" + "温织琳");
        data.add(ListAdapter.TODAY);
        data.add(MyApp.appwidget_list_time_descriptions[3]);
        data.add(null);
        data.add("5407" + "    " + "Java程序设计实验" + " " + "@" + "唐麟");
        data.add(ListAdapter.TODAY);
        data.add(MyApp.appwidget_list_time_descriptions[4]);
        data.add(null);
        data.add("--------" + "    " + "西南联大与现代中国" + " " + "@" + "");
        data.add(ListAdapter.TOMORROW);
        data.add(MyApp.appwidget_list_time_descriptions[5]);
        data.add(ListAdapter.TOMORROW);
        data.add(MyApp.appwidget_list_time_descriptions[6]);
        data.add(null);
        data.add("3203" + "    " + "大学英语二" + " " + "@" + "邓莎");
        data.add(null);
        data.add("17407" + "    " + "大学物理B" + " " + "@" + "周建华");
        data.add(ListAdapter.TOMORROW);
        data.add(MyApp.appwidget_list_time_descriptions[7]);
        data.add(ListAdapter.TOMORROW);
        data.add(MyApp.appwidget_list_time_descriptions[8]);
        data.add(null);
        data.add("E2103" + "    " + "大学生社会礼仪" + " " + "@" + "李星");
        data.add(ListAdapter.TOMORROW);
        data.add(MyApp.appwidget_list_time_descriptions[9]);

        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent.setComponent(new ComponentName(MyApp.PACKAGE_NAME, CLASS_NAME));
        intent.putStringArrayListExtra(ListRemoteViewsService.EXTRA_DATA_ARRAY_LIST_OF_STRING, data);
        context.sendBroadcast(intent);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){
            ArrayList<String> data = intent.getStringArrayListExtra(ListRemoteViewsService.EXTRA_DATA_ARRAY_LIST_OF_STRING);
            if (data != null){
                AppWidgetManager widget_manager = AppWidgetManager.getInstance(context);
                int[] widget_ids = widget_manager.getAppWidgetIds(new ComponentName(MyApp.PACKAGE_NAME, CLASS_NAME));
                myUpdate(context, widget_manager, widget_ids, data);
            }
        }
        super.onReceive(context, intent);
    }

    private void myUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, ArrayList<String> data){
        RemoteViews remoteViews = new RemoteViews(MyApp.PACKAGE_NAME, R.layout.appwidget_layout_list);
        Intent data_intent = new Intent(context, ListRemoteViewsService.class);
        data_intent.putStringArrayListExtra(ListRemoteViewsService.EXTRA_DATA_ARRAY_LIST_OF_STRING, data);
        remoteViews.setRemoteAdapter(R.id.appwidget_list_listview, data_intent);
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
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
