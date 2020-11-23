package com.telephone.coursetable.Database.Key;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GoToClassKey {
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

    public GoToClassKey(@NonNull String username, @NonNull String term, long weekday, @NonNull String time, @NonNull String courseno, long startweek, long endweek, boolean oddweek) {
        this.username = username;
        this.term = term;
        this.weekday = weekday;
        this.time = time;
        this.courseno = courseno;
        this.startweek = startweek;
        this.endweek = endweek;
        this.oddweek = oddweek;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null){
            return false;
        }
        try {
            GoToClassKey key = (GoToClassKey) obj;
            return key.username.equals(this.username) &&
                    key.term.equals(this.term) &&
                    key.weekday == this.weekday &&
                    key.time.equals(this.time) &&
                    key.courseno.equals(this.courseno) &&
                    key.startweek == this.startweek &&
                    key.endweek == this.endweek &&
                    key.oddweek == this.oddweek;
        }catch (Exception e){
            return false;
        }
    }
}
