package com.foolchi.safeguard.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.domain.AppInfo;
import com.foolchi.safeguard.engine.AppInfoProvider;

import java.util.List;

/**
 * Created by foolchi on 7/6/14.
 */
public class AppManagerActivity extends Activity implements OnClickListener{

    private static final int GET_ALL_APP_FINISH = 1;
    private ListView lv_app_manager;
    private LinearLayout ll_app_manager_progress;
    private AppInfoProvider provider;
    private AppManagerAdapter adapter;
    private List<AppInfo> list;
    private PopupWindow popupWindow;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case GET_ALL_APP_FINISH:
                    ll_app_manager_progress.setVisibility(View.GONE);
                    adapter = new AppManagerAdapter();
                    lv_app_manager.setAdapter(adapter);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_manager);

        lv_app_manager = (ListView)findViewById(R.id.lv_app_manager);
        ll_app_manager_progress = (LinearLayout)findViewById(R.id.ll_app_manager_progress);
        ll_app_manager_progress.setVisibility(View.VISIBLE);

        new Thread(){
            public void run(){
                provider = new AppInfoProvider(AppManagerActivity.this);
                list = provider.getAllApps();
                Message msg = new Message();
                msg.what = GET_ALL_APP_FINISH;
                handler.sendMessage(msg);
            }
        }.start();

        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismissPopupWindow();
                // 存放当前item坐标值(x,y)
                int[] location = new int[2];
                view.getLocationInWindow(location);
                View popupView = View.inflate(AppManagerActivity.this, R.layout.popup_item, null);
                LinearLayout ll_app_uninstall = (LinearLayout)popupView.findViewById(R.id.ll_app_uninstall);
                LinearLayout ll_app_run = (LinearLayout)popupView.findViewById(R.id.ll_app_start);
                LinearLayout ll_app_share = (LinearLayout)popupView.findViewById(R.id.ll_app_share);
                ll_app_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_app_run.setOnClickListener(AppManagerActivity.this);
                ll_app_share.setOnClickListener(AppManagerActivity.this);

                AppInfo info = (AppInfo)lv_app_manager.getItemAtPosition(position);
                ll_app_uninstall.setTag(info);
                ll_app_run.setTag(info);
                ll_app_share.setTag(info);

                LinearLayout ll_app_popup = (LinearLayout)popupView.findViewById(R.id.ll_app_popup);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
                scaleAnimation.setDuration(300);

                final float scale = view.getContext().getResources().getDisplayMetrics().density;
                popupWindow = new PopupWindow(popupView, (int)(230 * scale + 0.5), (int)(70 * scale + 0.5));
                Drawable drawable = new ColorDrawable(Color.TRANSPARENT);
                popupWindow.setBackgroundDrawable(drawable);

                int x = location[0] + (int)(60 * scale + 0.5);
                int y = location[1];
                popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, x, y);

                ll_app_popup.startAnimation(scaleAnimation);
            }
        });

        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                dismissPopupWindow();
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                dismissPopupWindow();
            }
        });
    }

    private void dismissPopupWindow(){
        if (popupWindow != null){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    @Override
    public void onClick(View view) {
        AppInfo item = (AppInfo)view.getTag();

        switch (view.getId()){
            case R.id.ll_app_uninstall:
                if (item.isSystemApp()){
                    Toast.makeText(AppManagerActivity.this, "Cannot uninstall system app", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ll_app_start:
                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(item.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
                    ActivityInfo[] activityInfos = packageInfo.activities;
                    if (activityInfos != null && activityInfos.length > 0){
                        ActivityInfo startActivity = activityInfos[0];
                        Intent intent = new Intent();
                        intent.setClassName(item.getPackageName(), startActivity.name);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(AppManagerActivity.this, "Cannot start this app", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.ll_app_share:
                break;

            default:
                break;
        }
        dismissPopupWindow();

    }

    private class AppManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            AppInfo info = list.get(position);
            if (convertView == null){
                View view = View.inflate(AppManagerActivity.this, R.layout.app_manager_item, null);
                AppManagerViews views = new AppManagerViews();
                views.iv_app_icon = (ImageView)view.findViewById(R.id.iv_app_manager_icon);
                views.tv_app_name = (TextView)view.findViewById(R.id.tv_app_manager_name);
                views.iv_app_icon.setImageDrawable(info.getIcon());
                views.tv_app_name.setText(info.getAppName());
                view.setTag(views);
                return view;
            }
            else {
                AppManagerViews views = (AppManagerViews)convertView.getTag();
                views.iv_app_icon.setImageDrawable(info.getIcon());
                views.tv_app_name.setText(info.getAppName());
                return convertView;
            }
        }
    }

    private class AppManagerViews{
        public ImageView iv_app_icon;
        public TextView tv_app_name;
    }
}
