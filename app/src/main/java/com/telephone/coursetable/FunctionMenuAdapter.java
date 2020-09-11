package com.telephone.coursetable;

import android.content.Context;

import java.util.Map;

import android.content.Intent;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.telephone.coursetable.Library.LibraryActivity;
import com.telephone.coursetable.TeachersEvaluation.TeachersEvaluation;

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
                if (view != null &&  ((TextView)view.findViewById(R.id.textView_graduation_score_cname)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_graduation_score, viewGroup, false);
                view.setOnClickListener(collapse);
                break;
            case 2:
                if (view != null &&  ((TextView)view.findViewById(R.id.grades_cname)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_grades, viewGroup, false);
                view.setOnClickListener(collapse);
                break;
            case 3:
                if (view != null &&  ((TextView)view.findViewById(R.id.library)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_library, viewGroup, false);
                view.setOnClickListener(view12 -> new Thread(() -> {
                    Intent intent = new Intent(context, LibraryActivity.class);
                    intent.putExtra(LibraryActivity.EXTRA_USERNAME, MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username);
                    intent.putExtra(LibraryActivity.EXTRA_VPN_PASSWORD, MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).vpn_password);
                    context.startActivity(intent);
                }).start());
                break;
            case 4:
                if (view != null &&  ((TextView)view.findViewById(R.id.change_term_item)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_change_terms, viewGroup, false);
                view.setOnClickListener(view12 -> context.startActivity(new Intent(context, ChangeTerms.class)));
                break;
            case 5:
                if (view != null &&  ((TextView)view.findViewById(R.id.function_menu_itemtv_term)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_exam, viewGroup, false);
                view.setOnClickListener(collapse);
                break;
            case 6:
                if (view != null &&  ((TextView)view.findViewById(R.id.teachers_evaluation_evaluation)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_teachers_evaluation, viewGroup, false);
                view.setOnClickListener(view14 -> new Thread(() -> TeachersEvaluation.evaluation(
                        (AppCompatActivity)context,
                        MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username,
                        MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).password,
                        MyApp.getCurrentAppDB().termInfoDao()
                        )).start());
                break;
            case 7:
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_update, viewGroup, false);
                view.setOnClickListener(view13 -> {
                    Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases");
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                });
                break;
        }
        switch (i){
            case 0:
                ((TextView)view.findViewById(R.id.textView_pinfo_key)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.textView_pinfo_value)).setText(groups.get(i).getValue().get(i1).get(1));
                break;
            case 1:
                ((TextView)view.findViewById(R.id.textView_graduation_score_cname)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.textView_graduation_score_xf)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.textView_graduation_score_score)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.textView_graduation_score_check)).setText(groups.get(i).getValue().get(i1).get(3));
                break;
            case 2:
                ((TextView)view.findViewById(R.id.grades_cname)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.grades_grade)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.grades_usual)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.grades_exp)).setText(groups.get(i).getValue().get(i1).get(3));
                ((TextView)view.findViewById(R.id.grades_test)).setText(groups.get(i).getValue().get(i1).get(4));
                if (groups.get(i).getValue().get(i1).get(5) != null){
                    ((TextView)view.findViewById(R.id.grades_cname)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(5)));
                }else {
                    TypedValue a = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
                    ((TextView)view.findViewById(R.id.grades_cname)).setBackgroundColor(a.data);
                }
                break;
            case 3:
                ((TextView)view.findViewById(R.id.library)).setText(groups.get(i).getValue().get(i1).get(0));
                break;
            case 4:
                ((TextView)view.findViewById(R.id.change_term_item)).setText(groups.get(i).getValue().get(i1).get(0));
                break;
            case 5:
                ((TextView)view.findViewById(R.id.function_menu_itemtv_term)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_cname)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_cno)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_date)).setText(groups.get(i).getValue().get(i1).get(3));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_time)).setText(groups.get(i).getValue().get(i1).get(4));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_room)).setText(groups.get(i).getValue().get(i1).get(5));
                if (groups.get(i).getValue().get(i1).get(6) != null){
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_term)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(6)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cname)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(6)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cno)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(6)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_date)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(6)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_time)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(6)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_room)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(6)));
                }else {
                    TypedValue a = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_term)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cname)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cno)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_date)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_time)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_room)).setBackgroundColor(a.data);
                }
                break;
            case 6:
                ((TextView)view.findViewById(R.id.teachers_evaluation_evaluation)).setText(groups.get(i).getValue().get(i1).get(0));
                break;
            case 7:
                ((TextView)view.findViewById(R.id.update_update)).setText(groups.get(i).getValue().get(i1).get(0));
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
