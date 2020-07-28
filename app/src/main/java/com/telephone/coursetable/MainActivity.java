package com.telephone.coursetable;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.User;
import com.telephone.coursetable.Database.UserDao;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String current_term = "2019-2020_2";
    private long current_week = 6;
    private GoToClassDao gdao;
    private TermInfoDao tdao;
    private UserDao udao;

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
        ((TextView)findViewById(R.id.textView_update)).setVisibility(View.INVISIBLE);

        gdao = MyApp.getCurrentAppDB().goToClassDao();
        tdao = MyApp.getCurrentAppDB().termInfoDao();
        udao = MyApp.getCurrentAppDB().userDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> acuser = udao.getActivatedUser();
                if (acuser.isEmpty()){
                    ((TextView)findViewById(R.id.textView_title)).setText(getResources().getString(R.string.title) + getResources().getString(R.string.no_user_title_suffix));
                    return;
                }else{
                    String last_update_time = acuser.get(0).updateTime;
                    ((TextView)findViewById(R.id.textView_update)).setText(getResources().getString(R.string.ok_user_title_suffix) + last_update_time);
                    ((TextView)findViewById(R.id.textView_update)).setVisibility(View.VISIBLE);
                }
                Locate locate = Login.locateNow(Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern(getResources().getString(R.string.ts_datetime_format)))).getTime(),
                        tdao, getSharedPreferences(getResources().getString(R.string.hours_preference_file_name), MODE_PRIVATE),
                        MyApp.times, DateTimeFormatter.ofPattern(getResources().getString(R.string.server_hours_time_format)),
                        getResources().getString(R.string.hours_pref_time_start_suffix),
                        getResources().getString(R.string.hours_pref_time_end_suffix),
                        getResources().getString(R.string.hours_pref_time_des_suffix));
                long current_weekday = locate.weekday;
                TextView weekday_tv;
                switch ((int)current_weekday){
                    case 1:
                        weekday_tv = (TextView)findViewById(R.id.textView_wd1);
                        break;
                    case 2:
                        weekday_tv = (TextView)findViewById(R.id.textView_wd2);
                        break;
                    case 3:
                        weekday_tv = (TextView)findViewById(R.id.textView_wd3);
                        break;
                    case 4:
                        weekday_tv = (TextView)findViewById(R.id.textView_wd4);
                        break;
                    case 5:
                        weekday_tv = (TextView)findViewById(R.id.textView_wd5);
                        break;
                    case 6:
                        weekday_tv = (TextView)findViewById(R.id.textView_wd6);
                        break;
                    case 7:
                        weekday_tv = (TextView)findViewById(R.id.textView_wd7);
                        break;
                    default:
                        weekday_tv = null;
                }
                weekday_tv.setBackgroundColor(getResources().getColor(R.color.colorCurrentWeekday, getTheme()));
                weekday_tv.setTextColor(getResources().getColor(R.color.colorCurrentWeekdayText, getTheme()));
                ((TextView)findViewById(R.id.textView_date)).setText(locate.month + "/" + locate.day);
                List<String> time_list = Arrays.asList(MyApp.times);
                List<ShowTableNode> list = gdao.getSpecifiedWeekTable(current_term, current_week);
                long last_weekdayIndex = -2;
                long last_timeIndex = -2;
                String lastCont = "";
                for (ShowTableNode node : list){
                    long weekdayIndex = node.weekday - 1;
                    long timeIndex = time_list.indexOf(node.time);
                    if (timeIndex == -1){
                        continue;
                    }
                    if (weekdayIndex != last_weekdayIndex || timeIndex != last_timeIndex){
                        last_weekdayIndex = weekdayIndex;
                        last_timeIndex = timeIndex;
                        lastCont = "";
                    }
                    final TextView tv = (TextView)findViewById(MyApp.nodeIds[(int)timeIndex][(int)weekdayIndex]);
                    StringBuilder sb = new StringBuilder();
                    sb.append(lastCont);
                    if (node.courseno != null){
                        sb.append("[").append(node.courseno).append("]").append("\n");
                    }
                    if (node.cname != null){
                        sb.append(node.cname).append("\n");
                    }
                    if (node.name != null){
                        sb.append("@").append(node.name).append("\n");
                    }
                    if (node.croomno != null){
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
        }).start();
    }

    public void Login(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
