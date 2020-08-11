package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"term", "weekday", "time", "courseno", "startweek", "endweek", "oddweek"})
public class GoToClass {
    @NonNull
    public String term;//学期
    @NonNull
    public long weekday;//星期几
    @NonNull
    public String time;//时间段
    @NonNull
    public String courseno;
    @NonNull
    public long startweek;
    @NonNull
    public long endweek;
    @NonNull
    public boolean oddweek;//false

    //返回一个GoToClass的List


    public long id;//0
    public String croomno;//教室
    public long hours;//0

    //新添加的一个属性
    //public String cname;

    public GoToClass(String term, long weekday, String time, String courseno,String croomno,
                     long startweek, long endweek, boolean oddweek,long id, long hours){
        this.term = term;
        this.weekday = weekday;
        this.time = time;
        this.courseno = courseno;
        //this.cname = cname;
        this.id = id;
        this.croomno = croomno;
        this.startweek = startweek;
        this.endweek = endweek;
        this.oddweek = oddweek;
        this.hours = hours;
    }
}
