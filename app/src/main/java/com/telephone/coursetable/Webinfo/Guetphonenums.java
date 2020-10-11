package com.telephone.coursetable.Webinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Guetphonenums extends AppCompatActivity {

    List<String> phonenums = new LinkedList<>();
    List<String> searchphonenums = new LinkedList<>();

    private List<Webinfoview> webinfoviewslist = new ArrayList<>();
    private List<Webinfoview> searchlist = new ArrayList<>();
    private ListView listView;
    private View view;

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

    private void addinfo(String num) {
        phonenums.add(num);
    }

    private String getinfo(int id) {
        return searchphonenums.get(id);
    }
    private void do_search(AutoCompleteTextView editText){
        editText.setEnabled(!editText.isEnabled());
        editText.setEnabled(!editText.isEnabled());
        editText.clearFocus();
        searchphonenums.clear();
        searchlist.clear();
        if(editText.length()==0){
            Snackbar.make(view, "请输入关键字", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
            guetphonenumsAdapter arrayAdapter = new guetphonenumsAdapter(Guetphonenums.this, R.layout.webinfo_item, webinfoviewslist);
            listView.setAdapter(arrayAdapter);
        }
        else if( !search(editText.getText().toString())){
            Snackbar.make(view, "未查询到电话", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Guetphonenums.this, Webinfo.class));
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.setRunning_activity(MyApp.RunningActivity.GUET_PHONE);
        MyApp.setRunning_activity_pointer(this);

        setContentView(R.layout.activity_guetphonenums);
        view=findViewById(R.id.guetphonepac);

        initWeb();
        Button xx = (Button)findViewById(R.id.cleartext);
        xx.setVisibility(View.INVISIBLE);

        guetphonenumsAdapter arrayAdapter = new guetphonenumsAdapter(Guetphonenums.this, R.layout.webinfo_item, webinfoviewslist);
        listView = (ListView) findViewById(R.id.listphonenum);
        listView.setAdapter(arrayAdapter);
        AutoCompleteTextView editText = (AutoCompleteTextView) findViewById(R.id.searchphonenum);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.phoneName,android.R.layout.simple_dropdown_item_1line);
        editText.setAdapter(adapter);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    do_search(editText);
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    xx.setVisibility(View.INVISIBLE);
                    guetphonenumsAdapter arrayAdapter = new guetphonenumsAdapter(Guetphonenums.this, R.layout.webinfo_item, webinfoviewslist);
                    listView.setAdapter(arrayAdapter);
                    searchlist = new LinkedList<>(webinfoviewslist);
                    searchphonenums = new LinkedList<>(phonenums);
                }else {
                    xx.setVisibility(View.VISIBLE);
                }
            }
        });

        xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(null);
            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                do_search(editText);
            }
        });
        Button button = (Button) findViewById(R.id.searchbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                do_search(editText);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String needcall = getinfo(i);
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + needcall));//跳转到拨号界面，同时传递电话号码
                startActivity(dialIntent);
            }
        });
    }

    private void initWeb() {
        Webinfoview security;
        List<String> arrName = Arrays.asList(getResources().getStringArray(R.array.phoneName));
        List<String> arrNum = Arrays.asList(getResources().getStringArray(R.array.phoneNum));

        for(int i=0;i<arrName.size();i++){
            security=new Webinfoview(arrName.get(i), R.drawable.webinfo_phone);
            webinfoviewslist.add(security);
            addinfo(arrNum.get(i));
        }
        searchlist = new LinkedList<>(webinfoviewslist);
        searchphonenums = new LinkedList<>(phonenums);
    }

    private List<String> getKeywords(String key){
        List<String> res = new LinkedList<>();
        for (int sub_len = key.length(); sub_len > 0; sub_len--){
            for (int start_index = 0; start_index <= key.length() - sub_len; start_index++){
                int end_index = start_index + sub_len;
                res.add(key.substring(start_index, end_index));
            }
        }
        return res;
    }

    private boolean search(String key) {
        searchlist.clear();
        searchphonenums.clear();
        List<String> keywords = getKeywords(key);
        Log.e("keyword list", ""+keywords);
        List<Webinfoview> copy_name = new LinkedList<>(webinfoviewslist);
        List<String> copy_num = new LinkedList<>(phonenums);
        for (String kw : keywords) {
            kw = kw.toLowerCase(Locale.SIMPLIFIED_CHINESE);
            List<Integer> delete_indexes = new LinkedList<>();
            for (int i = 0; i < copy_name.size(); i++) {
                Webinfoview webinfoview = copy_name.get(i);
                if (webinfoview.getTitle().toLowerCase(Locale.SIMPLIFIED_CHINESE).contains(kw)) {
                    searchlist.add(webinfoview);
                    searchphonenums.add(copy_num.get(i));
                    delete_indexes.add(i);
                }
            }
            Collections.reverse(delete_indexes);
            for (int d : delete_indexes){
                copy_name.remove(d);
                copy_num.remove(d);
            }
        }
        if(searchlist.size()==0){
            guetphonenumsAdapter arrayAdapter = new guetphonenumsAdapter(Guetphonenums.this, R.layout.webinfo_item, searchlist);
            listView.setAdapter(arrayAdapter);
            return false;
        }
        guetphonenumsAdapter arrayAdapter = new guetphonenumsAdapter(Guetphonenums.this, R.layout.webinfo_item, searchlist);
        listView.setAdapter(arrayAdapter);
        return true;
    }

}