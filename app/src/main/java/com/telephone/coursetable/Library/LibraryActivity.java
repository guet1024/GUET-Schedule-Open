package com.telephone.coursetable.Library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.telephone.coursetable.FunctionMenu;
import com.telephone.coursetable.Login_vpn;
import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LibraryActivity extends AppCompatActivity {

    public final static String EXTRA_USERNAME = "com.telephone.coursetable.library.username";
    public final static String EXTRA_VPN_PASSWORD = "com.telephone.coursetable.library.password";
    public final static String MESSAGE_STRING = "com.telephone.coursetable.library.message";

    private String username;
    private String password;
    volatile private int maxPage;
    private String message;
    private InputMethodManager inputMethodManager;
    volatile private int maxBookNum;
    volatile private int bookNum;
    private List<Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>>> books;
    private String cookie;
    private String html;
    private boolean interrupt;

    EditText etMessage;
    Button btSend;
    Button btReduce;
    Button btPlus;
    TextView tvPage;
    TextView tvToast;
    ProgressBar progressBar;
    ExpandableListView menu_listf;

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
        startActivity(new Intent(this, FunctionMenu.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.LIBRARY);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.library_activity);

        progressBar = findViewById(R.id.progressBar);
        etMessage = findViewById(R.id.message);
        btSend = findViewById(R.id.send_message);
        btReduce =  findViewById(R.id.reduce);
        btPlus = findViewById(R.id.plus);
        tvPage = findViewById(R.id.page);
        tvToast = findViewById(R.id.toast);
        menu_listf = findViewById(R.id.menu_list);

        progressBar.setVisibility(View.INVISIBLE);
        tvToast.setVisibility(View.INVISIBLE);
        maxBookNum = 0;
        books = new LinkedList<>();


        Intent intent = getIntent();
        username = intent.getStringExtra(EXTRA_USERNAME);
        password = intent.getStringExtra(EXTRA_VPN_PASSWORD);

//        username = "";
//        password = "";

        Intent intent_get = getIntent();
        if ( intent_get.getStringExtra(LibraryActivity.MESSAGE_STRING) != null ) {
            username = intent_get.getStringExtra(LibraryActivity.EXTRA_USERNAME);
            password = intent_get.getStringExtra(LibraryActivity.EXTRA_VPN_PASSWORD);
            etMessage.setText(intent_get.getStringExtra(LibraryActivity.MESSAGE_STRING));
            doSearch();
        }

        btReduce.setEnabled(false);
        btPlus.setEnabled(false);

        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ( i == EditorInfo.IME_ACTION_SEARCH ) {
                    startIntent();
                    return true;
                }
                return false;
            }
        });

        btSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startIntent();
            }
        });

        btReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePage(0);
            }
        });

        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePage(1);
            }
        });

    }

    private void doSearch() {
        etMessage.clearFocus();
        btReduce.setEnabled(false);
        btPlus.setEnabled(false);
        message = etMessage.getText().toString();
        interrupt = false;

        if ( MyApp.getRunning_activity_pointer() != null && LibraryActivity.this.toString().equals(MyApp.getRunning_activity_pointer().toString()) ) {
            progressBar.setVisibility(View.VISIBLE);
            tvToast.setVisibility(View.INVISIBLE);
        }

        if (message.isEmpty()) {
            ExceptionError( getResources().getString(R.string.wan_library_input_null) );
            return;
        }else if (message.split("").length > 60) {
            ExceptionError( getResources().getString(R.string.wan_library_input_long) );
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                checkActivity_tvToastISTrue("登录中...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                            if ( tvToast.getText().toString().equals("登录中...") ) {
                                checkActivity_tvToastISTrue("用校园网更快哦~", true);
                            }
                        } catch (InterruptedException e) {
                            // Restore interrupt status.
                            Thread.currentThread().interrupt();
                        }
                    }
                }).start();

                cookie = Login_vpn.wan_vpn_login_text(LibraryActivity.this, username, password);
                if ( cookie.contains("fail:") ) {
                    ExceptionError( cookie.substring(5) );
                    return;
                }

                bookNum = 1;
                books = new LinkedList<>();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPage.setText("");
                        menu_listf.setAdapter(new BookAdapter(LibraryActivity.this, null, true, menu_listf));
                    }
                });

                checkActivity_tvToastISTrue("搜索中...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                            if ( tvToast.getText().toString().equals("搜索中...") ) {
                                checkActivity_tvToastISTrue("用校园网更快哦~", true);
                            }
                        } catch (InterruptedException e) {
                            // Restore interrupt status.
                            Thread.currentThread().interrupt();
                        }
                    }
                }).start();

                html = getHtml_(1);
                if (html == null) {
                    ExceptionError( getResources().getString(R.string.wan_library_getResource_fail) );
                    interrupt = true;
                    return;
                }

                Document doc_xml = Jsoup.parse(html);
                Elements maxbooknum = doc_xml.select("html > body > form#form1 > div.body > div.mainbody2_out > div.mainbody2_in > div.mainbody > div.turnpage > div.total > span#labAllCount");
                Elements maxpage = doc_xml.select("html > body > form#form1 > div.body > div.mainbody2_out > div.mainbody2_in > div.mainbody > div.turnpage > div.ctrl > span#labConutPage");
                if (maxbooknum.isEmpty() || maxpage.isEmpty()) {
                    ExceptionError( getResources().getString(R.string.wan_library_getResource_fail) );
                    return;
                }
                maxBookNum = Integer.parseInt(maxbooknum.get(0).ownText());
                maxPage = Integer.parseInt(maxpage.get(0).ownText());

                if (maxPage > 50) maxPage = 50;
                if (maxBookNum > 500) maxBookNum = 500;
                else if (maxBookNum == 0) {
                    tvPage.setText("第0本，共0本");
                    ExceptionError( getResources().getString(R.string.wan_library_search_null) );
                    return;
                }
                tvPage.setText("第" + bookNum + "本/共" + maxBookNum + "本");

                for (int page = 1; page <= maxPage; page++) {
                    com.telephone.coursetable.LogMe.LogMe.e("共" + maxPage + "页", "第" + page + "页");
                    com.telephone.coursetable.LogMe.LogMe.e("message：", message);

                    com.telephone.coursetable.LogMe.LogMe.e("my", LibraryActivity.this.toString());
                    com.telephone.coursetable.LogMe.LogMe.e("running", MyApp.getRunning_activity_pointer().toString());
                    if ( MyApp.getRunning_activity_pointer() == null || !LibraryActivity.this.toString().equals(MyApp.getRunning_activity_pointer().toString())){
                        com.telephone.coursetable.LogMe.LogMe.e("new search activity detected, exit", LibraryActivity.this.toString());
                        return;
                    }

                    html = getHtml_(page);
                    if (html == null) {
                        checkActivity_progressBarISTrue(false);
                        interrupt = true;
                        return;
                    }

                    List<Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>>> books_page = broad(cookie, html, LibraryActivity.this, message, page);
                    if (books_page == null) {
                        checkActivity_progressBarISTrue(false);
                        interrupt = true;
                        return;
                    }
                    for (Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>> book_num : books_page) {

                        if ( MyApp.getRunning_activity_pointer() != null && LibraryActivity.this.toString().equals(MyApp.getRunning_activity_pointer().toString()) ) {
                            books.add(book_num);

                            if (books.size() == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        etMessage.setEnabled(!etMessage.isEnabled());
                                        etMessage.setEnabled(!etMessage.isEnabled());
                                        etMessage.clearFocus();
                                        ExceptionError("");
                                        checkActivity_tvToastISTrue("", false);
                                        menu_listf.setGroupIndicator(null);
                                        menu_listf.setAdapter(new BookAdapter(LibraryActivity.this, books.get(0), true, menu_listf));
                                        menu_listf.expandGroup(0);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void handlePage(int method) {
        int askBookNum = bookNum;
        if ( method == 0 ) askBookNum--;
        if ( method == 1 ) askBookNum++;
        if ( askBookNum == 0 || askBookNum == maxBookNum+1 ){
            if ( method == 0 ) runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "已经是第一本书了", Toast.LENGTH_SHORT), 1000));
            if ( method == 1 ) runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "已经是最后一本书了", Toast.LENGTH_SHORT), 1000));
            return;
        } else if ( askBookNum > books.size() ) {
            if ( interrupt ) runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "搜索过程中出现异常，请重新搜索", Toast.LENGTH_SHORT), 1000));
            else runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "数据获取中", Toast.LENGTH_SHORT), 500));
            return;
        }

        bookNum = askBookNum;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvPage.setText( "第" + bookNum + "本，共" + maxBookNum + "本" );
                menu_listf.setAdapter(new BookAdapter(LibraryActivity.this, books.get(bookNum-1), true, menu_listf));
                menu_listf.expandGroup(0);
            }
        });
    }

    private void checkActivity_tvToastISTrue(String message, boolean display) {
        runOnUiThread(()->{
            if ( MyApp.getRunning_activity_pointer() != null && LibraryActivity.this.toString().equals(MyApp.getRunning_activity_pointer().toString()) ) {
                if (display) {
                    tvToast.setVisibility(View.VISIBLE);
                    tvToast.setText(message);
                }
                else tvToast.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void ExceptionError(String message) {
        checkActivity_progressBarISTrue(false);
        checkActivity_tvToastISTrue(message, true);
        if ( books!=null && !books.isEmpty() ) {
            btPlus.setEnabled(true);
            btReduce.setEnabled(true);
        }
    }

    private void checkActivity_progressBarISTrue(boolean display) {
        runOnUiThread(()-> {
            if ( MyApp.getRunning_activity_pointer() != null && LibraryActivity.this.toString().equals(MyApp.getRunning_activity_pointer().toString())) {
                if (display) progressBar.setVisibility(View.VISIBLE);
                else progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getHtml_(int page_){
        String html_;
        int times = 0;
        do {
            times++;
            html_ = GetHttp.getHtml(cookie, message, page_);
        }while ( html_ == null & times <= 2 );
        return html_;
    }

    private void controlToastTime(final Toast toast, int duration) {
        toast.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }

    public List<Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>>> broad(String cookie, String html, Context c, String message, int page) {

        List<List<Map.Entry<String, String>>> res_1 = Show.getBookInfo(html);
        String id = new String();
        for (List<Map.Entry<String, String>> infos : res_1) {
            id = id + infos.get(0).getValue() + ";";
        }

        String xml;
        int times = 0;
        do {
            times++;
            xml = GetHttp.getXml(cookie, id, message, page);
        }while ( xml == null & times <= 2);
        if ( xml == null ) {
            return null;
        }

        List<List<List<Map.Entry<String, String>>>> res_2 = Show.getBookLocal(xml);
        List<Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>>> groups = new LinkedList<>();
        for (int i=0 ; i<res_1.size() ; i++) {
            List<Map.Entry<String, String>> infos = res_1.get(i);
            List<List<Map.Entry<String, String>>> locals = res_2.get(i);
            groups.add(Map.entry(infos, locals));
        }
        return groups;
    }

    private void startIntent() {
        Intent intent_send = new Intent(LibraryActivity.this, LibraryActivity.class);
        intent_send.putExtra( EXTRA_USERNAME, username );
        intent_send.putExtra( EXTRA_VPN_PASSWORD, password );
        intent_send.putExtra( MESSAGE_STRING, etMessage.getText().toString() );
        startActivity(intent_send);
    }

}
