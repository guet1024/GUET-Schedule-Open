package com.telephone.coursetable.Update;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.telephone.coursetable.BuildConfig;
import com.telephone.coursetable.Gson.Update.Release;
import com.telephone.coursetable.LogMe.LogMe;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Update {

    public static void whatIsNew(@NonNull Context c, @Nullable AppCompatActivity app, @Nullable Runnable error, @Nullable Runnable new_version, @Nullable Runnable no_new_version, @Nullable TextView tv, @Nullable String origin, @Nullable View view, @Nullable String already) {
        final String NAME = "whatIsNew()";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(c);
//        String url ="https://api.github.com/repos/Telephone2019/CourseTable/releases/latest";
        String url ="https://gitee.com/api/v5/repos/telephone2019/guet-curriculum/releases/latest";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    if (response == null || response.isEmpty()) {
                        if (error != null) {
                            error.run();
                        }
                        com.telephone.coursetable.LogMe.LogMe.e(NAME, "response is null or empty");
                    } else {
                        com.telephone.coursetable.LogMe.LogMe.e(NAME, "response: " + response);
                        Release latest = new Gson().fromJson(response, Release.class);
                        String version = "v" + BuildConfig.VERSION_NAME;
                        String latest_tag = latest.getTag_name();
                        if (!latest_tag.equals(version)) {
                            if (new_version != null) {
                                new_version.run();
                            }
                            if (tv != null && origin != null && app != null) {
                                app.runOnUiThread(()->tv.setText(origin + "    new " + latest_tag + "⇧"));
                            }
                            if (view != null && app != null) {
                                app.runOnUiThread(()->view.setOnClickListener(view1 -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                                    builder.setMessage("您想要查看最新版本更新详情吗?");
                                    builder.setPositiveButton("查看详情",
                                            (dialogInterface, i) -> {
                                                Uri see_uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
//                                            Uri see_uri = Uri.parse("https://gitee.com/telephone2019/guet-curriculum/releases/" + latest_tag);
                                                c.startActivity(new Intent(Intent.ACTION_VIEW, see_uri));
                                            })
                                            .setNegativeButton("直接下载",
                                                    (dialogInterface, i) -> {
                                                        String body = latest.getBody();
                                                        String apk_name = body.substring(0, body.indexOf(".apk") + 4);
                                                        Uri download_uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/download/" + latest_tag + "/" + apk_name);
                                                        c.startActivity(new Intent(Intent.ACTION_VIEW, download_uri));
                                                    });
                                    builder.create().show();
                                }));
                            }
                            Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
//                            Uri uri = Uri.parse("https://gitee.com/telephone2019/guet-curriculum/releases/" + latest_tag);
                            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(c, 0, notificationIntent, 0);
                            Notification notification =
                                    new NotificationCompat.Builder(c, MyApp.notification_channel_id_update)
                                            .setContentTitle("新版发布: " + latest_tag)
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(latest_tag + "版本已发布! \n版本更新内容:\n    " + latest.getName() + "\n点击查看详情/下载安装"))
                                            .setSmallIcon(R.drawable.feather_pen_trans)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true)
                                            .setTicker("新版发布: " + latest_tag)
                                            .build();
                            if (already != null && already.equals(latest_tag)){
                                return;
                            }
                            NotificationManagerCompat.from(c).notify(MyApp.notification_id_new_version, notification);
                            MyApp.getCurrentApp().new_version = latest_tag;
                        } else {
                            if (no_new_version != null) {
                                no_new_version.run();
                            }
                        }
                    }
                },
                net_error -> {
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "error: " + net_error);
                    if (error != null) {
                        error.run();
                    }
                    if (tv != null && origin != null && app != null) {
                        app.runOnUiThread(()->tv.setText(origin + "　网络错误，请重试/✈"));
                    }
                    if (view != null && app != null) {
                        app.runOnUiThread(()->view.setOnClickListener(view1 -> {
                            Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
                            c.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        }));
                    }
                }
//        );
        ){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    try {
                        parsed = new String(response.data, "GBK");
                    } catch (UnsupportedEncodingException ex) {
                        parsed = new String(response.data);
                    }
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private static void clean(Socket s, FileOutputStream fos) {
        try {
            s.close();
            fos.close();
        }catch (Exception ignored){}
    }

    private static void install(Context c, File file){
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        Uri shared_uri =  FileProvider.getUriForFile(c,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file);
        intent.setDataAndType(shared_uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        c.startActivity(intent);
    }

    /**
     *
     * @param c
     * @param file_name the filename to save the downloaded file as
     * @param md5 the MD5 value used to verify the downloaded file
     * @return
     * - true : download success, start installing
     * - false : something went wrong
     */
    public static boolean download_and_install(Context c, String file_name, String md5){
        final String NAME = "download_and_install()";
        String dirname = "downloaded_apks";
        Socket isocket = null;
        FileOutputStream fos = null;
        int dlen = 0;
        int wlen = 0;
        try {
            File dir = new File(c.getFilesDir().getAbsolutePath() + "/" + dirname);
            File file = new File(dir, file_name);
            File new_file = new File(dir, md5);
            if (new_file.exists()){
                LogMe.e(NAME, "installing " + file_name + "...");
                install(c, new_file);
                return true;
            }
            try {
                dir.mkdir();
                file.createNewFile();
            }catch (Exception ignored){}
            FileOutputStream os = new FileOutputStream(file);
            fos = os;
            Socket socket = new Socket();
            isocket = socket;
            socket.setSoTimeout(15000); // 15s
            socket.connect(new InetSocketAddress("47.115.61.46", 65535));
            if (!socket.isConnected()){
                LogMe.e(NAME, "can not connect the socket, fail...");
                clean(socket, os);
                return false;
            }
            LogMe.e(NAME, "local address: " + socket.getLocalAddress().getHostAddress() + " local port: " + socket.getLocalPort());
            InputStream is = socket.getInputStream();
            OutputStream sos = socket.getOutputStream();
            byte[] request = new String("GET / HTTP/1.1").getBytes(StandardCharsets.UTF_8);
            sos.write(request);
            StringBuilder text = new StringBuilder();
            byte[] bytes = new byte[1];
            while (true){
                int r = is.read();
                if (r == -1){ // reach the end before break
                    LogMe.e(NAME, "read HTTP fail...");
                    clean(socket, os);
                    return false;
                }
                // UTF-8使用8位码元和变长编码，通过以下措施：
                //     1. 将非标准ASCII码元的最高位置1
                //     2. 标准ASCII字符的UTF-8编码为单字节，其余字符的UTF-8编码使用一个以上的字节
                // UTF-8可以兼容标准ASCII
                // 这也就意味着，对于标准ASCII字符，可以像使用标准ASCII编码一样使用UTF-8编码
                bytes[0] = (byte)r;
                text.append(new String(bytes, StandardCharsets.UTF_8));
                int size = text.length();
                String current = text.toString();
                if (size > 4 && current.substring(size - 4).equals("\r\n\r\n")){
                    break;
                }
            }
            String http = text.toString();
            LogMe.e(NAME, http);
            String symbol1 = "Content-Length: ";
            String symbol2 = "\r\n";
            int index1 = http.indexOf(symbol1);
            int index2 = http.indexOf(symbol2, index1);
            String len_s = http.substring(index1 + symbol1.length(), index2);
            int len = Integer.parseInt(len_s);
            LogMe.e(NAME, "get Content-Length: " + len);
            byte[] apk = new byte[len];
            int total = 0;
            while (total < len) {
                int res = is.read(apk, total, len - total);
                if (res == -1){
                    LogMe.e(NAME, "when getting apk, expect something but reach the end, fail...");
                    clean(socket, os);
                    return false;
                }
                total += res;
                dlen += res;
                LogMe.e(NAME, "receive " + res + " data, total: " + total + " , need: " + len);
            }
            os.write(apk, 0, len);
            wlen += len;
            LogMe.e(NAME, "downloaded file has been written to " + file_name);
            socket.close();
            os.close();
            String dmd5 = getFileMD5(file);
            LogMe.e(NAME, "the MD5 of the downloaded file is: " + dmd5);
            if (!dmd5.equals(md5)){
                LogMe.e(NAME, "MD5 verification fail");
                return false;
            }
            LogMe.e(NAME, "rename res: " + file.renameTo(new_file));
            LogMe.e(NAME, "installing " + file_name + "...");
            install(c, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            LogMe.e(NAME, "dlen = " + dlen + " wlen = " + wlen);
            clean(isocket, fos);
            return false;
        }
    }

    /**
     * thanks the author of: http://blog.csdn.net/l2show/article/details/48182367
     * @param file
     * @return
     */
    private static String getFileMD5(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                digest.update(buf, 0, len);
            }
            in.close();
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

}
