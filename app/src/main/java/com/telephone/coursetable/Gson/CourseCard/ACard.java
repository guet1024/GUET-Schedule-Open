package com.telephone.coursetable.Gson.CourseCard;

public class ACard {
    private String cno;
    private String cname;
    private int start_week;
    private int end_week;
    private String tname;
    private String tno;
    private String croom;
    private String comment;
    private double grade_point;
    private String ctype;
    private String examt;

    public ACard(String cno, String cname, int start_week, int end_week, String tname, String tno, String croom, String comment, double grade_point, String ctype, String examt) {
        this.cno = cno;
        this.cname = cname;
        this.start_week = start_week;
        this.end_week = end_week;
        this.tname = tname;
        this.tno = tno;
        this.croom = croom;
        this.comment = comment;
        this.grade_point = grade_point;
        this.ctype = ctype;
        this.examt = examt;
    }

    public String getCno() {
        return cno;
    }

    public String getCname() {
        return cname;
    }

    public int getStart_week() {
        return start_week;
    }

    public int getEnd_week() {
        return end_week;
    }

    public String getTname() {
        return tname;
    }

    public String getTno() {
        return tno;
    }

    public String getCroom() {
        return croom;
    }

    public String getComment() {
        return comment;
    }

    public double getGrade_point() {
        return grade_point;
    }

    public String getCtype() {
        return ctype;
    }

    public String getExamt() {
        return examt;
    }
}
