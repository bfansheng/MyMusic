package com.bfansheng.mymusic.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfansheng.mymusic.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Hp on 2016/2/24.
 */
public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> list;

    public SearchAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_searchlist, null);
            holder = new ViewHolder();

            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.author.setText(list.get(position).get("author").toString());
        holder.title.setText(list.get(position).get("title").toString());
        //接口回调的方法，完成图片的读取;
        DownLoadImage downImage = new DownLoadImage(list.get(position).get("image").toString());
        downImage.loadImage(new DownLoadImage.ImageCallBack() {

            @Override
            public void setDrawable(Drawable drawable) {
                holder.image.setImageDrawable(drawable);
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView author, title;
        ImageView image;
    }
}
