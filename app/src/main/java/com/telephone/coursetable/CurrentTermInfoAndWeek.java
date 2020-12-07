package com.telephone.coursetable;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.telephone.coursetable.Clock.GetDateInfo;
import com.telephone.coursetable.Database.Methods.Methods;
import com.telephone.coursetable.Database.TermInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @clear
 */
public class CurrentTermInfoAndWeek {

    private MutableLiveData<Map.Entry<TermInfo, Long>> current_term_and_week = null;
    private MainActivity main = null;

    private static final String[] weekday_names = new String[]{
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六",
            "星期日"
    };
    private static final int[] weekIconIds = new int[]{
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


    public CurrentTermInfoAndWeek(@NonNull MainActivity main, @NonNull TextView[] week_boxes, @NonNull FloatingActionButton week_button) {
        this.main = main;
        // init but not post
        current_term_and_week = new MutableLiveData<>();
        /** the observe() MUST be called on ui thread */
        main.runOnUiThread(()->{
            current_term_and_week.observe(main, new Observer<Map.Entry<TermInfo, Long>>() {
                /**
                 * this method will be called in ui thread !!
                 */
                @Override
                public void onChanged(Map.Entry<TermInfo, Long> termInfoLongEntry) {
                    new Thread(()->{
                        String[] a_week_date_info = GetDateInfo.get_a_week_date_info_all_timezone_using_gmt8(termInfoLongEntry.getKey(), termInfoLongEntry.getValue());
                        main.runOnUiThread(() -> {
                            for (int i = 0; i < week_boxes.length; i++) {
                                week_boxes[i].setText(weekday_names[i] + "\n" + a_week_date_info[i]);
                            }
                            week_button.setImageResource(weekIconIds[(int) (long) termInfoLongEntry.getValue()]);
                        });
                    }).start();
                }
            });
        });
    }

    @UiThread
    public long currentWeek_must_ui(){
        return current_term_and_week.getValue().getValue();
    }

    @UiThread
    public TermInfo currentTermInfo_must_ui(){
        return current_term_and_week.getValue().getKey();
    }

    public void updateWeek(long weekNum){
        if (weekNum < 0){
            weekNum = 0;
        }
        long weekNum_f = weekNum;
        main.runOnUiThread(()->{
            current_term_and_week.setValue(com.telephone.coursetable.Database.Methods.Methods.entry(currentTermInfo_must_ui(), weekNum_f));
        });
    }

    public void updateTermInfo(@Nullable TermInfo termInfo){
        main.runOnUiThread(()->{
            current_term_and_week.setValue(com.telephone.coursetable.Database.Methods.Methods.entry(termInfo, currentWeek_must_ui()));
        });
    }

    public void update(@Nullable TermInfo termInfo, long weekNum){
        if (weekNum < 0){
            weekNum = 0;
        }
        long weekNum_f = weekNum;
        main.runOnUiThread(()->{
            current_term_and_week.setValue(com.telephone.coursetable.Database.Methods.Methods.entry(termInfo, weekNum_f));
        });
    }

    public void updateTermInfo(@Nullable String termName){
        new Thread(()->{
            TermInfo termInfo_to_insert = null;
            if (termName != null){
                List<TermInfo> terms = MyApp.getCurrentAppDB().termInfoDao().getTermByTermName(termName);
                if (!terms.isEmpty()){
                    termInfo_to_insert = terms.get(0);
                }
            }
            // this may be threads-unsafe ???
            updateTermInfo(termInfo_to_insert);
        }).start();
    }
}
