package com.telephone.coursetable.Gson;

//成绩查询

import java.util.List;

public class ScoreQueryS {
    private boolean success;
    private long total;
    private List<ScoreQuery_Data> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<ScoreQuery_Data> getData() {
        return data;
    }
}
