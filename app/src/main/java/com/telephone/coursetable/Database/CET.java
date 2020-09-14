package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @clear
 */
@Entity
public class CET {
    public String name;
    public String sex;
    public String postdate;
    public String dptno;
    public String dptname;
    public String spno;
    public String spname;
    public String grade;
    public String bj;
    public String term;//  学期代码
    public String stid;
    public String code;//  等级考试名称
    public double score;// 折算成绩
    public double stage;// 等级考试成绩
    @NonNull @PrimaryKey
    public String card;//  证书编号
    public String operator;

    public CET(String name, String sex, String postdate, String dptno, String dptname, String spno, String spname, String grade, String bj, String term, String stid, String code, double score, double stage, @NonNull String card, String operator) {
        this.name = name;
        this.sex = sex;
        this.postdate = postdate;
        this.dptno = dptno;
        this.dptname = dptname;
        this.spno = spno;
        this.spname = spname;
        this.grade = grade;
        this.bj = bj;
        this.term = term;
        this.stid = stid;
        this.code = code;
        this.score = score;
        this.stage = stage;
        this.card = card;
        this.operator = operator;
    }
}
