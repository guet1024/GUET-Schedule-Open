package com.telephone.coursetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
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
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Clock.Locate;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Gson.Comment.Comment;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Https.Get;
import com.telephone.coursetable.Https.Post;
import com.telephone.coursetable.LogMe.LogMe;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Comments extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ua = "guet-coursetable";
    private static final String referer = "guet-coursetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ((RecyclerView)findViewById(R.id.comments_list_view)).setLayoutManager(new LinearLayoutManager(Comments.this));
        ((RecyclerView)findViewById(R.id.comments_list_view)).setAdapter(new CommentsAdapter(new LinkedList<>()));
        ((SwipeRefreshLayout)findViewById(R.id.comment_swipe_refresh)).setOnRefreshListener(Comments.this);
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
                                    new Thread(() -> {
                                        HttpConnectionAndCode post_res = Post.post(
                                                getString(R.string.put_cno_cmts_url),
                                                new String[]{
                                                        "cno" + "=" + "2012797",
                                                        "sno" + "=" + "1800301129"
                                                },
                                                ua,
                                                referer,
                                                cmt,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                true
                                        );
                                        runOnUiThread(() -> {
                                            if (post_res.code == 0) {
                                                Toast.makeText(Comments.this, "评论发表成功", Toast.LENGTH_SHORT).show();
                                                onRefresh();
                                            }else {
                                                Toast.makeText(Comments.this, "网络连接失败，评论发表失败", Toast.LENGTH_SHORT).show();
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
            HttpConnectionAndCode get_comments_res = Get.get(
                    getString(R.string.get_cno_cmts_url),
                    new String[]{
                            "cno" + "=" + "2012797"
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
            runOnUiThread(() -> {
                if (get_comments_res.code == 0) {
                    List<Comment> comments = Arrays.asList(MyApp.gson.fromJson(get_comments_res.comment, Comment[].class));
                    setAdapter(comments);
                    LogMe.e(NAME, "get comments for " + "cno" + ": " + "2012797" + " | success");
                } else {
                    Toast.makeText(Comments.this, "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
                    LogMe.e(NAME, "fail to get comments for " + "cno" + ": " + "2012797");
                }
                ((SwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh)).setRefreshing(false);
            });
        }).start();
    }
}