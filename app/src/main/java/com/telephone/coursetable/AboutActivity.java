package com.telephone.coursetable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class AboutActivity extends AppCompatActivity {

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
        MyApp.setRunning_activity(MyApp.RunningActivity.ABOUT);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.about_us);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aboutus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(AboutActivity.this, FunctionMenu.class));
                return true;
            case R.id.itqq:
                boolean blQQ = joinQQGroup("Jm2emUYqOfaVWX3WL17GY0nN2wOBN1wG");
                if ( !blQQ ) {
                    Snackbar.make(findViewById(R.id.aboutus), "未安装手Q或安装的版本不支持", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
                break;
            case R.id.itgh:
                boolean blgh = joinGitHub();
                if ( !blgh ) {
                    Snackbar.make(findViewById(R.id.aboutus), "页面跳转失败", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
                break;
            default:
        }
        return true;
    }

    /****************
     *
     * 发起添加群流程。群号：GUET课程表 交流群(336405176) 的 key 为： Jm2emUYqOfaVWX3WL17GY0nN2wOBN1wG
     * 调用 joinQQGroup(Jm2emUYqOfaVWX3WL17GY0nN2wOBN1wG) 即可发起手Q客户端申请加群 GUET课程表 交流群(336405176)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public boolean joinGitHub() {
        Uri uri = Uri.parse("https://github.com/Telephone2019/CourseTable");
        try {
            AboutActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
