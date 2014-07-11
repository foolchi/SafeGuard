package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.foolchi.safeguard.R;
/**
 * Created by foolchi on 6/21/14.
 */
public class SetupGuide2Activity extends Activity implements OnClickListener{

    private Button bt_bind, bt_next, bt_previous;
    private CheckBox cb_bind;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_guide2);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        bt_bind = (Button)findViewById(R.id.bt_guide_bind);
        bt_next = (Button)findViewById(R.id.bt_guide_next);
        bt_previous = (Button)findViewById(R.id.bt_guide_previous);
        bt_bind.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_previous.setOnClickListener(this);

        cb_bind = (CheckBox)findViewById(R.id.cb_guide_check);
        String sim = sp.getString("simSerial", null);
        if (sim != null){
            cb_bind.setText("Binding");
            cb_bind.setChecked(true);
        }
        else {
            cb_bind.setText("No Binding");
            cb_bind.setChecked(false);
        }
        cb_bind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cb_bind.setText("Binding");
                    setSimInfo();
                }
                else {
                    cb_bind.setText("No Binding");
                    eraseSimInfo();
                }
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bt_guide_bind:
                setSimInfo();
                cb_bind.setText("Binding");
                cb_bind.setChecked(true);
                break;

            case R.id.bt_guide_next:
                Intent intent = new Intent(this, SetupGuide3Activity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            case R.id.bt_guide_previous:
                Intent intent2 = new Intent(this, SetupGuide1Activity.class);
                finish();
                startActivity(intent2);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            default:
                break;
        }
    }

    private void setSimInfo(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String simSerial = telephonyManager.getSimSerialNumber();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("simSerial", simSerial);
        editor.commit();
    }

    private void eraseSimInfo(){
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("simSerial");
        editor.commit();
    }
}
