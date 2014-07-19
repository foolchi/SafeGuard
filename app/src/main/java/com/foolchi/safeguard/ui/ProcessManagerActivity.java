package com.foolchi.safeguard.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.domain.TaskInfo;
import com.foolchi.safeguard.engine.TaskInfoProvider;
import com.foolchi.safeguard.utils.TextFormater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by foolchi on 7/16/14.
 */
public class ProcessManagerActivity extends Activity implements OnClickListener {

    private static final int LOAD_FINISHED = 1;

    private TextView tv_process_count, tv_process_memory;
    private ActivityManager activityManager;
    private List<RunningAppProcessInfo> runningAppProcessInfos;
    private LinearLayout ll_process_load;
    private ListView lv_process_list;
    private Button bt_process_clear, bt_process_setting;

    private TaskInfoProvider taskInfoProvider;
    private List<TaskInfo> taskInfos;
    private TaskInfoAdapter adapter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case LOAD_FINISHED:
                    ll_process_load.setVisibility(View.INVISIBLE);
                    adapter = new TaskInfoAdapter();
                    lv_process_list.setAdapter(adapter);
                    break;

                default:
                    break;
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        boolean flags = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.process_manager);
        if (flags){
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.process_manager_title);
        }

        tv_process_count = (TextView)findViewById(R.id.tv_process_count);
        tv_process_memory = (TextView)findViewById(R.id.tv_process_memory);
        ll_process_load = (LinearLayout)findViewById(R.id.ll_process_load);
        lv_process_list = (ListView)findViewById(R.id.lv_process_list);
        bt_process_clear = (Button)findViewById(R.id.bt_process_clear);
        bt_process_setting = (Button)findViewById(R.id.bt_process_setting);
        bt_process_clear.setOnClickListener(this);
        bt_process_setting.setOnClickListener(this);

        initData();
    }

    private void initData(){
        initTitle();
        ll_process_load.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                taskInfoProvider = new TaskInfoProvider(ProcessManagerActivity.this);
                taskInfos = taskInfoProvider.getAllTask(runningAppProcessInfos);
                Message msg = new Message();
                msg.what = LOAD_FINISHED;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private int getRunningAppCount(){
        runningAppProcessInfos = activityManager.getRunningAppProcesses();
        return runningAppProcessInfos.size();
    }

    private String getAvailMemory(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long size = memoryInfo.availMem;
        return TextFormater.dataSizeFormat(size);
    }

    private void initTitle(){
        tv_process_count.setText(getString(R.string.process_count) + ": " + getRunningAppCount());
        tv_process_memory.setText(getString(R.string.process_memory) + ": " + getAvailMemory());
    }

    @Override
    public void onClick(View view) {

    }


    private class TaskInfoAdapter extends BaseAdapter{

        private List<TaskInfo> userTaskInfo;
        private List<TaskInfo> systemTaskInfo;

        public TaskInfoAdapter(){
            userTaskInfo = new ArrayList<TaskInfo>();
            systemTaskInfo = new ArrayList<TaskInfo>();

            for (TaskInfo taskInfo : taskInfos){
                if (taskInfo.isSystemProcess()){
                    systemTaskInfo.add(taskInfo);
                }
                else {
                    userTaskInfo.add(taskInfo);
                }
            }
        }

        @Override
        public int getCount() {
            return taskInfos.size() + 2;
        }

        @Override
        public Object getItem(int i) {
            if (i == 0){
                return 0;
            }
            if (i <= userTaskInfo.size()){
                return userTaskInfo.get(i-1);
            }
            if (i == userTaskInfo.size() + 1){
                return i;
            }
            if (i <= taskInfos.size() + 2){
                return systemTaskInfo.get(i-userTaskInfo.size()-2);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view;
            TaskInfoViews views;
            TaskInfo taskInfo;

            if (position == 0){
                return newTextView("User process(" + userTaskInfo.size() + ")");
            }
            if (position == userTaskInfo.size() + 1){
                return newTextView("System process(" + systemTaskInfo.size() + ")");
            }

            if (position <= userTaskInfo.size()){
                taskInfo = userTaskInfo.get(position-1);
            }
            else if (position <= taskInfos.size() + 2){
                taskInfo = systemTaskInfo.get(position - userTaskInfo.size() - 2);
            }
            else {
                taskInfo = new TaskInfo();
            }

            if (convertView == null || convertView instanceof TextView){
                view = View.inflate(ProcessManagerActivity.this, R.layout.process_manager_item, null);
                views = new TaskInfoViews();
                views.iv_process_icon = (ImageView)view.findViewById(R.id.iv_process_manager_icon);
                views.tv_process_name = (TextView)view.findViewById(R.id.tv_process_manager_name);
                views.tv_process_memory = (TextView)view.findViewById(R.id.tv_process_manager_memory);
                views.cb_process_state = (CheckBox)view.findViewById(R.id.cb_process_manager_state);
                view.setTag(views);
            }
            else {
                view = convertView;
                views = (TaskInfoViews)view.getTag();
            }

            views.iv_process_icon.setImageDrawable(taskInfo.getIcon());
            views.tv_process_memory.setText("Memory use: " + TextFormater.dataSizeFormat(taskInfo.getMemory()));
            views.tv_process_name.setText(taskInfo.getName());
            views.cb_process_state.setChecked(taskInfo.isCheck());
            return view;
        }

        private TextView newTextView(String title){
            TextView tv_title = new TextView(ProcessManagerActivity.this);
            tv_title.setText(title);
            return tv_title;
        }

    }
    private class TaskInfoViews{
        ImageView iv_process_icon;
        TextView tv_process_name;
        TextView tv_process_memory;
        CheckBox cb_process_state;
    }
}
