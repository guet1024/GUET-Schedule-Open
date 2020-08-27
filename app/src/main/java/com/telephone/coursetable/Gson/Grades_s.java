package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * 成绩查询
 * @clear
 */
public class Grades_s {
    private boolean success;
    private long total;
    private List<Grades> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<Grades> getData() {
        return data;
    }
}
