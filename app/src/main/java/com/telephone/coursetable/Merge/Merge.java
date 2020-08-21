package com.telephone.coursetable.Merge;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Gson.PersonInfo;
import com.telephone.coursetable.Gson.PersonInfo_s;
import com.telephone.coursetable.Gson.StudentInfo;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Login;

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
}
