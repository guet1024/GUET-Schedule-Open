package com.telephone.coursetable.Http;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Post {
    /**
     * @non-ui
     * @return
     * - 0 GET success
     * - -1 cannot open url
     * - -2 cannot close input stream
     * - -3 can not get output stream
     * - -4 POST send body fail
     * - -5 cannot get response
     */
    public static HttpConnectionAndCode post(@NonNull final String u,
                                            @NonNull final List<String> parms,
                                            @NonNull final String user_agent,
                                            @Nullable final String data,
                                            @Nullable final String cookie,
                                            @Nullable final String tail,
                                            @Nullable final String cookie_delimiter){
        URL url = null;
        HttpURLConnection cnt = null;
        DataOutputStream dos = null;
        InputStreamReader in = null;
        String response = null;
        int resp_code = 0;
        try {
            StringBuilder u_bulider = new StringBuilder();
            u_bulider.append(u);
            if (!parms.isEmpty()) {
                u_bulider.append("?").append(TextUtils.join("&", parms));
            }
            url = new URL(u);
            cnt = (HttpURLConnection) url.openConnection();
            cnt.setDoOutput(true);
            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", user_agent);
            if (cookie != null){
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("POST");
            cnt.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-1);
        }
        String body = "";
        if (data != null){
            body += data;
        }
        try {
            dos = new DataOutputStream(cnt.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-3);
        }
        try {
            dos.writeBytes(body);
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-4);
        }
        try {
            resp_code = cnt.getResponseCode();
            in = new InputStreamReader(cnt.getInputStream());
            //getContentLength() returns the "Content-Length" value in the response header
            int content_len = cnt.getContentLength();
            StringBuilder response_builder = new StringBuilder();
            for (int i = 0; i < content_len; i++){
                //the conversion to char is necessary
                response_builder.append((char)in.read());
            }
            response = response_builder.toString();
            if (tail != null) {
                if (response.contains(tail)) {
                    response = response.substring(0, response.indexOf(tail) + tail.length());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-5);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-2);
        }

        String set_cookie = null;
        if (cookie_delimiter != null) {
            CookieManager cookieman = new CookieManager();
            StringBuilder cookie_builder = new StringBuilder();
            //getHeaderFields() returns the header fields of response
            List<String> cookies = cnt.getHeaderFields().get("Set-Cookie");
            if (cookies != null) {
                for (String cookie_resp : cookies) {
                    cookieman.getCookieStore().add(null, HttpCookie.parse(cookie_resp).get(0));
                }
            }
            if (cookieman.getCookieStore().getCookies().size() > 0) {
                cookie_builder.append(TextUtils.join(cookie_delimiter, cookieman.getCookieStore().getCookies()));
            }
            set_cookie = cookie_builder.toString();
        }

        //do not disconnect, keep alive
        return new HttpConnectionAndCode(cnt, 0, response, set_cookie, resp_code);
    }
}
