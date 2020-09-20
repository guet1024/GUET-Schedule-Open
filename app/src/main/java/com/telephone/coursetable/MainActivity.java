package com.telephone.coursetable;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.Locate;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

public class MainActivity extends AppCompatActivity {

    private boolean term_week_is_changing = false;

    private CurrentWeek current_week = null;

    /** the timestamp of last pressing back */
    private long exit_ts = 0;

    private GoToClassDao gdao;
    private TermInfoDao tdao;
    private UserDao udao;
    private PersonInfoDao pdao;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private PickerPanel pickerPanel;

    private NumberPicker.OnScrollListener scroll = (numberPicker, i) -> {
        switch (i){
            case NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: default:
                term_week_is_changing = true;
                break;
            case NumberPicker.OnScrollListener.SCROLL_STATE_IDLE:
                term_week_is_changing = false;
                break;
        }
    };

    private String[] termValues = null;
    private String[] weekValues = null;

    private volatile boolean visible = true;
    private volatile Intent outdated = null;

    synchronized public boolean isVisible(){
        return visible;
    }

    synchronized public boolean setOutdated(){
        if (visible) return false;
        outdated = new Intent(this, MainActivity.class);
        return true;
    }

    synchronized public void hide(){
        visible = false;
    }

    synchronized public void show(){
        visible = true;
        if (outdated != null){
            startActivity(outdated);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    @Override
    protected void onPause() {
        hide();
        super.onPause();
    }
  
    private volatile View view;

    public View getView(){
        return view;
    }

    /**
     * @ui
     * @clear
     */
    @Override
    protected void onDestroy() {
        final String NAME = "onDestroy()";
        Log.e(NAME, "Main Activity on destroy = " + this.toString());
        Log.e(NAME, "cached Main Activity Pointer = " + MyApp.getRunning_main().toString());
        if (MyApp.getRunning_main().toString().equals(this.toString())) {
            Log.e(NAME, "remove cached Main Activity Pointer = " + MyApp.getRunning_main().toString());
            MyApp.setRunning_main(null);
        }
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    /**
     * @ui
     * @clear
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        MyApp.setRunning_main(this);
        MyApp.setRunning_activity(MyApp.RunningActivity.MAIN);
        MyApp.setRunning_activity_pointer(this);
        view = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_main, null, false);
        getSupportActionBar().hide();

        pickerPanel = new PickerPanel(
                (ImageView) MainActivity.this.view.findViewById(R.id.termPickerBackground),
                (NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker),
                (NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker),
                (TextView) MainActivity.this.view.findViewById(R.id.term_picker_text),
                (TextView) MainActivity.this.view.findViewById(R.id.week_picker_text),
                (FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton2)
        );
        pref = MyApp.getCurrentSharedPreference();
        editor = MyApp.getCurrentSharedPreferenceEditor();
        gdao = MyApp.getCurrentAppDB().goToClassDao();
        tdao = MyApp.getCurrentAppDB().termInfoDao();
        udao = MyApp.getCurrentAppDB().userDao();
        pdao = MyApp.getCurrentAppDB().personInfoDao();

        pickerPanel.hide(this);

        final boolean lockdown = MyApp.isRunning_login_thread() || MyApp.isRunning_fetch_service();
        if (lockdown) {
            ((TextView) MainActivity.this.view.findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.updating_user_title_suffix));
            ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setVisibility(View.INVISIBLE);
            ((ImageView) MainActivity.this.view.findViewById(R.id.imageView3)).setOnClickListener(null);
            for (int id : MyApp.timetvIds) {
                ((TextView) MainActivity.this.view.findViewById(id)).setOnClickListener(null);
            }
            ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
            setContentView(view);
        } else {
            new Thread(() -> {
                if (udao.getActivatedUser().isEmpty()) {
                    boolean islan = MyApp.isLAN();
                    runOnUiThread(() -> {
                        ((TextView) MainActivity.this.view.findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.no_user_title_suffix));
                        ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setVisibility(View.INVISIBLE);
                        ((ImageView) MainActivity.this.view.findViewById(R.id.imageView3)).setOnClickListener(MainActivity.this::Login);
                        for (int id : MyApp.timetvIds) {
                            ((TextView) MainActivity.this.view.findViewById(id)).setOnClickListener(null);
                        }
                        ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
                        Intent notificationIntent;
                        if (islan){
                            notificationIntent = new Intent(MainActivity.this, Login.class);
                        }else {
                            notificationIntent = new Intent(MainActivity.this, Login_vpn.class);
                        }
                        PendingIntent pendingIntent =
                                PendingIntent.getActivity(MainActivity.this, 0, notificationIntent, 0);
                        Notification notification =
                                new NotificationCompat.Builder(MainActivity.this, MyApp.notification_channel_id_normal)
                                        .setContentTitle("您还未登录")
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("点击登录 >>"))
                                        .setSmallIcon(R.drawable.feather_pen_trans)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true)
                                        .setTicker("您还未登录")
                                        .build();
                        NotificationManagerCompat.from(MainActivity.this).notify(MyApp.notification_id_click_to_login, notification);
                        ((TextView)MainActivity.this.view.findViewById(R.id.textView_title)).setOnClickListener(MainActivity.this::Login);
                        setContentView(view);
                    });
                } else {
                    User u = udao.getActivatedUser().get(0);
                    String name = pdao.selectAll().get(0).name;
                    Locate locate = Clock.locateNow(Clock.nowTimeStamp(), tdao, pref, MyApp.times,
                            DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                            getResources().getString(R.string.pref_hour_start_suffix),
                            getResources().getString(R.string.pref_hour_end_suffix),
                            getResources().getString(R.string.pref_hour_des_suffix)
                    );
                    List<TermInfo> terms = tdao.selectAll();
                    List<String> term_names = new LinkedList<>();
                    term_names.add(getResources().getString(R.string.term_vacation));
                    for (TermInfo term : terms) {
                        term_names.add(term.termname);
                    }
                    termValues = term_names.toArray(new String[0]);
                    runOnUiThread(()->{
                        ((TextView) MainActivity.this.view.findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title));
                        ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setVisibility(View.VISIBLE);
                        ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setText("  " + name + "\n" + "  " + u.updateTime + "同步");
                        ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setOnClickListener(MainActivity.this::openFunctionMenu);
                        ((ImageView) MainActivity.this.view.findViewById(R.id.imageView3)).setOnClickListener(MainActivity.this::Login);
                        for (int id : MyApp.timetvIds) {
                            ((TextView) MainActivity.this.view.findViewById(id)).setOnClickListener(MainActivity.this::setTime);
                        }
                        ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setVisibility(View.VISIBLE);
                        MainActivity.this.view.findViewById(R.id.main_drag_background).setOnDragListener(new View.OnDragListener() {
                            @Override
                            public boolean onDrag(View view, DragEvent dragEvent) {
                                int action = dragEvent.getAction();
                                switch (action) {
                                    case DragEvent.ACTION_DRAG_STARTED:
                                    case DragEvent.ACTION_DRAG_ENTERED:
                                    case DragEvent.ACTION_DRAG_EXITED:
                                    case DragEvent.ACTION_DROP:
                                        break;
                                    case DragEvent.ACTION_DRAG_ENDED:
                                        MainActivity.this.view.findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
                                        break;
                                    case DragEvent.ACTION_DRAG_LOCATION:
                                        float y = dragEvent.getY();
                                        float h = MainActivity.this.view.findViewById(R.id.floatingActionButton).getHeight();
                                        float margin = 10;
                                        Log.i("FAB", "drag y(relative) = " + y);
                                        if (y + h + margin > view.getHeight()){
                                            Log.i("FAB", "lower boundary");
                                            setCenterToAbsoluteYofScreen(getAbsoluteYofScreenFromDragBackgroundRelativeY(view.getHeight() - margin - h));
                                        }else if (y - h - margin < 0){
                                            Log.i("FAB", "upper boundary");
                                            setCenterToAbsoluteYofScreen(getAbsoluteYofScreenFromDragBackgroundRelativeY(0 + margin + h));
                                        }else {
                                            setCenterToAbsoluteYofScreen(getAbsoluteYofScreenFromDragBackgroundRelativeY(y));
                                        }
                                        break;
                                }
                                return true;
                            }
                        });
                        ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setOnLongClickListener(view -> {
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                            MainActivity.this.view.findViewById(R.id.floatingActionButton).setVisibility(View.INVISIBLE);
                            view.startDragAndDrop(null, shadowBuilder, null, 0);
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            return true;
                        });
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setWrapSelectorWheel(false);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setDisplayedValues(termValues);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setMinValue(0);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setMaxValue(termValues.length - 1);
                        weekValues = new String[]{
                                "0",
                                "1",
                                "2",
                                "3",
                                "4",
                                "5",
                                "6",
                                "7",
                                "8",
                                "9",
                                "10",
                                "11",
                                "12",
                                "13",
                                "14",
                                "15",
                                "16",
                                "17",
                                "18",
                                "19",
                                "20",
                                "21",
                                "22",
                                "23",
                                "24"
                        };
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setDisplayedValues(weekValues);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setMinValue(0);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setMaxValue(weekValues.length - 1);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setOnScrollListener(scroll);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setOnScrollListener(scroll);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                        current_week = new CurrentWeek(MainActivity.this);
                        ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setOnValueChangedListener((numberPicker, oldValue, newValue) -> current_week.setValue(newValue));
                        if (locate.term != null) {
                            ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(locate.term.termname));
                            ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setValue(Math.toIntExact(locate.week));
                            current_week.setValue(Math.toIntExact(locate.week));
                        } else {
                            ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(getResources().getString(R.string.term_vacation)));
                            ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setValue(0);
                            current_week.setValue(0);
                        }
                        showTable(locate);
                        setContentView(view);
                    });
                }
            }).start();
        }
    }

    private float getAbsoluteYofScreenFromDragBackgroundRelativeY(float y){
        View view = MainActivity.this.view.findViewById(R.id.main_drag_background);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return y + location[1];
    }

    private void setCenterToAbsoluteYofScreen(float y){
        View view = MainActivity.this.view.findViewById(R.id.floatingActionButton);
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        float now_top_distance = l[1];
        float height = view.getHeight();
        float need_top_distance = y - height/2;
        float more_top_distance = need_top_distance - now_top_distance;
        view.setTop(view.getTop() + (int)more_top_distance);
        view.setBottom(view.getBottom() + (int)more_top_distance);
    }

    /**
     * @ui
     * @clear
     */
    @Override
    public void onBackPressed() {
        final String NAME = "onBackPressed()";
        if (pickerPanel.isShown()){
            if (term_week_is_changing) return;
            String selected_term_name = termValues[((NumberPicker)MainActivity.this.view.findViewById(R.id.termPicker)).getValue()];
            long selected_week = ((NumberPicker)MainActivity.this.view.findViewById(R.id.weekPicker)).getValue();
            new Thread(() -> {
                Locate locate = Clock.locateNow(Clock.nowTimeStamp(), tdao, pref, MyApp.times,
                        DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                        getResources().getString(R.string.pref_hour_start_suffix),
                        getResources().getString(R.string.pref_hour_end_suffix),
                        getResources().getString(R.string.pref_hour_des_suffix));
                List<TermInfo> term_list = tdao.getTermByTermName(selected_term_name);
                if (!term_list.isEmpty()) {
                    locate.term = term_list.get(0);
                    locate.week = selected_week;
                }else {
                    locate.term = null;
                    locate.week = Clock.NO_TERM;
                }
                runOnUiThread(()-> showTable(locate));
            }).start();
        }else {
            long nts = Clock.nowTimeStamp();
            Log.e(NAME,"MainActivity press back" + ": " + nts);
            if (nts - exit_ts < 2000) {
                new Thread(()->{
                    finishAffinity();
                    finishAndRemoveTask();
                }).start();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exit_ts = nts;
            }
        }
    }

    /**
     * @ui
     * @clear
     */
    public void openFunctionMenu(View view){
        startActivity(new Intent(this, FunctionMenu.class));
    }

    /**
     * @ui/non-ui
     * @clear
     */
    public void refresh(){
        runOnUiThread(()->{
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        });
    }

    /**
     * @ui
     * jump to Login Activity
     * @clear
     */
    public void Login(View view){
        NotificationManagerCompat.from(MainActivity.this).cancel(MyApp.notification_id_click_to_login);
        new Thread(() -> {
            if (MyApp.isLAN()){
                runOnUiThread(() -> {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                });
            }else {
                runOnUiThread(() -> {
                    Intent intent = new Intent(MainActivity.this, Login_vpn.class);
                    startActivity(intent);
                });
            }
        }).start();
    }

    /**
     * @ui
     * @clear
     */
    public void setTime(View view){
        Intent intent = new Intent(this, ChangeHours.class);
        startActivity(intent);
    }

    /**
     * @ui
     * @clear
     */
    public void returnToday(View view){
        new Thread(()->{
            Locate locate = Clock.locateNow(Clock.nowTimeStamp(), tdao, pref, MyApp.times,
                    DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                    getResources().getString(R.string.pref_hour_start_suffix),
                    getResources().getString(R.string.pref_hour_end_suffix),
                    getResources().getString(R.string.pref_hour_des_suffix));
            runOnUiThread(()->{
                if (locate.term != null) {
                    ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(locate.term.termname));
                    ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setValue(Math.toIntExact(locate.week));
                    current_week.setValue(Math.toIntExact(locate.week));
                } else {
                    ((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(getResources().getString(R.string.term_vacation)));
                    ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).setValue(0);
                    current_week.setValue(0);
                }
            });
        }).start();
    }

    /**
     * @ui
     * 1. clear old table, date, high-light
     * 2. show table
     * 3. show date
     * 4. show high-light
     * 5. hide {@link #pickerPanel}
     * @clear
     */
    private void showTable(@NonNull Locate locate){
        new Thread(()->{
            List<ShowTableNode> nodes = null;
            if (locate.term != null){
                nodes = gdao.getSpecifiedWeekTable(locate.term.term, locate.week);
            }
            List<String> texts = new LinkedList<>();
            for (int time_index = 0; time_index < MyApp.nodeIds.length; time_index++){
                for (int weekday = 1; weekday <= MyApp.weekdaytvIds.length; weekday++){
                    StringBuilder text = new StringBuilder();
                    if (nodes != null){
                        String time = MyApp.times[time_index];
                        long weekday_f = weekday;
                        List<ShowTableNode> my_node_list = nodes.stream()
                                .filter(showTableNode -> showTableNode.weekday == weekday_f && showTableNode.time.equals(time))
                                .collect(Collectors.toList());
                        for (int i = 0; i < my_node_list.size(); i++){
                            ShowTableNode my_node = my_node_list.get(i);
                            if (my_node_list.size() > 1){
                                if (i > 0){
                                    text.append("▬▬▬▬\n");
                                }
                                if (my_node.cname != null) {
                                    String my_cname = my_node.cname;
                                    if (my_cname.length() > 6){
                                        my_cname = my_cname.substring(0, 6) + "...";
                                    }
                                    text.append(my_cname).append("\n");
                                }else {
                                    text.append(" ").append("\n");
                                }
                                if (my_node.croomno != null) {
                                    text.append("(").append(my_node.croomno).append(")").append("\n");
                                }else {
                                    text.append("()").append("\n");
                                }
                            }else {
                                if (my_node.courseno != null) {
                                    text.append("[").append(my_node.courseno).append("]").append("\n");
                                }else {
                                    text.append("[]").append("\n");
                                }
                                if (my_node.cname != null) {
                                    text.append(my_node.cname).append("\n");
                                }else {
                                    text.append(" ").append("\n");
                                }
                                if (my_node.name != null) {
                                    text.append("@").append(my_node.name).append("\n");
                                }else {
                                    text.append("@").append("\n");
                                }
                                if (my_node.croomno != null) {
                                    text.append("(").append(my_node.croomno).append(")").append("\n");
                                }else {
                                    text.append("()").append("\n");
                                }
                            }
                        }
                    }
                    texts.add(text.toString());
                }
            }
            runOnUiThread(()-> {
                for (int id : MyApp.weekdaytvIds) {
                    ((TextView) MainActivity.this.view.findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorWeekdayAndTimeBackground, getTheme()));
                    ((TextView) MainActivity.this.view.findViewById(id)).setTextColor(((TextView) MainActivity.this.view.findViewById(R.id.term_picker_text)).getCurrentTextColor());
                }
                for (int id : MyApp.timetvIds) {
                    ((TextView) MainActivity.this.view.findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorWeekdayAndTimeBackground, getTheme()));
                    ((TextView) MainActivity.this.view.findViewById(id)).setTextColor(((TextView) MainActivity.this.view.findViewById(R.id.term_picker_text)).getCurrentTextColor());
                }
                for (int[] id_list : MyApp.nodeIds) {
                    for (int id : id_list) {
                        ((TextView) MainActivity.this.view.findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorTableNodeBackground, getTheme()));
                        ((TextView) MainActivity.this.view.findViewById(id)).setTextColor(((TextView) MainActivity.this.view.findViewById(R.id.term_picker_text)).getCurrentTextColor());
                    }
                }
                for (int row_index = 0; row_index < MyApp.nodeIds.length; row_index++) {
                    for (int column_index = 0; column_index < MyApp.weekdaytvIds.length; column_index++) {
                        String node_text = texts.get(row_index * MyApp.weekdaytvIds.length + column_index);
                        ((TextView)MainActivity.this.view.findViewById(MyApp.nodeIds[row_index][column_index])).setText(node_text);
                    }
                }
                ((TextView)MainActivity.this.view.findViewById(R.id.textView_date)).setText(locate.month + "/" + locate.day);
                int time_tv_ids_index = -1;
                if (locate.time != null) {
                    time_tv_ids_index = Arrays.asList(MyApp.times).indexOf(locate.time);
                }
                int weekday_tv_ids_index = -1;
                weekday_tv_ids_index = (int) (locate.weekday - 1);
                if (time_tv_ids_index != -1){
                    ((TextView)MainActivity.this.view.findViewById(MyApp.timetvIds[time_tv_ids_index])).setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                    ((TextView)MainActivity.this.view.findViewById(MyApp.timetvIds[time_tv_ids_index])).setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                }
                if (weekday_tv_ids_index != -1){
                    ((TextView)MainActivity.this.view.findViewById(MyApp.weekdaytvIds[weekday_tv_ids_index])).setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                    ((TextView)MainActivity.this.view.findViewById(MyApp.weekdaytvIds[weekday_tv_ids_index])).setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                }
                if (time_tv_ids_index != -1 && weekday_tv_ids_index != -1){
                    TextView node_tv = (TextView)MainActivity.this.view.findViewById(MyApp.nodeIds[time_tv_ids_index][weekday_tv_ids_index]);
                    if (!node_tv.getText().toString().isEmpty()){
                        node_tv.setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                    }
                }
                pickerPanel.hide(MainActivity.this);
            });
        }).start();
    }

    /**
     * @ui
     * @clear
     */
    public void openOrHidePanel(View view){
        if (pickerPanel.isShown()){
            if (term_week_is_changing) return;
            String selected_term_name = termValues[((NumberPicker)MainActivity.this.view.findViewById(R.id.termPicker)).getValue()];
            long selected_week = ((NumberPicker)MainActivity.this.view.findViewById(R.id.weekPicker)).getValue();
            new Thread(() -> {
                Locate locate = Clock.locateNow(Clock.nowTimeStamp(), tdao, pref, MyApp.times,
                        DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                        getResources().getString(R.string.pref_hour_start_suffix),
                        getResources().getString(R.string.pref_hour_end_suffix),
                        getResources().getString(R.string.pref_hour_des_suffix));
                List<TermInfo> term_list = tdao.getTermByTermName(selected_term_name);
                if (!term_list.isEmpty()) {
                    locate.term = term_list.get(0);
                    locate.week = selected_week;
                }else {
                    locate.term = null;
                    locate.week = Clock.NO_TERM;
                }
                runOnUiThread(()-> showTable(locate));
            }).start();
        }else{
            pickerPanel.show(MainActivity.this);
        }
    }
}