package com.bfansheng.mymusic.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bfansheng.mymusic.R;

import java.util.List;

/**
 * Created by Hp on 2016/2/21.
 */
public class MyAdapter extends ArrayAdapter<String> {

    private int resourceId;

    public MyAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView对已加载布局进行缓存，所以如果convertView不为空，可以直接使用
        String musicName = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.list_item);
            //使用view的setTag方法保存viewHolder对象
            view.setTag(viewHolder);
        } else {
            view = convertView;
            //通过getTag方法获取保存的对象
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(String.valueOf(position + 1) + "\t\t" + musicName);
        return view;
    }

    //用来对控件的实例进行缓存
    class ViewHolder {
        TextView textView;
    }
}
