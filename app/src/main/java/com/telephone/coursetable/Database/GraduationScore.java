package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GraduationScore {
    public String name;
    public String cname;
    public String engname;
    public String engcj;
    public String tname;//	"通识必修" 课程性质 √√√√√√√√√
    public String stid;
    public String term;
    public String courseid;
    public double planxf;//	0 计划中的学分（换成勾和叉） √√√√√√√√√****
    public double credithour;//	3.5 已得学分（换成勾和叉） √√√√√√√√√****
    public String coursetype;
    public double lvl;
    public String sterm;//	"2019-2020_1" 选修学期 √√√√√√√√√
    public String courseno;//	"1911082" 课号 √√√√√√√√√
    @NonNull
    @PrimaryKey
    public String scid;//	"BG0000006X0" 课程代号 √√√√√√√√√
    public String scname;//	"大学英语3" 课程名称 √√√√√√√√√****
    public long score;//	73 成绩 √√√√√√√√√****
    public String zpxs;
    public double xf;//	3.5 课程学分 √√√√√√√√√****
    public String stp;//	"BG" 课程性质代码 √√√√√√√√√

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
