package com.foolchi.safeguard.adapter;

import android.media.Image;
import android.widget.BaseAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.foolchi.safeguard.R;
/**
 * Created by foolchi on 6/20/14.
 */
public class MainUIAdapter extends BaseAdapter{
    private static final String[] NAMES = new String[]{
            "手机防盗", "短信备份", "软件管理",
            "流量管理", "任务管理", "手机杀毒",
            "系统优化", "高级工具", "设置中心"
    };
    private static final int[] ICONS = new int[]{
            R.drawable.widget01, R.drawable.widget02, R.drawable.widget03,
            R.drawable.widget04, R.drawable.widget05, R.drawable.widget06,
            R.drawable.widget07, R.drawable.widget08, R.drawable.widget09
    };

    private static ImageView imageView;
    private static TextView textView;
    private Context context;
    private LayoutInflater inflater;
    private SharedPreferences sp;

    public MainUIAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount(){
        return NAMES.length;
    }

    @Override
    public Object getItem(int position){
        return position;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        /*
        View view = inflater.inflate(R.layout.main_item, null);
        imageView = (ImageView)view.findViewById(R.id.iv_main_icon);
        textView = (TextView)view.findViewById(R.id.tv_main_name);
        imageView.setImageResource(ICONS[position]);
        textView.setText(NAMES[position]);

        if (position == 0){
            String name = sp.getString("lostName", "");
            if (!name.equals("")){
                textView.setText(name);
            }
        }
        return view;
        */

        // Optimisation for view
        // convertView 相当于缓存，如果不存在则重绘
        MainViews views;
        View view;
        if (convertView == null){
            views = new MainViews();
            view = inflater.inflate(R.layout.main_item, null);
            views.imageView = (ImageView)view.findViewById(R.id.iv_main_icon);
            views.textView = (TextView)view.findViewById(R.id.tv_main_name);
            views.imageView.setImageResource(ICONS[position]);
            views.textView.setText(NAMES[position]);
            view.setTag(views);
        }
        else {
            view = convertView;
            views = (MainViews)view.getTag();
            views.imageView = (ImageView)view.findViewById(R.id.iv_main_icon);
            views.textView = (TextView)view.findViewById(R.id.tv_main_name);
            views.imageView.setImageResource(ICONS[position]);
            views.textView.setText(NAMES[position]);
        }

        if (position == 0){
            String name = sp.getString("lostName", "");
            if (!"".equals(name)){
                views.textView.setText(name);
            }
        }
        return view;
    }

    private class MainViews{
        ImageView imageView;
        TextView textView;
    }

}
