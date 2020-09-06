package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @clear
 */
@Entity(primaryKeys = {"examdate", "kssj", "croomno"})
public class ExamInfo {
    @NonNull
    public String croomno;//	"02204Y"    教室
    public String croomname;
    public String tch;
    public String tch1;
    public String tch2;
    public String js;
    public String js1;
    public String js2;
    public long roomrs;
    public String term;//	"2019-2020_2"   考试学期
    public String grade;
    public String dpt;
    public String spno;
    public String spname;
    public String courseid;
    public String courseno;//	"1920736"   课号
    public String labno;
    public String labname;
    public String dptno;
    public String teacherno;
    public String name;//	"陈宏"    任课教师
    public String xf;
    public String cname;//	"计算机组成原理B"  课程名称
    public String sctcnt;
    public String stucnt;
    public String scoretype;
    public String examt;
    public String kctype;
    public String typeno;
    @NonNull
    public String examdate;//	"2020-07-15"    日期
    public String examtime;
    public long examstate;
    public String exammode;
    public String xm;
    public String refertime;
    public long zc;
    public long xq;
    public String ksjc;
    public String jsjc;
    public long bkzt;
    @NonNull
    public String kssj;//	"14:00-16:00"   考试时间
    public String comm;
    public String rooms;
    public String lsh;
    public long zone;
    public String checked1;
    public String postdate;
    public String operator;
    public long sts;
    public long ets;

    public ExamInfo(@NonNull String croomno, String croomname, String tch, String tch1, String tch2, String js, String js1, String js2, long roomrs, String term, String grade, String dpt, String spno, String spname, String courseid, String courseno, String labno, String labname, String dptno, String teacherno, String name, String xf, String cname, String sctcnt, String stucnt, String scoretype, String examt, String kctype, String typeno, @NonNull String examdate, String examtime, long examstate, String exammode, String xm, String refertime, long zc, long xq, String ksjc, String jsjc, long bkzt, @NonNull String kssj, String comm, String rooms, String lsh, long zone, String checked1, String postdate, String operator) {
        this.croomno = croomno;
        this.croomname = croomname;
        this.tch = tch;
        this.tch1 = tch1;
        this.tch2 = tch2;
        this.js = js;
        this.js1 = js1;
        this.js2 = js2;
        this.roomrs = roomrs;
        this.term = term;
        this.grade = grade;
        this.dpt = dpt;
        this.spno = spno;
        this.spname = spname;
        this.courseid = courseid;
        this.courseno = courseno;
        this.labno = labno;
        this.labname = labname;
        this.dptno = dptno;
        this.teacherno = teacherno;
        this.name = name;
        this.xf = xf;
        this.cname = cname;
        this.sctcnt = sctcnt;
        this.stucnt = stucnt;
        this.scoretype = scoretype;
        this.examt = examt;
        this.kctype = kctype;
        this.typeno = typeno;
        this.examdate = examdate;
        this.examtime = examtime;
        this.examstate = examstate;
        this.exammode = exammode;
        this.xm = xm;
        this.refertime = refertime;
        this.zc = zc;
        this.xq = xq;
        this.ksjc = ksjc;
        this.jsjc = jsjc;
        this.bkzt = bkzt;
        this.kssj = kssj;
        this.comm = comm;
        this.rooms = rooms;
        this.lsh = lsh;
        this.zone = zone;
        this.checked1 = checked1;
        this.postdate = postdate;
        this.operator = operator;
        this.sts = getSTS(examdate, kssj);
        this.ets = getETS(examdate, kssj);
    }

    private long getSTS(String date, String time){
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8);
        LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        String start_time = time.substring(0, time.indexOf("-"));
        String hour = start_time.substring(0, start_time.indexOf(":"));
        String min = start_time.substring(start_time.indexOf(":") + 1);
        LocalTime localTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(min));
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return Timestamp.valueOf(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getTime();
    }

    private long getETS(String date, String time){
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8);
        LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        String end_time = time.substring(time.indexOf("-") + 1);
        String hour = end_time.substring(0, end_time.indexOf(":"));
        String min = end_time.substring(end_time.indexOf(":") + 1);
        LocalTime localTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(min));
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return Timestamp.valueOf(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getTime();
//        return 1599461072000L;
    }
}
