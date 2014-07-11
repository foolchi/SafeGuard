package com.foolchi.safeguard.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.domain.SmsInfo;
import com.foolchi.safeguard.engine.SmsService;
/**
 * Created by foolchi on 7/5/14.
 */
public class SmsBackupActivity extends Activity {

    private ProgressBar progressBar;
    private Button bt_smsbackup;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_backup);

        progressBar = (ProgressBar)findViewById(R.id.pb_smsbackup);
        bt_smsbackup = (Button)findViewById(R.id.bt_smsbackup);

        final Thread t = new Thread(){
            public void run(){
                SmsService smsService = new SmsService(SmsBackupActivity.this);
                List<SmsInfo> infos = smsService.getSmsInfo();
                File dir = new File(Environment.getExternalStorageDirectory(), "/SafeGuard/backup");
                if (!dir.exists()){
                    dir.mkdir();
                }

                System.out.println("Mkdir");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String currentDate = sdf.format(new Date());
                System.out.println("Get date");

                File file = new File(Environment.getExternalStorageDirectory() + "/documents/smsbackup.xml");
                XmlSerializer xmlSerializer = Xml.newSerializer();
                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    xmlSerializer.setOutput(fos, "utf-8");
                    xmlSerializer.startDocument("utf-8", true);
                    xmlSerializer.startTag(null, "smss");

                    progressBar.setMax(infos.size());
                    int current = 0;
                    progressBar.setProgress(current);

                    for (SmsInfo info : infos){
                        System.out.println(current + "/ " + infos.size() + ":" + info.getAddress() + ":" + info.getBody());
                        xmlSerializer.startTag(null, "sms");

                        xmlSerializer.startTag(null, "id");
                        xmlSerializer.text(info.getId());
                        xmlSerializer.endTag(null, "id");

                        xmlSerializer.startTag(null, "address");
                        xmlSerializer.text(info.getAddress());
                        xmlSerializer.endTag(null, "address");

                        xmlSerializer.startTag(null, "date");
                        xmlSerializer.text(info.getDate());
                        xmlSerializer.endTag(null, "date");

                        xmlSerializer.startTag(null, "type");
                        xmlSerializer.text(info.getType() + "");
                        xmlSerializer.endTag(null, "type");

                        xmlSerializer.startTag(null, "body");
                        xmlSerializer.text(info.getBody());
                        xmlSerializer.endTag(null, "body");

                        xmlSerializer.endTag(null, "sms");
                        current += 1;
                        //System.out.println(current + "/ " + infos.size() + ":" + info.getAddress() + ":" + info.getBody());
                        progressBar.setProgress(current);
                    }

                    xmlSerializer.endTag(null, "smss");
                    xmlSerializer.endDocument();

                    fos.flush();
                    fos.close();
                    bt_smsbackup.setText("Finished");

                    // 子线程无法弹出Toast，因为子线程没有Looper
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "Backup finished", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
                catch (Exception e){
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "Oh no, Backup failed", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        };

        bt_smsbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_smsbackup.getText().toString().equals("Start")
                    || bt_smsbackup.getText().toString().equals("Finished") ){
                    bt_smsbackup.setText("Stop");
                    t.start();
                }
                else if (bt_smsbackup.getText().toString().equals("Stop")){
                    bt_smsbackup.setText("Start");
                    t.interrupt();
                    progressBar.setProgress(0);
                    progressBar.setMax(0);
                }
            }
        });
    }
}
