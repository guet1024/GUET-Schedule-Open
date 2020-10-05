package com.telephone.coursetable.Https;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.MyApp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;


public class GetBitmap {
    /**
     * @non-ui
     * @return
     * - 0 GET success
     * - -1 cannot open url
     * - -5 cannot get response
     * - -6 response check fail
     * - -7 302
     * @clear
     */
    public static HttpConnectionAndCode get(@NonNull final String u,
                                            @Nullable final String[] parms,
                                            @NonNull final String user_agent,
                                            @NonNull final String referer,
                                            @Nullable final String cookie,
                                            @Nullable final String cookie_delimiter){
        URL url = null;
        HttpsURLConnection cnt = null;
        String response = null;
        Bitmap bmp = null;
        int resp_code = 0;
        try {
            StringBuilder u_bulider = new StringBuilder();
            u_bulider.append(u);
            if (parms != null && parms.length > 0) {
                u_bulider.append("?").append(TextUtils.join("&", parms));
            }
            String url_s = u_bulider.toString();
            if (MyApp.ip_override){
                url_s = url_s.replace(MyApp.guet_v_domain, MyApp.guet_v_ip);
            }
            url = new URL(url_s);
            cnt = (HttpsURLConnection) url.openConnection();
            cnt.setDoOutput(true);
            cnt.setDoInput(true);
            cnt.setRequestProperty("User-Agent", user_agent);
            cnt.setRequestProperty("Referer", referer);
            if (cookie != null){
                cnt.setRequestProperty("Cookie", cookie);
            }
            cnt.setRequestMethod("GET");
            cnt.setInstanceFollowRedirects(false);
            cnt.setRequestProperty("Connection", "keep-alive");
            cnt.setReadTimeout(1000);
            cnt.setConnectTimeout(1000);
            SSLSocketFactory exist_ssl = MyApp.getCurrentApp().ssl;
            if (exist_ssl != null){
                cnt.setSSLSocketFactory(exist_ssl);
            }
            if (MyApp.ip_override && cnt.getURL().toString().contains(MyApp.guet_v_ip)) {
                cnt.setRequestProperty("Host", MyApp.guet_v_domain);
                cnt.setHostnameVerifier((hostname, session) -> HttpsURLConnection.getDefaultHostnameVerifier().verify(MyApp.guet_v_domain, session));
            }
            cnt.connect();
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-1);
        }
        MyApp.getCurrentApp().ssl = cnt.getSSLSocketFactory();
        try {
            resp_code = cnt.getResponseCode();
            if (resp_code == 302){
                return new HttpConnectionAndCode(cnt, -7, "");
            }
            response = "";
            bmp = BitmapFactory.decodeStream(cnt.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpConnectionAndCode(-5);
        }

        //get cookie from server
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
                List<HttpCookie> cookieList = cookieman.getCookieStore().getCookies();
                List<String> cookieStringList = new LinkedList<>();
                for (HttpCookie httpCookie : cookieList){
                    String str = httpCookie.getName() + "=" + httpCookie.getValue();
                    cookieStringList.add(str);
                }
                String cookie_join = TextUtils.join(cookie_delimiter, cookieStringList);
                cookie_builder.append(cookie_join);
            }
            set_cookie = cookie_builder.toString();
        }

        //do not disconnect, keep alive

        //do not disconnect, keep alive
        //if cookie_delimiter != null but no server cookie, set_cookie = ""
        //if no response, response = ""
        HttpConnectionAndCode res = new HttpConnectionAndCode(cnt, 0, response, set_cookie, resp_code);
        res.obj = bmp;
        return res;
    }
}
