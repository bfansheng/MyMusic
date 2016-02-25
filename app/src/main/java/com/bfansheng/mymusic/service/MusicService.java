package com.bfansheng.mymusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bfansheng.mymusic.MainActivity;
import com.bfansheng.mymusic.R;
import com.bfansheng.mymusic.fragment.MyMusicFragment;

import java.io.File;

/**
 * Created by Hp on 2016/2/19.
 */
public class MusicService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MusicBinder musicBinder = new MusicBinder();
    public final static File musicPath = new File(Environment.getExternalStorageDirectory().getPath() + "/netease/cloudmusic/Music");

    public class MusicBinder extends Binder {

        public MediaPlayer mediaPlayer1 = mediaPlayer;
        private int currentPosition;

        //播放
        public void startMusic(int position) {
            currentPosition = position;
            setCurrentPosition(position);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.start();
                }
            }).start();
        }

        //返回当前播放歌曲位置
        public int getCurrentPosition() {
            return currentPosition;
        }

        public void setCurrentPosition(int position) {
            currentPosition = position;
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer1;
        }

        public void setMediaPlayer(MediaPlayer mediaPlayer1) {
            mediaPlayer = mediaPlayer1;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
