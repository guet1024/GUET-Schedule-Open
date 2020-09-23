package com.telephone.coursetable.Library;

import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Https.Get;

public class GetHttp {
    public static String getHtml(String cookie, String message,int page) {
        String url = "https://202.193.64.75/http/77726476706e69737468656265737421a2a713d276693b1e2958c7fdcb0c/NTRdrBookRetr.aspx" ;
        HttpConnectionAndCode html = Get.get(
                url,
                new String[]{
                        "page=" + page,
                        "strKeyValue=" + message,
                        "strType=text",
                        "tabletype=*",
                        "RepSearch=",
                        "strKeyValue2=",
                        "",
                        "strAllAuthor=",
                        "strAllPubyear=",
                        "strAllPublish=",
                        "strAllLanguage=",
                        "strCondition2=",
                        "strpageNum=10",
                        "strVip=",
                        "strStartYear=",
                        "strEndYear=",
                        "strPublisher=",
                        "strAuthorer=",
                        "strSortType=",
                        "strSort=asc",
                },
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36 Edg/84.0.522.63",
                "https://202.193.64.75/http/77726476706e69737468656265737421a2a713d276693b1e2958c7fdcb0c/NTRdrBookRetr.aspx?page=" + page + "&strKeyValue=" + message + "&strType=text&tabletype=*&RepSearch=&strKeyValue2=&&strAllAuthor=&strAllPubyear=&strAllPublish=&strAllLanguage=&strCondition2=&strpageNum=10&strVip=&strStartYear=&strEndYear=&strPublisher=&strAuthorer=&strSortType=&strSort=asc",
                cookie,
                null,
                null,
                null,
                null,
                null
        );
        if (html.code == 0) {
            return html.comment;
        }
        return null;
    }

    public static String getXml(String cookie, String id, String message, int page) {
            String url = "https://202.193.64.75/http/77726476706e69737468656265737421a2a713d276693b1e2958c7fdcb0c/GetlocalInfoAjax.aspx";
            HttpConnectionAndCode xml = Get.get(
                    url,
                    new String[]{
                            "vpn-12-o1-202.193.70.139=",
                            "ListRecno=" + id ,
                    },
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36 Edg/85.0.564.41",
                    "https://202.193.64.75/http/77726476706e69737468656265737421a2a713d276693b1e2958c7fdcb0c/NTRdrBookRetr.aspx?page=" + page + "&strKeyValue="+ message +"&strType=text&tabletype=*&RepSearch=&strKeyValue2=&&strAllAuthor=&strAllPubyear=&strAllPublish=&strAllLanguage=&strCondition2=&strpageNum=10&strVip=&strStartYear=&strEndYear=&strPublisher=&strAuthorer=&strSortType=&strSort=asc" ,
                    cookie,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (xml.code == 0) {
                return xml.comment;
            }
            return null;
    }
}
