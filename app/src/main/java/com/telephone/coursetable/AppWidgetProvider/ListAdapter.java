package com.telephone.coursetable.AppWidgetProvider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.util.List;

public class ListAdapter implements RemoteViewsService.RemoteViewsFactory {

    private List<String> data;

    public ListAdapter(List<String> data){
        this.data = data;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return data.size()/2;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews view = new RemoteViews(MyApp.PACKAGE_NAME, R.layout.appwidget_list_item);
        int color_index = 2 * i;
        int text_index = color_index + 1;
        if (data.get(color_index) == null) {//content
            view.setTextColor(R.id.appwidget_list_item_tv, 0xF55FFF);
        } else {//title
            view.setTextColor(R.id.appwidget_list_item_tv, 0x000000);
        }
        if (data.get(text_index) != null) {
            view.setTextViewText(R.id.appwidget_list_item_tv, data.get(text_index));
        } else {
            view.setTextViewText(R.id.appwidget_list_item_tv, "");
        }
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
