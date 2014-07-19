package com.foolchi.safeguard.domain;

import android.graphics.drawable.Drawable;
/**
 * Created by foolchi on 7/19/14.
 */
public class TaskInfo {

    private String name;
    private Drawable icon;
    private int id;
    private long memory;
    private boolean isCheck;
    private String packageName;
    private boolean isSystemProcess;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystemProcess() {
        return isSystemProcess;
    }

    public void setSystemProcess(boolean isSystemProcess) {
        this.isSystemProcess = isSystemProcess;
    }



}
