package com.telephone.coursetable.Database.KeyAndValue;

import androidx.annotation.NonNull;

import com.telephone.coursetable.Database.Key.GoToClassKey;

public class GoToClassKeyAndValue {
    @NonNull
    public String username;
    @NonNull
    public String term;
    @NonNull
    public long weekday;
    @NonNull
    public String time;
    @NonNull
    public String courseno;
    @NonNull
    public long startweek;
    @NonNull
    public long endweek;
    @NonNull
    public boolean oddweek;

    public String my_comm;

    public GoToClassKey getKey(){
        return new GoToClassKey(username, term, weekday, time, courseno, startweek, endweek, oddweek);
    }

    public String getValue(){
        return my_comm;
    }
}
