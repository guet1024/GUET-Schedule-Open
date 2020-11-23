package com.telephone.coursetable.Merge;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.telephone.coursetable.Database.CETDao;
import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GradesDao;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.Key.GoToClassKey;
import com.telephone.coursetable.Database.LABDao;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Gson.CET;
import com.telephone.coursetable.Gson.CET_s;
import com.telephone.coursetable.Gson.ExamInfo;
import com.telephone.coursetable.Gson.ExamInfo_s;
import com.telephone.coursetable.Gson.GoToClass_ClassInfo;
import com.telephone.coursetable.Gson.GoToClass_ClassInfo_s;
import com.telephone.coursetable.Gson.Grades;
import com.telephone.coursetable.Gson.Grades_s;
import com.telephone.coursetable.Gson.GraduationScore;
import com.telephone.coursetable.Gson.GraduationScore_s;
import com.telephone.coursetable.Gson.Hour;
import com.telephone.coursetable.Gson.Hour_s;
import com.telephone.coursetable.Gson.LAB;
import com.telephone.coursetable.Gson.LAB_s;
import com.telephone.coursetable.Gson.PersonInfo;
import com.telephone.coursetable.Gson.PersonInfo_s;
import com.telephone.coursetable.Gson.StudentInfo;
import com.telephone.coursetable.Gson.TermInfo;
import com.telephone.coursetable.Gson.TermInfo_s;
import com.telephone.coursetable.R;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Merge {

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void personInfo(@NonNull String origin_p, @NonNull String origin_stu, @NonNull PersonInfoDao pdao){
        PersonInfo_s p_s = new Gson().fromJson(origin_p, PersonInfo_s.class);
        PersonInfo p = p_s.getData();
        StudentInfo stu = new Gson().fromJson(origin_stu, StudentInfo.class);
        pdao.insert(
                new com.telephone.coursetable.Database.PersonInfo(
                        p.getStid(),p.getGrade(),p.getClassno(),p.getSpno(),p.getName(),p.getName1(),
                        p.getEngname(),p.getSex(),p.getPass(),p.getDegree(),p.getDirection(),p.getChangetype(),
                        p.getSecspno(),p.getClasstype(),p.getIdcard(),p.getStype(),p.getXjzt(),p.getChangestate(),
                        p.getLqtype(),p.getZsjj(),p.getNation(),p.getPolitical(),p.getNativeplace(),
                        p.getBirthday(),p.getEnrolldate(),p.getLeavedate(),p.getDossiercode(),p.getHostel(),
                        p.getHostelphone(),p.getPostcode(),p.getAddress(),p.getPhoneno(),p.getFamilyheader(),
                        p.getTotal(),p.getChinese(),p.getMaths(),p.getEnglish(),p.getAddscore1(),
                        p.getAddscore2(),p.getComment(),p.getTestnum(),p.getFmxm1(),p.getFmzjlx1(),
                        p.getFmzjhm1(),p.getFmxm2(),p.getFmzjlx2(),p.getFmzjhm2(),p.getDs(),p.getXq(),
                        p.getRxfs(),p.getOldno(),stu.getDptno(), stu.getDptname(), stu.getSpname()
                )
        );
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void goToClass_ClassInfo(@NonNull String origin_g, @NonNull GoToClassDao gdao, @NonNull ClassInfoDao cdao, @NonNull HashMap<GoToClassKey, String> my_comment_map, @NonNull String username){
        GoToClass_ClassInfo_s g_s = new Gson().fromJson(origin_g, GoToClass_ClassInfo_s.class);
        List<GoToClass_ClassInfo> g = g_s.getData();
        for (GoToClass_ClassInfo i : g){
            gdao.insert(
                    new GoToClass(
                            username,
                            i.getTerm(), i.getWeek(), i.getSeq(), i.getCourseno(), i.getStartweek(),
                            i.getEndweek(), i.isOddweek(), i.getId(), i.getCroomno(), i.getHours(),
                            i.getComm(),
                            my_comment_map.get(new GoToClassKey(
                                    username,
                                    i.getTerm(), i.getWeek(), i.getSeq(), i.getCourseno(), i.getStartweek(),
                                    i.getEndweek(), i.isOddweek()
                            )), false
                    )
            );
            cdao.insert(
                    new ClassInfo(
                            username,
                            i.getCourseno(), i.getCtype(), i.getTname(), i.getExamt(), i.getDptname(),
                            i.getDptno(), i.getSpname(), i.getSpno(), i.getGrade(), i.getCname(),
                            i.getTeacherno(), i.getName(), i.getCourseid(), i.getMaxcnt(),
                            i.getXf(), i.getLlxs(), i.getSyxs(), i.getSjxs(), i.getQtxs(), i.getSctcnt(), 0
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void graduationScore(@NonNull String origin_g, @NonNull String origin_g2, @NonNull GraduationScoreDao gdao){
        GraduationScore_s yxxf = new Gson().fromJson(origin_g, GraduationScore_s.class);
        GraduationScore_s plan_cj = new Gson().fromJson(origin_g2, GraduationScore_s.class);
        List<GraduationScore> yxxf_list = yxxf.getData();
        List<GraduationScore> plan_cj_list = plan_cj.getData();
        //plan cj
        for (GraduationScore cj : plan_cj_list){
            gdao.insert(
                    new com.telephone.coursetable.Database.GraduationScore(
                            cj.getName(), cj.getCname(), cj.getEngname(), cj.getEngcj(), cj.getTname(),
                            cj.getStid(), cj.getTerm(), cj.getCourseid(), cj.getPlanxf(), cj.getCredithour(),
                            cj.getCoursetype(), cj.getLvl(), cj.getSterm(), cj.getCourseno(), cj.getScid(),
                            cj.getScname(), cj.getScore(), cj.getZpxs(), cj.getXf(), cj.getStp()
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void termInfo(@NonNull Context c, @NonNull String origin_t, @NonNull TermInfoDao tdao){
        TermInfo_s t_s = new Gson().fromJson(origin_t, TermInfo_s.class);
        List<TermInfo> t = t_s.getData();
        Resources r = c.getResources();
        DateTimeFormatter server_formatter = DateTimeFormatter.ofPattern(r.getString(R.string.server_terminfo_datetime_format));
        DateTimeFormatter ts_formatter = DateTimeFormatter.ofPattern(r.getString(R.string.ts_datetime_format));
        for (TermInfo i : t){
            String sts_string = LocalDateTime.parse(i.getStartdate(), server_formatter).format(ts_formatter);
            String ets_string = LocalDateTime.parse(i.getEnddate(), server_formatter).format(ts_formatter);
            long sts = Timestamp.valueOf(sts_string).getTime();
            long ets = Timestamp.valueOf(ets_string).getTime();
            tdao.insert(
                    new com.telephone.coursetable.Database.TermInfo(
                            i.getTerm(), i.getStartdate(), i.getEnddate(), i.getWeeknum(), i.getTermname(),
                            i.getSchoolyear(), i.getComm(), sts, ets
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void hour(@NonNull Context c, @NonNull String origin_h, @NonNull SharedPreferences.Editor editor){
        Hour_s h_s = new Gson().fromJson(origin_h, Hour_s.class);
        List<Hour> h = h_s.getData();
        Resources r = c.getResources();
        String ss = r.getString(R.string.pref_hour_start_suffix);
        String sbs = r.getString(R.string.pref_hour_start_backup_suffix);
        String es = r.getString(R.string.pref_hour_end_suffix);
        String ebs = r.getString(R.string.pref_hour_end_backup_suffix);
        String ds = r.getString(R.string.pref_hour_des_suffix);
        String dbs = r.getString(R.string.pref_hour_des_backup_suffix);
        for (Hour i : h){
            String memo = i.getMemo();
            if (memo == null || memo.isEmpty()){
                continue;
            }
            String des = i.getNodename();
            String node_no = i.getNodeno();
            int index = memo.indexOf('-');
            String stime = memo.substring(0, index);
            String etime = memo.substring(index + 1);
            editor.putString(node_no + ss, stime);
            editor.putString(node_no + es, etime);
            editor.putString(node_no + ds, des);
            editor.putString(node_no + sbs, stime);
            editor.putString(node_no + ebs, etime);
            editor.putString(node_no + dbs, des);
        }
        editor.commit();
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void grades(@NonNull String origin_g, @NonNull GradesDao gdao){
        Grades_s g_s = new Gson().fromJson(origin_g, Grades_s.class);
        List<Grades> g = g_s.getData();
        for (Grades i : g){
            gdao.insert(
                    new com.telephone.coursetable.Database.Grades(
                            i.getDptno(), i.getDptname(), i.getSpno(), i.getSpname(), i.getBj(), i.getGrade(),
                            i.getStid(), i.getName(), i.getTerm(), i.getCourseid(), i.getCourseno(),
                            i.getCname(), i.getCourselevel(), i.getScore(), i.getZpxs(), i.getKctype(),
                            i.getTypeno(), i.getCid(), i.getCno(), i.getSycj(), i.getQzcj(), i.getPscj(),
                            i.getKhcj(), i.getZpcj(), i.getKslb(), i.getCjlb(), i.getKssj(), i.getXf(),
                            i.getXslb(), i.getTname1(), i.getStage(), i.getExamt(), i.getXs(), i.getCjlx(),
                            i.getChk(), i.getComm()
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void examInfo(@NonNull String origin_e, @NonNull ExamInfoDao edao, @NonNull TermInfoDao termInfoDao, @NonNull Context c){
        ExamInfo_s e_s = new Gson().fromJson(origin_e, ExamInfo_s.class);
        List<ExamInfo> e = e_s.getData();
        for (ExamInfo i : e){
            if (i.getKssj() == null){
                i.setKssj("");
            }
            if (i.getExamdate() == null){
                i.setExamdate("");
            }
            edao.insert(new com.telephone.coursetable.Database.ExamInfo(
                    i.getCroomno(), i.getCroomname(), i.getTch(), i.getTch1(), i.getTch2(), i.getJs(),
                    i.getJs1(), i.getJs2(), i.getRoomrs(), i.getTerm(), i.getGrade(), i.getDpt(),
                    i.getSpno(), i.getSpname(), i.getCourseid(),i.getCourseno(), i.getLabno(), i.getLabname(),
                    i.getDptno(), i.getTeacherno(), i.getName(), i.getXf(), i.getCname(), i.getSctcnt(),
                    i.getStucnt(), i.getScoretype(), i.getExamt(), i.getKctype(), i.getTypeno(),
                    i.getExamdate(), i.getExamtime(), i.getExamstate(), i.getExammode(), i.getXm(),
                    i.getRefertime(), i.getZc(), i.getXq(), i.getKsjc(), i.getJsjc(), i.getBkzt(),
                    i.getKssj(), i.getComm(), i.getRooms(), i.getLsh(), i.getZone(), i.getChecked1(),
                    i.getPostdate(), i.getOperator(), termInfoDao, c
            ));
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void cet(@NonNull String origin_cet, @NonNull CETDao cetDao){
        CET_s c_s = new Gson().fromJson(origin_cet, CET_s.class);
        List<CET> c = c_s.getData();
        for (CET i : c){
            cetDao.insert(new com.telephone.coursetable.Database.CET(
                    i.getName(), i.getSex(), i.getPostdate(), i.getDptno(), i.getDptname(), i.getSpno(),
                    i.getSpname(), i.getGrade(), i.getBj(), i.getTerm(), i.getStid(), i.getCode(),
                    i.getScore(), i.getStage(), i.getCard(), i.getOperator()
            ));
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void lab(@NonNull String origin_lab, @NonNull LABDao labDao, @NonNull GoToClassDao goToClassDao, @NonNull ClassInfoDao classInfoDao, @NonNull HashMap<GoToClassKey, String> my_comment_map, @NonNull String username){
        LAB_s lab_s = new Gson().fromJson(origin_lab, LAB_s.class);
        List<LAB> labList = lab_s.getData();
        for (LAB lab : labList) {
            labDao.insert(new com.telephone.coursetable.Database.LAB(
                    lab.getTerm(), lab.getLabid(), lab.getItemname(), lab.getCourseid(), lab.getCname(),
                    lab.getSpno(), lab.getSpname(), lab.getGrade(), lab.getTeacherno(), lab.getName(),
                    lab.getSrname(), lab.getSrdd(), lab.getXh(), lab.getBno(), lab.getPersons(),
                    lab.getZc(), lab.getXq(), lab.getJc(), lab.getJc1(), lab.getAssistantno(), lab.getTeachers(),
                    lab.getComm(), lab.getCourseno(), lab.getStusct(), lab.getSrid()
            ));
            goToClassDao.insert(new GoToClass(
                    username,
                    lab.getTerm(), lab.getXq(), lab.getJc() + "",
                    com.telephone.coursetable.Database.LAB.getUniqueSerialNumber(lab.getXh(), lab.getBno() + ""),
                    lab.getZc(), lab.getZc(), false, 0, lab.getSrdd(), 0,
                    com.telephone.coursetable.Database.LAB.getFullLabName(lab.getCname(), lab.getItemname()) + "（备注：" + lab.getComm() + "）",
                    my_comment_map.get(new GoToClassKey(
                            username,
                            lab.getTerm(), lab.getXq(), lab.getJc() + "",
                            com.telephone.coursetable.Database.LAB.getUniqueSerialNumber(lab.getXh(), lab.getBno() + ""),
                            lab.getZc(), lab.getZc(), false
                    )), false
            ));
            classInfoDao.insert(new ClassInfo(
                    username,
                    com.telephone.coursetable.Database.LAB.getUniqueSerialNumber(lab.getXh(), lab.getBno() + ""),
                    "", "", "", "", "", lab.getSpname(), lab.getSpno(),
                    lab.getGrade(),
                    com.telephone.coursetable.Database.LAB.getLabName(lab.getCname()),
                    lab.getTeacherno(), lab.getName(), lab.getCourseid(),
                    lab.getPersons(), 0, 0, 0, 0, 0, lab.getStusct(), 0
            ));
        }
    }
}
