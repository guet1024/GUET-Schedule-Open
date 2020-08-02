package com.telephone.coursetable.Gson;

//实验成绩

import java.util.List;

public class ExperimentScoreQueryS {
    private boolean success;
    private long total;
    private List<ExperimentScoreQuery_Data> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<ExperimentScoreQuery_Data> getData() {
        return data;
    }
}
