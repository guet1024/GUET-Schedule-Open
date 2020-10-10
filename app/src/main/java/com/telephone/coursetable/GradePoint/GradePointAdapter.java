package com.telephone.coursetable.GradePoint;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.telephone.coursetable.R;

import java.util.List;
import java.util.Map;

public class GradePointAdapter implements ExpandableListAdapter {

    private Context context;
    private DataSetObservable dataSetObservable;
    private List<Map.Entry<String, List<Map.Entry<String, String>>>> groups;
    private boolean singleExpanded;
    private ExpandableListView list;
    private AssetManager assetManager;
    private Typeface typeface;
    private TextPaint textPaint;

    public GradePointAdapter(Context context, List<Map.Entry<String, List<Map.Entry<String, String>>>> groups, boolean singleExpanded, ExpandableListView list) {
        this.context = context;
        this.dataSetObservable = new DataSetObservable();
        this.groups = groups;
        this.singleExpanded = singleExpanded;
        this.list = list;
    }

    @Override

    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        dataSetObservable.registerObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        dataSetObservable.unregisterObserver(dataSetObserver);
    }

    @Override
    public int getGroupCount() {
        if (groups == null) return 0;
        return 1;
    }

    @Override
    public int getChildrenCount(int i) {
        return groups.get(0).getValue().size();
    }

    @Override
    public Object getGroup(int i) {
        return groups.get(0).getKey();
    }

    @Override
    public Object getChild(int i, int i1) {
        return groups.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {


        if(view == null){
            view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_grade_points_title, viewGroup, false);
        }

        assetManager = context.getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/xzbjj.ttf");

        ((TextView) view.findViewById(R.id.textView_group_text)).setText( groups.get(0).getKey() );
        setText( (TextView)view.findViewById(R.id.textView_group_text) );

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean isLastChild, View view, ViewGroup viewGroup) {

        if(view == null){
            view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_grade_points_text, viewGroup, false);
        }

        //i是有几个列表，i1是键值对中值列表
        ((TextView)view.findViewById(R.id.year)).setText( groups.get(i).getValue().get(i1).getValue() );
        setText( (TextView)view.findViewById(R.id.year) );

        ((TextView)view.findViewById(R.id.point)).setText( groups.get(i).getValue().get(i1).getKey() );
        setText( (TextView)view.findViewById(R.id.point) );

        return view;
    }

    private void setText(TextView textView) {
        textView.setTypeface(typeface);
        textPaint = textView.getPaint();
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(3);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return groups.get(0).getKey().isEmpty();
    }

    @Override
    public void onGroupExpanded(int gp) {
        if (singleExpanded) {
            for (int i = 0; i < 1; i++){
                if (gp != i){
                    list.collapseGroup(i);
                }
            }
        }
    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long gp, long cp) {
        return gp * 1000000 + (cp + 1);
    }

    @Override
    public long getCombinedGroupId(long gp) {
        return gp * 1000000;
    }

}
