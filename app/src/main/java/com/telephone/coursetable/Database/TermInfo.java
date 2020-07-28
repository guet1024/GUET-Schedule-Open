package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    public long sts;
    public long ets;

    public TermInfo(String term,
                    String startdate,
                    String enddate,
                    String weeknum,
                    String termname,
                    String schoolyear,
                    String comm,
                    long sts,
                    long ets){
        this.term = term;
        this.startdate = startdate;
        this.enddate = enddate;
        this.weeknum = weeknum;
        this.termname = termname;
        this.schoolyear = schoolyear;
        this.comm = comm;
        this.sts = sts;
        this.ets = ets;
    }
}
