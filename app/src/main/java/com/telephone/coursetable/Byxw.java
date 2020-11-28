package com.telephone.coursetable;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Gson.GraduationCondition;
import com.telephone.coursetable.Gson.GraduationCondition_s;
import com.telephone.coursetable.Gson.GraduationDegreeEvaluation;
import com.telephone.coursetable.Gson.GraduationDegreeEvaluation_s;
import com.telephone.coursetable.Gson.GraduationFee;
import com.telephone.coursetable.Http.Get;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Http.Post;
import com.telephone.coursetable.LogMe.LogMe;
import com.telephone.coursetable.OCR.OCR;

import java.util.List;

public class Byxw {

    private static volatile boolean byxw_thread_running = false;

    synchronized private static void setByxw_thread_running(boolean byxw_thread_running){
        Byxw.byxw_thread_running = byxw_thread_running;
    }
    synchronized private static boolean getByxw_thread_running(){
        return Byxw.byxw_thread_running;
    }

    private static final Class<TeacherEvaluationPanel> byxw_class = TeacherEvaluationPanel.class;

    public static boolean Byxw_Query(@NonNull AppCompatActivity c){
        final String NAME = "Byxw_Query()";

        if(getByxw_thread_running()){
            return false;
        }else {
            setByxw_thread_running(true);
        }


        byxw_class.cast(c).prepare_start();

        UserDao udao = MyApp.getCurrentAppDB().userDao();
        PersonInfoDao pdao = MyApp.getCurrentAppDB().personInfoDao();
        TermInfoDao tdao = MyApp.getCurrentAppDB().termInfoDao();
        String id = udao.getActivatedUser().get(0).username;
        String pwd = udao.getActivatedUser().get(0).password;
        String cookie = "";
        printlog(c,"正在登录中~");
        while (true) {
            //
            if(check_quit(c)){
                return duel_quit(c,NAME,"");
            }
            HttpConnectionAndCode hcac = LAN.checkcode(c);
            if (hcac.obj == null) {
                return duel_quit(c,NAME,"登录失败，请检查校园网连接后重试。");
            }

            cookie = hcac.cookie;
            StringBuilder stringBuilder = new StringBuilder(cookie);
            Bitmap bitmap = (Bitmap) hcac.obj;
            String ckcode = OCR.getTextFromBitmap(c, bitmap, MyApp.ocr_lang_code);
            //
            if(check_quit(c)){
                return duel_quit(c,NAME,"");
            }
            HttpConnectionAndCode login_res = Login.login(c, id, pwd, ckcode, cookie, stringBuilder);
            if (login_res.code != 0) {
                if (login_res.code == -6) {
                    if (login_res.comment.contains("验证码")) {
                        continue;
                    } else {
                        return duel_quit(c,NAME,"登录失败，密码错误。请更新重新登录以您的学分制系统密码。");
                    }
                } else {
                    return duel_quit(c,NAME,"登录失败，请检查校园网连接后重试。");
                }
            }
            cookie = stringBuilder.toString();
            break;
        }

        String referer = "http://172.16.13.22/Login/MainDesktop";
        String user_agent = c.getResources().getString(R.string.user_agent);

        printlog(c,"正在进行财务费用更新~");
        //
        if(check_quit(c)){
            return duel_quit(c,NAME,"");
        }
        HttpConnectionAndCode money_res = Post.post(
                "http://172.16.13.22/student/genstufee/",
                null,
                user_agent,
                referer,
                "ctype=byyqxf&stid=" + id + "&grade=" + pdao.getGrade().get(0) + "&spno=" + pdao.selectAll().get(0).spno,
                cookie,
                "}",
                null,
                "\"success\":true",
                null,
                null,
                null
        );
        if (money_res.code != 0){
            return duel_quit(c,NAME,"财务费用更新失败。请检查校园网连接后重试。");
        }

        GraduationFee gf = MyApp.gson.fromJson(money_res.comment,GraduationFee.class);
        printlog(c,"您的财务费用信息如下：");
        printlog(c,"---------------------------");
        printlog(c,gf.getMsg());
        printlog(c,"---------------------------");

        printlog(c,"正在获取毕业条件~");
        //
        if(check_quit(c)){
            return duel_quit(c,NAME,"");
        }
        HttpConnectionAndCode con_res = Get.get(
                "http://172.16.13.22/comm/getsctxw",
                null,
                user_agent,
                referer,
                cookie,
                "}]}",
                null,
                "\"success\":true",
                null,
                null,
                null,
                null
        );
        if(con_res.code != 0){
            return duel_quit(c,NAME,"毕业条件获取失败。请检查校园网连接后重试。");
        }
        printlog(c,"毕业条件如下：");
        printlog(c,"*************************");
        GraduationCondition_s gcs = MyApp.gson.fromJson(con_res.comment,GraduationCondition_s.class);
        List<GraduationCondition> gc_list = gcs.getData();
        for(GraduationCondition gc : gc_list){
            printlog(c,gc.getComm());
        }
        printlog(c,"*************************");


        printlog(c,"正在毕业采集中~");
        List<TermInfo> termlist = tdao.selectAll();
        for(TermInfo term : termlist){
            printlog(c,"正在采集"+term.termname+"的信息");
            //
            if(check_quit(c)){
                return duel_quit(c,NAME,"");
            }
            HttpConnectionAndCode make_term_res = Post.post(
                    "http://172.16.13.22/student/genstuby/" + term.term,
                    null,
                    user_agent,
                    referer,
                    "ctype=byyqxf&stid=" + id + "&grade=" + pdao.getGrade().get(0) + "&spno=" + pdao.selectAll().get(0).spno,
                    cookie,
                    "}",
                    null,
                    "\"success\":true",
                    null,
                    null,
                    null
            );
            if (make_term_res.code != 0){
                return duel_quit(c,NAME,"毕业采集失败。请检查校园网连接后重试。");
            }
        }
        printlog(c,"正在查询您的毕业学位信息~");
        //
        if(check_quit(c)){
            return duel_quit(c,NAME,"");
        }
        HttpConnectionAndCode query_res = Get.get(
                "http://172.16.13.22/student/getbyxw",
                null,
                user_agent,
                referer,
                cookie,
                "]}",
                null,
                "\"success\":true",
                null,
                null,
                null,
                30000
        );
        if (query_res.code != 0){
            return duel_quit(c,NAME,"查询失败。请检查校园网连接后重试。");
        }
        printlog(c,"查询成功~");
        printlog(c,">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        GraduationDegreeEvaluation data = MyApp.gson.fromJson(query_res.comment, GraduationDegreeEvaluation_s.class).getData().get(0);
        printlog(c, "等级考试成绩：");
        printlog(c,"[ "+data.getCetshow()+" ]");
        printlog(c,"折算等级考试成绩：");
        printlog(c,"[ "+data.getCet()+"："+data.getCetcj()+" ]");
        printlog(c, "学分绩：" + data.getXfj());
        printlog(c, "外语平均分：" + data.getFpjf());
        printlog(c, "备注：" + data.getComm());
        printlog(c,"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return duel_quit(c,NAME,"");
    }

    private static void printlog(@NonNull AppCompatActivity c,String str){
        byxw_class.cast(c).print(str);
    }

    private static void end(@NonNull AppCompatActivity c, String str){
        printlog(c, str);
        byxw_class.cast(c).cleanup_end();
    }

    private static boolean check_quit(@NonNull AppCompatActivity c){
        return
                MyApp.getRunning_activity_pointer() == null ||
                        !c.toString().equals(MyApp.getRunning_activity_pointer().toString()) ||
                        !byxw_class.cast(c).isVisible();
    }

    private static boolean duel_quit(@NonNull AppCompatActivity c,String NAME,String endstr){
        LogMe.e(NAME, "Byxw thread stop");
        setByxw_thread_running(false);
        end(c,endstr);
        return true;
    }
}
