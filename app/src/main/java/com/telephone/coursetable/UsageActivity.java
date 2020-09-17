package com.telephone.coursetable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

public class UsageActivity extends AppCompatActivity {

    UserGuideAdapter userGuideAdapter;
    private ViewPager2 viewPager;

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
        MyApp.setRunning_activity(MyApp.RunningActivity.USAGE);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_usage);
        viewPager = (ViewPager2) findViewById(R.id.viewpage);
        userGuideAdapter = new UserGuideAdapter();
        viewPager.setAdapter(userGuideAdapter);
    }
}
