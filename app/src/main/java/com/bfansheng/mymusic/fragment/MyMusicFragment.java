package com.bfansheng.mymusic.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.bfansheng.mymusic.R;
import com.bfansheng.mymusic.service.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 2016/2/17.
 */
public class MyMusicFragment extends Fragment implements View.OnClickListener {

    private List<String> musicList = new ArrayList<String>();
    public final static File musicPath = new File(Environment.getExternalStorageDirectory().getPath() + "/netease/cloudmusic/Music");
    private Button next_button;
    private Button previous_button;
    private Button play;
    private MusicService.MusicBinder musicBinder;

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
        next_button = (Button) getActivity().findViewById(R.id.next_music);
        next_button.setOnClickListener(this);
        previous_button = (Button) getActivity().findViewById(R.id.previous_music);
        previous_button.setOnClickListener(this);

        Intent intent = new Intent(getActivity(), MusicService.class);
        //getActivity().startService(intent);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //-----------------------------------------------------------------------------
        ListView listView = (ListView) getActivity().findViewById(R.id.list_music);
        musicList = getMusicList();
        ArrayAdapter<String> mAdaptert = new ArrayAdapter<String>
                (getActivity(), R.layout.item_musiclist, musicList);
        listView.setAdapter(mAdaptert);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicBinder.setCurrentPosition(position);
                musicBinder.resetMusic();
                musicBinder.startMusic(position);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_music:
                playPrevious();
                break;
            case R.id.next_music:
                playNext();
                break;
            case R.id.play_music:
                musicBinder.pauseMusic();
            default:
                break;
        }
    }

    public List<String> getMusicList() {
        if (musicPath.listFiles().length > 0) {
            for (File file : musicPath.listFiles()) {
                musicList.add(file.getName());
            }
        }
        return musicList;
    }

    public void playPrevious() {
        if (musicBinder.getCurrentPosition() - 1 < 0) {
            musicBinder.setCurrentPosition(musicList.size()-1);
            musicBinder.startMusic(musicList.size() - 1);
        } else {
            musicBinder.previousMusic();
        }
    }

    public void playNext() {
        if (musicBinder.getCurrentPosition() + 1 == musicList.size()) {
            musicBinder.setCurrentPosition(0);
            musicBinder.startMusic(0);
        } else {
            musicBinder.nextMusic();
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(connection);
        super.onDestroy();
    }
}
