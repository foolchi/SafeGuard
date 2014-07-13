package com.foolchi.safeguard.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import com.foolchi.safeguard.dao.AppLockDao;

import com.foolchi.safeguard.domain.AppInfo;

/**
 * Created by foolchi on 7/6/14.
 */
public class AppInfoProvider {

    private PackageManager packageManager;
    private AppLockDao appLockDao;

    public AppInfoProvider(Context context){
        packageManager = context.getPackageManager();
        appLockDao = new AppLockDao(context);
    }

    public List<AppInfo> getAllApps(){
        List<AppInfo> list = new ArrayList<AppInfo>();
        AppInfo myAppInfo;

        // 获取所有程序信息，包括已卸载但是没有清除数据的应用
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo info : packageInfos){
            myAppInfo = new AppInfo();
            String packageName = info.packageName;
            ApplicationInfo appInfo = info.applicationInfo;
            Drawable icon = appInfo.loadIcon(packageManager);
            String appName = appInfo.loadLabel(packageManager).toString();
            myAppInfo.setAppName(appName);
            myAppInfo.setPackageName(packageName);
            myAppInfo.setIcon(icon);

            if (isSystemApp(appInfo)){
                myAppInfo.setSystemApp(true);
            }
            else{
                myAppInfo.setSystemApp(false);
            }

            myAppInfo.setLocked(isLockedApp(packageName));

            list.add(myAppInfo);
        }
        return list;
    }

    public boolean isSystemApp(ApplicationInfo appInfo){

        if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
            return true;
        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
            return true;
        return false;
    }

    public boolean isLockedApp(String packageName){
        if (appLockDao.find(packageName))
            return true;
        return false;
    }
}
