package com.telephone.coursetable.Gson.CourseCard;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class CourseCardData implements Serializable {
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

    /**
     * thank the author of: https://www.cnblogs.com/zhangfengshi/p/11122335.html
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public CourseCardData deepClone() throws IOException, ClassNotFoundException {
        //写入对象
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(this);
        //读取对象
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (CourseCardData) oi.readObject();
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

    public String getTerm() {
        return term;
    }

    public String getTime_id() {
        return time_id;
    }

    public void setCards(@NonNull List<ACard> cards) {
        this.cards = cards;
    }
}
