package com.telephone.coursetable.GuetTools;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.telephone.coursetable.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WebLinksAdapter implements ListAdapter {

    private DataSetObservable dataSetObservable;
    private List<Map.Entry<String, String>> list;
    private Context c;

    public WebLinksAdapter(List<Map.Entry<String, String>> list, Context c){
        this.dataSetObservable = new DataSetObservable();
        this.list = list;
        this.c = c;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = ((LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.web_links_item, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.weblinks_item_link)).setText(list.get(position).getKey());
        convertView.setOnClickListener(view->c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getValue()))));
        return convertView;
    }

    //all items are with the same view type
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    //all items are with the same view type
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    //all items are enabled
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    //all items are enabled
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
