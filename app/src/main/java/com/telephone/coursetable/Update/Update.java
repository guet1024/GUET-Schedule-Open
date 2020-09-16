package com.telephone.coursetable.Update;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.telephone.coursetable.BuildConfig;
import com.telephone.coursetable.Gson.Update.Release;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

public class Update {

    public static void whatIsNew(@NonNull Context c, @Nullable Runnable error, @Nullable Runnable new_version, @Nullable Runnable no_new_version, @Nullable TextView tv, @Nullable String origin, @Nullable View view) {
        final String NAME = "whatIsNew()";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(c);
        String url ="https://api.github.com/repos/Telephone2019/CourseTable/releases/latest";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    if (response == null || response.isEmpty()) {
                        if (error != null) {
                            error.run();
                        }
                        Log.e(NAME, "response is null or empty");
                    } else {
                        Log.e(NAME, "response: " + response);
                        Release latest = new Gson().fromJson(response, Release.class);
                        String version = "v" + BuildConfig.VERSION_NAME;
                        String latest_tag = latest.getTag_name();
                        if (!latest_tag.equals(version)) {
                            if (new_version != null) {
                                new_version.run();
                            }
                            if (tv != null && origin != null) {
                                tv.setText(origin + "    new " + latest_tag + "⇧");
                            }
                            if (view != null) {
                                view.setOnClickListener(view1 -> {
                                    Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
                                    c.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                });
                            }
                            Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable/releases/latest");
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
                            NotificationManagerCompat.from(c).notify(MyApp.notification_id_new_version, notification);
                        } else {
                            if (no_new_version != null) {
                                no_new_version.run();
                            }
                        }
                    }
                },
                net_error -> {
                    Log.e(NAME, "error: " + net_error);
                    if (error != null) {
                        error.run();
                    }
                }
        );
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
