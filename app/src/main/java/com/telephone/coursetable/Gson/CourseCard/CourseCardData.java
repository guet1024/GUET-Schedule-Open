package com.telephone.coursetable.Gson.CourseCard;

import androidx.annotation.NonNull;

import java.util.List;

public class CourseCardData {
    private String termname;
    private int week;
    private int weekday;
    private String time_des;
    @NonNull
    private List<ACard> cards;

    // this is for add and remove course
    private String term;
    private String time_id;

    public CourseCardData(String termname, int week, int weekday, String time_des, @NonNull List<ACard> cards, String term, String time_id) {
        this.termname = termname;
        this.week = week;
        this.weekday = weekday;
        this.time_des = time_des;
        this.cards = cards;
        this.term = term;
        this.time_id = time_id;
    }

    public String getTermname() {
        return termname;
    }

    public int getWeek() {
        return week;
    }

    public int getWeekday() {
        return weekday;
    }

    public String getTime_des() {
        return time_des;
    }

    @NonNull
    public List<ACard> getCards() {
        return cards;
    }
}
