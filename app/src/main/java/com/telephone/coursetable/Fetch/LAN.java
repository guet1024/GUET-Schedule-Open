package com.telephone.coursetable.Fetch;

import android.content.Context;
import android.content.res.Resources;

import com.telephone.coursetable.Http.Get;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Http.GetBitmap;
import com.telephone.coursetable.Http.Post;
import com.telephone.coursetable.R;

public class LAN {

    /**
     * @return
     * - obj != null : success
     * - obj == null : fail
     * @clear
     */
    public static HttpConnectionAndCode checkcode(Context c){
        Resources r = c.getResources();
        return GetBitmap.get(
                r.getString(R.string.lan_get_checkcode_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_checkcode_referer),
                null,
                r.getString(R.string.cookie_delimiter)
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode personInfo(Context c, String cookie){
        Resources r = c.getResources();
        return Get.get(
                r.getString(R.string.lan_get_person_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_person_referer),
                cookie,
                "}}",
                null,
                r.getString(R.string.lan_get_person_success_contain_response_text),
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode termInfo(Context c, String cookie){
        Resources r = c.getResources();
        return Get.get(
                r.getString(R.string.lan_get_terms_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_terms_referer),
                cookie,
                "]}",
                null,
                r.getString(R.string.lan_get_terms_success_contain_response_text),
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode goToClass_ClassInfo(Context c, String cookie){
        Resources r = c.getResources();
        return Get.get(
                r.getString(R.string.lan_get_table_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_table_referer),
                cookie,
                "]}",
                null,
                r.getString(R.string.lan_get_table_success_contain_response_text),
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode hour(Context c, String cookie){
        Resources r = c.getResources();
        return Get.get(
                r.getString(R.string.lan_get_hours_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_hours_referer),
                cookie,
                "]}",
                null,
                r.getString(R.string.lan_get_hours_success_contain_response_text),
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode studentInfo(Context c, String cookie){
        Resources r = c.getResources();
        return Post.post(
                r.getString(R.string.lan_get_student_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_student_referer),
                null,
                cookie,
                "}",
                null,
                r.getString(R.string.lan_get_student_success_contain_response_text),
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode graduationScore(Context c, String cookie){
        Resources r = c.getResources();
        HttpConnectionAndCode gen = Post.post(
                "http://bkjw.guet.edu.cn/student/genyxxf",
                null,
                r.getString(R.string.user_agent),
                "http://bkjw.guet.edu.cn/Login/MainDesktop",
                "stid=1",
                cookie,
                "}",
                null,
                "\"success\":true",
                null,
                null
        );
        return Get.get(
                r.getString(R.string.lan_get_graduation_score_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_graduation_score_referer),
                cookie,
                "}]}",
                null,
                r.getString(R.string.lan_get_graduation_score_success_contain_response_text),
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode graduationScore2(Context c, String cookie){
        Resources r = c.getResources();
        HttpConnectionAndCode gen = Post.post(
                "http://bkjw.guet.edu.cn/student/genyxxf",
                null,
                r.getString(R.string.user_agent),
                "http://bkjw.guet.edu.cn/Login/MainDesktop",
                "stid=1",
                cookie,
                "}",
                null,
                "\"success\":true",
                null,
                null
        );
        return Get.get(
                "http://bkjw.guet.edu.cn/student/getplancj",
                null,
                r.getString(R.string.user_agent),
                "http://bkjw.guet.edu.cn/Login/MainDesktop",
                cookie,
                "}]}",
                null,
                "\"success\":true",
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode grades(Context c, String cookie){
        Resources r = c.getResources();
        return Get.get(
                r.getString(R.string.lan_get_grades_url),
                new String[]{"term="},
                r.getString(R.string.user_agent),
                r.getString(R.string.lan_get_grades_referer),
                cookie,
                "}]}",
                null,
                r.getString(R.string.lan_get_grades_success_contain_response_text),
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode examInfo(Context c, String cookie){
        Resources r = c.getResources();
        return Get.get(
                "http://bkjw.guet.edu.cn/student/getexamap?&term=",
                null,
                r.getString(R.string.user_agent),
                "http://bkjw.guet.edu.cn/Login/MainDesktop",
                cookie,
                "}]}",
                null,
                "\"success\":true",
                null,
                null
        );
    }

    /**
     * @return
     * - code == 0 : success
     * - code == other : fail
     * @clear
     */
    public static HttpConnectionAndCode cet(Context c, String cookie){
        Resources r = c.getResources();
        return Get.get(
                "http://bkjw.guet.edu.cn/student/GetLvlScore?term=",
                null,
                r.getString(R.string.user_agent),
                "http://bkjw.guet.edu.cn/Login/MainDesktop",
                cookie,
                "}]}",
                null,
                "\"success\":true",
                null,
                null
        );
    }
}
