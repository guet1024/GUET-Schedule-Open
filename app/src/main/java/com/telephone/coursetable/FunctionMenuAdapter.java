package com.telephone.coursetable;

import android.content.Context;

import java.util.Map;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.telephone.coursetable.Library.LibraryActivity;
import com.telephone.coursetable.TeachersEvaluation.TeachersEvaluation;
import com.telephone.coursetable.Update.Update;

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
                if (view != null &&  ((TextView)view.findViewById(R.id.cet_card_id)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_cet, viewGroup, false);
                view.setOnClickListener(collapse);
                break;
            case 8:
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_update, viewGroup, false);
                final View view_f = view;
                final TextView update_textview = (TextView)view_f.findViewById(R.id.update_update);
                final ProgressBar update_progressbar = (ProgressBar)view_f.findViewById(R.id.update_progressBar);
                final AppCompatActivity c = (AppCompatActivity) context;
                update_progressbar.setVisibility(View.INVISIBLE);
                class Check implements Runnable{
                    TextView t;
                    ProgressBar p;
                    View v;
                    AppCompatActivity a;

                    public Check(TextView t, ProgressBar p, View v, AppCompatActivity a) {
                        this.t = t;
                        this.p = p;
                        this.v = v;
                        this.a = a;
                    }

                    @Override
                    public void run() {
                        a.runOnUiThread(()->{
                            t.setText(groups.get(i).getValue().get(i1).get(0));
                            p.setVisibility(View.VISIBLE);
                            v.setOnClickListener(view18 -> Log.e("Update Check", "duplicated check"));
                            Update.whatIsNew(
                                    context,
                                    ()-> a.runOnUiThread(()->{
                                        t.setText(groups.get(i).getValue().get(i1).get(0) + "(网络错误，点击重试)");
                                        p.setVisibility(View.INVISIBLE);
                                        v.setOnClickListener(view15 -> Check.this.run());
                                    }),
                                    ()-> a.runOnUiThread(()->{
                                        t.setText(groups.get(i).getValue().get(i1).get(0) + "(有新版本⬆)");
                                        p.setVisibility(View.INVISIBLE);
                                        v.setOnClickListener(view17 -> {
                                            Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases");
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                        });
                                    }),
                                    ()-> a.runOnUiThread(()->{
                                        t.setText(groups.get(i).getValue().get(i1).get(0) + "(已是最新版本😋)");
                                        p.setVisibility(View.INVISIBLE);
                                        v.setOnClickListener(view16 -> Check.this.run());
                                    })
                            );
                        });
                    }
                }
                Check check = new Check(update_textview, update_progressbar, view_f, c);
                view.setOnClickListener(view13 -> check.run());
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
                double grade;
                String grade_s = groups.get(i).getValue().get(i1).get(1);
                try {
                    grade = Double.parseDouble(grade_s);
                }catch (Exception e){
                    grade = -1;
                }
                if(i1 > 0 && (grade_s.contains("旷") ||
                        grade_s.contains("缺") ||
                        grade_s.contains("取消") ||
                        grade_s.contains("不") ||
                        (grade >= 0 && grade < 60))){
                    ((TextView)view.findViewById(R.id.grades_grade)).setTextColor(0xFFE18989);
                }else if (i1 == 0){
                    ColorStateList old = ((TextView)view.findViewById(R.id.grades_usual)).getTextColors();
                    ((TextView)view.findViewById(R.id.grades_grade)).setTextColor(old);
                }else {
                    ((TextView)view.findViewById(R.id.grades_grade)).setTextColor(0xFF82B983);
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
                ((TextView)view.findViewById(R.id.cet_term)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.cet_exam)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.cet_score)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.cet_percentile_score)).setText(groups.get(i).getValue().get(i1).get(3));
                ((TextView)view.findViewById(R.id.cet_card_id)).setText(groups.get(i).getValue().get(i1).get(4));
                break;
            case 8:
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
