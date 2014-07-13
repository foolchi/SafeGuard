package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.service.IService;
import com.foolchi.safeguard.service.WatchDogService;
import com.foolchi.safeguard.utils.MD5Encoder;

/**
 * Created by foolchi on 7/11/14.
 */
public class LockActivity extends Activity {

    private ImageView iv_app_icon;
    private TextView tv_app_name;
    private EditText et_app_pwd;
    private String password;
    private IService iService;
    private MyConnection connection;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_lock_layout);
        iv_app_icon = (ImageView)findViewById(R.id.iv_lock_app_icon);
        tv_app_name = (TextView)findViewById(R.id.tv_lock_app_name);
        et_app_pwd = (EditText)findViewById(R.id.et_lock_pwd);

        connection = new MyConnection();
        Intent intent = new Intent(this, WatchDogService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        password = getSharedPreferences("config", Context.MODE_PRIVATE).getString("password", "");

        try {
            packageName = getIntent().getStringExtra("packageName");
            ApplicationInfo appInfo = getPackageManager().getPackageInfo(packageName, 0).applicationInfo;
            Drawable app_icon = appInfo.loadIcon(getPackageManager());
            String app_name = appInfo.loadLabel(getPackageManager()).toString();
            iv_app_icon.setImageDrawable(app_icon);
            tv_app_name.setText(app_name);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void confirm(View v){
        String input = et_app_pwd.getText().toString().trim();

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please set the anti-thief first", Toast.LENGTH_LONG).show();
        }
        else if (password.equals(MD5Encoder.encode(input))){
            finish();
            iService.stopApp(packageName);
        }
        else {
            Toast.makeText(this, "Incorrect Password", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()){
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy(){
        if (connection != null){
            unbindService(connection);
            connection = null;
        }
        super.onDestroy();
    }

    private class MyConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iService = (IService) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
