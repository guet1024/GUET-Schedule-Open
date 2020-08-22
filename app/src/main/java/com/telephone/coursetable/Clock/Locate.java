package com.telephone.coursetable.Clock;

import com.telephone.coursetable.Database.TermInfo;

/**
 * @clear
 */
public class Locate {
    public TermInfo term;
    public long week;
    public long weekday;
    public long month;
    public long day;
    public String time; // this is time code
    public String time_description;

    public Locate(TermInfo term, long week, long weekday, long month, long day, String time, String time_description) {
        this.term = term;
        this.week = week;
        this.weekday = weekday;
        this.month = month;
        this.day = day;
        this.time = time;
        this.time_description = time_description;
    }

    @Override
    public String toString() {
        return "Locate{" +
                "term=" + term +
                ", week=" + week +
                ", weekday=" + weekday +
                ", month=" + month +
                ", day=" + day +
                ", time='" + time + '\'' +
                ", time_description='" + time_description + '\'' +
                '}';
    }
}
