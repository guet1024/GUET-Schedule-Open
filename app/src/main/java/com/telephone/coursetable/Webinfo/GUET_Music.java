package com.telephone.coursetable.Webinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GUET_Music extends AppCompatActivity implements View.OnClickListener,
        Runnable, ServiceConnection, SeekBar.OnSeekBarChangeListener {

    List<Map.Entry<String, String>> schoolsong = new LinkedList<>();//存歌词
    private ImageView playingPlay, schoolicon;//暂停建，学校图标
    private boolean isPlaying = true,  checkout=true;
    private MediaService.MusicController mMusicController;//音乐播放器
    private boolean running;
    private SeekBar mSeekBar;
    private TextView runtime, totaltime, songwords, songwords0, songwords1;//时间，歌词
    private boolean cantrelease=true;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setRunning_activity(MyApp.RunningActivity.GUET_MUSIC);
        MyApp.setRunning_activity_pointer(this);
        setContentView(R.layout.activity_g_u_e_t__music);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        Intent intent = new Intent(this, MediaService.class);
        startService(intent);

        bindService(intent, this, BIND_AUTO_CREATE);;

        addwongwords();
        initViews();
    }



    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onStop() {
        try{
            Intent intent = new Intent(this, MediaService.class);
            unbindService(this);
            stopService(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }

    private void addwongwords() {
        schoolsong.add(Map.entry("山连着水", "00:18"));
        schoolsong.add(Map.entry("水连着山", "00:21"));
        schoolsong.add(Map.entry("我们从东西南北走过来", "00:24"));
        schoolsong.add(Map.entry("一个目标", "00:29"));
        schoolsong.add(Map.entry("一个理想", "00:31"));
        schoolsong.add(Map.entry("自强不惜，艰苦奋斗", "00:33"));
        schoolsong.add(Map.entry("攀登电子科技高峰", "00:36"));
        schoolsong.add(Map.entry("造福人类未来", "00:38"));
        schoolsong.add(Map.entry("造福人类未来", "00:42"));
        schoolsong.add(Map.entry("啊 正德育栋梁", "00:45"));
        schoolsong.add(Map.entry("厚学出英才", "00:48"));
        schoolsong.add(Map.entry("笃行为根本", "00:50"));
        schoolsong.add(Map.entry("致新创品牌", "00:52"));
        schoolsong.add(Map.entry("啊 桂花树成林", "00:55"));
        schoolsong.add(Map.entry("尧山杜鹃开", "00:57"));
        schoolsong.add(Map.entry("漓江东流海", "01:00"));
        schoolsong.add(Map.entry("扬帆新时代 新时代", "01:02"));
        schoolsong.add(Map.entry("手手挽着 心连着心", "01:07"));
        schoolsong.add(Map.entry("我们从东西南北走过来", "01:11"));
        schoolsong.add(Map.entry("走向未来 走向未来", "01:16"));
        schoolsong.add(Map.entry("手挽着手 心连着心", "01:19"));
        schoolsong.add(Map.entry("我们从东西南北", "01:24"));
        schoolsong.add(Map.entry("走过来 走过来 走向未来", "01:26"));
    }

    private void initViews() {
        runtime = (TextView) findViewById(R.id.runingtime);
        totaltime = (TextView) findViewById(R.id.totaltime);

        songwords = (TextView) findViewById(R.id.songwords);
        songwords0 = (TextView) findViewById(R.id.songwords0);
        songwords1 = (TextView) findViewById(R.id.songwords1);

        songwords.setVisibility(View.INVISIBLE);
        songwords0.setVisibility(View.INVISIBLE);
        songwords1.setVisibility(View.INVISIBLE);

        mSeekBar = (SeekBar) findViewById(R.id.guetseekBar);
        playingPlay = (ImageView) findViewById(R.id.musicstart);
        schoolicon = (ImageView) findViewById(R.id.schoolicon);

        playingPlay.setOnClickListener(this);
        schoolicon.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void playing() {
        playingPlay.setImageResource(R.drawable.musicstop);
        mMusicController.play();//播放
        isPlaying = false;
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mMusicController = ((MediaService.MusicController) iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mMusicController = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id==R.id.totalsongwards ) {
            if(checkout){
                schoolicon.setVisibility(View.INVISIBLE);
                songwords.setVisibility(View.VISIBLE);
                songwords0.setVisibility(View.VISIBLE);
                songwords1.setVisibility(View.VISIBLE);
                checkout=false;
            }
            else {
                schoolicon.setVisibility(View.VISIBLE);
                songwords.setVisibility(View.INVISIBLE);
                songwords0.setVisibility(View.INVISIBLE);
                songwords1.setVisibility(View.INVISIBLE);
                checkout=true;
            }
        }
        if (isPlaying && id == R.id.musicstart && cantrelease) {
            playing();

        } else if (!isPlaying && id == R.id.musicstart && cantrelease) {
            mMusicController.pause();
            playingPlay.setImageResource(R.drawable.musicstart);
            isPlaying = true;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(!mMusicController.isruning()){
            mMusicController.setPosition(seekBar.getProgress());
        }
        //mMusicController.setPosition(seekBar.getProgress());

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        cantrelease=false;
        if(mMusicController.isruning())
        mMusicController.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        cantrelease=true;
        mMusicController.play();
        isPlaying=false;
        playingPlay.setImageResource(R.drawable.musicstop);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MediaService.class);
        unbindService(this);
        stopService(intent);
        startActivity(new Intent(GUET_Music.this,Webinfo.class));
    }

    @Override
    public void run() {
        try {
            running = true;
            while (running) {
                if (mMusicController != null) {
                    if (mMusicController.isstop()) {
                        return;
                    }
                    if (mMusicController == null) {
                        return;
                    }
                    long musicDuration = mMusicController.getMusicDuration();//文件总长度
                    final long position = mMusicController.getPosition();//改变进度
                    final Date dateTotal = new Date(musicDuration);
                    final SimpleDateFormat sb = new SimpleDateFormat("mm:ss");
                    GUET_Music.this.runOnUiThread(() -> mSeekBar.setMax((int) musicDuration));
                    GUET_Music.this.runOnUiThread(() -> mSeekBar.setProgress((int) position));
                    Date date = new Date(position);
                    GUET_Music.this.runOnUiThread(() -> runtime.setText(sb.format(date)));
                    if (sb.format(date).equals(sb.format(dateTotal))) {

                        if (cantrelease) {
                            GUET_Music.this.runOnUiThread(() -> mSeekBar.setProgress((int) 0));
                            mMusicController.play();
                            playingPlay.setImageResource(R.drawable.musicstop);
                            isPlaying = false;
                        }
                    }
                    try {
                        if ((sb.parse(sb.format(date))).getTime() < (sb.parse("00:17")).getTime()) {
                            GUET_Music.this.runOnUiThread(() -> songwords.setText(schoolsong.get(0).getKey()));
                            GUET_Music.this.runOnUiThread(() -> songwords1.setText(schoolsong.get(1).getKey()));
                            GUET_Music.this.runOnUiThread(() -> songwords0.setText(""));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        if ((sb.parse(sb.format(date))).getTime() > (sb.parse("01:26")).getTime()) {
                            GUET_Music.this.runOnUiThread(() -> songwords0.setText(schoolsong.get(schoolsong.size() - 2).getKey()));
                            GUET_Music.this.runOnUiThread(() -> songwords.setText(schoolsong.get(schoolsong.size() - 1).getKey()));
                            GUET_Music.this.runOnUiThread(() -> songwords1.setText(""));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String t = "/" + sb.format(dateTotal);
                    GUET_Music.this.runOnUiThread(() -> totaltime.setText(t));

                    for (int i = 0; i < schoolsong.size() - 1; i++) {
                        try {
                            if (
                                    (sb.parse(schoolsong.get(i + 1).getValue())).getTime() > (sb.parse(sb.format(date))).getTime() &&
                                            (sb.parse(sb.format(date))).getTime() >= (sb.parse(schoolsong.get(i).getValue())).getTime()
                            ) {
                                if (i == 0) {
                                    GUET_Music.this.runOnUiThread(() -> songwords0.setText(""));
                                    int finalI = i;
                                    GUET_Music.this.runOnUiThread(() -> songwords.setText(schoolsong.get(finalI).getKey()));
                                    GUET_Music.this.runOnUiThread(() -> songwords1.setText(schoolsong.get(finalI + 1).getKey()));
                                } else {
                                    int finalI1 = i;
                                    GUET_Music.this.runOnUiThread(() -> songwords0.setText(schoolsong.get(finalI1 - 1).getKey()));
                                    GUET_Music.this.runOnUiThread(() -> songwords.setText(schoolsong.get(finalI1).getKey()));
                                    GUET_Music.this.runOnUiThread(() -> songwords1.setText(schoolsong.get(finalI1 + 1).getKey()));
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}