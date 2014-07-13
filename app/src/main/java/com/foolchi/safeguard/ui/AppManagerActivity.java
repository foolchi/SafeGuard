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
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;
import com.foolchi.safeguard.dao.AppLockDao;
/**
 * Created by foolchi on 7/6/14.
 */
public class AppManagerActivity extends Activity implements OnClickListener{

    private static final int GET_ALL_APP_FINISH = 1;
    private static final int GET_USER_APP_FINISH = 2;
    private static final int GET_LOCKED_APP_FINISH = 3;
    private ListView lv_app_manager;
    private LinearLayout ll_app_manager_progress;
    private AppInfoProvider provider;
    private AppManagerAdapter adapter;
    private List<AppInfo> list;
    private PopupWindow popupWindow;
    private TextView tv_app_title, tv_app_lock;
    private ImageView iv_app_lock;
    private boolean flag = false;
    private AppLockDao appLockDao;
    private List<AppInfo> lockedApps;
    private boolean updateLockAppFinished = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            ll_app_manager_progress.setVisibility(View.GONE);

            switch (msg.what){
                case GET_ALL_APP_FINISH:
                    adapter = new AppManagerAdapter(list);
                    lv_app_manager.setAdapter(adapter);
                    flag = true;
                    break;

                case GET_USER_APP_FINISH:
                    adapter = new AppManagerAdapter(getUserApp());
                    lv_app_manager.setAdapter(adapter);
                    flag = true;
                    break;

                case GET_LOCKED_APP_FINISH:
                    adapter = new AppManagerAdapter(getLockedApp());
                    lv_app_manager.setAdapter(adapter);
                    flag = true;
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
        tv_app_title = (TextView)findViewById(R.id.tv_app_title);
        appLockDao = new AppLockDao(this);


        tv_app_title.setOnClickListener(this);

        initUI(GET_ALL_APP_FINISH);

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
                LinearLayout ll_app_lock = (LinearLayout)popupView.findViewById(R.id.ll_app_lock);
                tv_app_lock = (TextView)popupView.findViewById(R.id.tv_app_lock);
                iv_app_lock = (ImageView)popupView.findViewById(R.id.iv_app_lock);
                ll_app_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_app_run.setOnClickListener(AppManagerActivity.this);
                ll_app_share.setOnClickListener(AppManagerActivity.this);
                ll_app_lock.setOnClickListener(AppManagerActivity.this);

                AppInfo info = (AppInfo)lv_app_manager.getItemAtPosition(position);
                ll_app_uninstall.setTag(info);
                ll_app_run.setTag(info);
                ll_app_share.setTag(info);
                ll_app_lock.setTag(info);
                if (info.isLocked()){
                    iv_app_lock.setImageResource(R.drawable.lock);
                    tv_app_lock.setText(getString(R.string.app_lock));
                }
                else {
                    iv_app_lock.setImageResource(R.drawable.unlock);
                    tv_app_lock.setText(R.string.app_unlock);
                }

                LinearLayout ll_app_popup = (LinearLayout)popupView.findViewById(R.id.ll_app_popup);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
                scaleAnimation.setDuration(300);

                final float scale = view.getContext().getResources().getDisplayMetrics().density;
                popupWindow = new PopupWindow(popupView, (int)(300 * scale + 0.5), (int)(70 * scale + 0.5));
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

        if (!flag)
            return;

        AppInfo item = (AppInfo)view.getTag();

        switch (view.getId()){
            case R.id.tv_app_title:
                if ("All App".equals(tv_app_title.getText().toString().trim())){
                    tv_app_title.setText(getString(R.string.user_app));
                    adapter.setAppInfos(getUserApp());
                    adapter.notifyDataSetChanged();
                }
                else if (getString(R.string.user_app).equals(tv_app_title.getText().toString().trim())){
                    tv_app_title.setText(getString(R.string.locked_app));
                    adapter.setAppInfos(getLockedApp());
                    adapter.notifyDataSetChanged();
                }
                else {
                    tv_app_title.setText(getString(R.string.all_app));
                    adapter.setAppInfos(list);
                    adapter.notifyDataSetChanged();
                }
                break;

            case R.id.ll_app_uninstall:
                if (item.isSystemApp()){
                    Toast.makeText(AppManagerActivity.this, "Cannot uninstall system app", Toast.LENGTH_SHORT).show();
                }
                else {
                    String strUri = "package:" + item.getPackageName();
                    Uri uri = Uri.parse(strUri);
                    Intent deleteIntent = new Intent();
                    deleteIntent.setAction(Intent.ACTION_DELETE);
                    deleteIntent.setData(uri);
                    startActivityForResult(deleteIntent, 0);
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
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "A new fun app" + item.getAppName());
                startActivity(shareIntent);
                break;

            case R.id.ll_app_lock:
                if (item.isLocked()){
                    appLockDao.delete(item.getPackageName());
                    item.setLocked(false);
                }
                else {
                    appLockDao.add(item.getPackageName());
                    item.setLocked(true);
                }
                break;

            default:
                break;
        }
        dismissPopupWindow();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (getString(R.string.user_app).equals(tv_app_title.getText().toString().trim())){
            initUI(GET_USER_APP_FINISH);
            adapter.setAppInfos(getUserApp());
            adapter.notifyDataSetChanged();
        }
        else if (getString(R.string.locked_app).equals(tv_app_lock.getText().toString().trim())){
            initUI(GET_LOCKED_APP_FINISH);
            adapter.setAppInfos(getLockedApp());
            adapter.notifyDataSetChanged();
        }
        else {
            initUI(GET_ALL_APP_FINISH);
            adapter.setAppInfos(list);
            adapter.notifyDataSetChanged();
        }
    }

    private List<AppInfo> getUserApp(){

        List<AppInfo> userApps = new ArrayList<AppInfo>();
        for (AppInfo info : list){
            if (!info.isSystemApp()){
                userApps.add(info);
            }
        }
        return userApps;
    }

    private List<AppInfo> getLockedApp(){
        lockedApps = new ArrayList<AppInfo>();
        List<String> lockedAppPackageNames = appLockDao.getAllPackageName();
        for (AppInfo appInfo : list){
            if (lockedAppPackageNames.contains(appInfo.getPackageName())) {
                lockedApps.add(appInfo);
            }
        }
        return lockedApps;
    }

    private void initUI(final int appTable){
        flag = false;
        ll_app_manager_progress.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                provider = new AppInfoProvider(AppManagerActivity.this);
                list = provider.getAllApps();
                Message msg = new Message();
                msg.what = appTable;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private class AppManagerAdapter extends BaseAdapter{

        private List<AppInfo> list;

        public AppManagerAdapter(List<AppInfo> list){
            this.list = list;
        }

        public void setAppInfos(List<AppInfo> list){
            this.list = list;
        }
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
