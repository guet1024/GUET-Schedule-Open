package com.telephone.coursetable.Clock;

import android.content.SharedPreferences;

import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.R;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
     * @return now time stamp
     * @clear
     */
    public static long nowTimeStamp(){
        return Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getTime();
    }
}
