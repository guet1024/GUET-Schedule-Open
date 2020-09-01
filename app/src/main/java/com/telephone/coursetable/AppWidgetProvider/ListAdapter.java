package com.telephone.coursetable.AppWidgetProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
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
    public static final String TODAY = "166263635514";
    public static final String TOMORROW = "1662636359634";

    private List<String> data;
    private Context c;

    public ListAdapter(Context c, List<String> data){
        this.c = c;
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
            if (data.get(text_index) != null) {
                view.setTextViewText(R.id.appwidget_list_item_tv, "    " + data.get(text_index));
            } else {
                view.setTextViewText(R.id.appwidget_list_item_tv, "    ");
            }
            view.setInt(R.id.appwidget_list_item_tv, "setVisibility", View.VISIBLE);
            view.setInt(R.id.appwidget_list_item_tv_time, "setVisibility", View.INVISIBLE);
            view.setInt(R.id.appwidget_list_item_tv_time_tomorrow, "setVisibility", View.INVISIBLE);
        } else if (data.get(color_index).equals(TODAY)){//today time
            if (data.get(text_index) != null) {
                view.setTextViewText(R.id.appwidget_list_item_tv_time, data.get(text_index));
            } else {
                view.setTextViewText(R.id.appwidget_list_item_tv_time, "");
            }
            view.setInt(R.id.appwidget_list_item_tv, "setVisibility", View.INVISIBLE);
            view.setInt(R.id.appwidget_list_item_tv_time, "setVisibility", View.VISIBLE);
            view.setInt(R.id.appwidget_list_item_tv_time_tomorrow, "setVisibility", View.INVISIBLE);
        }else if (data.get(color_index).equals(TOMORROW)){//tomorrow time
            if (data.get(text_index) != null) {
                view.setTextViewText(R.id.appwidget_list_item_tv_time_tomorrow, data.get(text_index));
            } else {
                view.setTextViewText(R.id.appwidget_list_item_tv_time_tomorrow, "");
            }
            view.setInt(R.id.appwidget_list_item_tv, "setVisibility", View.INVISIBLE);
            view.setInt(R.id.appwidget_list_item_tv_time, "setVisibility", View.INVISIBLE);
            view.setInt(R.id.appwidget_list_item_tv_time_tomorrow, "setVisibility", View.VISIBLE);
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
