package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"sid", "room", "date", "time"})
public class CustomizedExam {
    @NonNull
    public String sid;
    @NonNull
    public String room;
    @NonNull
    public String date;
    @NonNull
    public String time;
    public long sts;
    public long ets;

    public CustomizedExam(@NonNull String sid, @NonNull String room, @NonNull String date, @NonNull String time, long sts, long ets) {
        this.sid = sid;
        this.room = room;
        this.date = date;
        this.time = time;
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
