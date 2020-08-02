package com.telephone.coursetable.Gson;

//有效学分

import java.util.List;

public class ValidScoreQueryS {
    private boolean success;
    private long total;
    private List<ValidScoreQuery_Data> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<ValidScoreQuery_Data> getData() {
        return data;
    }
}
