package com.telephone.coursetable.GradePoint;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Http.Post;
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
        HttpConnectionAndCode gpc = Post.post(
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/xuefenji.asp",
                null,
                r.getString(R.string.user_agent),
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/public/menu.asp?menu=mnall.asp",
                null,
                cookie,
                null,
                null,
                null,
                null,
                null
        );
        if(gpc.code == 0){
            return gpc.comment;
        }
        return null;
    }

    //教务处登录后
    //得到一段html文本
    public static String grade_point_html_wan(Context c, String cookie, String syear){
        Resources r = c.getResources();
        HttpConnectionAndCode gpc = Post.post(
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/xuefenji.asp",
                new String[]{
                        "xn=" + syear,
                        "lwPageSize=1000",
                        "lwBtnquery=%B2%E9%D1%AF"
                },
                r.getString(R.string.user_agent),
                "https://v.guet.edu.cn/http/77726476706e69737468656265737421a1a013d2766626013051d0/student/public/menu.asp?menu=mnall.asp",
                null,
                cookie,
                null,
                null,
                null,
                null,
                null
        );
        if(gpc.code == 0){
            return gpc.comment;
        }
        return null;
    }


    //学分系统登录后
    //处理得到的html文本
    public static List<Map.Entry<String, String>> grade_point_array(Context c, String cookie, String sid){

        List<Map.Entry<String, String>> gp_arr = new ArrayList<>();
        String from_now_on = "";
        String html;
        String xml;
        Elements sscore_html;
        Elements years_xml;
        Document doc;
        int time;

        String[] syears;
        String sy;
        String syear = "";
        sy = sid.substring(0,2);

        xml = GradePoint_Test.grade_year_html_wan(c, cookie);
        if (xml==null) return null;
        doc = Jsoup.parse( xml );
        years_xml = doc.select("html > body > form > table > tbody > tr > td > select > option");
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
            time = 0;
            do {
                html = grade_point_html_wan(c, cookie, year);
                if (html==null) return null;
                doc = Jsoup.parse(html);
                sscore_html = doc.select("html > body > table > tbody > tr > td > table > tbody > tr > td > B > font ");
            } while ( sscore_html.isEmpty() && (++time) < MyApp.check_code_regain_times);
            if ( sscore_html.size()==0 ) {
                if (!gp_arr.isEmpty()) return gp_arr;
                return null;
            }
            if ( year.isEmpty() ) {
                from_now_on = sscore_html.get(0).ownText();
            }
            else gp_arr.add( Map.entry(sscore_html.get(0).ownText(), year) );
        }
        gp_arr.add( Map.entry(from_now_on, "入学至今") );

        return gp_arr;
    }

    public static String get_vpn_login_aawres(Context context, String cookie, String sid, String aaw_pwd) {
        Resources resources = context.getResources();
        String NAME = "get_vpn_login_aawres()";
        int loop_aawlogin_times = 0;
        do {
            try {
                boolean login_res = Login_vpn.aaw_login(context, sid, aaw_pwd, cookie);
                Log.e(NAME, "success");
                break;
            } catch (ExceptionNetworkError exceptionNetworkError) {
                if ( loop_aawlogin_times++<MyApp.check_code_regain_times ) continue;
                Log.e(NAME, "ExceptionNetworkError");
                return resources.getString(R.string.wan_login_vpn_network_error_exception);
            } catch (ExceptionWrongUserOrPassword exceptionWrongUserOrPassword) {
                Log.e(NAME, "ExceptionWrongUserOrPassword | Exception302 | ExceptionUnknown");
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
