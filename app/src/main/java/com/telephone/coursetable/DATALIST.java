package com.telephone.coursetable;

import android.content.Context;
import android.content.SharedPreferences;

import com.telephone.coursetable.Database.CETDao;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GradesDao;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.LABDao;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfoDao;

public class DATALIST {
    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.PersonInfo}
     *      - {@link com.telephone.coursetable.Gson.StudentInfo}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#personInfo(Context, String)}
     *      - {@link com.telephone.coursetable.Fetch.LAN#studentInfo(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.PersonInfo}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#personInfo(String, String, PersonInfoDao)}
     * @clear
     */
    public final String PersonInfo = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.GoToClass_ClassInfo}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#goToClass_ClassInfo(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.GoToClass} + {@link com.telephone.coursetable.Database.ClassInfo}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#goToClass_ClassInfo(String, GoToClassDao, ClassInfoDao)}
     * @clear
     */
    public final String GoToClass_ClassInfo = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.GraduationScore}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#graduationScore(Context, String)}
     *      - {@link com.telephone.coursetable.Fetch.LAN#graduationScore2(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.GraduationScore}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#graduationScore(String, String, GraduationScoreDao)}
     * @clear
     */
    public final String GraduationScore = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.TermInfo}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#termInfo(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.TermInfo}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#termInfo(Context, String, TermInfoDao)}
     * @clear
     */
    public final String TermInfo = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.Hour}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#hour(Context, String)}
     * @preference
     *      {@link R.string#preference_file_name}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#hour(Context, String, SharedPreferences.Editor)}
     * @clear
     */
    public final String Hour = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.Grades}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#grades(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.Grades}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#grades(String, GradesDao)}
     * @clear
     */
    public final String Grades = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.ExamInfo}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#examInfo(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.ExamInfo}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#examInfo(String, ExamInfoDao)}
     * @clear
     */
    public final String ExamInfo = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.CET}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#cet(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.CET}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#cet(String, CETDao)}
     * @clear
     */
    public final String CET = null;

    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.LAB}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#lab(Context, String, String)}
     *      - {@link com.telephone.coursetable.Fetch.WAN#lab(Context, String, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.LAB}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#lab(String, LABDao, GoToClassDao, ClassInfoDao)}
     * @clear
     */
    public final String LAB = null;
}
/**
 * 先更改方法再改错，声明记得初始化， 修改之前想用法
 * - {@link com.telephone.coursetable.Login#fetch_merge(Context, java.lang.String, com.telephone.coursetable.Database.PersonInfoDao, com.telephone.coursetable.Database.TermInfoDao, com.telephone.coursetable.Database.GoToClassDao, com.telephone.coursetable.Database.ClassInfoDao, com.telephone.coursetable.Database.GraduationScoreDao, SharedPreferences.Editor, com.telephone.coursetable.Database.GradesDao, com.telephone.coursetable.Database.ExamInfoDao, com.telephone.coursetable.Database.CETDao, com.telephone.coursetable.Database.LABDao)}
 * - {@link com.telephone.coursetable.Login_vpn#fetch_merge(Context, java.lang.String, com.telephone.coursetable.Database.PersonInfoDao, com.telephone.coursetable.Database.TermInfoDao, com.telephone.coursetable.Database.GoToClassDao, com.telephone.coursetable.Database.ClassInfoDao, com.telephone.coursetable.Database.GraduationScoreDao, com.telephone.coursetable.Database.GradesDao, com.telephone.coursetable.Database.ExamInfoDao, com.telephone.coursetable.Database.CETDao, com.telephone.coursetable.Database.LABDao, SharedPreferences.Editor)}
 * - {@link com.telephone.coursetable.Login#deleteOldDataFromDatabase(com.telephone.coursetable.Database.GoToClassDao, com.telephone.coursetable.Database.ClassInfoDao, com.telephone.coursetable.Database.TermInfoDao, com.telephone.coursetable.Database.PersonInfoDao, com.telephone.coursetable.Database.GraduationScoreDao, com.telephone.coursetable.Database.GradesDao, com.telephone.coursetable.Database.ExamInfoDao, com.telephone.coursetable.Database.CETDao, com.telephone.coursetable.Database.LABDao)}
 * - {@link com.telephone.coursetable.Login_vpn#deleteOldDataFromDatabase(com.telephone.coursetable.Database.GoToClassDao, com.telephone.coursetable.Database.ClassInfoDao, com.telephone.coursetable.Database.TermInfoDao, com.telephone.coursetable.Database.PersonInfoDao, com.telephone.coursetable.Database.GraduationScoreDao, com.telephone.coursetable.Database.GradesDao, com.telephone.coursetable.Database.ExamInfoDao, com.telephone.coursetable.Database.CETDao, com.telephone.coursetable.Database.LABDao)}
 * - {@link com.telephone.coursetable.FetchService#lan_merge(com.telephone.coursetable.Database.PersonInfoDao, com.telephone.coursetable.Database.PersonInfoDao, com.telephone.coursetable.Database.TermInfoDao, com.telephone.coursetable.Database.TermInfoDao, com.telephone.coursetable.Database.GoToClassDao, com.telephone.coursetable.Database.GoToClassDao, com.telephone.coursetable.Database.ClassInfoDao, com.telephone.coursetable.Database.ClassInfoDao, com.telephone.coursetable.Database.GraduationScoreDao, com.telephone.coursetable.Database.GraduationScoreDao, SharedPreferences.Editor, SharedPreferences, com.telephone.coursetable.Database.GradesDao, com.telephone.coursetable.Database.GradesDao, java.util.List, com.telephone.coursetable.Database.ExamInfoDao, com.telephone.coursetable.Database.ExamInfoDao, com.telephone.coursetable.Database.CETDao, com.telephone.coursetable.Database.CETDao, com.telephone.coursetable.Database.LABDao, com.telephone.coursetable.Database.LABDao)}
 * - {@link com.telephone.coursetable.FetchService#wan_merge()}
 */
