package com.telephone.coursetable.Database;

/**
 * @clear
 */
public class ShowTableNode {
    public String courseno;
    public String cname;
    public String name;
    public String croomno;
    public long weekday;
    public String time;

    // edit by Telephone 2020/11/21 14:10, for course card
    public long start_week;
    public long end_week;
    public String tno;
    // edit by Telephone 2020/11/23 08:44, for course card
    public String sys_comm;
    public String my_comm;
    // edit by Telephone 2020/11/21 14:10, for course card
    public double grade_point;
    public String ctype;
    public String examt;

    // edit by Telephone 2020/11/23 08:44, for course card
    public boolean customized;
    // edit by Telephone 2020/11/24 00:56, for updating my_comment in edit course activity
    public boolean oddweek;
}
