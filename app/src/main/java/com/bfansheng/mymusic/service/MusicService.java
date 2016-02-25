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
import java.util.List;

/**
 * Created by Hp on 2016/2/19.
 */
public class MusicService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MusicBinder musicBinder = new MusicBinder();

    public class MusicBinder extends Binder {

        public MediaPlayer mediaPlayer1 = mediaPlayer;
        private int currentPosition = 0;
        private String name;
        private String artist;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String title, text;
        if (musicBinder.getName() == null) {
            title = "音乐即将到达战场";
            text = "伸出你们的双手";
        }else {
            title = musicBinder.getName();
            text = musicBinder.getArtist();
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
