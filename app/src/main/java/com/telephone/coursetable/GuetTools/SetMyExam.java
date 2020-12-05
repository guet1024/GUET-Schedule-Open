package com.telephone.coursetable.GuetTools;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.DateTime;
import com.telephone.coursetable.Database.CustomizedExam;
import com.telephone.coursetable.Database.CustomizedExamDao;
import com.telephone.coursetable.Database.ExamInfo;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class SetMyExam {
    public static void sts_or_ets_date_time(@NonNull AppCompatActivity activity, @NonNull List<ExamInfo> examInfos, boolean isSTS) {
        activity.runOnUiThread(() -> {
            DateTime now = DateTime.getDefault_Instance(Clock.nowTimeStamp());
            new DatePickerDialog(
                    activity,
                    R.style.date_time_picker,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month++;
                            DateTime start = new DateTime(TimeZone.getDefault(), year, month, dayOfMonth);
                            sts_or_ets_time(activity, examInfos, start, isSTS);
                        }
                    },
                    now.getYear(),
                    now.getMonth() - 1,
                    now.getDay()
            ).show();
        });
    }
    private static void sts_or_ets_time(@NonNull AppCompatActivity activity, @NonNull List<ExamInfo> examInfos, @NonNull DateTime start, boolean isSTS){
        activity.runOnUiThread(() -> {
            DateTime now = DateTime.getDefault_Instance(Clock.nowTimeStamp());
            new TimePickerDialog(
                    activity,
                    R.style.date_time_picker,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            start.hour_24 = hourOfDay;
                            start.minute = minute;
                            long ts = start.getTime();
                            ExamInfoDao edao = MyApp.getCurrentAppDB().examInfoDao();
                            CustomizedExamDao pool_dao = MyApp.getCurrentAppDB().customizedExamDao();
                            new Thread(()->{
                                String sid = MyApp.getCurrentAppDB().userDao().getActivatedUser().get(0).username;
                                for (ExamInfo exam : examInfos){
                                    if (isSTS) {
                                        edao.updateSTS(exam.croomno, exam.examdate, exam.kssj, ts);
                                        pool_dao.insert(new CustomizedExam(sid, exam.croomno, exam.examdate, exam.kssj, ts, exam.ets));
                                    }else {
                                        edao.updateETS(exam.croomno, exam.examdate, exam.kssj, ts);
                                        pool_dao.insert(new CustomizedExam(sid, exam.croomno, exam.examdate, exam.kssj, exam.sts, ts));
                                    }
                                }
                            }).start();
                        }
                    },
                    now.hour_24,
                    now.minute,
                    false
            ).show();
        });
    }
    public static final int TAG_KEY_EXAM_LIST = 18;
    public static final int TAG_KEY_IS_STS = 17;
    public static void setTag_exam_list(View view, @NonNull String gson_string){
        ExamInfo[] examInfos_ary = MyApp.gson.fromJson(gson_string, ExamInfo[].class);
        view.setTag(TAG_KEY_EXAM_LIST, Arrays.asList(examInfos_ary));
    }
    public static void setTag_is_sts(View view, boolean isSTS){
        view.setTag(TAG_KEY_IS_STS, (Boolean)isSTS);
    }
    public static void editExam_in_a_new_thread(@NonNull AppCompatActivity activity, @NonNull View view){
        List<ExamInfo> examList = (List<ExamInfo>) view.getTag(TAG_KEY_EXAM_LIST);
        boolean isSTS = (Boolean) view.getTag(TAG_KEY_IS_STS);
        new Thread(()->{
            sts_or_ets_date_time(activity, examList, isSTS);
        }).start();
    }
}
