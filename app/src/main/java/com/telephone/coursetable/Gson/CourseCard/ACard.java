package com.telephone.coursetable.Gson.CourseCard;

import java.io.Serializable;

public class ACard implements Serializable {
    private String cno;
    private String cname;
    private int start_week;
    private int end_week;
    private String tname;
    private String tno;
    private String croom;
    private double grade_point;
    private String ctype;
    private String examt;
    private String sys_comm;
    private String my_comm;
    private boolean customized;

    public ACard(String cno, String cname, int start_week, int end_week, String tname, String tno, String croom, double grade_point, String ctype, String examt, String sys_comm, String my_comm, boolean customized) {
        this.cno = cno;
        this.cname = cname;
        this.start_week = start_week;
        this.end_week = end_week;
        this.tname = tname;
        this.tno = tno;
        this.croom = croom;
        this.grade_point = grade_point;
        this.ctype = ctype;
        this.examt = examt;
        this.sys_comm = sys_comm;
        this.my_comm = my_comm;
        this.customized = customized;
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

    public double getGrade_point() {
        return grade_point;
    }

    public String getCtype() {
        return ctype;
    }

    public String getExamt() {
        return examt;
    }

    public String getSys_comm() {
        return sys_comm;
    }

    public String getMy_comm() {
        return my_comm;
    }

    public boolean isCustomized() {
        return customized;
    }
}
