package com.telephone.coursetable.Gson;

//有效学分

public class ValidScoreQuery_Data {
    private String name;
    private String cname;
    private String engname;
    private String engcj;
    private String tname;//	"通识必修" 课程性质 √√√√√√√√√
    private String stid;
    private String term;
    private String courseid;
    private double planxf;//	0 计划中的学分（换成勾和叉） √√√√√√√√√
    private double credithour;//	3.5 已得学分（换成勾和叉） √√√√√√√√√
    private String coursetype;
    private double lvl;
    private String sterm;//	"2019-2020_1" 选修学期 √√√√√√√√√
    private String courseno;//	"1911082" 课号 √√√√√√√√√
    private String scid;//	"BG0000006X0" 课程代号 √√√√√√√√√
    private String scname;//	"大学英语3" 课程名称 √√√√√√√√√
    private long score;//	73 成绩 √√√√√√√√√
    private String zpxs;
    private double xf;//	3.5 课程学分 √√√√√√√√√
    private String stp;//	"BG" 课程性质代码 √√√√√√√√√

    public String getName() {
        return name;
    }

    public String getCname() {
        return cname;
    }

    public String getEngname() {
        return engname;
    }

    public String getEngcj() {
        return engcj;
    }

    public String getTname() {
        return tname;
    }

    public String getStid() {
        return stid;
    }

    public String getTerm() {
        return term;
    }

    public String getCourseid() {
        return courseid;
    }

    public double getPlanxf() {
        return planxf;
    }

    public double getCredithour() {
        return credithour;
    }

    public String getCoursetype() {
        return coursetype;
    }

    public double getLvl() {
        return lvl;
    }

    public String getSterm() {
        return sterm;
    }

    public String getCourseno() {
        return courseno;
    }

    public String getScid() {
        return scid;
    }

    public String getScname() {
        return scname;
    }

    public long getScore() {
        return score;
    }

    public String getZpxs() {
        return zpxs;
    }

    public double getXf() {
        return xf;
    }

    public String getStp() {
        return stp;
    }
}
