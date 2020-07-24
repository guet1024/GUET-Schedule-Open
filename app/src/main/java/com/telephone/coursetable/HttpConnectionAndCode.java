package com.telephone.coursetable;

import java.net.HttpURLConnection;

public class HttpConnectionAndCode {
    public HttpURLConnection c;
    public int code;
    public String comment;
    public HttpConnectionAndCode(HttpURLConnection cn, int code_){
        c = cn;
        code = code_;
        comment = null;
    }
    public HttpConnectionAndCode(HttpURLConnection cn, int code_, String comm){
        c = cn;
        code = code_;
        comment = comm;
    }
}
