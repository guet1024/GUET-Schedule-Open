package com.telephone.coursetable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CurrentWeek extends ViewModel {

    private MutableLiveData<Long> current_week;

    public MutableLiveData<Long> getCurrent_week(){
        if (current_week == null){
            current_week = new MutableLiveData<Long>();
        }
        return current_week;
    }
}
