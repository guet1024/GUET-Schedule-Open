package com.telephone.coursetable.Gson;

import java.util.List;

/**
 * @clear
 */
public class TermInfo_s {
    private boolean success;
    private long total;
    private List<TermInfo> data;


    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<TermInfo> getData() {
        return data;
    }
}
