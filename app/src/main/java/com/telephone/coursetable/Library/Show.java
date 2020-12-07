package com.telephone.coursetable.Library;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Show {

    public static  List<List<Map.Entry<String, String>>>  getBookInfo(String html){
        List<List<Map.Entry<String, String>>> books = new LinkedList<>();
        List<Map.Entry<String, String>> bookinfoes;
        Document doc = Jsoup.parse(html);
        Elements book_infoes = doc.select("html > body > form#form1 > div.body > div.mainbody2_out > div.mainbody2_in > div.mainbody > div.content > div.main > ul.resultlist ");
        for (int i = 0; i < book_infoes.size(); i++){

            Element book = book_infoes.get(i);

            bookinfoes = new LinkedList<>();

            Elements bokkids = book.select("ul.resultlist > li > input#StrTmpRecno.inputISBN");
            String bokkid = bokkids.attr("value");
            bookinfoes.add(com.telephone.coursetable.Database.Methods.Methods.entry("书籍编号：", bokkid));
//            com.telephone.coursetable.LogMe.LogMe.e("书籍编号：", bokkid);

            Elements titles = book.select("ul.resultlist > li > div.into > h3.title > a");
            String title = titles.get(0).ownText();
            bookinfoes.add(com.telephone.coursetable.Database.Methods.Methods.entry("名称：", title));
//            com.telephone.coursetable.LogMe.LogMe.e("名称：", title);

            Element titlebar = book.select("ul.resultlist > li > div.into > div.titbar").get(0);
            Elements spans = titlebar.select("span");
            for (Element span : spans){
                String name = span.ownText();
                String value = span.select("span > strong").get(0).ownText();
                if( value.equals("v., :") ) value="";
                bookinfoes.add(com.telephone.coursetable.Database.Methods.Methods.entry(name, value));
//                com.telephone.coursetable.LogMe.LogMe.e(name, value);
            }

            books.add(bookinfoes);
//            com.telephone.coursetable.LogMe.LogMe.e("#######", "###############################");
        }
        return books;
    }

    public static List<List<List<Map.Entry<String, String>>>> getBookLocal(String xml) {
        Document doc_xml = Jsoup.parse(xml);
        Elements statuses = doc_xml.select("bookinfo > books");
        List<List<List<Map.Entry<String, String>>>> books = new LinkedList<>();
        List<List<Map.Entry<String, String>>> locals;
        for(Element local : statuses) {
            locals = new LinkedList<>();
            Elements bs = local.select("books > book");
            for (Element b : bs){
                List<Map.Entry<String, String>> local_node = new LinkedList<>();

                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("图书编号：", b.select("book > bookid ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("条码号：", b.select("book > barcode ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("索书号：", b.select("book > callno ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("馆藏状态：", b.select("book > localstatu ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("馆藏地点：", b.select("book > local ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("流通类型：", b.select("book > cirType ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("预约处理：", b.select("book > servicePoint ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("出借数据编号：", b.select("book > loanDatanum ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("登录号：", b.select("book > loannum ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("藏书编号：", b.select("book > hldcount ").get(0).ownText()));
                local_node.add(com.telephone.coursetable.Database.Methods.Methods.entry("所有藏书编号：", b.select("book > hldallnum ").get(0).ownText()));

//                com.telephone.coursetable.LogMe.LogMe.e("图书编号", b.select("book > bookid ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("条码号",  b.select("book > barcode ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("索书号",  b.select("book > callno ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("馆藏状态", b.select("book > localstatu ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("馆藏地点", b.select("book > local ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("流通类型", b.select("book > cirType ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("预约处理", b.select("book > servicePoint ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("出借数据编号", b.select("book > loanDatanum ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("登录号", b.select("book > loannum ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("藏书编号", b.select("book > hldcount ").get(0).ownText());
//                com.telephone.coursetable.LogMe.LogMe.e("所有藏书编号", b.select("book > hldallnum ").get(0).ownText());

                locals.add(local_node);

                com.telephone.coursetable.LogMe.LogMe.e("...................", ".............................");
            }
            books.add(locals);
        }
        return books;
    }


}
