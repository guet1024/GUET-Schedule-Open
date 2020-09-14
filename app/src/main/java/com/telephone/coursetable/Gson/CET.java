package com.telephone.coursetable.Gson;

/**
 * 等级考试成绩
 * @clear
 */
public class CET {
    private String name;
    private String sex;
    private String postdate;
    private String dptno;
    private String dptname;
    private String spno;
    private String spname;
    private String grade;
    private String bj;
    private String term;//  学期代码
    private String stid;
    private String code;//  等级考试名称
    private double score;// 折算成绩
    private double stage;// 等级考试成绩
    private String card;//  证书编号
    private String operator;

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getPostdate() {
        return postdate;
    }

    public String getDptno() {
        return dptno;
    }

    public String getDptname() {
        return dptname;
    }

    public String getSpno() {
        return spno;
    }

    public String getSpname() {
        return spname;
    }

    public String getGrade() {
        return grade;
    }

    public String getBj() {
        return bj;
    }

    public String getTerm() {
        return term;
    }

    public String getStid() {
        return stid;
    }

    public String getCode() {
        return code;
    }

    public double getScore() {
        return score;
    }

    public double getStage() {
        return stage;
    }

    public String getCard() {
        return card;
    }

    public String getOperator() {
        return operator;
    }
}
