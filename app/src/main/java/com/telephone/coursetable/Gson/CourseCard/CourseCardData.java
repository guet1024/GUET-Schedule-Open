package com.telephone.coursetable.Gson.CourseCard;

import java.util.List;

public class CourseCardData {
    private String termname;
    private int week;
    private int weekday;
    private String time_des;
    private List<ACard> cards;

    public CourseCardData(String termname, int week, int weekday, String time_des, List<ACard> cards) {
        this.termname = termname;
        this.week = week;
        this.weekday = weekday;
        this.time_des = time_des;
        this.cards = cards;
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

    public List<ACard> getCards() {
        return cards;
    }
}
