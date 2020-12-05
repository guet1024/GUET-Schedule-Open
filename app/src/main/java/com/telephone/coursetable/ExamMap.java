package com.telephone.coursetable;

import androidx.annotation.NonNull;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Database.ExamInfo;
import com.telephone.coursetable.Database.ExamInfoDao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ExamMap {

    static class key_exam{
        @NonNull
        String courseno;
        long sts;
        long ets;

        public key_exam(@NonNull String courseno, long sts, long ets) {
            this.courseno = courseno;
            this.sts = sts;
            this.ets = ets;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            key_exam key_exam = (key_exam) o;
            return sts == key_exam.sts &&
                    ets == key_exam.ets &&
                    courseno.equals(key_exam.courseno);
        }

        @Override
        public int hashCode() {
            return Objects.hash(courseno, sts, ets);
        }
    }


    public static List<Map.Entry<ExamInfo,String>> Generate_ExamList(){

        ExamInfoDao edao = MyApp.getCurrentAppDB().examInfoDao();
        List<ExamInfo> exam_list = edao.selectFromToday(Clock.nowTimeStamp());
        Map<key_exam,List<ExamInfo>> exam_map = new HashMap<>();

        for(ExamInfo e : exam_list){
            key_exam key = new key_exam(e.courseno,e.sts,e.ets);
            if(exam_map.containsKey(key)){
                exam_map.get(key).add(e);
            }else {
                exam_map.put(key, new LinkedList<ExamInfo>(){
                    {
                        add(e);
                    }
                });
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

        return res;
    }


}
