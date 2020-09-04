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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.FunctionMenu;
import com.telephone.coursetable.Login_vpn;
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

    private String username;
    private String password;
    private int maxPage;
    private String message;
    private InputMethodManager inputMethodManager;
    private int maxBookNum;
    private int bookNum;
    private List<Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>>> books;
    private String cookie;
    private String html;
    private String check;
    private long check_time;

    EditText etMessage;
    Button btSend;
    Button btReduce;
    Button btPlus;
    TextView tvPage;
    ExpandableListView menu_listf;

    public LibraryActivity() {
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, FunctionMenu.class));
        super.onBackPressed();
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

        etMessage = findViewById(R.id.message);
        btSend = findViewById(R.id.send_message);
        btReduce =  findViewById(R.id.reduce);
        btPlus = findViewById(R.id.plus);
        tvPage = findViewById(R.id.page);
        menu_listf = findViewById(R.id.menu_list);
        check_time = 0;

        maxBookNum = 0;
        inputMethodManager =(InputMethodManager) LibraryActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        username = intent.getStringExtra(EXTRA_USERNAME);
        password = intent.getStringExtra(EXTRA_VPN_PASSWORD);

//        username = "";
//        password = "";

        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ( i == EditorInfo.IME_ACTION_SEARCH ) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });

        btSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });

        btReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if ( html == null ) {
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "请重新搜索", Toast.LENGTH_SHORT), 1000));
                            return;
                        }
                        if ( etMessage.getText().toString().split("").length > 60 ){
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "输入字数限制为60字", Toast.LENGTH_SHORT), 1000));
                            return;
                        }
                        if ( etMessage.getText().toString().isEmpty() ){
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "输入为空", Toast.LENGTH_SHORT), 1000));
                            return;
                        }
                        if (maxBookNum == 0) {
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "没有查询到结果", Toast.LENGTH_SHORT), 500));
                            return;
                        }
                        if (bookNum > 1) {
                            bookNum--;
                        }
                        else {
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "已经是第一本书了", Toast.LENGTH_SHORT), 500));
                            return;
                        }
                        tvPage.setText("第"+bookNum+"本/共"+maxBookNum+"本");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                menu_listf.setAdapter(new BookAdapter(LibraryActivity.this, books.get(bookNum-1), true, menu_listf));
                                menu_listf.expandGroup(0);
                            }
                        });
                    }
                }).start();
            }
        });

        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if ( html == null ) {
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "请重新搜索", Toast.LENGTH_SHORT), 1000));
                            return;
                        }
                        if ( etMessage.getText().toString().split("").length > 60 ){
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "输入字数限制为60字", Toast.LENGTH_SHORT), 1000));
                            return;
                        }
                        if ( etMessage.getText().toString().isEmpty() ){
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "输入为空", Toast.LENGTH_SHORT), 1000));
                            return;
                        }
                        if (maxBookNum == 0) {
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "没有查询到结果", Toast.LENGTH_SHORT), 500));
                            return;
                        }
                        if (bookNum < maxBookNum) {
                            if ( bookNum > books.size()-1 ) {
                                runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "数据获取中", Toast.LENGTH_SHORT), 500));
                                return;
                            }
                            bookNum++;
                        }
                        else {
                            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "已经是最后一本书了", Toast.LENGTH_SHORT), 500));
                            return;
                        }
                        tvPage.setText("第"+bookNum+"本/共"+maxBookNum+"本");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                menu_listf.setAdapter(new BookAdapter(LibraryActivity.this, books.get(bookNum-1), true, menu_listf));
                                menu_listf.expandGroup(0);
                            }
                        });
                    }
                }).start();
            }
        });

    }

    private void doSearch() {
        long now_time = Clock.nowTimeStamp();
        if ( now_time - check_time < 1500 ) {
            check_time = now_time;
            runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "点击过快，请稍后", Toast.LENGTH_SHORT), 1000));
            return;
        }
        else check_time = now_time;
        new Thread(new Runnable() {

            @Override
            public void run () {
                synchronized (LibraryActivity.this) {
                    check = this.toString();
                }
                bookNum = 1;
                books = new LinkedList<>();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPage.setText("");
                        menu_listf.setAdapter(new BookAdapter(LibraryActivity .this, null,true,menu_listf));
                    }
                });

                cookie = Login_vpn.vpn_login(LibraryActivity.this, username, password);
                if (cookie == null) {
                    runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "WebVPN账号密码/网络异常", Toast.LENGTH_SHORT), 1000));
                    return;
                } else if (cookie.equals(getResources().getString(R.string.wan_vpn_ip_forbidden))) {
                    runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "WebVPN验证失败次数过多，请稍后重试", Toast.LENGTH_SHORT), 1000));
                    return;
                }

                inputMethodManager.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
                message = etMessage.getText().toString();

                if (message.split("").length > 60) {
                    runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "输入字数限制为60字", Toast.LENGTH_SHORT), 1000));
                    return;
                }
                if (message.isEmpty()) {
                    runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "输入为空", Toast.LENGTH_SHORT), 1000));
                    return;
                }

                runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "搜索中...", Toast.LENGTH_SHORT), 500));

                html = GetHttp.getHtml(cookie, message, 1);
                if (html == null) {
                    runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "搜索时间过长，请检查网络是否中断", Toast.LENGTH_SHORT), 1000));
                    return;
                }

                Document doc_xml = Jsoup.parse(html);
                Elements maxbooknum = doc_xml.select("html > body > form#form1 > div.body > div.mainbody2_out > div.mainbody2_in > div.mainbody > div.turnpage > div.total > span#labAllCount");
                Elements maxpage = doc_xml.select("html > body > form#form1 > div.body > div.mainbody2_out > div.mainbody2_in > div.mainbody > div.turnpage > div.ctrl > span#labConutPage");
                maxBookNum = Integer.parseInt(maxbooknum.get(0).ownText());
                maxPage = Integer.parseInt(maxpage.get(0).ownText());

                if (maxPage > 50) maxPage = 50;
                if (maxBookNum > 500) maxBookNum = 500;
                if (maxBookNum == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvPage.setText("第0本/共0本");
                            menu_listf.setAdapter(new BookAdapter(LibraryActivity .this, null,true,menu_listf));
                            runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "没有查询到结果",Toast.LENGTH_SHORT), 1000));
                            return;
                        }
                    });
                }

                tvPage.setText("第" + bookNum + "本/共" + maxBookNum + "本");

                for (int page = 1; page <= maxPage; page++) {

                    Log.e("共" + maxPage + "页", "第" + page + "页");
                    Log.e("message：", message);
                    html = GetHttp.getHtml(cookie, message, page);
                    if (html == null) {
                        runOnUiThread(() -> controlToastTime(Toast.makeText(LibraryActivity.this, "搜索时间过长，请检查网络是否中断", Toast.LENGTH_SHORT), 1000));
                        return;
                    }

                    List<Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>>> books_page = broad(cookie, html, LibraryActivity.this, username, password, message, page);
                    if (books_page == null) return;
                    for (Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>> book_num : books_page) {

                        synchronized (LibraryActivity.this) {
                            Log.e("check",check);
                            Log.e("this", this.toString());
                            if ( !check.equals(this.toString()) ) return;
                        }
                        books.add(book_num);
                        if (books.size() == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etMessage.setEnabled(!etMessage.isEnabled());
                                    etMessage.setEnabled(!etMessage.isEnabled());
                                    etMessage.clearFocus();
                                    menu_listf.setGroupIndicator(null);
                                    menu_listf.setAdapter(new BookAdapter(LibraryActivity.this, books.get(0), true, menu_listf));
                                    menu_listf.expandGroup(0);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
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

    public List<Map.Entry<List<Map.Entry<String, String>>, List<List<Map.Entry<String, String>>>>> broad(String cookie, String html, Context c, String username, String password, String message, int page) {

        List<List<Map.Entry<String, String>>> res_1 = Show.getBookInfo(html);
        String id = new String();
        for (List<Map.Entry<String, String>> infos : res_1) {
            id = id + infos.get(0).getValue() + ";";
        }

        String xml = GetHttp.getXml(cookie, id, message, page);
        if ( xml == null ) {
            runOnUiThread(()-> controlToastTime(Toast.makeText( LibraryActivity.this , "搜索时间过长，请检查网络是否中断", Toast.LENGTH_SHORT), 1000));
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


}
