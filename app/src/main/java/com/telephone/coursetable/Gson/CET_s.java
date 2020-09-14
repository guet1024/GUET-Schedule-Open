package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * 等级考试成绩
 * @clear
 */
public class CET_s {
    private boolean success;
    private long total;
    private List<CET> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<CET> getData() {
        return data;
    }
}
