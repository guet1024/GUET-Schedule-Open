package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * 已选课程
 * @clear
 */
public class SelectedCourses_s {
    private boolean success;
    private long total;
    private List<SelectedCourses> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<SelectedCourses> getData() {
        return data;
    }
}
