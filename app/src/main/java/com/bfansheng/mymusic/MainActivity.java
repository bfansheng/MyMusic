package com.bfansheng.mymusic;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.bfansheng.mymusic.fragment.AboutFragment;
import com.bfansheng.mymusic.fragment.MyMusicFragment;
import com.bfansheng.mymusic.fragment.SearchMusicFragment;
import com.bfansheng.mymusic.service.MusicService;

public class MainActivity extends AppCompatActivity implements MyMusicFragment.Flag {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private Intent intent;
    private int flag = 0;
    private int mode = 0;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getPlayMode() {
        return mode;
    }

    public void setPlayMode(int mode) {
        this.mode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawe_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        switchMyMusic();
        mNavigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_mymusic:
                        switchMyMusic();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_searchmusic:
                        SearchMusic();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_about:
                        switchAbout();
                        mDrawerLayout.closeDrawers();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        intent = new Intent(this, MusicService.class);
        startService(intent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchMyMusic() {
        getFragmentManager().beginTransaction().replace(R.id.fragment, new MyMusicFragment()).commit();
    }

    public void SearchMusic() {
        getFragmentManager().beginTransaction().replace(R.id.fragment, new SearchMusicFragment()).commit();
    }

    public void switchAbout() {
        getFragmentManager().beginTransaction().replace(R.id.fragment, new AboutFragment()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("退出");
            dialog.setMessage("你真的要退出吗？");
            dialog.setCancelable(false);
            dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            //显示AlertDialog
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
        //Log.i("MainActivity", "onDestroy");
    }
}
