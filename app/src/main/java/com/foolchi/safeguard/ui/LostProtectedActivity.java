package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.utils.MD5Encoder;

/**
 * Created by foolchi on 6/21/14.
 * 手机防盗界面
 */
public class LostProtectedActivity extends Activity implements OnClickListener{
    private SharedPreferences sp;
    private Dialog dialog;
    private EditText password;
    private EditText confirmPassword;
    private TextView tv_protectedNumber, tv_protectedGuide;
    private CheckBox cb_isProtected;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        if (isSetPassword()){
            showLoginDialog();
        }
        else {
            showFirstDialog();
        }
    }

    private void showLoginDialog(){
        dialog = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.login_dialog, null);
        password = (EditText)view.findViewById(R.id.et_protected_password);
        Button buttonYes = (Button)view.findViewById(R.id.bt_protected_login_yes);
        Button buttonCancel = (Button)view.findViewById(R.id.bt_protected_login_no);
        buttonYes.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showFirstDialog(){
        dialog = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.first_dialog, null);
        password = (EditText)view.findViewById(R.id.et_protected_first_password);
        confirmPassword = (EditText)view.findViewById(R.id.et_protected_confirm_password);
        Button buttonYes = (Button)view.findViewById(R.id.bt_protected_first_yes);
        Button buttonCancel = (Button)view.findViewById(R.id.bt_protected_first_no);
        buttonYes.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean isSetPassword(){
        String pwd = sp.getString("password", "");
        return !(pwd == null || pwd.equals(""));
    }

    private boolean isSetupGuide(){
        return sp.getBoolean("setupGuide", false);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bt_protected_first_yes:
                String fp = password.getText().toString().trim();
                String cp = password.getText().toString().trim();
                if (fp.equals("") || cp.equals("")){
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (fp.equals(cp)){
                        Editor editor = sp.edit();
                        editor.putString("password", MD5Encoder.encode(fp));
                        editor.commit();
                        dialog.dismiss();
                        if (!isSetupGuide()){
                            finish();
                            Intent intent = new Intent(this, SetupGuide1Activity.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
                break;

            case R.id.bt_protected_first_no:
                Intent intent_no = new Intent(this, MainActivity.class);
                dialog.dismiss();
                finish();
                startActivity(intent_no);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            case R.id.bt_protected_login_yes:
                String pwd = password.getText().toString().trim();
                if (pwd.equals("")){
                    Toast.makeText(this, "Please Input the Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    String str = sp.getString("password", "");
                    if (MD5Encoder.encode(pwd).equals(str)){
                        if (isSetupGuide()){
                            setContentView(R.layout.lost_protected);
                            tv_protectedNumber = (TextView)findViewById(R.id.tv_lost_protected_number);
                            tv_protectedGuide = (TextView)findViewById(R.id.tv_lost_protected_guide);
                            cb_isProtected = (CheckBox)findViewById(R.id.cb_lost_protected_isProtected);

                            tv_protectedNumber.setText("lostProtectedNumber:" + sp.getString("number", ""));
                            tv_protectedGuide.setOnClickListener(this);
                            boolean isProtecting = sp.getBoolean("isProtected", false);
                            if (isProtecting){
                                cb_isProtected.setText(R.string.guide4_item2);
                                cb_isProtected.setChecked(true);
                            }

                            cb_isProtected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    if (b){
                                        cb_isProtected.setText(R.string.guide4_item2);
                                        sp.edit().putBoolean("isProtected", true).commit();
                                    }
                                    else {
                                        cb_isProtected.setText(R.string.guide4_item1);
                                        sp.edit().putBoolean("isProtected", false).commit();
                                    }
                                }
                            });
                            /*
                            finish();
                            Intent intent = new Intent(this, SetupGuide1Activity.class);
                            startActivity(intent);
                            */
                        }
                        else {
                            finish();
                            Intent intent = new Intent(this, SetupGuide1Activity.class);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    }
                    else {
                        Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.bt_protected_login_no:
                Intent intent_login_no = new Intent(this, MainActivity.class);
                dialog.dismiss();
                finish();
                startActivity(intent_login_no);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);;
                break;

            case R.id.tv_lost_protected_guide:
                Intent setupGuideIntent = new Intent(this, SetupGuide1Activity.class);
                finish();
                startActivity(setupGuideIntent);
                break;

            default:
                break;
        }
    }
}
