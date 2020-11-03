package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * @clear
 */
@Entity(primaryKeys = {"xh", "bno"})
public class LAB {
    public String term; // "2020-2021_1",
    public String labid; // "018124732",
    public String itemname; // "简单LAN设计与以太网帧分析",
    public String courseid; // "BT0300181X1",
    public String cname; // "计算机网络（外文教材）",
    public String spno; // "080640S",
    public String spname; // "物联网工程",
    public String grade; // "2018",
    public String teacherno; // "030409",
    public String name; // "王虎寅",
    public String srname; // "网络实验室",
    public String srdd; // "花江校区5教5404",
    @NonNull
    public String xh; // "0181247322",
    public long bno; // 2,
    public long persons; // 65,
    public long zc; // 7,
    public long xq; // 7,
    public long jc; // 3,
    public long jc1; // 4,
    public String assistantno; // "030409 ",
    public String teachers; // ",20003007 ",
    public String comm; // "",
    public String courseno; // "",
    public long stusct; // 51,
    public String srid; // "039201"

    public String lab_name;
    public String full_lab_name;
    public String unique_serial_number;

    public LAB(String term, String labid, String itemname, String courseid, String cname, String spno, String spname, String grade, String teacherno, String name, String srname, String srdd, String xh, long bno, long persons, long zc, long xq, long jc, long jc1, String assistantno, String teachers, String comm, String courseno, long stusct, String srid) {
        this.term = term;
        this.labid = labid;
        this.itemname = itemname;
        this.courseid = courseid;
        this.cname = cname;
        this.spno = spno;
        this.spname = spname;
        this.grade = grade;
        this.teacherno = teacherno;
        this.name = name;
        this.srname = srname;
        this.srdd = srdd;
        this.xh = xh;
        this.bno = bno;
        this.persons = persons;
        this.zc = zc;
        this.xq = xq;
        this.jc = jc;
        this.jc1 = jc1;
        this.assistantno = assistantno;
        this.teachers = teachers;
        this.comm = comm;
        this.courseno = courseno;
        this.stusct = stusct;
        this.srid = srid;

        this.lab_name = getLabName(this.cname);
        this.full_lab_name = getFullLabName(this.cname, this.itemname);
        this.unique_serial_number = getUniqueSerialNumber(this.xh, this.bno+"");
    }

    public static String getLabName(String cname){return cname + "实验";}
    public static String getFullLabName(String cname, String item_name){return cname + "实验" + ": " + item_name;}
    public static String getUniqueSerialNumber(String project_id, String batch_no){return project_id + batch_no;}
}
