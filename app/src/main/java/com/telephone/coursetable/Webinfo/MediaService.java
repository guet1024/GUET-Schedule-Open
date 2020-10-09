package com.telephone.coursetable.Webinfo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.telephone.coursetable.R;

import java.util.ArrayList;
import java.util.List;

public class MediaService extends Service {
    private MediaPlayer mPlayer;
    private int index = 0;
    private boolean isstop=false;
    private boolean runing=false;

    public class MusicController extends Binder {
        public void play() {
            mPlayer.start();runing=true;//开启音乐
        }
        public void pause() {
            mPlayer.pause();
             runing=false;//暂停音乐
        }
        public long getMusicDuration() {
            return mPlayer.getDuration();//获取文件的总长度
        }
        public long getPosition() {
            return mPlayer.getCurrentPosition();//获取当前播放进度
        }
        public void setPosition (int position) {
            mPlayer.seekTo(position);//重新设定播放进度
        }
        public boolean isstop(){
            return isstop;//判断是否停止广播
        }
        public boolean isruning(){
            return runing;//判断音乐是否在播放
        }

    }

    @Override
    public void onDestroy() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        isstop=true;
        mPlayer.release();
        mPlayer = null;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = MediaPlayer.create(this, R.raw.the_faintest_sign);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }

}
