package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.receiver.MyAdminReceiver;

/**
 * Created by foolchi on 6/21/14.
 */
public class SetupGuide4Activity extends Activity implements  OnClickListener{

    private Button bt_previous, bt_finish;
    private CheckBox cb_protected;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_guide4);

        bt_previous = (Button)findViewById(R.id.bt_guide_previous);
        bt_finish = (Button)findViewById(R.id.bt_guide_finish);
        bt_finish.setOnClickListener(this);
        bt_previous.setOnClickListener(this);
        cb_protected = (CheckBox)findViewById(R.id.cb_guide_protected);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean isProtecting = sp.getBoolean("isProtected", false);
        if (isProtecting){
            cb_protected.setText(R.string.guide4_item2);
            cb_protected.setChecked(true);
        }

        cb_protected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    cb_protected.setText(R.string.guide4_item2);
                    Editor editor = sp.edit();
                    editor.putBoolean("isProtected", true);
                    editor.commit();
                }
                else {
                    cb_protected.setText(R.string.guide4_item1);
                    sp.edit().putBoolean("isProtected", false).commit();
                }
            }
        });

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_guide_finish:
                if (cb_protected.isChecked()){
                    finishSetupGuide();
                    finish();
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Warning");
                    builder.setMessage("Are you really want to finish");
                    builder.setCancelable(false);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sp.edit().putBoolean("setupGuide", false).commit();
                            finish();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //sp.edit().putBoolean("setupGuide", false).commit();
                            builder.create().hide();
                        }
                    }).create().show();
                }
                break;

            case R.id.bt_guide_previous:
                Intent intent = new Intent(this, SetupGuide3Activity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            default:
                break;
        }

    }

    private void finishSetupGuide(){
        sp.edit().putBoolean("setupGuide", true).commit();

        // 拿到一个设备管理器
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        // 新建一个组件，用来启动注册管理器界面
        ComponentName componentName = new ComponentName(this, MyAdminReceiver.class);
        // 判断是否已经注册
        if (!devicePolicyManager.isAdminActive(componentName)){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivity(intent);
        }
    }
}
