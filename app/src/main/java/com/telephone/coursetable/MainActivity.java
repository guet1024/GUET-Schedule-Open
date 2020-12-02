package com.telephone.coursetable;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.Locate;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.Privacy;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Gson.CourseCard.ACard;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private long resume_time = 0;

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
        }else {
            new Thread(()-> {
                String username = null;
                if (udao != null) {
                    List<User> ac_users = udao.getActivatedUser();
                    if (!ac_users.isEmpty()) {
                        username = ac_users.get(0).username;
                    }
                }
                if (resume_time > 1 && username != null) {
                    String username_f = username;
                    returnToday(null);
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        runOnUiThread(() -> {
                            String selected_term_name = termValues[((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).getValue()];
                            long selected_week = ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).getValue();
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
                                } else {
                                    locate.term = null;
                                    locate.week = Clock.NO_TERM;
                                }
                                Map.Entry<Integer, Integer> g = getTime_enhanced();
                                runOnUiThread(() -> showTable(username_f, locate, g));
                            }).start();
                        });
                    }).start();
                }
            }).start();
        }
    }

    @Override
    protected void onResume() {
        final String NAME = "onResume()";
        super.onResume();
        resume_time++;
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "resume time = " + resume_time);
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
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "Main Activity on destroy = " + this.toString());
        com.telephone.coursetable.LogMe.LogMe.e(NAME, "cached Main Activity Pointer = " + MyApp.getRunning_main().toString());
        if (MyApp.getRunning_main().toString().equals(this.toString())) {
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "remove cached Main Activity Pointer = " + MyApp.getRunning_main().toString());
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
                (FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton2),
                (SwipeRefreshLayout) MainActivity.this.view.findViewById(R.id.main_pull_refresh)
        );
        pref = MyApp.getCurrentSharedPreference();
        editor = MyApp.getCurrentSharedPreferenceEditor();
        gdao = MyApp.getCurrentAppDB().goToClassDao();
        tdao = MyApp.getCurrentAppDB().termInfoDao();
        udao = MyApp.getCurrentAppDB().userDao();
        pdao = MyApp.getCurrentAppDB().personInfoDao();

        pickerPanel.hide(this);
        ((SwipeRefreshLayout) MainActivity.this.view.findViewById(R.id.main_pull_refresh)).setColorSchemeResources(
                R.color.colorRefresh1,
                R.color.colorRefresh2,
                R.color.colorRefresh3,
                R.color.colorRefresh4,
                R.color.colorRefresh5
                );
        ((SwipeRefreshLayout) MainActivity.this.view.findViewById(R.id.main_pull_refresh)).setOnRefreshListener(() -> {
            ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setEnabled(false);
            returnToday(null);
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                String username = null;
                if (!udao.getActivatedUser().isEmpty()) {
                    username = udao.getActivatedUser().get(0).username;
                    String username_f = username;
                    runOnUiThread(() -> {
                        String selected_term_name = termValues[((NumberPicker) MainActivity.this.view.findViewById(R.id.termPicker)).getValue()];
                        long selected_week = ((NumberPicker) MainActivity.this.view.findViewById(R.id.weekPicker)).getValue();
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
                            } else {
                                locate.term = null;
                                locate.week = Clock.NO_TERM;
                            }
                            Map.Entry<Integer, Integer> g = getTime_enhanced();
                            runOnUiThread(() -> {
                                showTable(username_f, locate, g);
                                com.telephone.coursetable.LogMe.LogMe.e("main-pull-refresh", "success!");
                                ((SwipeRefreshLayout) MainActivity.this.view.findViewById(R.id.main_pull_refresh)).setRefreshing(false);
                                ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setEnabled(true);
                            });
                        }).start();
                    });
                }
            }).start();
        });
        ((SwipeRefreshLayout) MainActivity.this.view.findViewById(R.id.main_pull_refresh)).setEnabled(false);

        final boolean lockdown = MyApp.isRunning_login_thread() || MyApp.isRunning_fetch_service();
        if (lockdown) {
            ((TextView) MainActivity.this.view.findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.updating_user_title_suffix));
            ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setVisibility(View.INVISIBLE);
            ((TextView) MainActivity.this.view.findViewById(R.id.main_into_more_text_view)).setVisibility(View.INVISIBLE);
            ((ImageView) MainActivity.this.view.findViewById(R.id.imageView3)).setOnClickListener(null);
            for (int id : MyApp.timetvIds) {
                ((TextView) MainActivity.this.view.findViewById(id)).setOnClickListener(null);
            }
            ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
            setContentView(view);
        } else {
            new Thread(()->{
                if (MyApp.getCurrentAppDB().privacyDao().selectPrivacy(getString(R.string.privacy_version)).isEmpty()){
                    runOnUiThread(()->{
                        AlertDialog dialog = Login.getAlertDialog(
                                MainActivity.this,
                                null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(()->MyApp.getCurrentAppDB().privacyDao().insert(new Privacy(getString(R.string.privacy_version)))).start();
                                        init_thread();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FetchService.startAction_STOP_SERVICE(MainActivity.this);
                                        System.exit(0);
                                    }
                                },
                                getLayoutInflater().inflate(R.layout.privacy_dialog, null),
                                getString(R.string.privacy_title_no_line),
                                "我已阅读且同意",
                                "我不同意，退出"
                        );
                        dialog.setCancelable(false);
                        dialog.show();
                    });
                }else {
                    init_thread();
                }
            }).start();
        }
    }

    private void init_thread(){
        new Thread(() -> {
            if (udao.getActivatedUser().isEmpty()) {
                boolean islan = MyApp.isLAN();
                runOnUiThread(() -> {
                    ((TextView) MainActivity.this.view.findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.no_user_title_suffix));
                    ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setVisibility(View.INVISIBLE);
                    ((TextView) MainActivity.this.view.findViewById(R.id.main_into_more_text_view)).setVisibility(View.INVISIBLE);
                    ((ImageView) MainActivity.this.view.findViewById(R.id.imageView3)).setOnClickListener(MainActivity.this::Login);
                    for (int id : MyApp.timetvIds) {
                        ((TextView) MainActivity.this.view.findViewById(id)).setOnClickListener(null);
                    }
                    ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
                    ((TextView) MainActivity.this.view.findViewById(R.id.textView_title)).setOnClickListener(MainActivity.this::Login);
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
                runOnUiThread(() -> {
                    ((TextView) MainActivity.this.view.findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title));
                    ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setVisibility(View.VISIBLE);
                    ((TextView) MainActivity.this.view.findViewById(R.id.main_into_more_text_view)).setVisibility(View.VISIBLE);
                    ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setText("  " + name + "\n" + "  " + u.updateTime + "同步");
                    ((TextView) MainActivity.this.view.findViewById(R.id.textView_update_time)).setOnClickListener(MainActivity.this::openFunctionMenu);
                    ((ImageView) MainActivity.this.view.findViewById(R.id.imageView3)).setOnClickListener(MainActivity.this::Login);
                    for (int id : MyApp.timetvIds) {
                        ((TextView) MainActivity.this.view.findViewById(id)).setOnClickListener(MainActivity.this::setTime);
                    }
                    ((SwipeRefreshLayout) MainActivity.this.view.findViewById(R.id.main_pull_refresh)).setEnabled(true);
                    ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setVisibility(View.VISIBLE);
                    ((FloatingActionButton) MainActivity.this.view.findViewById(R.id.floatingActionButton)).setTag(u.username);
                    MainActivity.this.view.findViewById(R.id.main_drag_background).setOnDragListener(new View.OnDragListener() {
                        @Override
                        public boolean onDrag(View view, DragEvent dragEvent) {
                            int action = dragEvent.getAction();
                            switch (action) {
                                case DragEvent.ACTION_DRAG_STARTED:
                                case DragEvent.ACTION_DRAG_ENTERED:
                                case DragEvent.ACTION_DROP:
                                    break;
                                case DragEvent.ACTION_DRAG_EXITED:
                                    view.updateDragShadow(new View.DragShadowBuilder());
                                    break;
                                case DragEvent.ACTION_DRAG_ENDED:
                                    MainActivity.this.view.findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
                                    break;
                                case DragEvent.ACTION_DRAG_LOCATION:
                                    float y = dragEvent.getY();
                                    float h = MainActivity.this.view.findViewById(R.id.floatingActionButton).getHeight();
                                    float margin = 10;
                                    com.telephone.coursetable.LogMe.LogMe.i("FAB", "drag y(relative) = " + y);
                                    if (y + h + margin > view.getHeight()) {
                                        com.telephone.coursetable.LogMe.LogMe.i("FAB", "lower boundary");
                                        setCenterToAbsoluteYofScreen(getAbsoluteYofScreenFromDragBackgroundRelativeY(view.getHeight() - margin - h));
                                    } else if (y - h - margin < 0) {
                                        com.telephone.coursetable.LogMe.LogMe.i("FAB", "upper boundary");
                                        setCenterToAbsoluteYofScreen(getAbsoluteYofScreenFromDragBackgroundRelativeY(0 + margin + h));
                                    } else {
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
                        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
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
                    new Thread(() -> {
                        Map.Entry<Integer, Integer> g = getTime_enhanced();
                        runOnUiThread(() -> {
                            showTable(u.username, locate, g);
                            setContentView(view);
                        });
                    }).start();
                });
            }
        }).start();
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
                Map.Entry<Integer, Integer> g = getTime_enhanced();
                String username = udao.getActivatedUser().get(0).username;
                runOnUiThread(()-> showTable(username, locate, g));
            }).start();
        }else {
            long nts = Clock.nowTimeStamp();
            com.telephone.coursetable.LogMe.LogMe.e(NAME,"MainActivity press back" + ": " + nts);
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

    public void Card(View view){

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
     * jump to FunctionMenu Activity
     * @clear
     */
    public void More(View view){
        startActivity(new Intent(this, FunctionMenu.class));
    }

    /**
     * open test activity
     * @clear
     */
    public void Test(View view){
        if (MyApp.isDebug())
            startActivity(new Intent(this, TestActivity.class));
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
            runOnUiThread(()-> {
                clearRedPoint(MainActivity.this,
                        (FrameLayout) findViewById(R.id.main_into_more_text_view_frame)
                );
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
    private void showTable(@NonNull String username, @NonNull Locate locate, @Nullable Map.Entry<Integer, Integer> breakTime) {
        final int black = 0xFF000000;
        final int white = 0xFFFFFFFF;
        final int gray = 0xFF505050;
        final int red = 0xFFF99393;
        final int red_black = 0xFF260101;
        final int green = 0xffCDE8AF;
        final int green_black = 0xFF435232;
        final int blue = 0xffBEEAFD;
        final int blue_black = 0xFF2A5263;
        final int pink = 0xffFDBED2;
        final int pink_black = 0xFF4B2E37;
        final int orange = 0xffFDE4BE;
        final int orange_black = 0xFF524532;
        final int purple = 0xffD3BEFD;
        final int purple_black = 0xFF372D4C;
        final int light_blue = 0xffBEFFFD;
        final int light_blue_black = 0xFF2D4C4B;
        final int light_yellow = 0xffF2FFBE;
        final int light_yellow_black = 0xFF4C5231;
        final int light_green = 0xffBEFFBF;
        final int light_green_black = 0xFF355935;
        final int transparent = 0x00000000;
        final int default_node_b_color = transparent;
        final int default_node_f_color = black;
        new Thread(() -> {
            List<ShowTableNode> nodes = null;
            String my_month = null;
            String my_day = null;
            if (locate.term != null) {
                nodes = gdao.getSpecifiedWeekTable(username, locate.term.term, locate.week);
                long that_ts = Clock.getTimeStampForWeekAndWeekdaySince(locate.term.sts, locate.week, locate.weekday);
                if (that_ts > 0) {
                    Date date = new Date(that_ts);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    my_month = (calendar.get(Calendar.MONTH) + 1) + "";
                    my_day = calendar.get(Calendar.DAY_OF_MONTH) + "";
                }
            }
            String my_month_f = my_month;
            String my_day_f = my_day;
            List<Map.Entry<String, Map.Entry<Integer, Integer>>> texts = new LinkedList<>();
            List<CourseCardData> courseCardDataList = new LinkedList<>();
            Map<String, Integer> colorMap = new HashMap<>();
            int[][] colors = new int[][]{
                    {green, green_black}, //green
                    {blue, blue_black}, //blue
                    {pink, pink_black}, //pink
                    {orange, orange_black}, //orange
                    {purple, purple_black}, //purple
                    {light_blue, light_blue_black}, //light blue
                    {light_yellow, light_yellow_black}, //light yellow
                    {light_green, light_green_black} //light green
            };
            int color_index = 0;
            for (int time_index = 0; time_index < MyApp.nodeIds.length; time_index++) {
                for (int weekday = 1; weekday <= MyApp.weekdaytvIds.length; weekday++) {
                    StringBuilder text = new StringBuilder();
                    int color = default_node_b_color;
                    int text_color = default_node_f_color;
                    final int conflict_color = red;
                    final int conflict_text_color = red_black;
                    final int null_color = default_node_b_color;
                    final int null_text_color = default_node_f_color;
                    CourseCardData data = null;
                    if (nodes != null) { // not vacation
                        String time = MyApp.times[time_index];
                        long weekday_f = weekday;
                        data = new CourseCardData(locate.term.termname, (int) locate.week, weekday,
                                Clock.getTimeDesUsingDefaultConfig(MainActivity.this, time),
                                new LinkedList<>(), locate.term.term, time);
                        List<ShowTableNode> my_node_list = nodes.stream()
                                .filter(showTableNode -> showTableNode.weekday == weekday_f && showTableNode.time.equals(time))
                                .collect(Collectors.toList());
                        for (int i = 0; i < my_node_list.size(); i++) {
                            ShowTableNode my_node = my_node_list.get(i);
                            String place = "";
                            String cname = "";
                            String t_name = "";
                            String cname_long = "";
                            String cno = "";
                            if (my_node_list.size() > 1) {

                                if (i > 0) {
                                    text.append("▶");
                                }

                                if (my_node.croomno != null) {
                                    place = my_node.croomno;
                                }
                                if (my_node.cname != null) {
                                    cname = my_node.cname;
                                    cname_long = my_node.cname;
                                }
                                if (cname.length() > 6) {
                                    cname = cname.substring(0, 5) + "...";
                                }
                                if (my_node.name != null) {
                                    t_name = my_node.name;
                                }
                                if (my_node.courseno != null) {
                                    cno = my_node.courseno;
                                }

                                String words = place + "#" + cname + "@" + t_name;
                                if (words.length() > 18) {
                                    words = words.substring(0, 17) + "...";
                                }

                                text.append(words);
                                color = conflict_color;
                                text_color = conflict_text_color;

                            } else {

                                if (my_node.croomno != null) {
                                    place = my_node.croomno;
                                }
                                if (my_node.cname != null) {
                                    cname = my_node.cname;
                                    cname_long = my_node.cname;
                                }
                                if (cname.length() > 8) {
                                    cname = cname.substring(0, 7) + "...";
                                }
                                if (my_node.name != null) {
                                    t_name = my_node.name;
                                }
                                if (my_node.courseno != null) {
                                    cno = my_node.courseno;
                                }

                                String words = place + "#" + cname + "@" + t_name;

                                text.append(words);
                                if (cno.isEmpty()) {
                                    color = null_color;
                                    text_color = null_text_color;
                                } else if (colorMap.containsKey(cno)) {
                                    int p = colorMap.get(cno);
                                    color = colors[p][0];
                                    text_color = colors[p][1];
                                } else {
                                    color = colors[color_index][0];
                                    text_color = colors[color_index][1];
                                    colorMap.put(cno, color_index);
                                    color_index++;
                                    if (color_index >= colors.length) {
                                        color_index = 0;
                                    }
                                }

                            }
                            String tno = "";
                            if (my_node.tno != null) {
                                tno = my_node.tno;
                            }
                            String sys_comm = "";
                            if (my_node.sys_comm != null) {
                                sys_comm = my_node.sys_comm;
                            }
                            String my_comm = "";
                            if (my_node.my_comm != null) {
                                my_comm = my_node.my_comm;
                            }
                            String ctype = "";
                            if (my_node.ctype != null) {
                                ctype = my_node.ctype;
                            }
                            String examt = "";
                            if (my_node.examt != null) {
                                examt = my_node.examt;
                            }
                            boolean customized = my_node.customized;
                            data.getCards().add(new ACard(
                                    cno, cname_long, (int) my_node.start_week, (int) my_node.end_week, t_name,
                                    tno, place, my_node.grade_point, ctype, examt, sys_comm, my_comm, my_node.oddweek, customized
                            ));
                        }
                    }
                    texts.add(Map.entry(text.toString(), Map.entry(color, text_color)));
                    courseCardDataList.add(data);
                }
            }
            runOnUiThread(() -> {
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
                        ((TextView) MainActivity.this.view.findViewById(id)).setBackgroundColor(default_node_b_color);
                        ((TextView) MainActivity.this.view.findViewById(id)).setTextColor(default_node_f_color);
                        ((TextView) MainActivity.this.view.findViewById(id)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        ((TextView) MainActivity.this.view.findViewById(id)).setTag(null);
                        ((TextView) MainActivity.this.view.findViewById(id)).setOnClickListener(null);
                    }
                }
                for (int id : MyApp.restLineIds) {
                    MainActivity.this.view.findViewById(id).setVisibility(View.INVISIBLE);
                }
                for (int row_index = 0; row_index < MyApp.nodeIds.length; row_index++) {
                    for (int column_index = 0; column_index < MyApp.weekdaytvIds.length; column_index++) {
                        Map.Entry<String, Map.Entry<Integer, Integer>> entry = texts.get(row_index * MyApp.weekdaytvIds.length + column_index);
                        CourseCardData cdd = courseCardDataList.get(row_index * MyApp.weekdaytvIds.length + column_index);
                        String node_text = entry.getKey();
                        int node_b_color = entry.getValue().getKey();
                        int node_f_color = entry.getValue().getValue();
                        ((TextView) MainActivity.this.view.findViewById(MyApp.nodeIds[row_index][column_index])).setText(node_text);
                        ((TextView) MainActivity.this.view.findViewById(MyApp.nodeIds[row_index][column_index])).setBackgroundColor(node_b_color);
                        ((TextView) MainActivity.this.view.findViewById(MyApp.nodeIds[row_index][column_index])).setTextColor(node_f_color);
                        if (cdd != null) {
                            ((TextView) MainActivity.this.view.findViewById(MyApp.nodeIds[row_index][column_index])).setTag(cdd);
                            ((TextView) MainActivity.this.view.findViewById(MyApp.nodeIds[row_index][column_index])).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CourseCardData courseCardData = (CourseCardData) v.getTag();
                                    CourseCard.startMe(MainActivity.this, courseCardData);
                                }
                            });
                        }
                    }
                }
                ((TextView) MainActivity.this.view.findViewById(R.id.textView_date)).setText(
                        ((my_month_f == null) ? (locate.month + "") : (my_month_f)) +
                                "/" +
                                ((my_day_f == null) ? (locate.day + "") : (my_day_f))
                );
                int time_tv_ids_index = -1;
                if (locate.time != null) {
                    time_tv_ids_index = Arrays.asList(MyApp.times).indexOf(locate.time);
                }
                int weekday_tv_ids_index = -1;
                weekday_tv_ids_index = (int) (locate.weekday - 1);
                if (time_tv_ids_index != -1) {
                    ((TextView) MainActivity.this.view.findViewById(MyApp.timetvIds[time_tv_ids_index])).setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                    ((TextView) MainActivity.this.view.findViewById(MyApp.timetvIds[time_tv_ids_index])).setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                }
                if (weekday_tv_ids_index != -1) {
                    ((TextView) MainActivity.this.view.findViewById(MyApp.weekdaytvIds[weekday_tv_ids_index])).setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                    ((TextView) MainActivity.this.view.findViewById(MyApp.weekdaytvIds[weekday_tv_ids_index])).setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                }
                if (time_tv_ids_index != -1 && weekday_tv_ids_index != -1) {
                    TextView node_tv = (TextView) MainActivity.this.view.findViewById(MyApp.nodeIds[time_tv_ids_index][weekday_tv_ids_index]);
                    if (!node_tv.getText().toString().isEmpty()) {
                        node_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    }
                }
                if (breakTime != null) {
                    Map.Entry<Integer, Integer> mbreakTime = Map.entry(breakTime.getKey() + 1, breakTime.getValue() + 1);
                    if (!mbreakTime.getKey().equals(mbreakTime.getValue())) {
                        int index = mbreakTime.getKey();
                        if (index < MyApp.restLineIds.length) {
                            int rest_line_id = MyApp.restLineIds[mbreakTime.getKey()];
                            MainActivity.this.view.findViewById(rest_line_id).setVisibility(View.VISIBLE);
                        }
                    }
                }
                addRedPoint(
                        MainActivity.this,
                        (FrameLayout)findViewById(R.id.main_into_more_text_view_frame),
                        findViewById(R.id.main_into_more_text_view)
                );
                pickerPanel.hide(MainActivity.this);
            });
        }).start();
    }

    /**
     * @ui
     * @clear
     */
    public void openOrHidePanel(View view){
        String username = view.getTag().toString();
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
                Map.Entry<Integer, Integer> g = getTime_enhanced();
                runOnUiThread(()-> showTable(username, locate, g));
            }).start();
        }else{
            pickerPanel.show(MainActivity.this);
        }
    }

    /**
     * @non-ui
     * @clear
     */
    private Map.Entry<Integer, Integer> getTime_enhanced(){
        return Clock.findNowTime(Clock.nowTimeStamp(),
                MyApp.getCurrentSharedPreference(),
                DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                getResources().getString(R.string.pref_hour_start_suffix),
                getResources().getString(R.string.pref_hour_end_suffix));
    }

    public static void addRedPoint(@NonNull AppCompatActivity c, @NonNull FrameLayout frameLayout, @NonNull View anchor){
        c.runOnUiThread(()->{
            BadgeDrawable badgeDrawable = BadgeDrawable.create(c);
            badgeDrawable.setBackgroundColor(c.getColor(R.color.colorRedPoint));

            frameLayout.setForeground(badgeDrawable);
            frameLayout.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                    badgeDrawable.updateBadgeCoordinates(anchor, frameLayout)
            );
        });
    }

    public static void clearRedPoint(@NonNull AppCompatActivity c, @NonNull FrameLayout frameLayout){
        c.runOnUiThread(()->{
            frameLayout.setForeground(null);
        });
    }
}
