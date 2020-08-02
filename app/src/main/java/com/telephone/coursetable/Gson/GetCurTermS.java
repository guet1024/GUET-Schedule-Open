package com.telephone.coursetable.Gson;

//获取当前学期

public class GetCurTermS {
    private boolean success;
    private long total;

    public boolean isSuccess() {
        return success;
    }

    public long getTotal() {
        return total;
    }

    public GetCurTerm_Data getGct() {
        return gct;
    }

    private GetCurTerm_Data gct;

}
