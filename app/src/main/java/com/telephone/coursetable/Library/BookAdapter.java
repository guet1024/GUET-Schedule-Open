package com.telephone.coursetable.Library;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.telephone.coursetable.R;

import java.util.List;
import java.util.Map;

public class BookAdapter implements ExpandableListAdapter {

    private Context context;

    private DataSetObservable dataSetObservable;
    private Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>> groups;
    private boolean singleExpanded;
    private ExpandableListView list;

    public BookAdapter(Context context, Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>> groups, boolean singleExpanded, ExpandableListView list) {
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
        return groups.getValue().size();
    }

    @Override
    public Object getGroup(int i) {
        return groups;
    }

    @Override
    public Object getChild(int i, int i1) {
        return groups.getValue().get(i1);
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
            view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.library_infoes_activity, viewGroup, false);
        }

        if (groups.getKey().size()==14) {
            ((TextView) view.findViewById(R.id.message_info_id)).setText("    "+groups.getKey().get(0).getKey()+groups.getKey().get(0).getValue());
            ((TextView) view.findViewById(R.id.message_info_mingcheng)).setText("    "+groups.getKey().get(1).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_name)).setText(groups.getKey().get(1).getValue());
            ((TextView) view.findViewById(R.id.message_info_zuozhe)).setText("    "+groups.getKey().get(2).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_author)).setText(groups.getKey().get(2).getValue());
            ((TextView) view.findViewById(R.id.message_info_chubanshe)).setText("    "+groups.getKey().get(3).getKey());
            ((TextView) view.findViewById(R.id.message_info_publisher)).setText(groups.getKey().get(3).getValue());
            ((TextView) view.findViewById(R.id.message_info_publishdate)).setText("    "+groups.getKey().get(4).getKey()+groups.getKey().get(4).getValue());
            ((TextView) view.findViewById(R.id.message_info_isbn)).setText("    "+groups.getKey().get(5).getKey()+groups.getKey().get(5).getValue());
            ((TextView) view.findViewById(R.id.message_info_ssh)).setText("    "+groups.getKey().get(6).getKey()+groups.getKey().get(6).getValue());
            ((TextView) view.findViewById(R.id.message_info_flh)).setText("    "+groups.getKey().get(7).getKey()+groups.getKey().get(7).getValue());
            ((TextView) view.findViewById(R.id.message_info_ys)).setText("    "+groups.getKey().get(8).getKey()+groups.getKey().get(8).getValue());
            ((TextView) view.findViewById(R.id.message_info_jg)).setText("    "+groups.getKey().get(9).getKey()+groups.getKey().get(9).getValue());
            ((TextView) view.findViewById(R.id.message_info_fbs)).setText("    "+groups.getKey().get(10).getKey()+groups.getKey().get(10).getValue());
            ((TextView) view.findViewById(R.id.message_info_zgs)).setText("    "+groups.getKey().get(11).getKey()+groups.getKey().get(11).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljts)).setText("    "+groups.getKey().get(12).getKey()+groups.getKey().get(12).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljcs)).setText("    "+groups.getKey().get(13).getKey()+groups.getKey().get(13).getValue());
        }

        if (groups.getKey().size()==13) {
            ((TextView) view.findViewById(R.id.message_info_id)).setText("    "+groups.getKey().get(0).getKey()+groups.getKey().get(0).getValue());
            ((TextView) view.findViewById(R.id.message_info_mingcheng)).setText("    "+groups.getKey().get(1).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_name)).setText(groups.getKey().get(1).getValue());
            ((TextView) view.findViewById(R.id.message_info_zuozhe)).setText("    "+groups.getKey().get(2).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_author)).setText(groups.getKey().get(2).getValue());
            ((TextView) view.findViewById(R.id.message_info_chubanshe)).setText("    "+groups.getKey().get(3).getKey());
            ((TextView) view.findViewById(R.id.message_info_publisher)).setText(groups.getKey().get(3).getValue());
            ((TextView) view.findViewById(R.id.message_info_publishdate)).setText("    "+groups.getKey().get(4).getKey()+groups.getKey().get(4).getValue());
            ((TextView) view.findViewById(R.id.message_info_isbn)).setText("    "+groups.getKey().get(5).getKey()+groups.getKey().get(5).getValue());
            ((TextView) view.findViewById(R.id.message_info_ssh)).setText("    "+groups.getKey().get(6).getKey()+groups.getKey().get(6).getValue());
            ((TextView) view.findViewById(R.id.message_info_flh)).setText("    "+"分类号：");
            ((TextView) view.findViewById(R.id.message_info_ys)).setText("    "+groups.getKey().get(7).getKey()+groups.getKey().get(7).getValue());
            ((TextView) view.findViewById(R.id.message_info_jg)).setText("    "+groups.getKey().get(8).getKey()+groups.getKey().get(8).getValue());
            ((TextView) view.findViewById(R.id.message_info_fbs)).setText("    "+groups.getKey().get(9).getKey()+groups.getKey().get(9).getValue());
            ((TextView) view.findViewById(R.id.message_info_zgs)).setText("    "+groups.getKey().get(10).getKey()+groups.getKey().get(10).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljts)).setText("    "+groups.getKey().get(11).getKey()+groups.getKey().get(11).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljcs)).setText("    "+groups.getKey().get(12).getKey()+groups.getKey().get(12).getValue());
        }

        if (groups.getKey().size()==12) {
            ((TextView) view.findViewById(R.id.message_info_id)).setText("    "+groups.getKey().get(0).getKey()+groups.getKey().get(0).getValue());
            ((TextView) view.findViewById(R.id.message_info_mingcheng)).setText("    "+groups.getKey().get(1).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_name)).setText(groups.getKey().get(1).getValue());
            ((TextView) view.findViewById(R.id.message_info_zuozhe)).setText("    "+groups.getKey().get(2).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_author)).setText(groups.getKey().get(2).getValue());
            ((TextView) view.findViewById(R.id.message_info_chubanshe)).setText("    "+groups.getKey().get(3).getKey());
            ((TextView) view.findViewById(R.id.message_info_publisher)).setText(groups.getKey().get(3).getValue());
            ((TextView) view.findViewById(R.id.message_info_publishdate)).setText("    "+groups.getKey().get(4).getKey()+groups.getKey().get(4).getValue());
            ((TextView) view.findViewById(R.id.message_info_isbn)).setText("    "+groups.getKey().get(5).getKey()+groups.getKey().get(5).getValue());
            ((TextView) view.findViewById(R.id.message_info_ssh)).setText("    "+groups.getKey().get(6).getKey()+groups.getKey().get(6).getValue());
            ((TextView) view.findViewById(R.id.message_info_flh)).setText("    "+"分类号：");
            ((TextView) view.findViewById(R.id.message_info_ys)).setText("    "+groups.getKey().get(7).getKey()+groups.getKey().get(7).getValue());
            ((TextView) view.findViewById(R.id.message_info_jg)).setText("    "+"价格：");
            ((TextView) view.findViewById(R.id.message_info_fbs)).setText("    "+groups.getKey().get(8).getKey()+groups.getKey().get(8).getValue());
            ((TextView) view.findViewById(R.id.message_info_zgs)).setText("    "+groups.getKey().get(9).getKey()+groups.getKey().get(9).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljts)).setText("    "+groups.getKey().get(10).getKey()+groups.getKey().get(10).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljcs)).setText("    "+groups.getKey().get(11).getKey()+groups.getKey().get(11).getValue());
        }

        if (groups.getKey().size()==11) {
            ((TextView) view.findViewById(R.id.message_info_id)).setText("    "+groups.getKey().get(0).getKey()+groups.getKey().get(0).getValue());
            ((TextView) view.findViewById(R.id.message_info_mingcheng)).setText("    "+groups.getKey().get(1).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_name)).setText(groups.getKey().get(1).getValue());
            ((TextView) view.findViewById(R.id.message_info_zuozhe)).setText("    "+groups.getKey().get(2).getKey()+"　");
            ((TextView) view.findViewById(R.id.message_info_author)).setText(groups.getKey().get(2).getValue());
            ((TextView) view.findViewById(R.id.message_info_chubanshe)).setText("    "+groups.getKey().get(3).getKey());
            ((TextView) view.findViewById(R.id.message_info_publisher)).setText(groups.getKey().get(3).getValue());
            ((TextView) view.findViewById(R.id.message_info_publishdate)).setText("    "+groups.getKey().get(4).getKey()+groups.getKey().get(4).getValue());
            ((TextView) view.findViewById(R.id.message_info_isbn)).setText("    "+groups.getKey().get(5).getKey()+groups.getKey().get(5).getValue());
            ((TextView) view.findViewById(R.id.message_info_ssh)).setText("    "+groups.getKey().get(6).getKey()+groups.getKey().get(6).getValue());
            ((TextView) view.findViewById(R.id.message_info_flh)).setText("    "+"分类号：");
            ((TextView) view.findViewById(R.id.message_info_ys)).setText("    "+"页数：");
            ((TextView) view.findViewById(R.id.message_info_jg)).setText("    "+"价格：");
            ((TextView) view.findViewById(R.id.message_info_fbs)).setText("    "+groups.getKey().get(7).getKey()+groups.getKey().get(7).getValue());
            ((TextView) view.findViewById(R.id.message_info_zgs)).setText("    "+groups.getKey().get(8).getKey()+groups.getKey().get(8).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljts)).setText("    "+groups.getKey().get(9).getKey()+groups.getKey().get(9).getValue());
            ((TextView) view.findViewById(R.id.message_info_ljcs)).setText("    "+groups.getKey().get(10).getKey()+groups.getKey().get(10).getValue());
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean isLastChild, View view, ViewGroup viewGroup) {

        if(view == null){
            view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.library_locals_activity, viewGroup, false);
        }

        //i是有几个列表，i1是键值对中值列表
        ((TextView)view.findViewById(R.id.message_local_bookid)).setText("          "+groups.getValue().get(i1).get(0).getKey()+groups.getValue().get(i1).get(0).getValue());
        ((TextView)view.findViewById(R.id.message_local_barcode)).setText("          "+groups.getValue().get(i1).get(1).getKey()+groups.getValue().get(i1).get(1).getValue());
        ((TextView)view.findViewById(R.id.message_local_callno)).setText("          "+groups.getValue().get(i1).get(2).getKey()+groups.getValue().get(i1).get(2).getValue());
        ((TextView)view.findViewById(R.id.message_local_localstatu)).setText("          "+groups.getValue().get(i1).get(3).getKey()+groups.getValue().get(i1).get(3).getValue());
        ((TextView)view.findViewById(R.id.message_local_local)).setText("          "+groups.getValue().get(i1).get(4).getKey()+groups.getValue().get(i1).get(4).getValue());
        ((TextView)view.findViewById(R.id.message_local_cirType)).setText("          "+groups.getValue().get(i1).get(5).getKey()+groups.getValue().get(i1).get(5).getValue());

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
        return groups.getKey().isEmpty();
    }

    @Override
    public void onGroupExpanded(int gp) {
        if (singleExpanded) {
            for (int i = 0; i < groups.getKey().size(); i++){
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
