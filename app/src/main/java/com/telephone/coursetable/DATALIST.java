package com.telephone.coursetable;

import android.content.Context;
import android.content.SharedPreferences;

import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GraduationScoreDao;
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
     *      - {@link com.telephone.coursetable.Fetch.LAN#goToClass_ClassInfo(Context, String, String)}
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
     * @database
     *      {@link com.telephone.coursetable.Database.GraduationScore}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#graduationScore(String, GraduationScoreDao)}
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
}
