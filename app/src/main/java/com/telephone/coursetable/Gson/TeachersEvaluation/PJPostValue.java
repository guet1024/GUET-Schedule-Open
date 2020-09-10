package com.telephone.coursetable.Gson.TeachersEvaluation;

public class PJPostValue {
    private String term;//: "2019-2020_2"
    private String courseno;//: "1920262"
    private String stid;//: "1800301130"
    private String cname;//: "马克思主义基本原理概论"
    private String name;//: "邱启照"
    private String teacherno;//: "080101"
    private String courseid;//: "BG0000022X0"
    private long lb;//: 1
    private long chk;//: 1
    private boolean can;// : true

    private String userid;
    private String bz;
    private long score;

    public PJPostValue(String term, String courseno, String stid, String cname, String name, String teacherno, String courseid, long lb, long chk, boolean can, String userid, String bz, long score) {
        this.term = term;
        this.courseno = courseno;
        this.stid = stid;
        this.cname = cname;
        this.name = name;
        this.teacherno = teacherno;
        this.courseid = courseid;
        this.lb = lb;
        this.chk = chk;
        this.can = can;
        this.userid = userid;
        this.bz = bz;
        this.score = score;
    }

    public String getTerm() {
        return term;
    }

    public String getCourseno() {
        return courseno;
    }

    public String getStid() {
        return stid;
    }

    public String getCname() {
        return cname;
    }

    public String getName() {
        return name;
    }

    public String getTeacherno() {
        return teacherno;
    }

    public String getCourseid() {
        return courseid;
    }

    public long getLb() {
        return lb;
    }

    public long getChk() {
        return chk;
    }

    public boolean isCan() {
        return can;
    }

    public String getUserid() {
        return userid;
    }

    public String getBz() {
        return bz;
    }

    public long getScore() {
        return score;
    }
}
