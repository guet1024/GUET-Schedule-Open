package com.telephone.coursetable.Gson;

//毕业学位

import java.util.List;

public class GraduationScoreS {
    private boolean success;
    private long total;
    private List<GraduationScore_Data> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<GraduationScore_Data> getData() {
        return data;
    }
}
