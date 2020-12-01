package com.telephone.coursetable.Update;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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
import java.io.FilenameFilter;
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
//        String url ="https://gitee.com/api/v5/repos/telephone2019/guet-curriculum/releases/latest";
//        String url ="https://guetcob.com:44334/vmd5apk";
        String url ="https://guetcob.com:44334/vmd5urlapk";
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
                        String version = BuildConfig.VERSION_NAME;
                        String latest_tag = response.substring(0, response.indexOf(' '));
                        String md5 = response.substring(response.indexOf(' ') + 1, response.indexOf(' ', response.indexOf(' ') + 1));
                        String apk_url = response.substring(response.indexOf(' ', response.indexOf(' ') + 1) + 1);
                        if (!latest_tag.equals(version)) {
                            if (new_version != null) {
                                new_version.run();
                            }
                            if (tv != null && origin != null && app != null) {
                                app.runOnUiThread(()->tv.setText(origin + "    new v" + latest_tag + "⇧"));
                            }
                            if (view != null && app != null) {
                                app.runOnUiThread(() -> view.setOnClickListener(view1 -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                                    builder.setMessage("您想要查看最新版本更新详情吗?");
                                    builder.setPositiveButton("查看详情",
                                            (dialogInterface, i) -> {
                                                Uri see_uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
                                                c.startActivity(new Intent(Intent.ACTION_VIEW, see_uri));
                                            })
                                            .setNegativeButton("直接下载",
                                                    (dialogInterface, i) -> {
                                                        app.runOnUiThread(()->
//                                                                Snackbar.make(view,
//                                                                        "正在下载新版本，下载完成后将会自动安装，请不要关闭应用",
//                                                                        BaseTransientBottomBar.LENGTH_LONG
//                                                                ).setTextColor(Color.WHITE).show()
                                                                        Snackbar.make(view,
                                                                                "正在跳转到浏览器下载......",
                                                                                BaseTransientBottomBar.LENGTH_SHORT
                                                                        ).setTextColor(Color.WHITE).show()
                                                        );
//                                                        Update.use_download_manager_to_download_and_install(
//                                                                c, "GUET课程表v" + latest_tag + ".apk",
//                                                                c.getString(R.string.update_download_url),
//                                                                md5
//                                                        );
                                                        Uri see_uri = Uri.parse(apk_url);
                                                        c.startActivity(new Intent(Intent.ACTION_VIEW, see_uri));
                                                    });
                                    builder.create().show();
                                }));
                            }
                            Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
                            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(c, 0, notificationIntent, 0);
                            Notification notification =
                                    new NotificationCompat.Builder(c, MyApp.notification_channel_id_update)
                                            .setContentTitle("新版发布: " + latest_tag)
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(latest_tag + "版本已发布! \n点击查看更新详情\n转到 “更多 -> 应用更新” 中即可下载安装"))
                                            .setSmallIcon(R.drawable.feather_pen_trans)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true)
                                            .setTicker("新版发布: " + latest_tag)
                                            .build();
                            if (already != null && already.equals(latest_tag)){ // if already notify, skip notify
                                return;
                            }
                            NotificationManagerCompat.from(c).notify(MyApp.notification_id_new_version, notification);
                            MyApp.getCurrentApp().new_version = latest_tag;
                        } else {
                            if (no_new_version != null) {
                                no_new_version.run();
                            }
                            File[] apk_fileList = c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name.endsWith(".apk");
                                }
                            });
                            for(File apk : apk_fileList){
                                LogMe.e(NAME, "deleted one apk file");
                                apk.delete();
                            }
                        }
                    }
                },
                net_error -> {
                    com.telephone.coursetable.LogMe.LogMe.e(NAME, "error: " + net_error);
                    if (error != null) {
                        error.run();
                    }
                    // edit by Telephone, 2020/11/20 10:29. Now, no need to override the on-click-listener. Just let it re-query.
//                    if (tv != null && origin != null && app != null) {
//                        app.runOnUiThread(()->tv.setText(origin + "　网络错误，请重试/✈"));
//                    }
//                    if (view != null && app != null) {
//                        app.runOnUiThread(()->view.setOnClickListener(view1 -> {
//                            Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
//                            c.startActivity(new Intent(Intent.ACTION_VIEW, uri));
//                        }));
//                    }
                }
//        );
        ){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, StandardCharsets.UTF_8);
                } catch (Exception e) {
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

    public static void use_download_manager_to_download_and_install(Context c, String file_name, String url, String md5) {
        final String NAME = "use_download_manager_to_download()";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType("application/vnd.android.package-archive");
        request.setAllowedOverMetered(true);
        request.setTitle("正在下载安装包");
        request.setDescription(file_name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalFilesDir(c, Environment.DIRECTORY_DOWNLOADS, file_name);
        File file = new File(c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + file_name);
        File new_file = new File(c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + md5);
        file.delete();
        LogMe.e(NAME, "deleted the file already exists with the same name");
        if (new_file.exists()){
            LogMe.e(NAME, file_name + " already exists, installing " + file_name + "...");
            install(c, new_file);
            return;
        }
        DownloadManager manager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        c.registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String dmd5 = getFileMD5(file);
                        LogMe.e(NAME, "the MD5 of the downloaded file is: " + dmd5);
                        if (!dmd5.equals(md5)){
                            file.delete();
                            LogMe.e(NAME, "MD5 verification fail, downloaded file deleted, not install");
                        }else {
                            LogMe.e(NAME, "MD5 verification success, installing...");
                            LogMe.e(NAME, "rename res: " + file.renameTo(new_file));
                            LogMe.e(NAME, "installing " + file_name + "...");
                            install(c, new_file);
                        }
                    }
                },
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        );
        LogMe.e(NAME, "start to download: " + file_name);
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
