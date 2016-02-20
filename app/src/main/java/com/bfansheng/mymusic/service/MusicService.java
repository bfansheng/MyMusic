package com.bfansheng.mymusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

        private int currentPosition;

        public void startMusic(int position) {
            mediaPlayer.reset();
            currentPosition = position;
            setCurrentPosition(position);
            initMediaPlayer(position);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (musicBinder.getCurrentPosition() + 1 == new MyMusicFragment().getMusicList().size()) {
                        musicBinder.setCurrentPosition(0);
                        musicBinder.startMusic(0);
                    } else {
                        musicBinder.nextMusic();
                    }
                }
            });
        }

        //上一首
        public void previousMusic() {
            mediaPlayer.reset();
            musicBinder.startMusic(getCurrentPosition() - 1);
        }

        //下一首
        public void nextMusic() {
            mediaPlayer.reset();
            musicBinder.startMusic(getCurrentPosition() + 1);
        }

        //释放资源
        public void resetMusic() {
            mediaPlayer.reset();
        }

        //切换暂停播放
        public void pauseMusic() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }

        //返回当前播放歌曲位置
        public int getCurrentPosition() {
            return currentPosition;
        }

        public void setCurrentPosition(int position) {
            currentPosition = position;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
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

    //初始化MediaPlayer
    public void initMediaPlayer(int position) {
        try {
            File file = new File(musicPath + "/" + new MyMusicFragment().getMusicList().get(position));
            Log.i("MusicService", file.getPath());
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
