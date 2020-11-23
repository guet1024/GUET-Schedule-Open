package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * @clear
 */
@Entity(primaryKeys = {"username", "term", "weekday", "time", "courseno", "startweek", "endweek", "oddweek"})
public class GoToClass {
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

    public long id;
    public String croomno;
    public double hours;
    public String sys_comm;
    public String my_comm;
    public boolean customized;

    public GoToClass(@NonNull String username, @NonNull String term, long weekday, @NonNull String time, @NonNull String courseno, long startweek, long endweek, boolean oddweek, long id, String croomno, double hours, String sys_comm, String my_comm, boolean customized) {
        this.username = username;
        this.term = term;
        this.weekday = weekday;
        this.time = time;
        this.courseno = courseno;
        this.startweek = startweek;
        this.endweek = endweek;
        this.oddweek = oddweek;
        this.id = id;
        this.croomno = croomno;
        this.hours = hours;
        this.sys_comm = sys_comm;
        this.my_comm = my_comm;
        this.customized = customized;
    }
}
