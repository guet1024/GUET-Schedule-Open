package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"term"})
public class TermInfo {
    @NonNull
    public String term;

    public String startdate;
    public String enddate;
    public String weeknum;
    public String termname;
    public String schoolyear;
    public String comm;

    public TermInfo(String term,
                    String startdate,
                    String enddate,
                    String weeknum,
                    String termname,
                    String schoolyear,
                    String comm){
        this.term = term;
        this.startdate = startdate;
        this.enddate = enddate;
        this.weeknum = weeknum;
        this.termname = termname;
        this.schoolyear = schoolyear;
        this.comm = comm;
    }
}
