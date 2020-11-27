package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * @clear
 */
@Entity(primaryKeys = {"stid"})
public class PersonInfo {
    @NonNull
    public String stid; //学号//

    public long grade; //年级//
    public String classno; //班级//
    public String spno; //专业代码//
    public String name; //姓名//
    public String name1; //曾用名/*
    public String engname; //英文名/*
    public String sex; //性别/*
    public String pass; ///*
    public String degree; ///*
    public String direction; ///*
    public String changetype; //状态//
    public String secspno; //第二专业代码/*
    public String classtype; //班级类型/*
    public String idcard; //身份证号码//
    public String stype; //学生类型//
    public String xjzt; ///*
    public String changestate; //变更状态/*
    public String lqtype; ///*
    public String zsjj; ///*
    public String nation; //民族//
    public String political; //政治面貌//
    public String nativeplace; //籍贯//
    public String birthday; //生日/*
    public String enrolldate; //入学日期//
    public String leavedate; //离校日期//
    public String dossiercode; //文件代码/*
    public String hostel; //所在宿舍/*
    public String hostelphone; //本人联系电话/*
    public String postcode; //邮政编码/*
    public String address; //家庭地址/*
    public String phoneno; //家庭联系电话/*
    public String familyheader; //家庭联系人姓名/*
    public double total; //高考总分//
    public double chinese; //高考英语（或语文）//
    public double maths; //高考数学//
    public double english; //高考语文（或英语）//
    public double addscore1; //高考综合//
    public double addscore2; //高考其他//
    public String comment; //备注//
    public String testnum; //高考考生号//
    public String fmxm1; //监护人1姓名/*
    public String fmzjlx1; //监护人1证件类型/*
    public String fmzjhm1; //监护人1证件编码/*
    public String fmxm2; //监护人2姓名/*
    public String fmzjlx2; //监护人2证件类型/*
    public String fmzjhm2; //监护人2证件编码/*
    public String ds; //生源地市/*
    public String xq; ///*
    public String rxfs; ///*
    public String oldno; ///*
    public String dptno; //学院//
    public String dptname; //学院名称//
    public String spname; //专业//

    public PersonInfo(@NonNull String stid, long grade, String classno, String spno, String name, String name1, String engname, String sex, String pass, String degree, String direction, String changetype, String secspno, String classtype, String idcard, String stype, String xjzt, String changestate, String lqtype, String zsjj, String nation, String political, String nativeplace, String birthday, String enrolldate, String leavedate, String dossiercode, String hostel, String hostelphone, String postcode, String address, String phoneno, String familyheader, double total, double chinese, double maths, double english, double addscore1, double addscore2, String comment, String testnum, String fmxm1, String fmzjlx1, String fmzjhm1, String fmxm2, String fmzjlx2, String fmzjhm2, String ds, String xq, String rxfs, String oldno, String dptno, String dptname, String spname) {
        this.stid = stid;
        this.grade = grade;
        this.classno = classno;
        this.spno = spno;
        this.name = name;
        this.name1 = name1;
        this.engname = engname;
        this.sex = sex;
        this.pass = pass;
        this.degree = degree;
        this.direction = direction;
        this.changetype = changetype;
        this.secspno = secspno;
        this.classtype = classtype;
        this.idcard = idcard;
        this.stype = stype;
        this.xjzt = xjzt;
        this.changestate = changestate;
        this.lqtype = lqtype;
        this.zsjj = zsjj;
        this.nation = nation;
        this.political = political;
        this.nativeplace = nativeplace;
        this.birthday = birthday;
        this.enrolldate = enrolldate;
        this.leavedate = leavedate;
        this.dossiercode = dossiercode;
        this.hostel = hostel;
        this.hostelphone = hostelphone;
        this.postcode = postcode;
        this.address = address;
        this.phoneno = phoneno;
        this.familyheader = familyheader;
        this.total = total;
        this.chinese = chinese;
        this.maths = maths;
        this.english = english;
        this.addscore1 = addscore1;
        this.addscore2 = addscore2;
        this.comment = comment;
        this.testnum = testnum;
        this.fmxm1 = fmxm1;
        this.fmzjlx1 = fmzjlx1;
        this.fmzjhm1 = fmzjhm1;
        this.fmxm2 = fmxm2;
        this.fmzjlx2 = fmzjlx2;
        this.fmzjhm2 = fmzjhm2;
        this.ds = ds;
        this.xq = xq;
        this.rxfs = rxfs;
        this.oldno = oldno;
        this.dptno = dptno;
        this.dptname = dptname;
        this.spname = spname;
    }
}
