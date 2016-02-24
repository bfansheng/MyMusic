package com.bfansheng.mymusic.utils;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import java.net.URL;

/**
 * Created by Hp on 2016/2/24.
 */
public class DownLoadImage {
    public String image_path;

    public DownLoadImage(String image_path) {
        this.image_path = image_path;
    }

    public void loadImage(final ImageCallBack callBack) {

        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Drawable drawable = (Drawable) msg.obj;
                callBack.setDrawable(drawable);
            }

        };

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Drawable drawable = Drawable.createFromStream(new URL(
                            image_path).openStream(), "");

                    Message message = Message.obtain();
                    message.obj = drawable;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface ImageCallBack {
        public void setDrawable(Drawable drawable);
    }
}
