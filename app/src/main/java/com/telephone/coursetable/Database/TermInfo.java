package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @clear
 */
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
    public long sts_backup;
    public int delay_week;

    public TermInfo(@NonNull String term, String startdate, String enddate, String weeknum, String termname, String schoolyear, String comm, long sts, long ets) {
        this.term = term;
        this.startdate = startdate;
        this.enddate = enddate;
        this.weeknum = weeknum;
        this.termname = termname;
        this.schoolyear = schoolyear;
        this.comm = comm;
        this.sts = sts;
        this.ets = ets;
        this.sts_backup = sts;
        this.delay_week = 0;
    }

    public void setDelay(int delay_week){
        this.delay_week = delay_week;
        this.sts = sts_backup;
        this.sts += delay_week * 86400000 * 7;
        if (this.sts >= this.ets) sts = sts_backup;
    }
}
