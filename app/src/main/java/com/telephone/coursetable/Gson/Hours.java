package com.telephone.coursetable.Gson;

import java.util.List;

public class Hours {
    private boolean success;
    private long total;
    private List<Hour> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<Hour> getData() {
        return data;
    }
}
