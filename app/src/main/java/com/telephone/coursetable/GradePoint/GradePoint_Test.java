package com.telephone.coursetable.GradePoint;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Https.Get;
import com.telephone.coursetable.Https.Post;
import com.telephone.coursetable.Login_vpn;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.MyException.Exception302;
import com.telephone.coursetable.MyException.ExceptionNetworkError;
import com.telephone.coursetable.MyException.ExceptionUnknown;
import com.telephone.coursetable.MyException.ExceptionWrongUserOrPassword;
import com.telephone.coursetable.R;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradePoint_Test {

    //学分绩学年
    public static String grade_year_html_wan(Context c, String cookie ) {
        Resources r = c.getResources();
        HttpConnectionAndCode gpc = Get.get(
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/xuefenji.asp",
                null,
                r.getString(R.string.user_agent),
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/public/mnall.asp",
                cookie,
                null,
                null,
                null,
                null,
                false,
                null,
                null
        );
        if(gpc.code == 0) {
            return gpc.comment;
        }
        return null;
    }

    //学分绩
    public static String grade_point_html_wan(Context c, String cookie, String syear){
        Resources r = c.getResources();
        HttpConnectionAndCode gpc = Post.post(
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/xuefenji.asp",
                null,
                r.getString(R.string.user_agent),
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/xuefenji.asp",
                "xn=" + syear + "&lwPageSize=1000&lwBtnquery=%B2%E9%D1%AF",
                cookie,
                null,
                null,
                null,
                null,
                false
        );
        if(gpc.code == 0 && !gpc.comment.isEmpty()){
            String html = gpc.comment;
            Elements score_nodes = Jsoup.parse(html).select("html > body > table > tbody > tr > td > table > tbody > tr > td > B > font");
            if (score_nodes.isEmpty()){
                return null;
            }else if (score_nodes.get(0).ownText().isEmpty()){
                String id = Jsoup.parse(html).select("html > body > table > tbody > tr > td > table > tbody > tr > th").get(4).ownText();
                if (id.isEmpty()){
                    com.telephone.coursetable.LogMe.LogMe.e("grade point", "regain point");
                    return grade_point_html_wan(c, cookie, syear);
                }else {
                    return "";
                }
            }else {
                return score_nodes.get(0).ownText();
            }
        }
        return null;
    }


    //学分系统登录后
    //处理得到的html文本
    public static List<Map.Entry<String, String>> grade_point_array(Context c, String cookie, String sid){

        List<Map.Entry<String, String>> gp_arr = new ArrayList<>();
        String xml;
        String sscore;
        Elements years_xml;
        Document doc;

        String[] syears;
        String sy;
        String syear = "";
        sy = sid.substring(0,2);

        xml = grade_year_html_wan(c, cookie);
        if (xml==null) return null;
        doc = Jsoup.parse( xml );
        years_xml = doc.select("html > body > form > table > tbody > tr > td > select > option");
        if (years_xml.size()==0) return null;
        for ( int i = 0 ; i < years_xml.size() ; i++ ) {
            Element year = years_xml.get(i);
            syear = syear + year.ownText();
            if (year.ownText().contains(sy)) {
                break;
            }
            syear = syear + ",";
        }

        syears = syear.split(",");
        for ( String year : syears ) {
            sscore = grade_point_html_wan(c, cookie, year);
            com.telephone.coursetable.LogMe.LogMe.e("grade point", "year = " + year + " point = " + sscore);
            if (sscore == null) return null;
            if (year.isEmpty()) year = "入学至今";
            gp_arr.add(Map.entry(sscore, year));
        }
        gp_arr.add(gp_arr.get(0));
        gp_arr.remove(0);

        return gp_arr;
    }

    public static String get_vpn_login_aawres(Context context, String cookie, String sid, String aaw_pwd) {
        Resources resources = context.getResources();
        String NAME = "get_vpn_login_aawres()";
        int loop_aawlogin_times = 0;
        do {
            try {
                boolean login_res = Login_vpn.aaw_login(context, sid, aaw_pwd, cookie);
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "success");
                break;
            } catch (ExceptionNetworkError exceptionNetworkError) {
                if ( loop_aawlogin_times++<MyApp.check_code_regain_times ) continue;
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "ExceptionNetworkError");
                return resources.getString(R.string.wan_login_vpn_network_error_exception);
            } catch (ExceptionWrongUserOrPassword exceptionWrongUserOrPassword) {
                com.telephone.coursetable.LogMe.LogMe.e(NAME, "ExceptionWrongUserOrPassword | Exception302 | ExceptionUnknown");
                return resources.getString(R.string.login_fail_pwd_text_exception);
            } catch (ExceptionUnknown | Exception302 exceptionUnknown) {
                return resources.getString(R.string.wan_snackbar_unknown_fail_exception);
            }
        }while ( true );
        return null;
    }

    public static Get_grade_points_array wan_get_grade_point_array( Context c, String cookie, String sid, String aaw_pwd ) {
        String get_aawres = get_vpn_login_aawres(c, cookie, sid, aaw_pwd);
        Resources resources = c.getResources();
        if ( get_aawres!=null ) return new Get_grade_points_array(get_aawres);
        List<Map.Entry<String, String>> grade_point = grade_point_array(c, cookie, sid);
        if ( grade_point==null ) return new Get_grade_points_array(resources.getString(R.string.wan_login_vpn_network_error_exception));
        return new Get_grade_points_array(grade_point);
    }

}
