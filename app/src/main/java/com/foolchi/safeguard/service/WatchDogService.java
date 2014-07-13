package com.foolchi.safeguard.service;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.foolchi.safeguard.dao.AppLockDao;
import com.foolchi.safeguard.ui.LockActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by foolchi on 7/11/14.
 * Watch the app stack
 */
public class WatchDogService extends Service {

    private AppLockDao dao;
    private List<String> apps;
    private ActivityManager activityManager;
    private Intent intent;
    private boolean flag = true;
    private List<String> stopApps;
    private MyBinder myBinder;
    private KeyguardManager keyguardManager;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        System.out.println("Watch Dog Service Started");

        dao = new AppLockDao(this);
        myBinder = new MyBinder();
        apps = dao.getAllPackageName();
        stopApps = new ArrayList<String>();
        activityManager = (ActivityManager)getSystemService(Service.ACTIVITY_SERVICE);
        intent = new Intent(this, LockActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        keyguardManager = (KeyguardManager) getSystemService(Service.KEYGUARD_SERVICE);

        getContentResolver().registerContentObserver(Uri.parse("content://com.foolchi.safeguard.applockprovider"), true, new MyObserver(new Handler()));


        new Thread(){
            public void run(){
                while (flag){
                    try {
                        if (keyguardManager.inKeyguardRestrictedInputMode()){
                            stopApps.clear();
                        }
                        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
                        ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
                        String packageName = runningTaskInfo.topActivity.getPackageName();
                        System.out.println("Current Running: " + packageName);
                        if (stopApps.contains(packageName)){
                            sleep(1000);
                            continue;
                        }

                        if (apps.contains(packageName)){
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                        else {

                        }
                        sleep(1000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void invokeMethodStartApp(String packageName){
        if (stopApps.contains(packageName)){
            stopApps.remove(packageName);
        }
    }

    private void invokeMethodStopApp(String packageName){
        stopApps.add(packageName);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        flag = false;
    }

    private class MyBinder extends Binder implements IService{

        @Override
        public void startApp(String packageName) {
            invokeMethodStartApp(packageName);
        }

        @Override
        public void stopApp(String packageName) {
            invokeMethodStopApp(packageName);
        }
    }

    private class MyObserver extends ContentObserver{

        public MyObserver(Handler handler) {
            super(handler);
            apps = dao.getAllPackageName();
        }
    }
}
