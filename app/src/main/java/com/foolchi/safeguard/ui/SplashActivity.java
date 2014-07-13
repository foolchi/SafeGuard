package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.domain.UpdateInfo;
import com.foolchi.safeguard.engine.DownloadTask;
import com.foolchi.safeguard.engine.UpdateInfoService;

import java.io.File;

import static java.lang.Thread.sleep;

public class SplashActivity extends Activity {

    private TextView tv_version;
    private LinearLayout l_layout;
    private ProgressDialog progressDialog;
    private UpdateInfo info;
    private String version;
    private static final String TAG = "Safe Guard";
    private boolean update;

    /*
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (isNeedUpdate(version)){
                showUpdateDialog();
            }
        }
    };
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); // dismiss the title
        setContentView(R.layout.splash);

        // set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tv_version = (TextView)findViewById(R.id.tv_splash_version);
        version = getVersion();
        tv_version.setText("Version: " + version);
        l_layout = (LinearLayout)findViewById(R.id.ll_splash_main);
        AlphaAnimation alphaAnimation;
        alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);
        l_layout.startAnimation(alphaAnimation);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Downloading...");
        //handler.sendEmptyMessage(0);
        isNeedUpdate(version);
    }


    private void showUpdateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("Update warning");
        builder.setMessage(info.getDescription());
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which){
                if (Environment.getDataDirectory().equals(Environment.MEDIA_MOUNTED)){
                    File dir = new File(Environment.getDataDirectory(), "/safeguard");
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    String apkPath = Environment.getDataDirectory() + "/safeguard";
                    UpdateTask task = new UpdateTask(info.getUrl(), apkPath);
                    progressDialog.show();
                    new Thread(task).start();
                }
                else {
                    Toast.makeText(SplashActivity.this, "Please insert SD card", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadMainUI();
            }
        });
        builder.create().show();
    }

    private void isNeedUpdate(final String version){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateInfoService updateInfoService = new UpdateInfoService(SplashActivity.this);
                try{
                    info = updateInfoService.getUpdateInfo(R.string.serverUrl);

                    String v = info.getVersion();
                    if (v.equals(version)){
                        Log.i(TAG, "Current version: " + version);
                        Log.i(TAG, "New version available: " + v);
                        update = false;
                    }
                    else {
                        Log.i(TAG, "Need to update");
                        update = true;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

        if (update){
            showUpdateDialog();
        }
        else {
            loadMainUI();
        }

        /*
        UpdateInfoService updateInfoService = new UpdateInfoService(this);
        try{
            info = updateInfoService.getUpdateInfo(R.string.serverUrl);
            String v = info.getVersion();
            if (v.equals(version)){
                Log.i(TAG, "Current version: " + version);
                Log.i(TAG, "New version available: " + v);
                loadMainUI();
                return false;
            }
            else {
                Log.i(TAG, "Need to update");
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Cannot connect to the server", Toast.LENGTH_SHORT).show();
            loadMainUI();
        }
        return false;
        */
    }

    private void loadMainUI(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getVersion(){
        try{
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "Unknown Version";
        }
    }


    private void install(File file){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        finish();
        startActivity(intent);
    }

    //=============================================================================
    // Download
    //=============================================================================

    class UpdateTask implements Runnable{
        private String path, filePath;

        public UpdateTask(String path, String filePath){
            this.path = path;
            this.filePath = filePath;
        }

        @Override
        public void run(){
            try{
                File file = DownloadTask.getFile(path, filePath, progressDialog);
                progressDialog.dismiss();
                install(file);
            }
            catch (Exception e){
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(SplashActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                loadMainUI();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
