package com.telephone.coursetable.Gson;

import java.util.List;

//考试安排查询
public class ExamQueryS {
    private boolean success;
    private long total;
    private List<ExamQuery_Data> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<ExamQuery_Data> getData() {
        return data;
    }
}
