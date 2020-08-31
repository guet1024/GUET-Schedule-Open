package com.telephone.coursetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

    /**
     * @ui
     * @clear
     */
    @Override
    protected void onDestroy() {
        MyApp.running_main = null;
        super.onDestroy();
    }

    /**
     * @ui
     * @clear
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.running_main = this;
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        pickerPanel = new PickerPanel(
                (ImageView) findViewById(R.id.termPickerBackground),
                (NumberPicker) findViewById(R.id.termPicker),
                (NumberPicker) findViewById(R.id.weekPicker),
                (TextView) findViewById(R.id.term_picker_text),
                (TextView) findViewById(R.id.week_picker_text),
                (FloatingActionButton) findViewById(R.id.floatingActionButton2)
        );
        pref = MyApp.getCurrentSharedPreference();
        editor = MyApp.getCurrentSharedPreferenceEditor();
        gdao = MyApp.getCurrentAppDB().goToClassDao();
        tdao = MyApp.getCurrentAppDB().termInfoDao();
        udao = MyApp.getCurrentAppDB().userDao();
        pdao = MyApp.getCurrentAppDB().personInfoDao();

        pickerPanel.hide(this);

        final boolean lockdown = MyApp.running_login_thread || MyApp.running_fetch_service;
        if (lockdown) {
            ((TextView) findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.updating_user_title_suffix));
            ((TextView) findViewById(R.id.textView_update_time)).setVisibility(View.INVISIBLE);
            ((ImageView) findViewById(R.id.imageView3)).setOnClickListener(null);
            for (int id : MyApp.timetvIds) {
                ((TextView) findViewById(id)).setOnClickListener(null);
            }
            ((FloatingActionButton) findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
        } else {
            new Thread(() -> {
                if (udao.getActivatedUser().isEmpty()) {
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.no_user_title_suffix));
                        ((TextView) findViewById(R.id.textView_update_time)).setVisibility(View.INVISIBLE);
                        ((ImageView) findViewById(R.id.imageView3)).setOnClickListener(MainActivity.this::Login);
                        for (int id : MyApp.timetvIds) {
                            ((TextView) findViewById(id)).setOnClickListener(null);
                        }
                        ((FloatingActionButton) findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
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
                        ((TextView) findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title));
                        ((TextView) findViewById(R.id.textView_update_time)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.textView_update_time)).setText("  " + name + "\n" + "  " + u.updateTime + "同步");
                        ((TextView) findViewById(R.id.textView_update_time)).setOnClickListener(MainActivity.this::openFunctionMenu);
                        ((ImageView) findViewById(R.id.imageView3)).setOnClickListener(MainActivity.this::Login);
                        for (int id : MyApp.timetvIds) {
                            ((TextView) findViewById(id)).setOnClickListener(MainActivity.this::setTime);
                        }
                        ((FloatingActionButton) findViewById(R.id.floatingActionButton)).setVisibility(View.VISIBLE);
                        ((NumberPicker) findViewById(R.id.termPicker)).setWrapSelectorWheel(false);
                        ((NumberPicker) findViewById(R.id.termPicker)).setDisplayedValues(termValues);
                        ((NumberPicker) findViewById(R.id.termPicker)).setMinValue(0);
                        ((NumberPicker) findViewById(R.id.termPicker)).setMaxValue(termValues.length - 1);
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
                        ((NumberPicker) findViewById(R.id.weekPicker)).setDisplayedValues(weekValues);
                        ((NumberPicker) findViewById(R.id.weekPicker)).setMinValue(0);
                        ((NumberPicker) findViewById(R.id.weekPicker)).setMaxValue(weekValues.length - 1);
                        ((NumberPicker) findViewById(R.id.termPicker)).setOnScrollListener(scroll);
                        ((NumberPicker) findViewById(R.id.weekPicker)).setOnScrollListener(scroll);
                        current_week = new CurrentWeek(MainActivity.this);
                        ((NumberPicker) findViewById(R.id.weekPicker)).setOnValueChangedListener((numberPicker, oldValue, newValue) -> current_week.setValue(newValue));
                        if (locate.term != null) {
                            ((NumberPicker) findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(locate.term.termname));
                            ((NumberPicker) findViewById(R.id.weekPicker)).setValue(Math.toIntExact(locate.week));
                            current_week.setValue(Math.toIntExact(locate.week));
                        } else {
                            ((NumberPicker) findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(getResources().getString(R.string.term_vacation)));
                            ((NumberPicker) findViewById(R.id.weekPicker)).setValue(0);
                            current_week.setValue(0);
                        }
                        showTable(locate);
                    });
                }
            }).start();
        }
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
            String selected_term_name = termValues[((NumberPicker)findViewById(R.id.termPicker)).getValue()];
            long selected_week = ((NumberPicker)findViewById(R.id.weekPicker)).getValue();
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
        new Thread(() -> {
            if (MyApp.isLAN()){
                runOnUiThread(() -> {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                });
            }else {
                runOnUiThread(() -> {
                    Intent intent = new Intent(MainActivity.this, Login.class);
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
                    ((NumberPicker) findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(locate.term.termname));
                    ((NumberPicker) findViewById(R.id.weekPicker)).setValue(Math.toIntExact(locate.week));
                    current_week.setValue(Math.toIntExact(locate.week));
                } else {
                    ((NumberPicker) findViewById(R.id.termPicker)).setValue(Arrays.asList(termValues).indexOf(getResources().getString(R.string.term_vacation)));
                    ((NumberPicker) findViewById(R.id.weekPicker)).setValue(0);
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
                                    text.append(my_node.cname).append("\n");
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
                    ((TextView) findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorWeekdayAndTimeBackground, getTheme()));
                    ((TextView) findViewById(id)).setTextColor(((TextView) findViewById(R.id.term_picker_text)).getCurrentTextColor());
                }
                for (int id : MyApp.timetvIds) {
                    ((TextView) findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorWeekdayAndTimeBackground, getTheme()));
                    ((TextView) findViewById(id)).setTextColor(((TextView) findViewById(R.id.term_picker_text)).getCurrentTextColor());
                }
                for (int[] id_list : MyApp.nodeIds) {
                    for (int id : id_list) {
                        ((TextView) findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorTableNodeBackground, getTheme()));
                        ((TextView) findViewById(id)).setTextColor(((TextView) findViewById(R.id.term_picker_text)).getCurrentTextColor());
                    }
                }
                for (int row_index = 0; row_index < MyApp.nodeIds.length; row_index++) {
                    for (int column_index = 0; column_index < MyApp.weekdaytvIds.length; column_index++) {
                        String node_text = texts.get(row_index * MyApp.weekdaytvIds.length + column_index);
                        ((TextView)findViewById(MyApp.nodeIds[row_index][column_index])).setText(node_text);
                    }
                }
                ((TextView)findViewById(R.id.textView_date)).setText(locate.month + "/" + locate.day);
                int time_tv_ids_index = -1;
                if (locate.time != null) {
                    time_tv_ids_index = Arrays.asList(MyApp.times).indexOf(locate.time);
                }
                int weekday_tv_ids_index = -1;
                weekday_tv_ids_index = (int) (locate.weekday - 1);
                if (time_tv_ids_index != -1){
                    ((TextView)findViewById(MyApp.timetvIds[time_tv_ids_index])).setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                    ((TextView)findViewById(MyApp.timetvIds[time_tv_ids_index])).setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                }
                if (weekday_tv_ids_index != -1){
                    ((TextView)findViewById(MyApp.weekdaytvIds[weekday_tv_ids_index])).setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                    ((TextView)findViewById(MyApp.weekdaytvIds[weekday_tv_ids_index])).setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                }
                if (time_tv_ids_index != -1 && weekday_tv_ids_index != -1){
                    TextView node_tv = (TextView)findViewById(MyApp.nodeIds[time_tv_ids_index][weekday_tv_ids_index]);
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
            String selected_term_name = termValues[((NumberPicker)findViewById(R.id.termPicker)).getValue()];
            long selected_week = ((NumberPicker)findViewById(R.id.weekPicker)).getValue();
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
