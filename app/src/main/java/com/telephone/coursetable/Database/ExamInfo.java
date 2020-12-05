package com.telephone.coursetable.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.LogMe.LogMe;
import com.telephone.coursetable.Merge.Merge;
import com.telephone.coursetable.MyApp;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    public ExamInfo(@NonNull String croomno, String croomname, String tch, String tch1, String tch2, String js, String js1, String js2, long roomrs, String term, String grade, String dpt, String spno, String spname, String courseid, String courseno, String labno, String labname, String dptno, String teacherno, String name, String xf, String cname, String sctcnt, String stucnt, String scoretype, String examt, String kctype, String typeno, @NonNull String examdate, String examtime, long examstate, String exammode, String xm, String refertime, long zc, long xq, String ksjc, String jsjc, long bkzt, @NonNull String kssj, String comm, String rooms, String lsh, long zone, String checked1, String postdate, String operator, TermInfoDao termInfoDao, Context c, @NonNull String sid) {
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
        List<CustomizedExam.CustomizedSTSAndETS> read_from_pool = MyApp.getCurrentAppDB().customizedExamDao().selectForUserAndExam(sid, this.croomno, this.examdate, this.kssj);
        if (!read_from_pool.isEmpty()) {
            this.sts = read_from_pool.get(0).sts;
            this.ets = read_from_pool.get(0).ets;
        }else {
            setSTSandETS(c, examdate, kssj, termInfoDao);
        }
    }

    private void setSTSandETS(Context c, String date, String time, TermInfoDao termInfoDao){
        long day_first_ts = 0;
        try {
            d d = getD(date); // try to read date
            day_first_ts += get_the_first_ts_of_day_from_d_timezone_gmt8(d);
        }catch (Exception e){ // if fail
            if (this.zc != 0 && this.xq != 0){ // if week and weekday is set
                day_first_ts += termInfoDao.select(this.term).get(0).getTheFirstTimeStampOfWeekAndDay((int)this.zc, (int)this.xq);
            }else { // if week and weekday is NOT set
                day_first_ts += termInfoDao.select(this.term).get(0).getTheFirstTimeStampOfTheLastDay();
            }
        }
        long sts = day_first_ts;
        long ets = day_first_ts;
        try {
            t t = getT(time); // try to read start time and end time
            sts += (t.start_hour * 60 + t.start_minute) * 60000L;
            ets += (t.end_hour * 60 + t.end_minute) * 60000L;
        }catch (Exception e){ // if fail
            ets += (23 * 60 + 59) * 60000L;
            sts = ets - 1;
        }
        this.sts = sts;
        this.ets = ets;
    }

    private static long get_s_ts_from_d_t_timezone_gmt8(d d, t t){
        String format_str = "yyyy-MM-dd HH-mm";
        String str = String.format("%04d-%02d-%02d %02d-%02d", d.year, d.month, d.day, t.start_hour, t.start_minute);
        SimpleDateFormat format = new SimpleDateFormat(format_str, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            return format.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private static long get_e_ts_from_d_t_timezone_gmt8(d d, t t){
        String format_str = "yyyy-MM-dd HH-mm";
        String str = String.format("%04d-%02d-%02d %02d-%02d", d.year, d.month, d.day, t.end_hour, t.end_minute);
        SimpleDateFormat format = new SimpleDateFormat(format_str, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            return format.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private static long get_the_first_ts_of_day_from_d_timezone_gmt8(d d){
        String format_str = "yyyy-MM-dd HH-mm";
        String str = String.format("%04d-%02d-%02d 00-00", d.year, d.month, d.day);
        SimpleDateFormat format = new SimpleDateFormat(format_str, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            return format.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
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
