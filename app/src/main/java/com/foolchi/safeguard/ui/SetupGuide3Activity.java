package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foolchi.safeguard.R;
/**
 * Created by foolchi on 6/21/14.
 */
public class SetupGuide3Activity extends Activity implements OnClickListener{

    private Button bt_next, bt_previous, bt_select;
    private EditText et_phoneNumber;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_guide3);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        bt_next = (Button)findViewById(R.id.bt_guide_next);
        bt_previous = (Button)findViewById(R.id.bt_guide_previous);
        bt_select = (Button)findViewById(R.id.bt_guide_select);
        et_phoneNumber = (EditText)findViewById(R.id.et_guide_phoneNumber);
        bt_next.setOnClickListener(this);
        bt_previous.setOnClickListener(this);
        bt_select.setOnClickListener(this);
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_guide_select:
                Intent selectIntent = new Intent(this, SelectContactActivity.class);
                startActivityForResult(selectIntent, 1);
                break;

            case R.id.bt_guide_next:
                String number = et_phoneNumber.getText().toString().trim();
                System.out.println("Input Phone Number :" + number);
                if ("".equals(number)){
                    Toast.makeText(this, "Empty phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    Editor editor = sp.edit();
                    editor.putString("number", number);
                    editor.commit();

                    Intent intent = new Intent(this, SetupGuide4Activity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
                }
                break;

            case R.id.bt_guide_previous:
                Intent i = new Intent(this, SetupGuide2Activity.class);
                finish();
                startActivity(i);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            default:
                break;
        }

    }
}
