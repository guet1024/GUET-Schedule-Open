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

    public class MusicController extends Binder {
        public void play() {
            mPlayer.start();
        }
        public void pause() {
            mPlayer.pause();
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
        public boolean isruning(){
            return mPlayer.isPlaying();
        }

    }

    @Override
    public void onDestroy() {//停止音乐，断开连接
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
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
        mPlayer.setLooping(true);
    }//创建音乐

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }//绑定时返回音乐实列

}
