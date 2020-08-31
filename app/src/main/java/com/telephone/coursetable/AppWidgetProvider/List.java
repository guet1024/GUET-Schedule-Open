package com.telephone.coursetable.AppWidgetProvider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.util.ArrayList;

public class List extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(MyApp.PACKAGE_NAME, R.layout.appwidget_layout_list);
        java.util.ArrayList<String> data = new ArrayList<>();
        data.add(null);
        data.add("cdcd");
        data.add("");
        data.add(null);
        Intent data_intent = new Intent(context, ListRemoteViewsService.class);
        data_intent.putStringArrayListExtra(ListRemoteViewsService.EXTRA_DATA_ARRAY_LIST_OF_STRING, data);
        remoteViews.setRemoteAdapter(R.id.appwidget_list_listview, data_intent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
