package com.telephone.coursetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String current_term;
    private CurrentWeek current_week;

    private long exit_ts = 0;
    private boolean has_user = false;
    private boolean updating = false;

    private GoToClassDao gdao;
    private TermInfoDao tdao;
    private UserDao udao;
    private PersonInfoDao pdao;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private NumberPicker.OnValueChangeListener term_value_change_listener;
    private NumberPicker.OnValueChangeListener week_value_change_listener_init;
    private NumberPicker.OnValueChangeListener week_value_change_listener_dynamic;

    private PickerPanel pickerPanel;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.login_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.login_menu_login:
//                Intent intent = new Intent(this, Login.class);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ((TextView)findViewById(R.id.textView_update_time)).setVisibility(View.INVISIBLE);
        ((FloatingActionButton)findViewById(R.id.floatingActionButton)).setVisibility(View.INVISIBLE);
        pickerPanel.hide(MainActivity.this);

        gdao = MyApp.getCurrentAppDB().goToClassDao();
        tdao = MyApp.getCurrentAppDB().termInfoDao();
        udao = MyApp.getCurrentAppDB().userDao();
        pdao = MyApp.getCurrentAppDB().personInfoDao();

        pref = getSharedPreferences(getResources().getString(R.string.hours_preference_file_name), MODE_PRIVATE);
        editor = pref.edit();

        current_week = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(MyApp.getCurrentApp())).get(CurrentWeek.class);
        current_week.getCurrent_week().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                int iconIndex = Integer.parseInt(aLong.toString());
                int[] weekIconIds = {
                        R.drawable.vacation,
                        R.drawable.week_1,
                        R.drawable.week_2,
                        R.drawable.week_3,
                        R.drawable.week_4,
                        R.drawable.week_5,
                        R.drawable.week_6,
                        R.drawable.week_7,
                        R.drawable.week_8,
                        R.drawable.week_9,
                        R.drawable.week_10,
                        R.drawable.week_11,
                        R.drawable.week_12,
                        R.drawable.week_13,
                        R.drawable.week_14,
                        R.drawable.week_15,
                        R.drawable.week_16,
                        R.drawable.week_17,
                        R.drawable.week_18,
                        R.drawable.week_19,
                        R.drawable.week_20,
                        R.drawable.week_21,
                        R.drawable.week_22,
                        R.drawable.week_23,
                        R.drawable.week_24,
                        R.drawable.week_25,
                        R.drawable.week_26,
                        R.drawable.week_27,
                        R.drawable.week_28,
                        R.drawable.week_29,
                        R.drawable.week_30
                };
                ((FloatingActionButton)findViewById(R.id.floatingActionButton)).setImageResource(weekIconIds[iconIndex]);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                //init title
                List<User> acuser = udao.getActivatedUser();
                if (acuser.isEmpty()){
                    if (pref.getBoolean(getResources().getString(R.string.pref_user_updating_key), false)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView)findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.updating_user_title_suffix));
                            }
                        });
                        updating = true;
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.no_user_title_suffix));
                            }
                        });
                    }
                    return;
                }else{
                    has_user = true;
                    final String last_update_time = acuser.get(0).updateTime;
                    final String pname = pdao.selectAll().get(0).name;
                    final String pre = getResources().getString(R.string.ok_user_title_prefix);
                    final String suf = getResources().getString(R.string.ok_user_title_suffix);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.textView_update_time)).setText(pre + pname + "\n" + pre + last_update_time + suf);
                            ((TextView)findViewById(R.id.textView_update_time)).setVisibility(View.VISIBLE);
                            ((FloatingActionButton)findViewById(R.id.floatingActionButton)).setVisibility(View.VISIBLE);
                        }
                    });
                }
                //locate now
                Locate locate = Login.locateNow(Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern(getResources().getString(R.string.ts_datetime_format)))).getTime(),
                        tdao, pref,
                        MyApp.times, DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                        getResources().getString(R.string.hours_pref_time_start_suffix),
                        getResources().getString(R.string.hours_pref_time_end_suffix),
                        getResources().getString(R.string.hours_pref_time_des_suffix));
                String pref_term = pref.getString(getResources().getString(R.string.pref_current_term_key), null);
                long pref_week = pref.getLong(getResources().getString(R.string.pref_current_week_key), -1);
                //init current term and current week
                //after Login, the current term and current week in SharedPreferences will disappear
                Log.e("pref_week", ""+pref_week);
                if (pref_term == null || pref_week == -1){
                    if (locate.term == null){
                        current_term = getResources().getString(R.string.term_vacation);
                        current_week.getCurrent_week().postValue((long)0);
                    }else {
                        current_term = locate.term.termname;
                        current_week.getCurrent_week().postValue(locate.week);
                    }
                }else{
                    current_term = pref_term;
                    current_week.getCurrent_week().postValue(pref_week);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("init || current_week.getCurrent_week().getValue()", ""+current_week.getCurrent_week().getValue());
                //init term picker
                List<TermInfo> termInfoList = tdao.selectAll();
                final List<String> termNameList = new LinkedList<String>();
                for (TermInfo t : termInfoList){
                    termNameList.add(t.termname);
                }
                termNameList.add(getResources().getString(R.string.term_vacation));
                final String[] termNameArray = termNameList.toArray(new String[0]);
                ((NumberPicker)findViewById(R.id.termPicker)).setValue(0);
                ((NumberPicker)findViewById(R.id.termPicker)).setWrapSelectorWheel(false);
                ((NumberPicker)findViewById(R.id.termPicker)).setDisplayedValues(termNameArray);
                ((NumberPicker)findViewById(R.id.termPicker)).setMinValue(0);
                ((NumberPicker)findViewById(R.id.termPicker)).setMaxValue(termNameArray.length - 1);
                ((NumberPicker)findViewById(R.id.termPicker)).setValue(termNameList.indexOf(current_term));
                term_value_change_listener = new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(final NumberPicker numberPicker, int old_value, final int new_value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (new_value == numberPicker.getMaxValue()){
                                    //if on vacation
                                    ((NumberPicker)findViewById(R.id.weekPicker)).setValue(0);
                                    ((NumberPicker)findViewById(R.id.weekPicker)).setWrapSelectorWheel(false);
                                    ((NumberPicker)findViewById(R.id.weekPicker)).setDisplayedValues(new String[]{"0","bug"});
                                    ((NumberPicker)findViewById(R.id.weekPicker)).setMinValue(0);
                                    ((NumberPicker)findViewById(R.id.weekPicker)).setMaxValue(0);
                                    ((NumberPicker)findViewById(R.id.weekPicker)).setValue(0);
                                    current_week.getCurrent_week().postValue((long)0);
                                    week_value_change_listener_dynamic = new NumberPicker.OnValueChangeListener() {
                                        @Override
                                        public void onValueChange(NumberPicker numberPicker_week, int old_value_week, int new_value_week) {
                                            current_week.getCurrent_week().postValue((long)0);
                                        }
                                    };
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setOnValueChangedListener(week_value_change_listener_dynamic);
                                }else {
                                    //else
                                    long week_num = Long.parseLong(tdao.getWeekNumByTermName(numberPicker.getDisplayedValues()[new_value]).get(0));
                                    List<String> weekList = new LinkedList<String>();
                                    for (int i = 1; i <= week_num; i++) {
                                        weekList.add(i + "");
                                    }
                                    final String[] weekArray = weekList.toArray(new String[0]);
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setValue(0);
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setWrapSelectorWheel(false);
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setDisplayedValues(weekArray);
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setMinValue(0);
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setMaxValue(weekArray.length - 1);
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setValue(0);
                                    current_week.getCurrent_week().postValue((long)1);
                                    week_value_change_listener_dynamic = new NumberPicker.OnValueChangeListener() {
                                        @Override
                                        public void onValueChange(NumberPicker numberPicker_week, int old_value_week, int new_value_week) {
                                            current_week.getCurrent_week().postValue(Long.parseLong(weekArray[new_value_week]));
                                        }
                                    };
                                    ((NumberPicker) findViewById(R.id.weekPicker)).setOnValueChangedListener(week_value_change_listener_dynamic);
                                }
                                current_term = termNameArray[new_value];
                            }
                        }).start();
                    }
                };
                ((NumberPicker)findViewById(R.id.termPicker)).setOnValueChangedListener(term_value_change_listener);
                //init week picker
                if (current_term.equals(getResources().getString(R.string.term_vacation))){
                    //if on vacation
                    ((NumberPicker)findViewById(R.id.weekPicker)).setValue(0);
                    ((NumberPicker)findViewById(R.id.weekPicker)).setWrapSelectorWheel(false);
                    ((NumberPicker)findViewById(R.id.weekPicker)).setDisplayedValues(new String[]{"0","bug"});
                    ((NumberPicker)findViewById(R.id.weekPicker)).setMinValue(0);
                    ((NumberPicker)findViewById(R.id.weekPicker)).setMaxValue(0);
                    ((NumberPicker)findViewById(R.id.weekPicker)).setValue(0);
                    current_week.getCurrent_week().postValue((long)0);
                    week_value_change_listener_init = new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker numberPicker_week, int old_value_week, int new_value_week) {
                            current_week.getCurrent_week().postValue((long)0);
                        }
                    };
                    ((NumberPicker) findViewById(R.id.weekPicker)).setOnValueChangedListener(week_value_change_listener_init);
                }else {
                    //else
                    long week_num = Long.parseLong(tdao.getWeekNumByTermName(current_term).get(0));
                    List<String> weekList = new LinkedList<String>();
                    for (int i = 1; i <= week_num; i++) {
                        weekList.add(i + "");
                    }
                    final String[] weekArray = weekList.toArray(new String[0]);
                    ((NumberPicker) findViewById(R.id.weekPicker)).setValue(0);
                    ((NumberPicker) findViewById(R.id.weekPicker)).setWrapSelectorWheel(false);
                    ((NumberPicker) findViewById(R.id.weekPicker)).setDisplayedValues(weekArray);
                    ((NumberPicker) findViewById(R.id.weekPicker)).setMinValue(0);
                    ((NumberPicker) findViewById(R.id.weekPicker)).setMaxValue(weekArray.length - 1);
                    ((NumberPicker) findViewById(R.id.weekPicker)).setValue(weekList.indexOf(current_week + ""));
                    //no need to set current_week
                    week_value_change_listener_init = new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker numberPicker_week, int old_value_week, int new_value_week) {
                            current_week.getCurrent_week().postValue(Long.parseLong(weekArray[new_value_week]));
                        }
                    };
                    ((NumberPicker) findViewById(R.id.weekPicker)).setOnValueChangedListener(week_value_change_listener_init);
                }
                //show and hide picker panel to refresh their layout size
                //this may not work
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pickerPanel.show(MainActivity.this);
                        pickerPanel.hide(MainActivity.this);
                    }
                });
                //show table and hide picker panel
                showTable();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (pickerPanel.isShown()){
            //show table and hide picker panel
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showTable();
                }
            }).start();
        }else {
            long nts = Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern(getResources().getString(R.string.ts_datetime_format)))).getTime();
            Log.e("MainActivity press back", nts + "");
            if (nts - exit_ts < 2000) {
                onPause();
                System.exit(0);
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exit_ts = nts;
            }
        }
    }

    @Override
    protected void onPause() {
        if (current_term != null){
            editor.putString(getResources().getString(R.string.pref_current_term_key), current_term);
            editor.putLong(getResources().getString(R.string.pref_current_week_key), current_week.getCurrent_week().getValue());
        }
        editor.commit();
        super.onPause();
    }

    /**
     * jump to Login Activity
     */
    public void Login(View view){
        if (updating)return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MyApp.isLAN()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, Login.class);
                            startActivity(intent);
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, Login_vpn.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * @non-ui
     * change the pickers to locate term and week, also change the current_term and current_week
     */
    public void returnToday(View view){
        Locate locate = Login.locateNow(Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern(getResources().getString(R.string.ts_datetime_format)))).getTime(),
                tdao, pref,
                MyApp.times, DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                getResources().getString(R.string.hours_pref_time_start_suffix),
                getResources().getString(R.string.hours_pref_time_end_suffix),
                getResources().getString(R.string.hours_pref_time_des_suffix));
        String today_termname;
        String today_weeknum;
        if (locate.term == null){
            today_termname = getResources().getString(R.string.term_vacation);
            today_weeknum = "0";
        }else{
            today_termname = locate.term.termname;
            today_weeknum = locate.week + "";
        }
        final String today_termnamef = today_termname;
        final String today_weeknumf = today_weeknum;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] tpvs = ((NumberPicker)findViewById(R.id.termPicker)).getDisplayedValues();
                int tindex = Arrays.asList(tpvs).indexOf(today_termnamef);
                ((NumberPicker)findViewById(R.id.termPicker)).setValue(tindex);
                term_value_change_listener.onValueChange((NumberPicker)findViewById(R.id.termPicker), 0, tindex);
            }
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] wpvs = ((NumberPicker)findViewById(R.id.weekPicker)).getDisplayedValues();
                int windex = Arrays.asList(wpvs).indexOf(today_weeknumf);
                if (windex == -1)return;
                ((NumberPicker)findViewById(R.id.weekPicker)).setValue(windex);
                week_value_change_listener_dynamic.onValueChange((NumberPicker)findViewById(R.id.weekPicker), 0, windex);
            }
        });
    }

    /**
     * @non-ui
     * show table according to current_term and current_week, hide picker panel
     */
    public void showTable(){
        for(int[] id_list : MyApp.nodeIds){
            for (int id : id_list){
                final int idf = id;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(idf)).setText("");
                    }
                });
            }
        }
        if (!current_term.equals(getResources().getString(R.string.term_vacation))) {
            //override current_term from termname to term
            String current_term = tdao.getTermCodeByTermName(this.current_term).get(0);
            List<ShowTableNode> list = gdao.getSpecifiedWeekTable(current_term, current_week.getCurrent_week().getValue());
            List<String> time_list = Arrays.asList(MyApp.times);
            long last_weekdayIndex = -2;
            long last_timeIndex = -2;
            String lastCont = "";
            for (ShowTableNode node : list) {
                long weekdayIndex = node.weekday - 1;
                long timeIndex = time_list.indexOf(node.time);
                if (timeIndex == -1) {
                    //if not an available time
                    continue;
                }
                //if not the same node_text_view as last loop, discard last content
                if (weekdayIndex != last_weekdayIndex || timeIndex != last_timeIndex) {
                    lastCont = "";
                }
                last_weekdayIndex = weekdayIndex;
                last_timeIndex = timeIndex;
                final TextView tv = (TextView) findViewById(MyApp.nodeIds[(int) timeIndex][(int) weekdayIndex]);
                StringBuilder sb = new StringBuilder();
                sb.append(lastCont);
                if (node.courseno != null) {
                    sb.append("[").append(node.courseno).append("]").append("\n");
                }
                if (node.cname != null) {
                    sb.append(node.cname).append("\n");
                }
                if (node.name != null) {
                    sb.append("@").append(node.name).append("\n");
                }
                if (node.croomno != null) {
                    sb.append("(").append(node.croomno).append(")").append("\n");
                }
                final String newCont = sb.toString();
                lastCont = newCont;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(newCont);
                    }
                });
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pickerPanel.hide(MainActivity.this);
            }
        });
        //show light date, weekday, time, node
        //locate now
        Locate locate = Login.locateNow(Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern(getResources().getString(R.string.ts_datetime_format)))).getTime(),
                tdao, pref,
                MyApp.times, DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                getResources().getString(R.string.hours_pref_time_start_suffix),
                getResources().getString(R.string.hours_pref_time_end_suffix),
                getResources().getString(R.string.hours_pref_time_des_suffix));
        highLight(locate.weekday, locate.time, locate.month, locate.day);
    }

    public void openOrHidePanel(View view){
        if (pickerPanel.isShown()){
            //show table and hide picker panel
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showTable();
                }
            }).start();
        }else{
            //open picker panel
            pickerPanel.show(MainActivity.this);
        }
    }

    public void resetTermWeek(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                returnToday(null);
            }
        }).start();
    }

    public void setTime(View view){
        if (!pickerPanel.isShown() && has_user){
            Intent intent = new Intent(this, ChangeHours.class);
            startActivity(intent);
        }
    }

    public void highLight(final long weekday, String time, final long month, final long day){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int id : MyApp.weekdaytvIds){
                    ((TextView)findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorWeekdayAndTimeBackground, getTheme()));
                    ((TextView)findViewById(id)).setTextColor(((TextView)findViewById(R.id.term_picker_text)).getCurrentTextColor());
                }
                for(int id : MyApp.timetvIds){
                    ((TextView)findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorWeekdayAndTimeBackground, getTheme()));
                    ((TextView)findViewById(id)).setTextColor(((TextView)findViewById(R.id.term_picker_text)).getCurrentTextColor());
                }
                for(int[] id_list : MyApp.nodeIds){
                    for (int id : id_list){
                        ((TextView)findViewById(id)).setBackgroundColor(getResources().getColor(R.color.colorTableNodeBackground, getTheme()));
                        ((TextView)findViewById(id)).setTextColor(((TextView)findViewById(R.id.term_picker_text)).getCurrentTextColor());
                    }
                }
            }
        });
        final TextView weekday_tv = (TextView)findViewById(MyApp.weekdaytvIds[(int)weekday - 1]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weekday_tv.setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                weekday_tv.setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                ((TextView)findViewById(R.id.textView_date)).setText(month + "/" + day);
            }
        });
        if (time == null){
            return;
        }
        List<String> time_list = Arrays.asList(MyApp.times);
        final int currentTimeIndex = time_list.indexOf(time);
        final TextView time_tv = (TextView) findViewById(MyApp.timetvIds[currentTimeIndex]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time_tv.setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                time_tv.setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                TextView node = (TextView)findViewById(MyApp.nodeIds[currentTimeIndex][(int)weekday - 1]);
                if (!node.getText().toString().equals("")) {
                    node.setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                }
            }
        });
    }
}
