package com.telephone.coursetable.TeachersEvaluation;

import android.graphics.Bitmap;
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
import java.util.List;

public class TeachersEvaluation {//评教

    public static void evaluation(AppCompatActivity c, String id, String pwd, TermInfoDao tdao) {

        c.runOnUiThread(() -> Toast.makeText(c, "评教登录中，请耐心等待", Toast.LENGTH_SHORT).show());
        HttpConnectionAndCode httpConnectionAndCode = LAN.checkcode(c);
        if (httpConnectionAndCode.obj == null) {
            c.runOnUiThread(() -> Toast.makeText(c, "检查校园网连接后重试", Toast.LENGTH_LONG).show());
            return;
        }
        Bitmap bitmap = (Bitmap) httpConnectionAndCode.obj;
        String cookie = httpConnectionAndCode.cookie;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cookie);
        String ckcode = OCR.getTextFromBitmap(c, bitmap, MyApp.ocr_lang_code);
        HttpConnectionAndCode H = Login.login(c, id, pwd, ckcode, cookie, stringBuilder);
        if (H.code != 0 && H.code != -6) {
            c.runOnUiThread(() -> Toast.makeText(c, "评教登录失败,检查校园网连接后重试", Toast.LENGTH_LONG).show());
            return;
        } else {
            for (; H.code != 0; ) {

                if (H.comment.contains("密码")) {
                    c.runOnUiThread(() -> Toast.makeText(c, "评教登录密码错误, 再次登录以更新密码", Toast.LENGTH_LONG).show());
                    return;
                } else if (H.comment.contains("验证码")) {
                    httpConnectionAndCode = LAN.checkcode(c);
                    if (httpConnectionAndCode.obj == null) {
                        c.runOnUiThread(() -> Toast.makeText(c, "检查校园网连接后重试", Toast.LENGTH_LONG).show());
                        return;
                    }
                    cookie = httpConnectionAndCode.cookie;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(cookie);
                    bitmap = (Bitmap) httpConnectionAndCode.obj;
                    ckcode = OCR.getTextFromBitmap(c, bitmap, MyApp.ocr_lang_code);
                    H = Login.login(c, id, pwd, ckcode, cookie, stringBuilder);
                    if (H.code != 0 && H.code!=-6) {
                        c.runOnUiThread(() -> Toast.makeText(c, "评教登录失败,检查校园网连接后重试", Toast.LENGTH_LONG).show());
                        return;
                    }
                }
            }
        }
        c.runOnUiThread(() -> Toast.makeText(c, "登录成功,开始评教", Toast.LENGTH_SHORT).show());
        cookie = stringBuilder.toString();
        String referer = "http://bkjw.guet.edu.cn/Login/MainDesktop";
        String user_agent = c.getResources().getString(R.string.user_agent);
        int flag = 0;
        int flag1 = 0;
        List<TermInfo> list = tdao.selectAll();

        for (TermInfo t : list) {
            c.runOnUiThread(() -> Toast.makeText(c, "正在获取 "+t.termname+" 的评教信息", Toast.LENGTH_SHORT).show());
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
                c.runOnUiThread(() -> Toast.makeText(c, "评教失败，检查校园网连接后重试", Toast.LENGTH_LONG).show());
                return;
            }
            if (httpURLConnection.comment.contains("\"data\":[]}" ) || httpURLConnection.comment.contains("\"can\": false")) {
                c.runOnUiThread(() -> Toast.makeText(c, t.termname+" 评教未开放", Toast.LENGTH_SHORT).show());
                flag++;
            } else {
                String sss = httpURLConnection.comment;
                final PJGetValue_DataS res = new Gson().fromJson(httpURLConnection.comment, PJGetValue_DataS.class);
                final List<PJGetValue_Data> pvdlist = res.getData();
                for (PJGetValue_Data g : pvdlist) {
                    if(g.getChk()!=0){
                        c.runOnUiThread(() -> Toast.makeText(c, t.termname+" "+g.getName()+" 老师已提交", Toast.LENGTH_SHORT).show());
                        continue;
                    }
                    StringBuilder post_body_builder = new StringBuilder();
                    post_body_builder.append("term=").append(g.getTerm()).append("&courseno=").append(g.getCourseno())
                            .append("&stid=").append(g.getStid()).append("&cname=").append(URLEncoder.encode(g.getCname()))
                            .append("&name=").append(URLEncoder.encode(g.getName()))
                            .append("&teacherno=").append(g.getTeacherno())
                            .append("&courseid=").append(g.getCourseid())
                            .append("&lb=").append(g.getLb())
                            .append("&chk=").append(g.getChk())
                            .append("&can=").append(g.isCan())
                            .append("&userid=").append("&bz=%E5%A5%BD").append("&score=100");
                    String post_body = post_body_builder.toString();
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
                        c.runOnUiThread(() -> Toast.makeText(c, "网络波动，正在重新评价 "+t.termname+" "+g.getName()+" 老师", Toast.LENGTH_SHORT).show());
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
                        c.runOnUiThread(() -> Toast.makeText(c, "评教失败，检查校园网连接后重试", Toast.LENGTH_LONG).show());
                        return;
                    }
                    Log.e("term", t.term);
                    Log.e("body", post_body);
                    Log.e("resp_code", post_res.resp_code + "");
                    Log.e("response", post_res.comment);
                    Log.e("--------------", "------------------------------------");
                    c.runOnUiThread(() -> Toast.makeText(c, "正在评价 "+t.termname+" "+g.getName()+" 老师", Toast.LENGTH_SHORT).show());
                }
                flag1++;
            }
            if(flag1 == 2){
                int finalFlag = flag1;
                c.runOnUiThread(() -> Toast.makeText(c, "评教成功，一共评教了 " + finalFlag + " 个学期", Toast.LENGTH_LONG).show());
                Log.e("PJterm",finalFlag+"");
                return;
            }
        }
        if (flag == list.size()) {
            c.runOnUiThread(() -> Toast.makeText(c, "评教未开放", Toast.LENGTH_LONG).show());
            Log.e("PJterm","All terms are closed");
            return;
        } else {
            int finalFlag = flag1;
            c.runOnUiThread(() -> Toast.makeText(c, "评教成功，一共评教了 " + finalFlag + " 个学期", Toast.LENGTH_LONG).show());
            Log.e("PJterm",finalFlag+"");
        }
    }
}
