package com.telephone.coursetable.Clock;

import com.telephone.coursetable.Database.ShowTableNode;

import java.util.List;

public class FindClassOfCurrentOrNextTimeRes {
    public boolean isNow;
    public List<ShowTableNode> list;
    public String start = null;

    FindClassOfCurrentOrNextTimeRes(List<ShowTableNode> list){
        isNow = false;
        this.list = list;
    }

    FindClassOfCurrentOrNextTimeRes(List<ShowTableNode> list, boolean isNow){
        this.isNow = isNow;
        this.list = list;
    }

    FindClassOfCurrentOrNextTimeRes(List<ShowTableNode> list, boolean isNow, String start){
        this.isNow = isNow;
        this.list = list;
        this.start = start;
    }
}
