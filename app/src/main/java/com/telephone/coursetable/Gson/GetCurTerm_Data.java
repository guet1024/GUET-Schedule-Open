package com.telephone.coursetable.Gson;

//获取当前学期


public class GetCurTerm_Data {
    private String term;//	"2019-2020_2"
    private String startdate;//	"2020/2/24 0:00:00"
    private String enddate;//	"2020/7/17 0:00:00"
    private String weeknum;//	"20"
    private String termname;//	"2019-2020下学期"
    private String schoolyear;//	"2019"
    private String comm;//	null

    public String getTerm() {
        return term;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public String getWeeknum() {
        return weeknum;
    }

    public String getTermname() {
        return termname;
    }

    public String getSchoolyear() {
        return schoolyear;
    }

    public String getComm() {
        return comm;
    }
}
