package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @clear
 */
@Entity
public class GraduationScore {
    public String name;
    public String cname;    //计划课程名称
    public String engname;
    public String engcj;
    public String tname;
    public String stid;
    public String term;
    @NonNull
    @PrimaryKey
    public String courseid;     //计划课程代码，是计划中的课程代码，不是课程自带的
    public double planxf;
    public double credithour;   //计划学分
    public String coursetype;
    public double lvl;
    public String sterm;    //选修学期
    public String courseno; //选修课号
    public String scid;         //课程自带的课程代码
    public String scname;
    public long score;
    public String zpxs;     //成绩
    public double xf;
    public String stp;

    public GraduationScore(String name, String cname, String engname, String engcj, String tname, String stid, String term, String courseid, double planxf, double credithour, String coursetype, double lvl, String sterm, String courseno, String scid, String scname, long score, String zpxs, double xf, String stp) {
        this.name = name;
        this.cname = cname;
        this.engname = engname;
        this.engcj = engcj;
        this.tname = tname;
        this.stid = stid;
        this.term = term;
        this.courseid = courseid;
        this.planxf = planxf;
        this.credithour = credithour;
        this.coursetype = coursetype;
        this.lvl = lvl;
        this.sterm = sterm;
        this.courseno = courseno;
        this.scid = scid;
        this.scname = scname;
        this.score = score;
        this.zpxs = zpxs;
        this.xf = xf;
        this.stp = stp;
    }
}
