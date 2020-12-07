package com.telephone.coursetable.Clock;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.core.content.res.TypedArrayUtils;

import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.MyApp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GetDateInfo {

    // 遇事不决，GMT+8
    public static String[] get_a_week_date_info_all_timezone_using_gmt8(@Nullable TermInfo termInfo, long weekNum){

        long[] a_week_ts;

        if(termInfo == null || weekNum <= 0){
            long nts = Clock.nowTimeStamp();
            long today_ts1 = nts;
            long today_ts2 = nts;

            List<Long> ts_list = new LinkedList<>();
            ts_list.add(nts);

            int expand_num = 6;

            for (int i = 0; i < expand_num; i++) {
                today_ts1 -= 86400000L;
                ts_list.add(0, today_ts1);
            }

            for (int i = 0; i < expand_num; i++) {
                today_ts2 += 86400000L;
                ts_list.add(today_ts2);
            }

            DateTime now_gmt8 = DateTime.getGMT8_Instance(nts);

            int start_index = expand_num - (now_gmt8.getWeekday() - 1);
            int end_index = expand_num + (7 - now_gmt8.getWeekday());

            Long[] a_week_ts_Long = Arrays.copyOfRange(ts_list.toArray(new Long[0]), start_index, end_index + 1);
            a_week_ts = new long[a_week_ts_Long.length];
            for (int i = 0; i < a_week_ts_Long.length; i++) {
                a_week_ts[i] = a_week_ts_Long[i];
            }
        }else {
            a_week_ts = Clock.getAWeekTimeStampForWeekSince(
                    termInfo.sts,
                    weekNum
            );
        }

        String[] data_info_ary = new String[a_week_ts.length];

        for (int i = 0; i < a_week_ts.length; i++) {
            DateTime dateTime_gmt8 = DateTime.getGMT8_Instance(a_week_ts[i]);
            data_info_ary[i] = dateTime_gmt8.getMonth() + "." + dateTime_gmt8.getDay();
        }

        return data_info_ary;
    }
}
