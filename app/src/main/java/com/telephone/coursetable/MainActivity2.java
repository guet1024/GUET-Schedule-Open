package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.GoToClass;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private static String ss = "<table border=\"1\" cellspacing=\"0\">\n" +
            "  <tbody><tr>\n" +
            "    <th width=\"1%\">╲</th>\n" +
            "    <th width=\"15%\" nowrap=\"\">星期一</th>\n" +
            "    <th width=\"15%\" nowrap=\"\">星期二</th>\n" +
            "    <th width=\"15%\" nowrap=\"\">星期三</th>\n" +
            "    <th width=\"15%\" nowrap=\"\">星期四</th>\n" +
            "    <th width=\"15%\" nowrap=\"\">星期五</th>\n" +
            "    <th width=\"13%\" nowrap=\"\">星期六</th>\n" +
            "    <th width=\"13%\" nowrap=\"\">星期日</th>\n" +
            "  </tr>\n" +
            "  <tr><th>1,2节</th><td align=\"center\">&nbsp;</td><td align=\"center\">数据库系统原理B<br>(10-14)02309Y<br>课号：1920737<br>数据库系统原理B<br>(7-9)<br>课号：1920737</td><td align=\"center\">物联网工程导论<br>(3-8)<br>课号：1920739<br>数值分析<br>(9-17)02308Y<br>课号：1920738</td><td align=\"center\">数据库系统原理B<br>(10-14)02309Y<br>课号：1920737<br>数据库系统原理B<br>(7-9)<br>课号：1920737</td><td align=\"center\">大学英语4<br>(1-14)17505*<br>课号：1920418</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td></tr>\n" +
            "<tr><th>3,4节</th><td align=\"center\">大学英语4<br>(1-14)17203*<br>课号：1920418<br>马克思主义基本原理概论<br>(1-3)<br>课号：1920260</td><td align=\"center\">计算机组成原理B<br>(10-15)02302Y<br>课号：1920736<br>计算机组成原理B<br>(3-9)<br>课号：1920736</td><td align=\"center\">体育4<br>(1-16)<br>课号：1921650</td><td align=\"center\">计算机组成原理B<br>(11-15)02302Y<br>课号：1920736<br>计算机组成原理B<br>(3-9)<br>课号：1920736<br>计算机组成原理B<br>(10-10)02101Y<br>课号：1920736</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td></tr>\n" +
            "<tr><th>5,6节</th><td align=\"center\">单片机原理与应用<br>(3-10)05205*<br>课号：1920699<br>数值分析<br>(11-17)02309Y<br>课号：1920738</td><td align=\"center\">基于.NET的开发技术<br>(3-9)<br>课号：1920735<br>基于.NET的开发技术<br>(10-10)03305Y<br>课号：1920735</td><td align=\"center\">单片机原理与应用<br>(3-9)<br>课号：1920699<br>单片机原理与应用<br>(10-10)17604*<br>课号：1920699</td><td align=\"center\">基于.NET的开发技术<br>(3-9)<br>课号：1920735<br>基于.NET的开发技术<br>(10-10)03305Y<br>课号：1920735</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td></tr>\n" +
            "<tr><th>7,8节</th><td align=\"center\">物联网工程导论<br>(7-8)<br>课号：1920739</td><td align=\"center\">马克思主义基本原理概论<br>(1-9)02507Y<br>课号：1920260<br>马克思主义基本原理概论<br>(13-13)02409Y<br>课号：1920260<br>马克思主义基本原理概论<br>(10-10)02409Y<br>课号：1920260</td><td align=\"center\">&nbsp;</td><td align=\"center\">马克思主义基本原理概论<br>(1-11)02208Y<br>课号：1920260</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td></tr>\n" +
            "<tr><th>晚 上</th><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td><td align=\"center\">单片机原理与应用<br>(10-10)05205*<br>课号：1920699</td><td align=\"center\">&nbsp;</td><td align=\"center\">&nbsp;</td></tr>\n" +
            "\n" +
            "  <tr>\n" +
            "    <th>备注</th>\n" +
            "    <td colspan=\"7\">大学英语4:韦汉；马克思主义基本原理概论:张明琴；体育4:蓝瑞高；计算机组成原理B:陈宏；数据库系统原理B:文益民；物联网工程导论:林科；单片机原理与应用:林科；数值分析:刘振丙；基于.NET的开发技术:陈金龙</td>\n" +
            "  </tr>\n" +
            "  \n" +
            "</tbody></table>";

    public List<GoToClass> GetList_GoToClass(String html, String term){

        List<GoToClass> gtc_list = new LinkedList<GoToClass>();
        List<ClassInfo> gtc_ci_list = new LinkedList<>();

        String courseno = null;
        long startweek = 0;
        long endweek = 0;
        String croomno = null;//教室

        String cname = null;
        String name = null;

        //一个对照表
        List<ClassInfo> gtc_ci_compare_list = GetList_ClassInfo_Compare(html);


        Document doc = Jsoup.parse(html);//解析HTML字符串返回一个Document实现

        Elements trs = doc.select("tr");



        //在此列表中的全部tr元素
        for(int j=1;j<=5;j++){

            Element tr = trs.get(j);
            String text_th = tr.select("th").get(0).text();
            Elements tds = tr.select("td");

            //一个tr元素内的全部td元素
            for(int w=0;w<7;w++) {

                Element td = tds.get(w);
                String[] sArray = td.text().split(" ");

                for (int i = 0; i < sArray.length; i++) {

                    //内容分割后存入数组，每3个数组元素合成1个内容
                    //第1个为课程名
                    if (i % 3 == 0) {
                        cname = sArray[i];//得到课程名

                        for (ClassInfo ci : gtc_ci_compare_list) {
                            if (ci.cname.equals(cname)) {
                                name = ci.name;//对照课程名，得到教师名
                                break;
                            }
                        }
                    }

                    //第2个为(开始周-结束周)和上课的教室
                    //下面开始处理
                    else if (i % 3 == 1) {

                        String text = sArray[i];
                        int lindex = text.indexOf("(");
                        int rindex = text.indexOf(")");
                        int _index = text.indexOf("-");
                        startweek = Long.valueOf(text.substring(lindex + 1, _index));
                        endweek = Long.valueOf(text.substring(_index + 1, rindex));
                        croomno = text.substring(rindex + 1);

//                        //线下课，教室是由6个字符构成
//                        if(sArray[i].length()>10){
//                            croomno = sArray[i].substring(sArray[i].length()-6);
//                        }
//                        else {
//                            croomno = null;
//                        }
//
//                        if(sArray[i].charAt(2) == '-'){
//                            startweek = (long)sArray[i].charAt(1);
//                            if(sArray[i].charAt(4) == ')'){
//                                endweek = (long)sArray[i].charAt(3);//处理类似(8-9)的情况
//                            }
//                            else{
//                                endweek = Long.parseLong(sArray[i].substring(3,4));//处理类似(9-12)的情况
//                            }
//                        }
//                        else {
//                            startweek = Long.parseLong(sArray[i].substring(1,2));
//                            endweek = Long.parseLong(sArray[i].substring(4,5));//处理类似(12-14)的情况
//                        }

                    }

                    //第3个是课号
                    else {
                        courseno = sArray[i].substring(sArray[i].length() - 7);
                        //一个th和一个td元素组合成为列表中的一个元组
                        gtc_list.add(new GoToClass(term, (endweek - startweek), text_th, courseno,
                                croomno, startweek, endweek, false, 0, 0));
                    }

                }
//                gtc_ci_list.add(new ClassInfo(courseno,null,null,null,null,null,
//                        null,null,null,cname,null,name,null,null,0,
//                        0,0,0,0,0,0));
            }
        }


        return gtc_list;
    }

    public List<ClassInfo> GetList_ClassInfo_Compare(String html){

        Document doc = Jsoup.parse(html);
        Element tr = doc.select("tr").last();

        String name = null;//
        String cname = null;//

        List<ClassInfo> ci_list = new LinkedList<ClassInfo>();

        Element td = tr.select("td").first();
        String text_td = td.text();

        String[] notice = text_td.split("；");//"大学英语4:韦汉 数值分析:刘振丙 ..."

        //中文长度为2
        int l=2;
        for(String n: notice){

            int index = n.indexOf(":");
            cname = n.substring(0, index);
            name = n.substring(index + 1);

//            //名字只有两个字
//            if(notice[i].charAt(notice[i].length()-2*l-1) == ':'){
//                name = notice[i].substring(notice[i].length()-2*l);
//                cname = notice[i].substring(0,notice[i].length()-2*l-1);
//            }
//
//            //名字有三个字
//            else if(notice[i].charAt(notice[i].length()-3*l-1) == ':'){
//                name = notice[i].substring(notice[i].length()-3*l);
//                cname = notice[i].substring(0,notice[i].length()-3*l-1);
//            }
//
//            //名字有四个字
//            else if(notice[i].charAt(notice[i].length()-4*l-1) == ':'){
//                name = notice[i].substring(notice[i].length()-4*l);
//                cname = notice[i].substring(0,notice[i].length()-4*l-1);
//            }
//
//            else{
//                //遍历寻找':'
//            }

            ci_list.add(new ClassInfo(null,null,null,null,null,null,
                    null,null,null,cname,null,name,null,null,0,
                    0,0,0,0,0,0));

        }

        return ci_list;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ClassInfo> Ccc = GetList_ClassInfo_Compare(ss);
                for(int i=0;i<Ccc.size();i++){
                    String a = Ccc.get(i).cname;
                    String b = Ccc.get(i).name;
//            System.out.println(a+":"+b);
                    Log.e(""+i, a+":"+b);


                }

                List<GoToClass> Ggg = GetList_GoToClass(ss,"2019-2020-2");
                for (int i=0;i<Ggg.size();i++){
                    long a = Ggg.get(i).startweek;
                    long b = Ggg.get(i).endweek;
                    String room = Ggg.get(i).croomno;
                    String cno = Ggg.get(i).courseno;
                    String termm = Ggg.get(i).term;
                    Log.e(""+i,termm+"("+a+"-"+b+")"+room+"   "+cno);
                }
            }
        }).start();
    }
}