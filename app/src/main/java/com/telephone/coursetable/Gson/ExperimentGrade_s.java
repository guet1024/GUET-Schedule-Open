package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * 实验成绩
 * @clear
 */
public class ExperimentGrade_s {
    private boolean success;
    private long total;
    private List<ExperimentGrade> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<ExperimentGrade> getData() {
        return data;
    }
}
