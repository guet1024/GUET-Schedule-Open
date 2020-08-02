package com.telephone.coursetable.Gson;

//成绩查询

public class ScoreQueryS {
    private boolean success;
    private long total;
    private ScoreQuery_Data sqd;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public ScoreQuery_Data getSqd() {
        return sqd;
    }
}
