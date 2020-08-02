package com.telephone.coursetable;

import android.content.Context;

import java.util.Map;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

public class FunctionMenuAdapter implements ExpandableListAdapter {

    private Context context;

    private DataSetObservable dataSetObservable;
    private List<Map.Entry<String, List<List<String>>>> groups;
    private boolean singleExpanded;
    private ExpandableListView list;

    public FunctionMenuAdapter(Context context, List<Map.Entry<String, List<List<String>>>> groups, boolean singleExpanded, ExpandableListView list) {
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
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return groups.get(i).getValue().size();
    }

    @Override
    public Object getGroup(int i) {
        return groups.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return groups.get(i).getValue().get(i1);
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
            view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_group, viewGroup, false);
        }
//        if (isExpanded) {
//            ((TextView) view.findViewById(R.id.textView_group_text)).setText("Group Expanded");
//        }else {
//            ((TextView) view.findViewById(R.id.textView_group_text)).setText("Group");
//        }
        ((TextView)view.findViewById(R.id.textView_group_text)).setText(groups.get(i).getKey());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean isLastChild, View view, ViewGroup viewGroup) {
        if(view == null){
            switch (i){
                case 0:
                    view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_person_info, viewGroup, false);
                    break;
            }
        }
        switch (i){
            case 0:
                ((TextView)view.findViewById(R.id.textView_person_info_name_key)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.textView_person_info_name_value)).setText(groups.get(i).getValue().get(i1).get(1));
                break;
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return groups.isEmpty();
    }

    @Override
    public void onGroupExpanded(int gp) {
        if (singleExpanded) {
            for (int i = 0; i < groups.size(); i++){
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
