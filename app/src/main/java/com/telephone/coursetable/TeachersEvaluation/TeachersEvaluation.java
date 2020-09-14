package com.telephone.coursetable.TeachersEvaluation;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Fetch.LAN;
import com.telephone.coursetable.Gson.TeachersEvaluation.PJGetValue_Data;
import com.telephone.coursetable.Gson.TeachersEvaluation.PJGetValue_DataS;
import com.telephone.coursetable.Http.Get;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Http.Post;
import com.telephone.coursetable.Login;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.OCR.OCR;
import com.telephone.coursetable.R;

import java.net.URLEncoder;
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
    synchronized private static Thread getThread(){
        return thread;
    }
    synchronized private static void setThread(Thread thread){
        TeachersEvaluation.thread = thread;
    }
    synchronized private static boolean isEnd(){
        return end;
    }

    public static void evaluation(AppCompatActivity c, String id, String pwd, TermInfoDao tdao) {
        final String NAME = "evaluation()";
        if (getThread() != null){
            Log.e(NAME, "duplicated evaluation!");
            return;
        }
        Thread toastThread =
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Map.Entry<String, Integer> toast = gettoast();
                    if(toast!=null){
                        if (MyApp.getRunning_activity_pointer() == null){
                            setThread(null);
                            return;
                        }
                        c.runOnUiThread(()->Toast.makeText(c, toast.getKey(), toast.getValue()).show());
                        switch (toast.getValue()){
                            case Toast.LENGTH_SHORT: default:
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    // Restore interrupt status.
                                    Thread.currentThread().interrupt();
                                    e.printStackTrace();
                                }
                                break;
                            case Toast.LENGTH_LONG:
                                try {
                                    Thread.sleep(3500);
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
        String referer = "http://bkjw.guet.edu.cn/Login/MainDesktop";
        String user_agent = c.getResources().getString(R.string.user_agent);
        int flag = 0;
        int flag1 = 0;
        List<TermInfo> list = tdao.selectAll();

        for (TermInfo t : list) {
            addtoast( "正在获取 "+t.termname+" 的评教信息",Toast.LENGTH_SHORT,false);
            HttpConnectionAndCode httpURLConnection = Get.get("http://bkjw.guet.edu.cn/student/getpjcno",
                    new String[]{"term=" + t.term},
                    user_agent,
                    referer,
                    cookie,
                    "}]}",
                    null,
                    null,
                    null,
                    null);
            for (int i = 0; httpURLConnection.code != 0 && i < 3; i++) {
                httpURLConnection = Get.get("http://bkjw.guet.edu.cn/student/getpjcno",
                        new String[]{"term=" + t.term},
                        user_agent,
                        referer,
                        cookie,
                        "}]}",
                        null,
                        null,
                        null,
                        null);
            }
            if (httpURLConnection.code != 0) {
                addtoast( "评教失败，检查校园网连接后重试",Toast.LENGTH_LONG,true);
                return;
            }
            if (httpURLConnection.comment.contains("\"data\":[]}" ) || httpURLConnection.comment.contains("\"can\": false")) {
                addtoast( t.termname+" 评教未开放",Toast.LENGTH_SHORT,false);
                flag++;
            } else {
                String sss = httpURLConnection.comment;
                final PJGetValue_DataS res = new Gson().fromJson(httpURLConnection.comment, PJGetValue_DataS.class);
                final List<PJGetValue_Data> pvdlist = res.getData();
                for (PJGetValue_Data g : pvdlist) {
                    if(g.getChk()!=0){
                        addtoast(  t.termname+" "+g.getName()+" 老师已提交",Toast.LENGTH_SHORT,false);
                        continue;
                    }
                    String post_body = "term=" + g.getTerm() + "&courseno=" + g.getCourseno() +
                            "&stid=" + g.getStid() + "&cname=" + URLEncoder.encode(g.getCname()) +
                            "&name=" + URLEncoder.encode(g.getName()) +
                            "&teacherno=" + g.getTeacherno() +
                            "&courseid=" + g.getCourseid() +
                            "&lb=" + g.getLb() +
                            "&chk=" + g.getChk() +
                            "&can=" + g.isCan() +
                            "&userid=" + "&bz=%E5%A5%BD" + "&score=100";
                    HttpConnectionAndCode post_res = Post.post("http://bkjw.guet.edu.cn/student/SaveJxpgJg/1",
                            new String[]{t.term},
                            user_agent,
                            referer,
                            post_body,
                            cookie,
                            "}",
                            null,
                            null,
                            null,
                            null);
                    for (int i=0;post_res.code != 0 && i<3;i++) {
                        addtoast(  "网络波动，正在重新评价 "+t.termname+" "+g.getName()+" 老师",Toast.LENGTH_SHORT,false);
                        post_res = Post.post("http://bkjw.guet.edu.cn/student/SaveJxpgJg/1",
                                new String[]{t.term},
                                user_agent,
                                referer,
                                post_body,
                                cookie,
                                "}",
                                null,
                                null,
                                null,
                                null);
                    }
                    if(post_res.code!=0){
                        addtoast(  "评教失败，检查校园网连接后重试",Toast.LENGTH_LONG,true);
                        return;
                    }
                    Log.e("term", t.term);
                    Log.e("body", post_body);
                    Log.e("resp_code", post_res.resp_code + "");
                    Log.e("response", post_res.comment);
                    Log.e("--------------", "------------------------------------");
                    addtoast(  "正在评价 "+t.termname+" "+g.getName()+" 老师",Toast.LENGTH_SHORT,false);
                }
                flag1++;
            }
            if(flag1 == 2){
                addtoast(  "评教成功，一共评教了 " + flag1 + " 个学期",Toast.LENGTH_LONG,true);
                Log.e("PJterm", flag1 +"");
                return;
            }
        }
        if (flag == list.size()) {
            addtoast(  "评教未开放",Toast.LENGTH_LONG,true);
            Log.e("PJterm","All terms are closed");
            return;
        } else {
            addtoast(  "评教成功，一共评教了 " + flag1 + " 个学期",Toast.LENGTH_LONG,true);
            Log.e("PJterm", flag1 +"");
        }
    }
}
