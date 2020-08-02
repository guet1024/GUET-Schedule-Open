package com.telephone.coursetable.Gson;

//考试安排查询
public class ExamQueryS {
    private boolean success;
    private long total;
    private ExamQuery_Data eqd;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public ExamQuery_Data getEqd() {
        return eqd;
    }
}
