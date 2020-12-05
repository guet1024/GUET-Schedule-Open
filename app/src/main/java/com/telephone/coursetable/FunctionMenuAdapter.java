package com.telephone.coursetable;

import android.content.Context;

import java.util.Map;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.GradePoint.GradePointActivity;
import com.telephone.coursetable.Library.LibraryActivity;
import com.telephone.coursetable.TeachersEvaluation.TeachersEvaluation;
import com.telephone.coursetable.Update.Update;
import com.telephone.coursetable.Webinfo.Webinfo;

import java.util.List;

/**
 * @clear
 */
public class FunctionMenuAdapter implements ExpandableListAdapter {

    private Context context;

    private DataSetObservable dataSetObservable;
    private List<Map.Entry<String, List<List<String>>>> groups;

    public List<Map.Entry<String, List<List<String>>>> getGroups() {
        return groups;
    }

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
        view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_group, viewGroup, false);
        ((TextView)view.findViewById(R.id.textView_group_text)).setText(groups.get(i).getKey());
        // clear red point first
        MainActivity.clearRedPoint(
                menu,
                (FrameLayout)view.findViewById(R.id.textView_group_text_frame)
        );
        if (((TextView)view.findViewById(R.id.textView_group_text)).getText().toString().endsWith(" ")) { // if need red point, add it
            ((TextView) view.findViewById(R.id.textView_group_text)).setText(
                    ((TextView) view.findViewById(R.id.textView_group_text)).getText().toString().trim()
            );
            MainActivity.addRedPoint(
                    menu,
                    (FrameLayout)view.findViewById(R.id.textView_group_text_frame),
                    view.findViewById(R.id.textView_group_text)
            );
        }
        switch (i){
            case 0:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.personal_info);
                break;
            case 1:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.plan_courses);
                break;
            case 2:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.grades);
                if (groups.get(i).getValue().isEmpty()){
                    view.setOnClickListener(v -> {
                        Snackbar.make(v, "成绩单为空", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                        FunctionMenu.clearUnread(menu, v, FunctionMenuAdapter.this, i);
                    });
                }
                break;
            case 3:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.library);
                break;
            case 4:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.change_term);
                break;
            case 5:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.exam);
                if (groups.get(i).getValue().isEmpty()){
                    view.setOnClickListener(v -> {
                        Snackbar.make(v, "暂无考试安排", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                        FunctionMenu.clearUnread(menu, v, FunctionMenuAdapter.this, i);
                    });
                }
                break;
            case 6:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.teachers_evaluation);
                break;
            case 7:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.cet);
                if (groups.get(i).getValue().isEmpty()){
                    view.setOnClickListener(v -> {
                        Snackbar.make(v, "未查询到等级考试成绩", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                        FunctionMenu.clearUnread(menu, v, FunctionMenuAdapter.this, i);
                    });
                }
                break;
            case 8:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.grade_points);
                break;
            case 9:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.guet_tool);
                break;
            case 10:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.update);
                break;
            case 11:
                ((ImageView)view.findViewById(R.id.group_image)).setImageResource(R.drawable.about);
                break;
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean isLastChild, View view, ViewGroup viewGroup) {
        View.OnClickListener collapse = view1 -> list.collapseGroup(i);
        switch (i){
            case 0:
                if (view != null &&  ((TextView)view.findViewById(R.id.textView_pinfo_key)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_person_info, viewGroup, false);
                break;
            case 1:
                if (view != null &&  ((TextView)view.findViewById(R.id.textView_graduation_score_cname)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_graduation_score, viewGroup, false);
                break;
            case 2:
                if (view != null &&  ((TextView)view.findViewById(R.id.grades_cname)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_grades, viewGroup, false);
                break;
            case 3:
                if (view != null &&  ((TextView)view.findViewById(R.id.library)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_library, viewGroup, false);
                break;
            case 4:
                if (view != null &&  ((TextView)view.findViewById(R.id.change_term_item)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_change_terms, viewGroup, false);
                break;
            case 5:
                if (view != null &&  ((TextView)view.findViewById(R.id.function_menu_itemtv_term)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_exam, viewGroup, false);
                break;
            case 6:
                if (view != null &&  ((TextView)view.findViewById(R.id.teachers_evaluation_evaluation)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_teachers_evaluation, viewGroup, false);
                break;
            case 7:
                if (view != null &&  ((TextView)view.findViewById(R.id.cet_card_id)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_cet, viewGroup, false);
                break;
            case 8:
                if (view != null &&  ((TextView)view.findViewById(R.id.item_query_grade_points)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_grade_points, viewGroup, false);
                break;
            case 9:
                if (view != null &&  ((TextView)view.findViewById(R.id.item_guet_tools)) != null)break;
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_guet_tools, viewGroup, false);
                break;
            case 10:
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
                            v.setOnClickListener(view18 -> com.telephone.coursetable.LogMe.LogMe.e("Update Check", "duplicated check"));
                            Update.whatIsNew(
                                    a,
                                    a,
                                    ()-> a.runOnUiThread(()->{
                                        // edit by Telephone, 2020/11/20 10:29. Now, no need to override the on-click-listener. Just let it re-query.
                                        t.setText(groups.get(i).getValue().get(i1).get(0) + "　网络错误，请重试/✈");
                                        p.setVisibility(View.INVISIBLE);
                                        v.setOnClickListener(view15 -> Check.this.run());
                                    }),
                                    ()-> a.runOnUiThread(()->{
                                        p.setVisibility(View.INVISIBLE);
                                    }),
                                    ()-> a.runOnUiThread(()->{
                                        t.setText(groups.get(i).getValue().get(i1).get(0) + "　已是最新版本");
                                        p.setVisibility(View.INVISIBLE);
                                        v.setOnClickListener(view16 -> Check.this.run());
                                    }),
                                    t,
                                    groups.get(i).getValue().get(i1).get(0),
                                    view_f,
                                    null
                            );
                        });
                    }
                }
                Check check = new Check(update_textview, update_progressbar, view_f, c);
                view.setOnClickListener(view13 -> check.run());
                break;
            case 11:
                view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.function_menu_item_about, viewGroup, false);
                break;
        }
        switch (i){
            case 0:
                ((TextView)view.findViewById(R.id.textView_pinfo_key)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.textView_pinfo_value)).setText(groups.get(i).getValue().get(i1).get(1));
                view.setOnClickListener(collapse);
                break;
            case 1:
                ((TextView)view.findViewById(R.id.textView_graduation_score_cname)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.textView_graduation_score_xf)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.textView_graduation_score_score)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.textView_graduation_score_check)).setText(groups.get(i).getValue().get(i1).get(3));
                view.setOnClickListener(collapse);
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
                view.setOnClickListener(collapse);
                break;
            case 3:
                ((TextView)view.findViewById(R.id.library)).setText(groups.get(i).getValue().get(i1).get(0));
                view.setOnClickListener(view12 -> new Thread(() -> {
                    Intent intent = new Intent(context, LibraryActivity.class);
                    intent.putExtra(LibraryActivity.EXTRA_USERNAME, MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username);
                    intent.putExtra(LibraryActivity.EXTRA_VPN_PASSWORD, MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).vpn_password);
                    context.startActivity(intent);
                }).start());
                break;
            case 4:
                ((TextView)view.findViewById(R.id.change_term_item)).setText(groups.get(i).getValue().get(i1).get(0));
                view.setOnClickListener(view12 -> context.startActivity(new Intent(context, ChangeTerms.class)));
                break;
            case 5:
                ((TextView)view.findViewById(R.id.function_menu_itemtv_term)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_cname)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_cno)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_date)).setText(groups.get(i).getValue().get(i1).get(3));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_time)).setText(groups.get(i).getValue().get(i1).get(4));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_room)).setText(groups.get(i).getValue().get(i1).get(5));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_week_weekday_time)).setText(groups.get(i).getValue().get(i1).get(6));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_comment)).setText(groups.get(i).getValue().get(i1).get(7));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_sts)).setText(groups.get(i).getValue().get(i1).get(8));
                ((TextView)view.findViewById(R.id.function_menu_itemtv_ets)).setText(groups.get(i).getValue().get(i1).get(9));
                int color_index = 10;
                if (groups.get(i).getValue().get(i1).get(color_index) != null){
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_term)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cname)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cno)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_date)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_time)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_room)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_week_weekday_time)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_comment)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((LinearLayout)view.findViewById(R.id.function_menu_itemtv_sts_background)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                    ((LinearLayout)view.findViewById(R.id.function_menu_itemtv_ets_background)).setBackgroundColor(FunctionMenu.colors.get(groups.get(i).getValue().get(i1).get(color_index)));
                }else {
                    TypedValue a = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_term)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cname)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_cno)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_date)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_time)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_room)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_week_weekday_time)).setBackgroundColor(a.data);
                    ((TextView)view.findViewById(R.id.function_menu_itemtv_comment)).setBackgroundColor(a.data);
                    ((LinearLayout)view.findViewById(R.id.function_menu_itemtv_sts_background)).setBackgroundColor(a.data);
                    ((LinearLayout)view.findViewById(R.id.function_menu_itemtv_ets_background)).setBackgroundColor(a.data);
                }
                view.setOnClickListener(collapse);
                break;
            case 6:
                ((TextView)view.findViewById(R.id.teachers_evaluation_evaluation)).setText(groups.get(i).getValue().get(i1).get(0));
                view.setOnClickListener(view14 -> context.startActivity(new Intent(context, TeacherEvaluationPanel.class)));
                break;
            case 7:
                ((TextView)view.findViewById(R.id.cet_term)).setText(groups.get(i).getValue().get(i1).get(0));
                ((TextView)view.findViewById(R.id.cet_exam)).setText(groups.get(i).getValue().get(i1).get(1));
                ((TextView)view.findViewById(R.id.cet_score)).setText(groups.get(i).getValue().get(i1).get(2));
                ((TextView)view.findViewById(R.id.cet_percentile_score)).setText(groups.get(i).getValue().get(i1).get(3));
                ((TextView)view.findViewById(R.id.cet_card_id)).setText(groups.get(i).getValue().get(i1).get(4));
                view.setOnClickListener(collapse);
                break;
            case 8:
                ((TextView)view.findViewById(R.id.item_query_grade_points)).setText(groups.get(i).getValue().get(i1).get(0));
                view.setOnClickListener(view_item_query_grade_points -> TeacherEvaluationPanel.start(context, "毕业学位查询", "======= 查询结束 =======", R.menu.graduation_degree));
                break;
            case 9:
                ((TextView)view.findViewById(R.id.item_guet_tools)).setText(groups.get(i).getValue().get(i1).get(0));
                view.setOnClickListener(view_item_guet_tools -> context.startActivity(new Intent(context, Webinfo.class)));
                break;
            case 10:
                ((TextView)view.findViewById(R.id.update_update)).setText(groups.get(i).getValue().get(i1).get(0));
                break;
            case 11:
                ((TextView)view.findViewById(R.id.about_about)).setText(groups.get(i).getValue().get(i1).get(0));
                if (i1 == 0){
                    view.setOnClickListener(view19 -> context.startActivity(new Intent(context, UsageActivity.class)));
                }else if (i1 == 1){
                    view.setOnClickListener(view17 -> context.startActivity(new Intent(context, AboutActivity.class)));
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
        if (singleExpanded) {}
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
