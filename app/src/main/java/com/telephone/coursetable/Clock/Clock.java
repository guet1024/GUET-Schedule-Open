package com.telephone.coursetable.Clock;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.LinearLayout;

import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Clock {

    public static final long NO_TERM = 0;

    /**
     * @param sts the timestamp of start
     * @param nts the timestamp of current
     * @return
     * - 0 : sts > nts
     * - other : week num of specified start time and specified current time
     * @clear
     */
    public static long whichWeek(final long sts, final long nts){
        long res = 0;
        if (nts >= sts){
            res = 1;
            res += (nts - sts) / 604800000;
        }
        return res;
    }

    /**
     * @param nts the timestamp of current
     * @param pref the SharedPreferences storing time period information
     * @param timenos the array of time code
     * @param formatter the DateTimeFormatter of the time period information in the SharedPreferences
     * @param s_suffix the suffix of start time key
     * @param e_suffix the suffix of end time key
     * @param d_suffix the suffix of description key
     * @return
     * - null : corresponding time period not found
     * - not null : corresponding time period of specified current time
     * @clear
     */
    public static TimeAndDescription whichTime(long nts, SharedPreferences pref, String[] timenos, DateTimeFormatter formatter, String s_suffix, String e_suffix, String d_suffix){
        TimeAndDescription res = null;
        LocalTime n = Instant.ofEpochMilli(nts).atZone(ZoneOffset.ofHours(8)).toLocalTime();
        for (String timeno : timenos) {
            String sj = pref.getString(timeno + s_suffix, null);
            String ej = pref.getString(timeno + e_suffix, null);
            String des = pref.getString(timeno + d_suffix, null);
            if (sj != null && ej != null && des != null) {
                LocalTime sl = LocalTime.parse(sj, formatter);
                LocalTime el = LocalTime.parse(ej, formatter);
                if (sl.isBefore(n) || sl.equals(n)) {
                    if (el.isAfter(n) || el.equals(n)) {
                        res = new TimeAndDescription(timeno, des);
                        break;
                    }
                }
            }
        }
        return res;
    }

    /**
     * find a start time with specified time id using default config.
     * if not found, return null.
     * @clear
     */
    public static LocalTime getStartTimeUsingDefaultConfig(Context c, String time_id){
        String key = time_id + getSSFor_locateNow(c);
        String time_str = MyApp.getCurrentSharedPreference().getString(key, null);
        if (time_str == null){
            return null;
        }else {
            return LocalTime.parse(time_str, getDateTimeFormatterFor_locateNow(c));
        }
    }

    /**
     * find an end time with specified time id using default config.
     * if not found, return null.
     * @clear
     */
    public static LocalTime getEndTimeUsingDefaultConfig(Context c, String time_id){
        String key = time_id + getESFor_locateNow(c);
        String time_str = MyApp.getCurrentSharedPreference().getString(key, null);
        if (time_str == null){
            return null;
        }else {
            return LocalTime.parse(time_str, getDateTimeFormatterFor_locateNow(c));
        }
    }

    /**
     * find a time description with specified time id using default config.
     * if not found, return null.
     * @clear
     */
    public static String getTimeDesUsingDefaultConfig(Context c, String time_id){
        String key = time_id + getDSFor_locateNow(c);
        return MyApp.getCurrentSharedPreference().getString(key, null);
    }

    /**
     * @param nts the timestamp of current
     * @param tdao the {@link com.telephone.coursetable.Database.TermInfoDao} used to query the database for term info
     * @param pref {@link #whichTime(long, SharedPreferences, String[], DateTimeFormatter, String, String, String)}
     * @param times {@link #whichTime(long, SharedPreferences, String[], DateTimeFormatter, String, String, String)}
     * @param pref_time_period_formatter {@link #whichTime(long, SharedPreferences, String[], DateTimeFormatter, String, String, String)}
     * @param pref_s_suffix {@link #whichTime(long, SharedPreferences, String[], DateTimeFormatter, String, String, String)}
     * @param pref_e_suffix {@link #whichTime(long, SharedPreferences, String[], DateTimeFormatter, String, String, String)}
     * @param pref_d_suffix {@link #whichTime(long, SharedPreferences, String[], DateTimeFormatter, String, String, String)}
     * @return a {@link Locate}
     *      - res.{@link Locate#term} : corresponding {@link TermInfo} of specified current time, null if not found
     *      - res.{@link Locate#week} : corresponding week num of specified current time, {@link #NO_TERM} if res.{@link Locate#term} is null
     *      - res.{@link Locate#weekday} : weekday of specified current time
     *      - res.{@link Locate#month} : month of specified current time
     *      - res.{@link Locate#day} : day of specified current time
     *      - res.{@link Locate#time} : corresponding time code of specified current time, null if not found
     *      - res.{@link Locate#time_description} : corresponding time description of specified current time, null if not found
     * @clear
     */
    public static Locate locateNow(long nts, TermInfoDao tdao, SharedPreferences pref, String[] times, DateTimeFormatter pref_time_period_formatter, String pref_s_suffix, String pref_e_suffix, String pref_d_suffix){
        Locate res = new Locate(null, NO_TERM, 0, 0, 0, null, null);
        LocalDateTime n = Instant.ofEpochMilli(nts).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        List<TermInfo> which_term_res = tdao.whichTerm(nts);
        if (!which_term_res.isEmpty()){
            res.term = which_term_res.get(0);
            res.week = whichWeek(res.term.sts, nts);
        }
        res.weekday = n.getDayOfWeek().getValue();
        res.month = n.getMonthValue();
        res.day = n.getDayOfMonth();
        TimeAndDescription which_time_res = whichTime(nts, pref, times, pref_time_period_formatter, pref_s_suffix, pref_e_suffix, pref_d_suffix);
        if (which_time_res != null){
            res.time = which_time_res.time;
            res.time_description = which_time_res.des;
        }
        return res;
    }

    /**
     * @clear
     */
    public static DateTimeFormatter getDateTimeFormatterFor_locateNow(Context c){
        return DateTimeFormatter.ofPattern(c.getResources().getString(R.string.server_hours_time_format));
    }

    /**
     * @clear
     */
    public static String getSSFor_locateNow(Context c){
        return c.getResources().getString(R.string.pref_hour_start_suffix);
    }

    /**
     * @clear
     */
    public static String getESFor_locateNow(Context c){
        return c.getResources().getString(R.string.pref_hour_end_suffix);
    }

    /**
     * @clear
     */
    public static String getDSFor_locateNow(Context c){
        return c.getResources().getString(R.string.pref_hour_des_suffix);
    }

    /**
     * @return now time stamp
     * @clear
     */
    public static long nowTimeStamp(){
        return Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getTime();
    }

    /**
     * @return
     * null means can not get time from file
     */
    public static Map.Entry<Integer, Integer> findNowTime(long nts, SharedPreferences pref, DateTimeFormatter formatter, String s_suffix, String e_suffix) {
        Map.Entry<Integer, Integer> res = null;
        LocalTime start = LocalTime.parse("00:00", formatter);
        LocalTime end = LocalTime.parse("23:59", formatter);
        LocalTime n = Instant.ofEpochMilli(nts).atZone(ZoneOffset.ofHours(8)).toLocalTime();
        List<Integer> timelist = new ArrayList<>();

        timelist.add(-1);
        for ( int i=0 ; i<MyApp.times.length ; i++ ){
            timelist.add( i );
            timelist.add( i );
        }
        timelist.add(-1);

        for ( int i=0 ; i<timelist.size()-1 ; i++ ){
            if ( !timelist.get(i).equals(-1) && !timelist.get(i+1).equals(-1) ) {
                String sj;
                String ej;
                if (timelist.get(i).equals(timelist.get(i+1))) {
                    sj = pref.getString(MyApp.times[timelist.get(i)] + s_suffix, null);
                    ej = pref.getString(MyApp.times[timelist.get(i + 1)] + e_suffix, null);
                }else {
                    sj = pref.getString(MyApp.times[timelist.get(i)] + e_suffix, null);
                    ej = pref.getString(MyApp.times[timelist.get(i+1)] + s_suffix, null);
                }
                if (sj != null && ej != null) {
                    LocalTime sl = LocalTime.parse(sj, formatter);
                    LocalTime el = LocalTime.parse(ej, formatter);
                    if ( timelist.get(i).equals(timelist.get(i+1)) ){
                        if (sl.isBefore(n) || sl.equals(n)) {
                            if (el.isAfter(n) || el.equals(n)) {
                                res = Map.entry(timelist.get(i), timelist.get(i+1));
                                break;
                            }
                        }
                    }else {
                        if (sl.isBefore(n)) {
                            if (el.isAfter(n)) {
                                res = Map.entry(timelist.get(i), timelist.get(i+1));
                                break;
                            }
                        }
                    }
                }
            }
            else if (timelist.get(i).equals(-1)){
                LocalTime st = LocalTime.parse(pref.getString(MyApp.times[timelist.get(i+1)] + s_suffix, null), formatter);
                if (start.isBefore(n) || start.equals(n)) {
                    if (st!=null && st.isAfter(n)){
                        res = Map.entry(timelist.get(i), timelist.get(i+1));
                        break;
                    }
                }
            }
            else if (timelist.get(i+1).equals(-1)){
                LocalTime et = LocalTime.parse(pref.getString(MyApp.times[timelist.get(i)] + e_suffix, null), formatter);
                if (et!=null && et.isBefore(n)) {
                    if (end.isAfter(n) || end.equals(n)){
                        res = Map.entry(timelist.get(i), timelist.get(i+1));
                        break;
                    }
                }
            }
        }

        return res;
    }

    /**
     * @return
     * null 找不到当前时间段 || 处于假期 || 当天没课
     */
    public static FindClassOfCurrentOrNextTimeRes findClassOfCurrentOrNextTime(String username, long nts, TermInfoDao tdao, SharedPreferences pref, DateTimeFormatter formatter, String s_suffix, String e_suffix, String d_suffix) {
        Map.Entry<Integer, Integer> nowTime = findNowTime(nts, pref, formatter, s_suffix, e_suffix);
        List<ShowTableNode> surplus = new LinkedList<>();
        Locate locateNow = locateNow(nts, tdao, pref, MyApp.times, formatter, s_suffix, e_suffix, d_suffix);
        TermInfo term = locateNow.term;
        long week = locateNow.week;
        long weekday = locateNow.weekday;
        if ( nowTime == null ) return null;
        if ( term == null ) return null;
        if ( MyApp.getCurrentAppDB().goToClassDao().getTodayLessons(username, term.term, week, weekday).isEmpty() ) return null;
        int time = nowTime.getValue();
        if ( time == -1 ) return new FindClassOfCurrentOrNextTimeRes(surplus);
        do {
            surplus = MyApp.getCurrentAppDB().goToClassDao().getNode(username, term.term, week, weekday, MyApp.times[time] );
        }while ( surplus.isEmpty() && (++time) < MyApp.times.length );
        if (nowTime.getKey().equals(time)){
            return new FindClassOfCurrentOrNextTimeRes(surplus, true);
        }else {
            String start = null;
            if (time < MyApp.times.length) {
                start = pref.getString(MyApp.times[time] + s_suffix, null);
            }
            return new FindClassOfCurrentOrNextTimeRes(surplus, false, start);
        }
    }
}
