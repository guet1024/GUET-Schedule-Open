package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * 毕业学位评估
 * @clear
 */
public class GraduationDegreeEvaluation_s {
    private boolean success;
    private long total;
    private List<GraduationDegreeEvaluation> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<GraduationDegreeEvaluation> getData() {
        return data;
    }
}
