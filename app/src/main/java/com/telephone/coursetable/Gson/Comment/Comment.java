package com.telephone.coursetable.Gson.Comment;

public class Comment {
    private String cno; // "2013698",
    private String tno; // "2365"
    private String cname; // "大学物理"
    private String sno; // "1823695588",
    private long idx; // 5,
    private String dt; // "2020-11-19 20:02:34",
    private String cmt; // "测试网络评论功能"
    private String name; // "用户"
    private long ts; // 1606574174, WARNING: this is second
    private String mask; // "X同学"

    public String getCno() {
        return cno;
    }

    public String getTno() {
        return tno;
    }

    public String getCname() {
        return cname;
    }

    public String getSno() {
        return sno;
    }

    public long getIdx() {
        return idx;
    }

    public String getDt() {
        return dt;
    }

    public String getCmt() {
        return cmt;
    }

    public String getName() {
        return name;
    }

    public long getTs() {
        return ts;
    }

    public String getMask() {
        return mask;
    }

    public long getTimeStamp(){
        return getTs() * 1000L;
    }
}
