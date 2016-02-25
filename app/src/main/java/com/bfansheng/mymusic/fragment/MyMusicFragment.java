package com.bfansheng.mymusic.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bfansheng.mymusic.R;
import com.bfansheng.mymusic.service.MusicService;
import com.bfansheng.mymusic.utils.MyAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hp on 2016/2/17.
 */
public class MyMusicFragment extends Fragment implements View.OnClickListener {

    Flag mCallback;
    private List<String> musicList = new ArrayList<String>();
    private List<String> pathList = new ArrayList<String>();
    private List<String> titleList = new ArrayList<String>();
    private List<HashMap<String, String>> adapterList = new ArrayList<>();
    private MyAdapter arrayAdapter;
    private Button next_button;
    private Button previous_button;
    private Button play;
    private Button play_mode;
    private TextView listItem;
    private MusicService.MusicBinder musicBinder;
    private Intent intent;

    public interface Flag {
        public int getFlag();

        public void setFlag(int flag);

        public int getPlayMode();

        public void setPlayMode(int mode);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Service中onBinder方法返回的是一个Ibinder对象，需要向下转型为MusicBinder类才能使用
            musicBinder = (MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.DATA,
                                MediaStore.Audio.Media.DISPLAY_NAME}, null,
                        null, null);
        //获取音乐时长索引
        int timeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        //获取歌曲名称索引
        int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        //路径名索引
        int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        //歌手名索引
        int artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        //文件名索引
        int display_nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
        //专辑名索引
        int albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(titleIndex);
                String display_name = cursor.getString(display_nameIndex);
                String path = cursor.getString(pathIndex);
                String artist = cursor.getString(artistIndex);
                long time = cursor.getLong(timeIndex);
                String album = cursor.getString(albumIndex);
                HashMap<String, String> hashMap = new HashMap<>();
                if (time > 60000) {
                    hashMap.put("title", title);
                    hashMap.put("artist", artist);
                    hashMap.put("album", album);
                    adapterList.add(hashMap);
                    musicList.add(display_name);
                    pathList.add(path);
                    titleList.add(title);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        arrayAdapter = new MyAdapter(getActivity(), R.layout.item_musiclist, adapterList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mymusic, container, false);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        play = (Button) getActivity().findViewById(R.id.play_music);
        play.setOnClickListener(this);
        //切换fragment时判断音乐是否在播放，设置在切回来播放按钮选择
        if (mCallback.getFlag() == 1) { //表示音乐正在播放
            play.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
        } else if (mCallback.getFlag() == 2) { //表示音乐已经暂停
            play.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
        }
        play_mode = (Button) getActivity().findViewById(R.id.play_mode);
        play_mode.setOnClickListener(this);
        if (mCallback.getPlayMode() == 0) {
            play_mode.setBackgroundResource(R.drawable.ic_loop_white_24dp);
        } else if (mCallback.getPlayMode() == 1) {
            play_mode.setBackgroundResource(R.drawable.ic_shuffle_white_24dp);
        }
        next_button = (Button) getActivity().findViewById(R.id.next_music);
        next_button.setOnClickListener(this);
        previous_button = (Button) getActivity().findViewById(R.id.previous_music);
        previous_button.setOnClickListener(this);
        listItem = (TextView) getActivity().findViewById(R.id.list_item);
        intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //配置ListView
        ListView listView = (ListView) getActivity().findViewById(R.id.list_music);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicBinder.setCurrentPosition(position);
                play.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
                onComplete(musicBinder.getCurrentPosition());
                mCallback.setFlag(1);
            }
        });
    }

    //播放音乐点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_music:
                play.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
                playPrevious();
                mCallback.setFlag(1);
                break;
            case R.id.next_music:
                play.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
                playNext();
                mCallback.setFlag(1);
                break;
            case R.id.play_music:
                play();
                break;
            case R.id.play_mode:
                if (mCallback.getPlayMode() == 0) {
                    play_mode.setBackgroundResource(R.drawable.ic_shuffle_white_24dp);
                    mCallback.setPlayMode(1);
                } else if (mCallback.getPlayMode() == 1) {
                    play_mode.setBackgroundResource(R.drawable.ic_loop_white_24dp);
                    mCallback.setPlayMode(0);
                }
                break;
            default:
                break;
        }
    }

    //上一首
    public void playPrevious() {
        switch (mCallback.getPlayMode()) {
            case 0:
                if (musicBinder.getCurrentPosition() - 1 < 0) {
                    musicBinder.setCurrentPosition(musicList.size() - 1);
                    musicBinder.startMusic(musicList.size() - 1);
                } else {
                    musicBinder.getMediaPlayer().reset();
                    //musicBinder.startMusic(musicBinder.getCurrentPosition() - 1);
                    onComplete(musicBinder.getCurrentPosition() - 1);
                }
                break;
            case 1:
                playMode();
        }
    }

    //下一首
    public void playNext() {
        switch (mCallback.getPlayMode()) {
            case 0:
                if (musicBinder.getCurrentPosition() + 1 == musicList.size()) {
                    musicBinder.setCurrentPosition(0);
                    musicBinder.startMusic(0);
                } else {
                    musicBinder.getMediaPlayer().reset();
                    onComplete(musicBinder.getCurrentPosition() + 1);
                }
                break;
            case 1:
                playMode();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(connection);
        //Log.i("MyMusicFragment", "onDetach()");
    }

    //播放/暂停
    public void play() {
        if (musicBinder.getMediaPlayer().isPlaying()) {
            play.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
            mCallback.setFlag(2); //音乐已经暂停
            musicBinder.getMediaPlayer().pause();
        } else if (mCallback.getFlag() >= 1) { //flag == 0为第一次打开播放器，无法直接切换播放
            play.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
            mCallback.setFlag(1);
            musicBinder.getMediaPlayer().start();
        } else {
            Toast.makeText(getActivity(), "亲，您还未选择音乐哟^_^", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (Flag) activity;
    }

    public void onComplete(int position) {
        musicBinder.mediaPlayer1.reset();
        //初始化MediaPlayer
        try {
            File file = new File(pathList.get(position));
            Log.i("MusicService", String.valueOf(position) + file.getPath());
            Log.i("size", String.valueOf(musicList.size()));
            musicBinder.mediaPlayer1.setDataSource(file.getPath());
            musicBinder.mediaPlayer1.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getActivity().setTitle(String.valueOf(position + 1) + "\t\t\t" + titleList.get(position));
        musicBinder.startMusic(position);
        musicBinder.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (mCallback.getPlayMode()) {
                    case 0:
                        if (musicBinder.getCurrentPosition() + 1 == new MyMusicFragment().musicList.size()) {
                            // getActivity().setTitle(musicBinder.handleName(0));
                            musicBinder.setCurrentPosition(0);
                            musicBinder.startMusic(0);
                        } else {
                            // getActivity().setTitle(musicBinder.handleName(musicBinder.getCurrentPosition() + 1));
                            musicBinder.getMediaPlayer().reset();
                            onComplete(musicBinder.getCurrentPosition() + 1);
                        }
                        break;
                    case 1:
                        playMode();
                }
            }
        });
    }

    public void playMode() {
        int random = (int) (Math.random() * (musicList.size()));
        Log.i("随机数", String.valueOf(random));
        // getActivity().setTitle(musicBinder.handleName(random));
        onComplete(random);
    }
}
