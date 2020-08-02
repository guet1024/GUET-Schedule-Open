package com.telephone.coursetable.Gson;

//已选课程
public class CourseSelectedS {

    private boolean success;
    private long total;
    private CourseSelected_Data csd;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public CourseSelected_Data getCsd() {
        return csd;
    }

}
