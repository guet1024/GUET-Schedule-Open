package com.telephone.coursetable;

import android.content.Context;

import java.util.Map;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

/**
 * @clear
 */
public class FunctionMenuAdapter implements ExpandableListAdapter {

    private Context context;

    private DataSetObservable dataSetObservable;
    private List<Map.Entry<String, List<List<String>>>> groups;
    private boolean singleExpanded;
    private ExpandableListView list;
    private FunctionMenu menu;

    public FunctionMenuAdapter(Context context, List<Map.Entry<String, List<List<String>>>> groups, boolean singleExpanded, ExpandableListView list, FunctionMenu menu) {
        this.context = context;
        this.dataSetObservable = new DataSetObservable();
        this.groups = groups;
        this.singleExpanded = singleExpanded;
        this.list = list;
        this.menu = menu;
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
        return groups.get(i).getKey();
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
        ((TextView)view.findViewById(R.id.textView_group_text)).setText(groups.get(i).getKey());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean isLastChild, View view, ViewGroup viewGroup) {
        View.OnClickListener collapse = view1 -> list.collapseGroup(i);
        switch (i){
            case 0:
                if (view != null &&  ((TextView)view.findViewById(R.id.textView_pinfo_key)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_person_info, viewGroup, false);
                view.setOnClickListener(collapse);
                break;
            case 1:
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_graduation_score, viewGroup, false);
                view.setOnClickListener(collapse);
                break;
            case 2:
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_grades, viewGroup, false);
                view.setOnClickListener(collapse);
                break;
        }
        switch (i){
            case 0:
                ((TextView)view.findViewById(R.id.textView_pinfo_key)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.textView_pinfo_value)).setText(groups.get(i).getValue().get(i1).get(1));
                break;
            case 1:
                ((TextView)view.findViewById(R.id.textView_graduation_score_cname)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.textView_graduation_score_score)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.textView_graduation_score_xf)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.textView_graduation_score_success)).setText(groups.get(i).getValue().get(i1).get(3));
                ((TextView)view.findViewById(R.id.textView_graduation_score_plan)).setText(groups.get(i).getValue().get(i1).get(4));
                if (groups.get(i).getValue().get(i1).get(5) != null){
                    ((TextView)view.findViewById(R.id.textView_graduation_score_cname)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(5)));
                }
                break;
            case 2:
                ((TextView)view.findViewById(R.id.grades_cname)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.grades_grade)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.grades_usual)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.grades_exp)).setText(groups.get(i).getValue().get(i1).get(3));
                ((TextView)view.findViewById(R.id.grades_test)).setText(groups.get(i).getValue().get(i1).get(4));
                if (groups.get(i).getValue().get(i1).get(5) != null){
                    ((TextView)view.findViewById(R.id.grades_cname)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(5)));
                }
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
