package com.ysy.gdsmusicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ysy.gdsmusicplayer.MyApplication;

/**
 * Created by ggec5486 on 2015/7/15.
 */
public class MusicPlayService extends Service {

    private MyApplication mApp;
    private static int MusicState;
    private int operation;
    private static final int PLAYING = 1;// 定义该怎么对音乐操作的常量,如播放是1
    private static final int PAUSE = 2;// 暂停事件是2
    private static final int STOP = 3;// 停止事件是3
    private static final int PROGRESS_CHANGE = 4;// 进度条改变事件设为4

    private AudioTrack mAudioTrack;
    private Uri uri = null;// 路径地址
    private int currentTime;// 当前时间
    private int duration;// 总时间

    private AudioDecoder mAudioDecoder;
    private String musicPath = null;


    @Override
    public void onCreate() {
        super.onCreate();
        mAudioDecoder = new AudioDecoder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mApp = (MyApplication) getApplicationContext();
        mApp.setService((MusicPlayService) this);
        musicPath = intent.getStringExtra("song");
        operation = intent.getIntExtra("operation", -1);
        switch (operation) {
            case PLAYING:
                play();
                break;
            case STOP:
                stop();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stop() {
        MusicState = STOP;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioDecoder.shouldContinue(false);
                mAudioDecoder.stop();
            }
        }).start();
    }

    private void play() {
        MusicState = PLAYING;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioDecoder.shouldContinue(false);
                mAudioDecoder.stop();
                mAudioDecoder.shouldContinue(true);
                mAudioDecoder.decode(musicPath);
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Indicates if music is currently playing.
     */
    public boolean isPlayingMusic() {
        try {
            if (MusicState == PLAYING) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
