package com.telephone.coursetable;

import java.util.List;

public class Table {
    private boolean success;
    private long total;
    private List<TableNode> data;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public List<TableNode> getData() {
        return data;
    }
}
