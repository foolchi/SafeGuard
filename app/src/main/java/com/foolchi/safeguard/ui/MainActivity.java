package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.adapter.MainUIAdapter;

/**
 * Created by foolchi on 6/20/14.
 */
public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private MainUIAdapter adapter;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
        gridView = (GridView)findViewById(R.id.gv_main);
        adapter = new MainUIAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,
                                           final View view, int position, long id) {
                if (position == 0){
                    // 防盗
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("设置");
                    builder.setMessage("设置别名");
                    final EditText et = new EditText(MainActivity.this);
                    et.setHint("新名称");
                    builder.setView(et);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            String name = et.getText().toString();
                            if (name.equals("")){
                                Toast.makeText(MainActivity.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("lostName", name);
                                editor.commit();

                                TextView tv = (TextView)view.findViewById(R.id.tv_main_name);
                                tv.setText(name);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            return; // TODO Auto-generated method stub
                        }
                    });
                    builder.create().show();

                }
                return true;
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        switch (position){
            case 0:

                break;
            case 1:
                Intent intent1 = new Intent(this, SmsBackupActivity.class);
                //finish();
                startActivity(intent1);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            case 2:
                Intent intent2 = new Intent(this, AppManagerActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                Intent intent8 = new Intent(this, LostProtectedActivity.class);
                finish();
                startActivity(intent8);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            default:
                break;
        }
    }
}
