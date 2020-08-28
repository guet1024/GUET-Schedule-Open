package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @clear
 */
@Entity
public class Grades {
    public String dptno;
    public String dptname;
    public String spno;
    public String spname;
    public String bj;
    public long grade;
    public String stid;
    public String name;
    public String term;    //"2019-2020_2" //选修学期
    public String courseid;
    @NonNull
    @PrimaryKey
    public String courseno;    //"1920699" //课号
    public String cname;   //"单片机原理与应用" //课程名称//**
    public String courselevel;
    public long score;
    public String zpxs;    //"81" //总成绩//**
    public String kctype;
    public String typeno;
    public String cid;
    public String cno;
    public double sycj;  //89 //实验成绩//**
    public double qzcj;  //0 //期中成绩
    public double pscj;  //89 //平时成绩//**
    public double khcj;  //75 //考核成绩//**
    public double zpcj;  //81 //总评成绩
    public String kslb;
    public String cjlb;
    public double kssj;
    public double xf;  //2.5 //课程学分
    public String xslb;
    public String tname1;
    public String stage;
    public String examt;
    public double xs;
    public double cjlx;
    public double chk;
    public String comm;

    public Grades(String dptno, String dptname, String spno, String spname, String bj, long grade, String stid, String name, String term, String courseid, @NonNull String courseno, String cname, String courselevel, long score, String zpxs, String kctype, String typeno, String cid, String cno, double sycj, double qzcj, double pscj, double khcj, double zpcj, String kslb, String cjlb, double kssj, double xf, String xslb, String tname1, String stage, String examt, double xs, double cjlx, double chk, String comm) {
        this.dptno = dptno;
        this.dptname = dptname;
        this.spno = spno;
        this.spname = spname;
        this.bj = bj;
        this.grade = grade;
        this.stid = stid;
        this.name = name;
        this.term = term;
        this.courseid = courseid;
        this.courseno = courseno;
        this.cname = cname;
        this.courselevel = courselevel;
        this.score = score;
        this.zpxs = zpxs;
        this.kctype = kctype;
        this.typeno = typeno;
        this.cid = cid;
        this.cno = cno;
        this.sycj = sycj;
        this.qzcj = qzcj;
        this.pscj = pscj;
        this.khcj = khcj;
        this.zpcj = zpcj;
        this.kslb = kslb;
        this.cjlb = cjlb;
        this.kssj = kssj;
        this.xf = xf;
        this.xslb = xslb;
        this.tname1 = tname1;
        this.stage = stage;
        this.examt = examt;
        this.xs = xs;
        this.cjlx = cjlx;
        this.chk = chk;
        this.comm = comm;
    }
}
