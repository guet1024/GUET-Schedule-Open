package com.telephone.coursetable.Gson.TextBookEvaluation;

import static com.telephone.coursetable.Gson.TeachersEvaluation.Detail.encode;

public class Detail {
    private long a; // 95
    private long b; // 85
    private long c; // 75
    private String courseid; // "BT0300219X0"
    private long d; // 65
    private String dja; // "优"
    private String djb; // "良"
    private String djc; // "中"
    private String djd; // "差"
    private String l1; // ""
    private String l2; // ""
    private long lsh; // 7947
    private long pjno; // 374
    private String pjzb; // "教材适用"
    private double qz; // 0.4
    private long score; // 85
    private String term; // "2019-2020_1"
    private String type; // null
    private String zbnr; // "是否便于学生自学，有利于学生能力的培养，教材的深度、份量是否适当，例题和练习题是否合适等。"

    public Detail(long a, long b, long c, String courseid, long d, String dja, String djb, String djc, String djd, String l1, String l2, long lsh, long pjno, String pjzb, double qz, long score, String term, String type, String zbnr) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.courseid = courseid;
        this.d = d;
        this.dja = dja;
        this.djb = djb;
        this.djc = djc;
        this.djd = djd;
        this.l1 = l1;
        this.l2 = l2;
        this.lsh = lsh;
        this.pjno = pjno;
        this.pjzb = pjzb;
        this.qz = qz;
        this.score = score;
        this.term = term;
        this.type = type;
        this.zbnr = zbnr;
    }

    public Detail encode_myself(){
        dja = encode(dja);
        djb = encode(djb);
        djc = encode(djc);
        djd = encode(djd);
        courseid = encode(courseid);
        l1 = encode(l1);
        l2 = encode(l2);
        pjzb = encode(pjzb);
        term = encode(term);
        type = encode(type);
        zbnr = encode(zbnr);
        return this;
    }

    public long getA() {
        return a;
    }

    public long getB() {
        return b;
    }

    public long getC() {
        return c;
    }

    public String getCourseid() {
        return courseid;
    }

    public long getD() {
        return d;
    }

    public String getDja() {
        return dja;
    }

    public String getDjb() {
        return djb;
    }

    public String getDjc() {
        return djc;
    }

    public String getDjd() {
        return djd;
    }

    public String getL1() {
        return l1;
    }

    public String getL2() {
        return l2;
    }

    public long getLsh() {
        return lsh;
    }

    public long getPjno() {
        return pjno;
    }

    public String getPjzb() {
        return pjzb;
    }

    public double getQz() {
        return qz;
    }

    public long getScore() {
        return score;
    }

    public String getTerm() {
        return term;
    }

    public String getType() {
        return type;
    }

    public String getZbnr() {
        return zbnr;
    }
}
