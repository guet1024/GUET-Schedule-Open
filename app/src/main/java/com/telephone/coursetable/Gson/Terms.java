package com.telephone.coursetable.Gson;

import java.util.List;

public class Terms {
    private boolean success;
    private long total;
    private List<Term> data;


    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<Term> getData() {
        return data;
    }
}
