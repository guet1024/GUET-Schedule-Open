package com.telephone.coursetable.Webinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.telephone.coursetable.FunctionMenu;
import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Guetphonenums extends AppCompatActivity {

    List<Map.Entry<String, Integer>> phonenums = new LinkedList<>();
    List<Map.Entry<String, Integer>> searchphonenums = new LinkedList<>();
    ;
    private List<Webinfoview> webinfoviewslist = new ArrayList<>();
    private List<Webinfoview> searchlist = new ArrayList<>();
    private ListView listView;
    private boolean change = false;
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

    private void addinfo(String num, int id) {
        phonenums.add(Map.entry(num, id));
    }

    private Map.Entry<String, Integer> getinfo(int id) {
        if(change){
            for (int i = 0; i < searchphonenums.size(); i++) {
                if (searchphonenums.get(i).getValue() == id) {
                    return searchphonenums.get(i);
                }
            }
        }
        else {
            for (int i = 0; i < phonenums.size(); i++) {
                if (phonenums.get(i).getValue() == id) {
                    return phonenums.get(i);
                }
            }
        }
        return null;
    }
    private void alllistener(AutoCompleteTextView editText){
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
                    alllistener(editText);
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                xx.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                xx.setVisibility(View.VISIBLE);
            }
        });

        xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(null);
                guetphonenumsAdapter arrayAdapter = new guetphonenumsAdapter(Guetphonenums.this, R.layout.webinfo_item, webinfoviewslist);
                listView.setAdapter(arrayAdapter);
                xx.setVisibility(View.INVISIBLE);
            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alllistener(editText);
            }
        });
        Button button = (Button) findViewById(R.id.searchbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alllistener(editText);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Webinfoview web = webinfoviewslist.get(i);
                Map.Entry<String, Integer> needcall = getinfo(i);
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + needcall.getKey()));//跳转到拨号界面，同时传递电话号码
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
            addinfo(arrNum.get(i), i);
        }
    }

    private boolean search(String key) {
        int k=0;
        change = true;
        for (int i = 0; i < webinfoviewslist.size(); i++) {
            Webinfoview webinfoview = webinfoviewslist.get(i);
            if(webinfoview.getTitle().contains(key)){
                searchlist.add(webinfoview);
                searchphonenums.add(Map.entry(phonenums.get(i).getKey(),k ));
                k++;
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