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

        private MediaPlayer mediaPlayer1 = new MediaPlayer();
        private int currentPosition;
        public String musicName;

        //播放
        public void startMusic(int position) {
            mediaPlayer.reset();
            currentPosition = position;
            setCurrentPosition(position);
            initMediaPlayer(position);
            mediaPlayer1 = mediaPlayer;
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

        //初始化MediaPlayer
        public void initMediaPlayer(int position) {
            try {
                File file = new File(musicPath + "/" + new MyMusicFragment().getMusicList().get(position));
                Log.i("MusicService", String.valueOf(position) + file.getPath());
                Log.i("size", String.valueOf(new MyMusicFragment().getMusicList().size()));
                musicName = new MyMusicFragment().getMusicList().get(position);
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer1;
        }

        public void setMediaPlayer(MediaPlayer mediaPlayer1) {
            mediaPlayer = mediaPlayer1;
        }

        //使用正则表达式处理音乐文件名
        public String handleName(int position) {
            String name =  new MyMusicFragment().getMusicList().get(position);
            name = name.replaceAll(".*-", "");
            return name;
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
