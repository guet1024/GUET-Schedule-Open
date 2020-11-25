package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * @clear
 */
@Entity(primaryKeys = {"username", "courseno"})
public class ClassInfo {
    @NonNull
    public String username;
    @NonNull
    public String courseno;

    public String ctype;
    public String tname; //type name
    public String examt;
    public String dptname;
    public String dptno;
    public String spname;
    public String spno;
    public String grade;
    public String cname;
    public String teacherno;
    public String name; //teacher name
    public String courseid; //such as BG0000022X0, the course code of each course
    public long maxcnt;
    public double xf;
    public double llxs;
    public double syxs;
    public double sjxs;
    public double qtxs;
    public long sctcnt;

    public int custom_ref;

    public ClassInfo(@NonNull String username, @NonNull String courseno, String ctype, String tname, String examt, String dptname, String dptno, String spname, String spno, String grade, String cname, String teacherno, String name, String courseid, long maxcnt, double xf, double llxs, double syxs, double sjxs, double qtxs, long sctcnt, int custom_ref) {
        this.username = username;
        this.courseno = courseno;
        this.ctype = ctype;
        this.tname = tname;
        this.examt = examt;
        this.dptname = dptname;
        this.dptno = dptno;
        this.spname = spname;
        this.spno = spno;
        this.grade = grade;
        this.cname = cname;
        this.teacherno = teacherno;
        this.name = name;
        this.courseid = courseid;
        this.maxcnt = maxcnt;
        this.xf = xf;
        this.llxs = llxs;
        this.syxs = syxs;
        this.sjxs = sjxs;
        this.qtxs = qtxs;
        this.sctcnt = sctcnt;
        this.custom_ref = custom_ref;
    }
}
