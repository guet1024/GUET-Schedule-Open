package com.telephone.coursetable.Webinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.telephone.coursetable.MainActivity;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GUET_Music extends AppCompatActivity implements View.OnClickListener,
        Runnable, ServiceConnection, SeekBar.OnSeekBarChangeListener {

    List<Map.Entry<String, String>> lyric= new LinkedList<>();//存歌词
    private ImageView pause_start, schoolicon;//暂停键，学校图标（歌词切换）
    private volatile MediaService.MusicController mMusicController;//音乐播放器
    private SeekBar mSeekBar;
    private TextView runtime, totaltime, now_lyric, pre_lyric, next_lyric;//时间，歌词
    private long musicDuration;
    private volatile int pic_id = R.drawable.musicstart;


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
            Intent intent = new Intent(this, MediaService.class);
            unbindService(this);
            stopService(intent);
            startActivity(outdated);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
        Thread thread = new Thread(this);
        thread.start();
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

        Intent intent = new Intent(this, MediaService.class);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);
        addwongwords();
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void addwongwords() {
        lyric.add(Map.entry("山连着水", "00:18"));
        lyric.add(Map.entry("水连着山", "00:21"));
        lyric.add(Map.entry("我们从东西南北走过来", "00:24"));
        lyric.add(Map.entry("一个目标", "00:29"));
        lyric.add(Map.entry("一个理想", "00:31"));
        lyric.add(Map.entry("自强不惜，艰苦奋斗", "00:33"));
        lyric.add(Map.entry("攀登电子科技高峰", "00:36"));
        lyric.add(Map.entry("造福人类未来", "00:38"));
        lyric.add(Map.entry("造福人类未来", "00:42"));
        lyric.add(Map.entry("啊 正德育栋梁", "00:45"));
        lyric.add(Map.entry("厚学出英才", "00:48"));
        lyric.add(Map.entry("笃行为根本", "00:50"));
        lyric.add(Map.entry("致新创品牌", "00:52"));
        lyric.add(Map.entry("啊 桂花树成林", "00:55"));
        lyric.add(Map.entry("尧山杜鹃开", "00:57"));
        lyric.add(Map.entry("漓江东流海", "01:00"));
        lyric.add(Map.entry("扬帆新时代 新时代", "01:02"));
        lyric.add(Map.entry("手手挽着 心连着心", "01:07"));
        lyric.add(Map.entry("我们从东西南北走过来", "01:11"));
        lyric.add(Map.entry("走向未来 走向未来", "01:16"));
        lyric.add(Map.entry("手挽着手 心连着心", "01:19"));
        lyric.add(Map.entry("我们从东西南北", "01:24"));
        lyric.add(Map.entry("走过来 走过来 走向未来", "01:26"));
        lyric.add(Map.entry("", "01:45"));
    }

    private void initViews() {
        runtime = findViewById(R.id.runingtime);
        totaltime = findViewById(R.id.totaltime);

        now_lyric = findViewById(R.id.songwords);
        pre_lyric = findViewById(R.id.songwords0);
        next_lyric = findViewById(R.id.songwords1);

        now_lyric.setVisibility(View.INVISIBLE);
        pre_lyric.setVisibility(View.INVISIBLE);
        next_lyric.setVisibility(View.INVISIBLE);

        mSeekBar = findViewById(R.id.guetseekBar);
        pause_start = findViewById(R.id.musicstart);
        schoolicon = findViewById(R.id.schoolicon);

        pause_start.setOnClickListener(this);
        schoolicon.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        MyApp.clearRunningActivity(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mMusicController = ((MediaService.MusicController) iBinder);
        musicDuration = mMusicController.getMusicDuration();//文件总长度
        com.telephone.coursetable.LogMe.LogMe.e("file length",""+musicDuration);
        mSeekBar.setMax((int) musicDuration);
        totaltime.setText("/" + millisToTimeString(musicDuration));
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mMusicController = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id==R.id.totalsongwards ) {
            if(schoolicon.getVisibility()==View.VISIBLE){
                schoolicon.setVisibility(View.INVISIBLE);
                now_lyric.setVisibility(View.VISIBLE);
                pre_lyric.setVisibility(View.VISIBLE);
                next_lyric.setVisibility(View.VISIBLE);
            }
            else {
                schoolicon.setVisibility(View.VISIBLE);
                now_lyric.setVisibility(View.INVISIBLE);
                pre_lyric.setVisibility(View.INVISIBLE);
                next_lyric.setVisibility(View.INVISIBLE);
            }
        }
        if (id == R.id.musicstart) {
            if(mMusicController.isruning()){
                mMusicController.pause();
            }
            else {
                mMusicController.play();//播放
            }
        }
    }

    private String millisToTimeString(long millis){
        long min = 0;
        long sec = 0;
        long msPm = 60000;
        min = millis / msPm;
        millis -= min * msPm;
        sec = millis / 1000;
        return String.format(Locale.SIMPLIFIED_CHINESE, "%02d:%02d", min, sec);
    }

    private long timeStringToLong(String string){
        int _index = string.indexOf(":");
        long min = Long.parseLong(string.substring(0, _index));
        long sec = Long.parseLong(string.substring(_index + 1));
        return min * 60000 + sec * 1000;
    }

    private void setLyric(String n,String p,String next){
        String n_=now_lyric.getText().toString();
        String p_=pre_lyric.getText().toString();
        String next_=next_lyric.getText().toString();
        if(!n_.equals(n)){
            now_lyric.setText(n);
        }
        if(!p_.equals(p)){
            pre_lyric.setText(p);
        }
        if(!next_.equals(next)){
            next_lyric.setText(next);
        }
    }

    private String getnowlyric(int i){
        if(i>=0 && i<lyric.size()){
            return lyric.get(i).getKey();
        }
        else
            return "";
    }

    private String[] getLyric(long time){
        int index = 0;
        for(int i=index;i<lyric.size()-1;i++){
            String text = lyric.get(i + 1).getValue();
            long read_time = timeStringToLong(text);
            com.telephone.coursetable.LogMe.LogMe.i("time","read time = "+read_time+" time = "+time+" text = "+text);
            if( read_time > time ){
                index = i;
                break;
            }
        }
        com.telephone.coursetable.LogMe.LogMe.i("index","index = "+index);
        String[] s = new String[3];
        s[0] = getnowlyric(index - 1);
        s[1] = getnowlyric(index);
        s[2] = getnowlyric(index + 1);
        return s;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int j, boolean b) {
        final int position = seekBar.getProgress();
        com.telephone.coursetable.LogMe.LogMe.i("progress","change = "+position);

        String r = millisToTimeString(position);
        if(!r.equals(runtime.getText().toString())) runtime.setText(r);

        String[] gl= getLyric(position);
        setLyric(gl[1], gl[0], gl[2]);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        com.telephone.coursetable.LogMe.LogMe.e("seekbar", "touch");
        pause_start.setEnabled(false);
        if(mMusicController.isruning())
            mMusicController.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        com.telephone.coursetable.LogMe.LogMe.e("seekbar", "not touch");
        mMusicController.setPosition(seekBar.getProgress());
        if(!mMusicController.isruning()) {
            mMusicController.play();
        }
        new Thread(()->{
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            runOnUiThread(()->pause_start.setEnabled(true));
        }).start();
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
        com.telephone.coursetable.LogMe.LogMe.e("guet music","refresh ui thread start");
        try {
            while (visible) {
                if (mMusicController != null) {
                    if (mMusicController.isruning() && pic_id != R.drawable.musicstop) {
                        pause_start.setImageResource(R.drawable.musicstop);
                        pic_id = R.drawable.musicstop;
                    } else if (!mMusicController.isruning() && pic_id != R.drawable.musicstart) {
                        pause_start.setImageResource(R.drawable.musicstart);
                        pic_id = R.drawable.musicstart;
                    }

                    final long position = mMusicController.getPosition();//进度
                    GUET_Music.this.runOnUiThread(() -> {
                        if (pause_start.isEnabled()) {
                            int old = mSeekBar.getProgress();
                            if (old != position) {
                                long position_set = position;
//                                if (position_set == musicDuration) position_set = 0;
                                com.telephone.coursetable.LogMe.LogMe.i("mSeekBar", "set position = " + position_set + " max = " + musicDuration);
                                mSeekBar.setProgress((int) position_set);
                            }
                        }
                    });
                }else {
                    com.telephone.coursetable.LogMe.LogMe.e("guet music", "controller is null");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        com.telephone.coursetable.LogMe.LogMe.e("guet music","refresh ui thread end");
    }
}
