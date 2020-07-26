package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"term", "weekday", "time", "courseno", "startweek", "endweek", "oddweek"})
public class GoToClass {
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

    public long id;
    public String croomno;
    public long hours;

    public GoToClass(String term, long weekday, String time, String courseno, long id, String croomno, long startweek, long endweek, boolean oddweek, long hours){
        this.term = term;
        this.weekday = weekday;
        this.time = time;
        this.courseno = courseno;
        this.id = id;
        this.croomno = croomno;
        this.startweek = startweek;
        this.endweek = endweek;
        this.oddweek = oddweek;
        this.hours = hours;
    }
}
