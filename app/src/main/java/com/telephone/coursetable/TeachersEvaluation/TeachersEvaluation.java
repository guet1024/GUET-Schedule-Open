package com.telephone.coursetable.TeachersEvaluation;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.FunctionMenu;
import com.telephone.coursetable.Gson.TeachersEvaluation.Detail;
import com.telephone.coursetable.Gson.TeachersEvaluation.Detail_get;
import com.telephone.coursetable.Gson.TeachersEvaluation.Detail_get_s;
import com.telephone.coursetable.Gson.TeachersEvaluation.PJGetValue_Data;
import com.telephone.coursetable.Gson.TeachersEvaluation.PJGetValue_DataS;
import com.telephone.coursetable.Http.Get;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Http.Post;
import com.telephone.coursetable.LogMe.LogMe;
import com.telephone.coursetable.Login;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.OCR.OCR;
import com.telephone.coursetable.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TeachersEvaluation {//评教
    private static volatile List<Map.Entry<String, Integer>> toastlist = new LinkedList<>();
    private static volatile boolean end = false;
    private static volatile Thread thread = null;

    synchronized private static void addtoast(String s, int len, boolean end){
        toastlist.add(Map.entry(s, len));
        TeachersEvaluation.end = end;
    }
    synchronized private static Map.Entry<String, Integer> gettoast(){
        if(toastlist.isEmpty())
            return null;
        Map.Entry<String, Integer> res=toastlist.get(0);
        toastlist.remove(0);
        return res;
    }

    synchronized private static void clearall(){
        toastlist.clear();
    }

    synchronized private static Thread getThread(){
        return thread;
    }
    synchronized private static void setThread(Thread thread){
        TeachersEvaluation.thread = thread;
    }
    synchronized private static boolean isEnd(){
        return end;
    }

    public static void evaluation(FunctionMenu c, String id, String pwd, TermInfoDao tdao) {
        final String NAME = "evaluation()";
        if (getThread() != null){
            com.telephone.coursetable.LogMe.LogMe.e(NAME, "duplicated evaluation!");
            return;
        }
        clearall();
        Thread toastThread =
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Map.Entry<String, Integer> toast = gettoast();
                    if(toast!=null){
                        if (MyApp.getRunning_activity_pointer() == null ||
                                !c.toString().equals(MyApp.getRunning_activity_pointer().toString()) ||
                                !c.isVisible()
                        ){
                            setThread(null);
                            return;
                        }
                        c.runOnUiThread(()->Toast.makeText(c, toast.getKey(), toast.getValue()).show());
                        switch (toast.getValue()){
                            case Toast.LENGTH_SHORT: default:
                                try {
                                    Thread.sleep(2500);
                                } catch (InterruptedException e) {
                                    // Restore interrupt status.
                                    Thread.currentThread().interrupt();
                                    e.printStackTrace();
                                }
                                break;
                            case Toast.LENGTH_LONG:
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    // Restore interrupt status.
                                    Thread.currentThread().interrupt();
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }else {
                        if (isEnd()){
                            setThread(null);
                            return;
                        }
                    }
                }
            }
        });
        setThread(toastThread);
        toastThread.start();

        addtoast("评教登录中，请耐心等待",Toast.LENGTH_SHORT,false);
        if ( MyApp.getRunning_activity_pointer() == null ||
                !(c.toString().equals(MyApp.getRunning_activity_pointer().toString())) ||
                !c.isVisible()
        ){
            return;
        }
        HttpConnectionAndCode httpConnectionAndCode = LAN.checkcode(c);
        if (httpConnectionAndCode.obj == null) {
            addtoast("检查校园网连接后重试",Toast.LENGTH_LONG,true);
            return;
        }
        Bitmap bitmap = (Bitmap) httpConnectionAndCode.obj;
        String cookie = httpConnectionAndCode.cookie;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cookie);
        String ckcode = OCR.getTextFromBitmap(c, bitmap, MyApp.ocr_lang_code);
        if ( MyApp.getRunning_activity_pointer() == null ||
                !(c.toString().equals(MyApp.getRunning_activity_pointer().toString())) ||
                !c.isVisible()
        ){
            return;
        }
        HttpConnectionAndCode H = Login.login(c, id, pwd, ckcode, cookie, stringBuilder);
        if (H.code != 0 && H.code != -6) {
            addtoast("评教登录失败,检查校园网连接后重试",Toast.LENGTH_LONG,true);
            return;
        } else {
            for (; H.code != 0; ) {

                if (H.comment.contains("密码")) {
                    addtoast("评教登录密码错误, 再次登录以更新密码",Toast.LENGTH_LONG,true);
                    return;
                } else if (H.comment.contains("验证码")) {
                    if (MyApp.getRunning_activity_pointer() == null ||
                            !c.toString().equals(MyApp.getRunning_activity_pointer().toString()) ||
                            !c.isVisible()
                    ){
                        return;
                    }
                    httpConnectionAndCode = LAN.checkcode(c);
                    if (httpConnectionAndCode.obj == null) {
                        addtoast("检查校园网连接后重试",Toast.LENGTH_LONG,true);
                        return;
                    }
                    cookie = httpConnectionAndCode.cookie;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(cookie);
                    bitmap = (Bitmap) httpConnectionAndCode.obj;
                    ckcode = OCR.getTextFromBitmap(c, bitmap, MyApp.ocr_lang_code);
                    if ( MyApp.getRunning_activity_pointer() == null ||
                            !(c.toString().equals(MyApp.getRunning_activity_pointer().toString())) ||
                            !c.isVisible()
                    ){
                        return;
                    }
                    H = Login.login(c, id, pwd, ckcode, cookie, stringBuilder);
                    if (H.code != 0 && H.code!=-6) {
                        addtoast("评教登录失败,检查校园网连接后重试",Toast.LENGTH_LONG,true);
                        return;
                    }
                }
            }
        }
        addtoast("登录成功,开始评教",Toast.LENGTH_SHORT,false);
        cookie = stringBuilder.toString();
        String referer = "http://172.16.13.22/Login/MainDesktop";
        String user_agent = c.getResources().getString(R.string.user_agent);
        int flag = 0;
        int flag1 = 0;
        List<TermInfo> list = tdao.selectAll();

        for (TermInfo t : list) {
            addtoast( "正在获取 "+t.termname+" 的评教信息",Toast.LENGTH_SHORT,false);
            if ( MyApp.getRunning_activity_pointer() == null ||
                    !(c.toString().equals(MyApp.getRunning_activity_pointer().toString())) ||
                    !c.isVisible()
            ){
                return;
            }
            HttpConnectionAndCode httpURLConnection = Get.get("http://172.16.13.22/student/getpjcno",
                    new String[]{"term=" + t.term},
                    user_agent,
                    referer,
                    cookie,
                    "}]}",
                    null,
                    null,
                    null,
                    null,
                    null,
                    10000
            );
            for (int i = 0; httpURLConnection.code != 0 && i < 3; i++) {
                if (MyApp.getRunning_activity_pointer() == null ||
                        !c.toString().equals(MyApp.getRunning_activity_pointer().toString()) ||
                        !c.isVisible()
                ){
                    return;
                }
                httpURLConnection = Get.get("http://172.16.13.22/student/getpjcno",
                        new String[]{"term=" + t.term},
                        user_agent,
                        referer,
                        cookie,
                        "}]}",
                        null,
                        null,
                        null,
                        null,
                        null,
                        10000
                );
            }
            if (httpURLConnection.code != 0) {
                addtoast( "评教失败，检查校园网连接后重试",Toast.LENGTH_LONG,true);
                return;
            }
            if (httpURLConnection.comment.contains("\"data\":[]}" ) || httpURLConnection.comment.contains("\"can\": false")) {
                addtoast( t.termname+" 评教未开放",Toast.LENGTH_SHORT,false);
                flag++;
            } else {
                final PJGetValue_DataS res = new Gson().fromJson(httpURLConnection.comment, PJGetValue_DataS.class);
                final List<PJGetValue_Data> pvdlist = res.getData();
                for (PJGetValue_Data g : pvdlist) {

                    if(g.getChk()!=0){
                        addtoast(  t.termname+" "+g.getName()+" 老师已提交",Toast.LENGTH_SHORT,false);
                        continue;
                    }

                    HttpConnectionAndCode prepare_res = prepare_evaluation(c, t, g, user_agent, referer, cookie);
                    if (prepare_res == null){
                        return;
                    }else if (prepare_res.code != 0){
                        if (prepare_res.comment != null && !prepare_res.comment.isEmpty()){
                            LogMe.e(NAME, "prepare res comment: " + prepare_res.comment);
                        }
                        addtoast("评教失败(prepare)，检查校园网连接后重试", Toast.LENGTH_LONG, true);
                        return;
                    }

                    HttpConnectionAndCode detail_res = detail_evaluation(c, t, g, user_agent, referer, cookie);
                    if (detail_res == null){
                        return;
                    }else if (detail_res.code != 0){
                        if (detail_res.comment != null && !detail_res.comment.isEmpty()){
                            LogMe.e(NAME, "detail res comment: " + detail_res.comment);
                        }
                        addtoast("评教失败(detail)，检查校园网连接后重试", Toast.LENGTH_LONG, true);
                        return;
                    }

                    HttpConnectionAndCode total_res = total_evaluation(c, t, g, user_agent, referer, cookie);
                    if (total_res == null){
                        return;
                    }else if (total_res.code != 0){
                        if (total_res.comment != null && !total_res.comment.isEmpty()){
                            LogMe.e(NAME, "total res comment: " + total_res.comment);
                        }
                        addtoast("评教失败(total)，检查校园网连接后重试", Toast.LENGTH_LONG, true);
                        return;
                    }

                }
                flag1++;
            }
            if(flag1 == 2){
                addtoast(  "评教成功，一共评教了 " + flag1 + " 个学期",Toast.LENGTH_LONG,true);
                com.telephone.coursetable.LogMe.LogMe.e("PJterm", flag1 +"");
                return;
            }
        }
        if (flag == list.size()) {
            addtoast(  "评教未开放",Toast.LENGTH_LONG,true);
            com.telephone.coursetable.LogMe.LogMe.e("PJterm","All terms are closed");
            return;
        } else {
            addtoast(  "评教成功，一共评教了 " + flag1 + " 个学期",Toast.LENGTH_LONG,true);
            com.telephone.coursetable.LogMe.LogMe.e("PJterm", flag1 +"");
        }
    }

    /**
     * totally evaluate a teacher of specified term, if network fail, retry for no more than 3 times
     * @return
     * - null : you should stop evaluating immediately
     * - res.code == 0 : success
     * - res.code != 0 : fail
     */
    private static HttpConnectionAndCode total_evaluation(FunctionMenu c, TermInfo t, PJGetValue_Data teacher, String user_agent, String referer, String cookie) {
        String success_symbol = "\"success\":true";
        String tail = "}";
        String url = "http://172.16.13.22/student/SaveJxpgJg/1";
        String[] params = null;
        String post_body = "";
        try {
            post_body = "term=" + teacher.getTerm() + "&courseno=" + teacher.getCourseno() +
                    "&stid=" + teacher.getStid() + "&cname=" + URLEncoder.encode(teacher.getCname(), StandardCharsets.UTF_8.toString()) +
                    "&name=" + URLEncoder.encode(teacher.getName(), StandardCharsets.UTF_8.toString()) +
                    "&teacherno=" + teacher.getTeacherno() +
                    "&courseid=" + teacher.getCourseid() +
                    "&lb=" + teacher.getLb() +
                    "&chk=" + teacher.getChk() +
                    "&can=" + teacher.isCan() +
                    "&userid=" + "&bz=666" + "&score=100";
            post_body = post_body.replace("chk=0", "chk=");
        }catch (Exception ignored){}
        if ( MyApp.getRunning_activity_pointer() == null ||
                !(c.toString().equals(MyApp.getRunning_activity_pointer().toString())) ||
                !c.isVisible()
        ){
            return null;
        }
        addtoast(  "正在对 "+t.termname+" "+teacher.getName()+" 老师进行总评",Toast.LENGTH_SHORT,false);
        HttpConnectionAndCode post_res = Post.post(url,
                params,
                user_agent,
                referer,
                post_body,
                cookie,
                tail,
                null,
                success_symbol,
                null,
                null,
                null
        );
        for (int i = 0; post_res.code != 0 && post_res.code != -6 && i < 3; i++) {
            addtoast(  "网络波动，正在重新对 "+t.termname+" "+teacher.getName()+" 老师进行总评",Toast.LENGTH_SHORT,false);
            if (MyApp.getRunning_activity_pointer() == null ||
                    !c.toString().equals(MyApp.getRunning_activity_pointer().toString()) ||
                    !c.isVisible()
            ){
                return null;
            }
            post_res = Post.post(url,
                    params,
                    user_agent,
                    referer,
                    post_body,
                    cookie,
                    tail,
                    null,
                    success_symbol,
                    null,
                    null,
                    null
            );
        }
        return post_res;
    }

    /**
     * prepare to evaluate a teacher of specified term, if network fail, retry for no more than 3 times
     * @return
     * - null : you should stop evaluating immediately
     * - res.code == 0 : success
     * - res.code != 0 : fail
     */
    private static HttpConnectionAndCode prepare_evaluation(FunctionMenu c, TermInfo t, PJGetValue_Data teacher, String user_agent, String referer, String cookie){
        String success_symbol = "\"success\":true";
        String tail = "}";
        String url = "http://172.16.13.22/student/JxpgJg";
        String[] params = null;
        String post_body = "term=" + teacher.getTerm() + "&courseno=" + teacher.getCourseno() +
                "&teacherno=" + teacher.getTeacherno();
        if ( MyApp.getRunning_activity_pointer() == null ||
                !(c.toString().equals(MyApp.getRunning_activity_pointer().toString())) ||
                !c.isVisible()
        ){
            return null;
        }
        addtoast(  "正在准备对 "+t.termname+" "+teacher.getName()+" 老师进行评估",Toast.LENGTH_SHORT,false);
        HttpConnectionAndCode post_res = Post.post(url,
                params,
                user_agent,
                referer,
                post_body,
                cookie,
                tail,
                null,
                success_symbol,
                null,
                null,
                null
        );
        for (int i = 0; post_res.code != 0 && post_res.code != -6 && i < 3; i++) {
            addtoast(  "网络波动，正在重新准备对 "+t.termname+" "+teacher.getName()+" 老师进行评估",Toast.LENGTH_SHORT,false);
            if (MyApp.getRunning_activity_pointer() == null ||
                    !c.toString().equals(MyApp.getRunning_activity_pointer().toString()) ||
                    !c.isVisible()
            ){
                return null;
            }
            post_res = Post.post(url,
                    params,
                    user_agent,
                    referer,
                    post_body,
                    cookie,
                    tail,
                    null,
                    success_symbol,
                    null,
                    null,
                    null
            );
        }
        return post_res;
    }

    /**
     * evaluate a teacher of specified term in detail, if network fail, retry for no more than 3 times
     * @return
     * - null : you should stop evaluating immediately
     * - res.code == 0 : success
     * - res.code != 0 : fail
     */
    private static HttpConnectionAndCode detail_evaluation(FunctionMenu c, TermInfo t, PJGetValue_Data teacher, String user_agent, String referer, String cookie){
        String success_symbol = "\"success\":true";
        String tail = "}";
        String url = "http://172.16.13.22/student/SaveJxpg";
        String[] params = new String[]{
                "_dc=" + Clock.nowTimeStamp(),
                "term=" + teacher.getTerm(),
                "courseno=" + teacher.getCourseno(),
                "teacherno=" + teacher.getTeacherno()
        };
        List<Detail_get> table = getTable(teacher.getTerm(), teacher.getCourseno(), teacher.getTeacherno(), user_agent, referer, cookie, t, teacher);
        if (table == null || table.isEmpty()){
            return new HttpConnectionAndCode(null, 99, "can not get the evaluation table");
        }
        List<Detail> post_list = new LinkedList<>();
        for (Detail_get origin : table) {
            post_list.add(new Detail(
                    teacher.getTerm(), origin.getLsh(), teacher.getCourseid(), teacher.getLb(), 100,
                    teacher.getTeacherno(), teacher.getCourseno(), origin.getDja(), origin.getAfz(),
                    origin.getDjb(), origin.getBfz(), origin.getDjc(), origin.getCfz(), origin.getDjd(),
                    origin.getDfz(), origin.getDje(), origin.getEfz(), origin.getNr(), origin.getZbnh(),
                    origin.getQz(), origin.getZt()
            ).encode_myself());
        }
        String post_body = new Gson().toJson(post_list);
        post_body = post_body.replace("\\\\u", "\\u");
        if ( MyApp.getRunning_activity_pointer() == null ||
                !(c.toString().equals(MyApp.getRunning_activity_pointer().toString())) ||
                !c.isVisible()
        ){
            return null;
        }
        addtoast(  "正在对 "+t.termname+" "+teacher.getName()+" 老师进行细节评分",Toast.LENGTH_SHORT,false);
        HttpConnectionAndCode post_res = Post.post(url,
                params,
                user_agent,
                referer,
                post_body,
                cookie,
                tail,
                null,
                success_symbol,
                null,
                null,
                "application/json"
        );
        for (int i = 0; post_res.code != 0 && post_res.code != -6 && i < 3; i++) {
            addtoast(  "网络波动，正在重新对 "+t.termname+" "+teacher.getName()+" 老师进行细节评分",Toast.LENGTH_SHORT,false);
            if (MyApp.getRunning_activity_pointer() == null ||
                    !c.toString().equals(MyApp.getRunning_activity_pointer().toString()) ||
                    !c.isVisible()
            ){
                return null;
            }
            post_res = Post.post(url,
                    params,
                    user_agent,
                    referer,
                    post_body,
                    cookie,
                    tail,
                    null,
                    success_symbol,
                    null,
                    null,
                    "application/json"
            );
        }
        return post_res;
    }

    /**
     * get the evaluation table for teacher, if network fail, retry for no more than 3 times
     * @return
     * - null : network fail or something went wrong
     * - non-null : return a list of {@link Detail_get}
     */
    private static List<Detail_get> getTable(String term, String cno, String tno, String ua, String ref, String cookie, TermInfo t, PJGetValue_Data teacher) {
        String url = "http://172.16.13.22/student/jxpgdata";
        String[] params = new String[]{
                "_dc=" + Clock.nowTimeStamp(),
                "term=" + term,
                "courseno=" + cno,
                "teacherno=" + tno
        };
        String tail = "}]}";
        String success_symbol = "\"success\":true";
        addtoast(  "正在获取 "+t.termname+" "+teacher.getName()+" 老师的评分表",Toast.LENGTH_SHORT,false);
        HttpConnectionAndCode res = Get.get(
                url,
                params,
                ua,
                ref,
                cookie,
                tail,
                null,
                success_symbol,
                null,
                null,
                null,
                10000
        );
        for (int i = 0; res.code != 0 && res.code != -6 && i < 3; i++) {
            addtoast(  "网络波动，正在重新获取 "+t.termname+" "+teacher.getName()+" 老师的评分表",Toast.LENGTH_SHORT,false);
            res = Get.get(
                    url,
                    params,
                    ua,
                    ref,
                    cookie,
                    tail,
                    null,
                    success_symbol,
                    null,
                    null,
                    null,
                    10000
            );
        }
        if (res.code == 0){
            return new Gson().fromJson(res.comment, Detail_get_s.class).getData();
        }else {
            return null;
        }
    }

}
