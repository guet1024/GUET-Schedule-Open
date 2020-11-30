package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.Locate;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Gson.Comment.Comment;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Https.Get;
import com.telephone.coursetable.Https.Post;
import com.telephone.coursetable.LogMe.LogMe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Comments extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ua = "guet-coursetable";
    private static final String referer = "guet-coursetable";

    private View snack_bar_root_view;

    public static final String EXTRA_key = "key";
    public static final String EXTRA_key_value = "key_v";
    public static final String EXTRA_sid = "sid";
    public static final String EXTRA_name = "name";
    public static final String EXTRA_tname = "tname";
    public static final String EXTRA_ccd = "ccd";

    public static final String cno_key = "cno";
    public static final String tno_key = "tno";
    public static final String cname_key = "cname";

    private String key;
    private String key_value;
    private String sid;
    private String name;
    private String tname;
    private CourseCardData ccd;

    private String getcmturl;
    private String putcmturl;

    private volatile boolean visible = true;
    private volatile Intent outdated = null;

    synchronized public boolean setOutdated(){
        if (visible) return false;
        outdated = new Intent(this, MainActivity.class);
        return true;
    }

    synchronized public void hide(){
        visible = false;
    }

    synchronized public void show(){
        visible = true;
        if (outdated != null){
            startActivity(outdated);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    @Override
    protected void onPause() {
        hide();
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        CourseCard.startMe(Comments.this, ccd);
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.COMMENT);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_comments);
        ((RecyclerView)findViewById(R.id.comments_list_view)).setLayoutManager(new LinearLayoutManager(Comments.this));
        ((RecyclerView)findViewById(R.id.comments_list_view)).setAdapter(new CommentsAdapter(new LinkedList<>()));
        ((SwipeRefreshLayout)findViewById(R.id.comment_swipe_refresh)).setOnRefreshListener(Comments.this);
        ((SwipeRefreshLayout)findViewById(R.id.comment_swipe_refresh)).setColorSchemeResources(
                R.color.colorPrimary
        );

        snack_bar_root_view = findViewById(R.id.comments_list_view);

        key = getIntent().getStringExtra(EXTRA_key);
        key_value = getIntent().getStringExtra(EXTRA_key_value);
        sid = getIntent().getStringExtra(EXTRA_sid);
        name = getIntent().getStringExtra(EXTRA_name);
        tname = getIntent().getStringExtra(EXTRA_tname);
        ccd = MyApp.gson.fromJson(getIntent().getStringExtra(EXTRA_ccd), CourseCardData.class);

        getcmturl = getString(R.string.get_cmts_url_s) + key + getString(R.string.get_put_cmts_url_e);
        putcmturl = getString(R.string.put_cmts_url_s) + key + getString(R.string.get_put_cmts_url_e);

        if (tname != null){
            getSupportActionBar().setTitle(tname);
        }else {
            getSupportActionBar().setTitle(key_value);
        }

        ((SwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh)).setRefreshing(true);
        onRefresh();
    }

    public static void start(@NonNull Context c, @NonNull String key, @NonNull String key_value, @NonNull String sid, @NonNull String name, @Nullable String tname, @NonNull CourseCardData ccd){
        Intent intent = new Intent(c, Comments.class);
        intent.putExtra(EXTRA_key, key);
        intent.putExtra(EXTRA_key_value, key_value);
        intent.putExtra(EXTRA_sid, sid);
        intent.putExtra(EXTRA_name, name);
        intent.putExtra(EXTRA_tname, tname);
        intent.putExtra(EXTRA_ccd, MyApp.gson.toJson(ccd));
        c.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                CourseCard.startMe(Comments.this, ccd);
                break;
            case R.id.add_a_comment:
                final View dialog_view = getLayoutInflater().inflate(R.layout.comment_dialog, null);
                final EditText inputbox = (EditText) dialog_view.findViewById(R.id.comment_dialog_inputbox);
                Login.getAlertDialog(
                        this,
                        null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cmt = inputbox.getText().toString();
                                if (cmt.isEmpty()) {
                                    Toast.makeText(Comments.this, "评论不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    ((SwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh)).setRefreshing(true);
                                    new Thread(() -> {
                                        HttpConnectionAndCode post_res = null;
                                        try {
                                            post_res = Post.post(
                                                    putcmturl,
                                                    new String[]{
                                                            key + "=" + URLEncoder.encode(key_value, "utf-8"),
                                                            "sno" + "=" + sid
                                                    },
                                                    ua,
                                                    referer,
                                                    name + " " + cmt,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    true
                                            );
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                            post_res = new HttpConnectionAndCode(99);
                                        }
                                        HttpConnectionAndCode post_res_f = post_res;
                                        runOnUiThread(() -> {
                                            if (post_res_f.code == 0) {
                                                Snackbar.make(snack_bar_root_view, "评论发表成功", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                                onRefresh();
                                            }else if (post_res_f.c != null && post_res_f.resp_code == 500){
                                                Snackbar.make(snack_bar_root_view, "评论发表失败(服务器错误)", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                                ((SwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh)).setRefreshing(false);
                                            }else {
                                                Snackbar.make(snack_bar_root_view, "评论发表失败(网络异常)", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                                                ((SwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh)).setRefreshing(false);
                                            }
                                        });
                                    }).start();
                                }
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        },
                        dialog_view,
                        "发表评论",
                        "发表",
                        "取消"
                ).show();
                break;
        }
        return true;
    }

    private void setAdapter(@NonNull List<Comment> data_list){
        RecyclerView comment_list_view = findViewById(R.id.comments_list_view);
        ((CommentsAdapter)comment_list_view.getAdapter()).setData(data_list);
        comment_list_view.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        final String NAME = "onRefresh()";
        new Thread(() -> {
            HttpConnectionAndCode get_comments_res = null;
            try {
                get_comments_res = Get.get(
                        getcmturl,
                        new String[]{
                                key + "=" + URLEncoder.encode(key_value, "utf-8")
                        },
                        ua,
                        referer,
                        null,
                        null,
                        null,
                        "]",
                        null,
                        null,
                        null,
                        null
                );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                get_comments_res = new HttpConnectionAndCode(99);
            }
            HttpConnectionAndCode get_comments_res_f = get_comments_res;
            runOnUiThread(() -> {
                if (get_comments_res_f.code == 0) {
                    List<Comment> comments = Arrays.asList(MyApp.gson.fromJson(get_comments_res_f.comment, Comment[].class));
                    setAdapter(comments);
                    LogMe.e(NAME, "get comments for " + key + ": " + key_value + " | success");
                } else {
                    Snackbar.make(snack_bar_root_view, "评论列表刷新失败(网络异常)", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                    LogMe.e(NAME, "fail to get comments for " + key + ": " + key_value);
                }
                ((SwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh)).setRefreshing(false);
            });
        }).start();
    }
}