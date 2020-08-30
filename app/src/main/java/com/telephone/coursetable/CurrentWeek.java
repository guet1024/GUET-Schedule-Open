package com.telephone.coursetable;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @clear
 */
public class CurrentWeek extends ViewModel {

    private MutableLiveData<Long> current_week = null;
    private MainActivity main = null;
    private Observer<Long> observer = null;

    public CurrentWeek(@NonNull MainActivity main){
        current_week = new MutableLiveData<>();
        this.main = main;
        observer = aLong -> {
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
            CurrentWeek.this.main.runOnUiThread(()->((FloatingActionButton)CurrentWeek.this.main.findViewById(R.id.floatingActionButton)).setImageResource(weekIconIds[Math.toIntExact(aLong)]));
        };
        current_week.observe(this.main, observer);
    }

    public Long getValue(){
        return current_week.getValue();
    }

    public void postValue(long value){
        current_week.postValue(value);
    }

    public void setValue(long value){
        current_week.setValue(value);
    }
}
