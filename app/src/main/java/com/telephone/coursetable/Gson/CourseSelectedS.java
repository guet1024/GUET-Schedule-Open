package com.telephone.coursetable.Gson;

import java.util.List;

//已选课程
public class CourseSelectedS {

    private boolean success;
    private long total;
    private List<CourseSelected_Data> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<CourseSelected_Data> getData() {
        return data;
    }
}
