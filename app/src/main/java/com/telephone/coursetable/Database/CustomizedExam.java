package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// primary keys: [ when + cno + comment | sid ]
@Entity(primaryKeys = {"sid", "date", "time", "week", "weekday", "cno", "comment"})
public class CustomizedExam {
    @NonNull
    public String sid;
    @NonNull
    public String date;
    @NonNull
    public String time;
    public long week;
    public long weekday;
    @NonNull
    public String cno;
    @NonNull
    public String comment;

    public long sts;
    public long ets;

    public CustomizedExam(@NonNull String sid, @NonNull String date, @NonNull String time, long week, long weekday, @NonNull String cno, @NonNull String comment, long sts, long ets) {
        this.sid = sid;
        this.date = date;
        this.time = time;
        this.week = week;
        this.weekday = weekday;
        this.cno = cno;
        this.comment = comment;
        this.sts = sts;
        this.ets = ets;
    }

    public static class CustomizedSTSAndETS{
        public long sts;
        public long ets;

        public CustomizedSTSAndETS(long sts, long ets) {
            this.sts = sts;
            this.ets = ets;
        }
    }
}
