package com.example.commonapplications;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tipTextView = findViewById(R.id.tip_text);
        tipTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void clickTip(View view) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    public void clickWake(View view) {
        AppsPanel.setAlarm(this, 6000);
        Toast.makeText(this, R.string.service_button_toast_text, Toast.LENGTH_SHORT);
    }

    public void clickBlack(View view) {
        MyApplication app = (MyApplication) this.getApplication();
        app.color = MyApplication.COLOR_BLACK;
        Toast.makeText(this, R.string.wait_color_black, Toast.LENGTH_SHORT).show();
        Log.e("the color is set", "!!!!!!!!!!!!!!!!!!!! Black !!!!!!!!!!!!!!!!!!!!");
    }

    public void clickWhite(View view) {
        MyApplication app = (MyApplication) this.getApplication();
        app.color = MyApplication.COLOR_WHITE;
        Toast.makeText(this, R.string.wait_color_white, Toast.LENGTH_SHORT).show();
        Log.e("the color is set", "!!!!!!!!!!!!!!!!!!!! White !!!!!!!!!!!!!!!!!!!!");
    }
}
