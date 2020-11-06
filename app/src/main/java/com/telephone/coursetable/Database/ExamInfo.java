package com.telephone.coursetable.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.LogMe.LogMe;
import com.telephone.coursetable.Merge.Merge;

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

    public ExamInfo(){}// this is for Room

    public ExamInfo(@NonNull String croomno, String croomname, String tch, String tch1, String tch2, String js, String js1, String js2, long roomrs, String term, String grade, String dpt, String spno, String spname, String courseid, String courseno, String labno, String labname, String dptno, String teacherno, String name, String xf, String cname, String sctcnt, String stucnt, String scoretype, String examt, String kctype, String typeno, @NonNull String examdate, String examtime, long examstate, String exammode, String xm, String refertime, long zc, long xq, String ksjc, String jsjc, long bkzt, @NonNull String kssj, String comm, String rooms, String lsh, long zone, String checked1, String postdate, String operator, TermInfoDao termInfoDao, Context c) {
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
        this.sts = getSTS(c, examdate, kssj, termInfoDao);
        this.ets = getETS(c, examdate, kssj, termInfoDao);
    }

    private long getSTS(Context c, String date, String time, TermInfoDao termInfoDao){
        LocalDate localDate;
        LocalTime localTime;
        try {
            localDate = getD(date).getLocalDate();
        }catch (Exception e){
            if (this.zc != 0 && this.xq != 0){
                localDate = termInfoDao.select(this.term).get(0).getDateOfWeekAndDay((int)this.zc, (int)this.xq);
            }else {
                localDate = termInfoDao.select(this.term).get(0).getDateOfTheLastDay();
            }
        }
        try {
            localTime = getT(time).getStartLocalTime();
        }catch (Exception e){
            LocalTime temp = null;
            if (this.ksjc != null && !this.ksjc.isEmpty()){
                temp = Clock.getStartTimeUsingDefaultConfig(c, this.ksjc);
            }
            if (temp == null){
                temp = LocalTime.of(23, 58);
            }
            localTime = temp;
        }
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return Timestamp.valueOf(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getTime();
    }

    private long getETS(Context c, String date, String time, TermInfoDao termInfoDao){
        LocalDate localDate;
        LocalTime localTime;
        try {
            localDate = getD(date).getLocalDate();
        }catch (Exception e){
            if (this.zc != 0 && this.xq != 0){
                localDate = termInfoDao.select(this.term).get(0).getDateOfWeekAndDay((int)this.zc, (int)this.xq);
            }else {
                localDate = termInfoDao.select(this.term).get(0).getDateOfTheLastDay();
            }
        }
        try {
            localTime = getT(time).getEndLocalTime();
        }catch (Exception e){
            LocalTime temp = null;
            if (this.ksjc != null && !this.ksjc.isEmpty()){
                temp = Clock.getEndTimeUsingDefaultConfig(c, this.ksjc);
            }
            if (temp == null){
                temp = LocalTime.of(23, 59);
            }
            localTime = temp;
        }
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return Timestamp.valueOf(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getTime();
    }

    private static class d{
        public int year;
        public int month;
        public int day;

        public d(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
        public LocalDate getLocalDate(){
            return LocalDate.of(year, month, day);
        }
    }

    private static class t{
        public int start_hour;
        public int start_minute;
        public int end_hour;
        public int end_minute;

        public t(int start_hour, int start_minute, int end_hour, int end_minute) {
            this.start_hour = start_hour;
            this.start_minute = start_minute;
            this.end_hour = end_hour;
            this.end_minute = end_minute;
        }
        public LocalTime getStartLocalTime(){
            return LocalTime.of(start_hour, start_minute);
        }
        public LocalTime getEndLocalTime(){
            return LocalTime.of(end_hour, end_minute);
        }
    }

    private d getD(String text){
        int index1 = text.indexOf("-", 0);
        int index2 = text.indexOf("-", index1 + 1);
        String s1 = text.substring(0, index1);
        String s2 = text.substring(index1 + 1, index2);
        String s3 = text.substring(index2 + 1);
        return new d(Integer.parseInt(s1), Integer.parseInt(s2), Integer.parseInt(s3));
    }

    private t getT(String text){
        int index1 = text.indexOf(":", 0);
        int index2 = text.indexOf("-", index1 + 1);
        int index3 = text.indexOf(":", index2 + 1);
        String s1 = text.substring(0, index1);
        String s2 = text.substring(index1 + 1, index2);
        String s3 = text.substring(index2 + 1, index3);
        String s4 = text.substring(index3 + 1);
        return new t(Integer.parseInt(s1), Integer.parseInt(s2), Integer.parseInt(s3), Integer.parseInt(s4));
    }
}
