package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * 考试安排查询
 * @clear
 */
public class ExamInfo_s {
    private boolean success;
    private long total;
    private List<ExamInfo> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<ExamInfo> getData() {
        return data;
    }
}
