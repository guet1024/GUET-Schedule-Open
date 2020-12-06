package com.telephone.coursetable.GuetTools;

import androidx.annotation.NonNull;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Database.ExamInfo;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.MyApp;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ExamFilter {

    /**
     * // primary keys: when + cno + comment
     */
    static class key_exam{
        public long sts;
        public long ets;
        @NonNull
        public String courseno;
        @NonNull
        public String comm;

        public key_exam(long sts, long ets, @NonNull String courseno, @NonNull String comm) {
            this.sts = sts;
            this.ets = ets;
            this.courseno = courseno;
            this.comm = comm;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            key_exam key_exam = (key_exam) o;
            return sts == key_exam.sts &&
                    ets == key_exam.ets &&
                    courseno.equals(key_exam.courseno) &&
                    comm.equals(key_exam.comm);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sts, ets, courseno, comm);
        }
    }


    public static List<Map.Entry<ExamInfo,String>> generate_ExamList(){

        ExamInfoDao edao = MyApp.getCurrentAppDB().examInfoDao();
        List<ExamInfo> exam_list = edao.selectFromToday(Clock.nowTimeStamp());
        Map<key_exam,List<ExamInfo>> exam_map = new HashMap<>();

        for(ExamInfo e : exam_list){
            key_exam key = new key_exam(e.sts, e.ets, e.courseno, e.comm);
            if(exam_map.containsKey(key)){
                exam_map.get(key).add(e);
            }else {
                List<ExamInfo> list = new LinkedList<>();
                list.add(e);
                exam_map.put(key, list);
            }
        }

        Set<key_exam> ks = exam_map.keySet();

        List<Map.Entry<ExamInfo,String>> res = new LinkedList<>();

        for(key_exam k : ks){
            List<ExamInfo> elist = exam_map.get(k);
            String str = MyApp.gson.toJson(elist);

            StringBuilder sb = new StringBuilder(elist.get(0).croomno);
            for(int i=1; i<elist.size(); i++){
                sb.append(",").append(elist.get(i).croomno);
            }
            ExamInfo examInfo = elist.get(0);
            examInfo.croomno = sb.toString();

            res.add(Map.entry(examInfo,str));

        }

        res.sort(new Comparator<Map.Entry<ExamInfo, String>>() {
            @Override
            public int compare(Map.Entry<ExamInfo, String> o1, Map.Entry<ExamInfo, String> o2) {
                // <0: o1 --> o2
                // >0: o2 --> o1
                if (o2.getKey().sts == o1.getKey().sts) return 0;
                return (o2.getKey().sts > o1.getKey().sts) ? (-1) : (1);
            }
        });

        return res;
    }

    public static String getDisplayExamTip(){
        List<Map.Entry<ExamInfo,String>> exam_list = generate_ExamList();
        StringBuilder res = new StringBuilder();
        if (!exam_list.isEmpty()){
            long nts = Clock.nowTimeStamp();
            for (int i = 0; i < exam_list.size() && i < 3; i++) {
                long sts = exam_list.get(i).getKey().sts;
                if (sts < nts) sts = nts;
                long days = (sts - nts) / 86_400_000L;
                if (i > 0) res.append(" | ");
                res.append(days);
            }
            res.append(" 天后考试");
        }
        return res.toString();
    }
}
