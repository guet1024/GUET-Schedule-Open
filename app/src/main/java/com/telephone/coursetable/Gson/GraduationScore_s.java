package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * 有效学分
 * @clear
 */
public class GraduationScore_s {
    private boolean success;
    private long total;
    private List<GraduationScore> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<GraduationScore> getData() {
        return data;
    }
}
