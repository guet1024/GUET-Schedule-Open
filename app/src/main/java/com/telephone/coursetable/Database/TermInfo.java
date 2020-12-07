package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.telephone.coursetable.LogMe.LogMe;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

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
    public long ets_backup;
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
        this.ets_backup = ets;
        this.delay_week = 0;
    }

    public void setDelay(int delay_week){
        final String NAME = "setDelay()";
        this.delay_week = delay_week;
        this.sts = sts_backup;
        this.ets = ets_backup;
        long delay_ts = delay_week * 86400000L * 7;
        this.sts += delay_ts;
        this.ets += delay_ts;

        if (this.sts >= this.ets_backup) {  // delayed start time cannot be behind origin end time
            sts = sts_backup;
            ets = ets_backup;
            LogMe.e(NAME, "invalidated delay, fallback");
        }else {
            LogMe.e(NAME, "success");
        }
    }

    public long getTheLastTimeStampOfWeekAndDay(int week, int weekday){
        weekday++;
        long week_ts = (week - 1) * 86400000L * 7;
        long day_ts = (weekday - 1) * 86400000L;
        return (this.sts + week_ts + day_ts - 1L);
    }

    public long getTheFirstTimeStampOfWeekAndDay(int week, int weekday){
        long week_ts = (week - 1) * 86400000L * 7;
        long day_ts = (weekday - 1) * 86400000L;
        return (this.sts + week_ts + day_ts);
    }

    public long getTheLastTimeStampOfTheLastDay(){
        return (this.ets - 1L);
    }

    public long getTheFirstTimeStampOfTheLastDay(){
        return (this.ets - 86400000L);
    }

    @Override
    public String toString() {
        return "TermInfo{" +
                "term='" + term + '\'' +
                ", startdate='" + startdate + '\'' +
                ", enddate='" + enddate + '\'' +
                ", weeknum='" + weeknum + '\'' +
                ", termname='" + termname + '\'' +
                ", schoolyear='" + schoolyear + '\'' +
                ", comm='" + comm + '\'' +
                ", sts=" + sts +
                ", ets=" + ets +
                ", sts_backup=" + sts_backup +
                ", delay_week=" + delay_week +
                '}';
    }
}
