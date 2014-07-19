package com.foolchi.safeguard.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.foolchi.safeguard.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by foolchi on 7/19/14.
 */
public class TaskInfoProvider {

    private PackageManager packageManager;
    private ActivityManager activityManager;

    public TaskInfoProvider(Context context){
        packageManager = context.getPackageManager();
        activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public List<TaskInfo> getAllTask(List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos){
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos){
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setId(runningAppProcessInfo.pid);
            taskInfo.setPackageName(runningAppProcessInfo.processName);

            try {
                ApplicationInfo applicationInfo = packageManager.getPackageInfo(taskInfo.getPackageName(), 0).applicationInfo;
                taskInfo.setIcon(applicationInfo.loadIcon(packageManager));
                taskInfo.setName(applicationInfo.loadLabel(packageManager).toString());
                taskInfo.setSystemProcess(!filterApp(applicationInfo));
                Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(new int[]{taskInfo.getId()});
                long memory = memoryInfos[0].getTotalPrivateDirty() * 1024;
                taskInfo.setMemory(memory);
                taskInfos.add(taskInfo);
                taskInfo = null;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return taskInfos;
    }

    public boolean filterApp(ApplicationInfo info){
        if (((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
            || ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0))
            return true;
        return false;
    }
}
