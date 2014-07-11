package com.foolchi.safeguard.service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Environment;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;

import com.foolchi.safeguard.domain.SmsInfo;
import com.foolchi.safeguard.engine.SmsService;

public class BackupSmsService extends Service {
    private SmsService smsService;

    public BackupSmsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        smsService = new SmsService(this);

        new Thread(){
            public void run(){
                List<SmsInfo> infos = smsService.getSmsInfo();
                File dir = new File(Environment.getExternalStorageDirectory(), "/SafeGuard/backup");
                if (!dir.exists()){
                    dir.mkdir();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String currentDate = sdf.format(new Date());

                File file = new File(Environment.getExternalStorageDirectory() + "/SafeGuard/backup/smsbackup_" + currentDate + ".xml");
                XmlSerializer xmlSerializer = Xml.newSerializer();
                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    xmlSerializer.setOutput(fos, "utf-8");
                    xmlSerializer.startDocument("utf-8", true);
                    xmlSerializer.startTag(null, "smss");

                    for (SmsInfo info : infos){
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
                    }

                    xmlSerializer.endTag(null, "smss");
                    xmlSerializer.endDocument();

                    fos.flush();
                    fos.close();

                    // 子线程无法弹出Toast，因为子线程没有Looper
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "Backup finished", Toast.LENGTH_LONG).show();
                    Looper.loop();

                }
                catch (Exception e){
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "Backup failed", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
