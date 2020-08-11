package com.telephone.coursetable.Outside;

import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.GoToClass;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Parse {
    public static Map<String, String> GetMapFromHtml_Cno_Cname(String html){
        Map<String, String> res = new HashMap<>();
        Document doc = Jsoup.parse(html);
        Elements course_trs = doc.select("body > table > tbody > tr");
        for (int i = 1; i < course_trs.size() - 1; i++){
            Element tr = course_trs.get(i);
            Elements tds = tr.select("td");
            for (Element td : tds){
                String node_text = td.text();
                String[] text_rows = node_text.split(" ");
                for(int j = 0; j < text_rows.length - 2; j += 3){
                    String cname = text_rows[j];
                    String cno = text_rows[j + 2].split("：")[1];
                    res.put(cno, cname);
                }
            }
        }
        return res;
    }

    public static Map<String, String> GetMapFromHtml_Cno_Ccode(String html){
        Map<String, String> res = new HashMap<>();
        Document doc = Jsoup.parse(html);
        Elements course_trs = doc.select("body > form > table").get(2).select("table > tbody > tr");
        for (int i = 1; i < course_trs.size(); i++){
            Element tr = course_trs.get(i);
            Elements tds = tr.select("td");
            res.put(tds.get(0).text(), tds.get(1).text());
        }
        return res;
    }

    public static Map<String, String> GetMapFromHtml_Cname_Tname(String html){
        Map<String, String> res = new HashMap<>();
        Document doc = Jsoup.parse(html);
        Element comment_tr = doc.select("body > table > tbody > tr").last();
        Element td = comment_tr.select("td").first();
        String comment = td.text();
        String[] infos = comment.split("；");
        for (String info : infos){
            String[] kv = info.split(":");
            res.put(kv[0], kv[1]);
        }
        return res;
    }

    public static List<GoToClass> GetListFromHtml_GoToClass(String html, String term){
        List<GoToClass> res = new LinkedList<>();
        Document doc = Jsoup.parse(html);
        Elements course_trs = doc.select("body > table > tbody > tr");
        for (int i = 1; i < course_trs.size() - 1; i++){
            Element tr = course_trs.get(i);
            Elements tds = tr.select("td");
            int weekday = 0;
            for (Element td : tds){
                weekday++;
                String node_text = td.text();
                String[] text_rows = node_text.split(" ");
                for(int j = 0; j < text_rows.length - 2; j += 3){

                    String week_and_room = text_rows[j + 1];
                    int lindex = week_and_room.indexOf("(");
                    int _index = week_and_room.indexOf("-");
                    int rindex = week_and_room.indexOf(")");
                    long startweek = Long.valueOf(week_and_room.substring(lindex + 1, _index));
                    long endweek = Long.valueOf(week_and_room.substring(_index + 1, rindex));
                    String room = week_and_room.substring(rindex + 1);

                    String cno = text_rows[j + 2].split("：")[1];

                    res.add(new GoToClass(term, weekday, i+"", cno, 0, room, startweek, endweek, false, 0));
                }
            }
        }
        return res;
    }

    public static List<ClassInfo> GetListFromHtml_ClassInfo(Map<String, String> cno_cname, Map<String, String> cname_tname, Map<String, String> cno_ccode){
        List<ClassInfo> res = new LinkedList<>();
        for (String cno : cno_cname.keySet()){
            String cname = cno_cname.get(cno);
            String tname = cname_tname.get(cname);
            String ccode = cno_ccode.get(cno);
            res.add(new ClassInfo(cno, null, null, null, null, null,
                    null, null, null, cname, null, tname, ccode,
                    null, 0, 0, 0, 0, 0, 0, 0));
        }
        return res;
    }
}
