package com.foolchi.safeguard.engine;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.foolchi.safeguard.domain.SmsInfo;
/**
 * Created by foolchi on 7/5/14.
 */
public class SmsService {
    private Context context;

    public SmsService(Context context){
        this.context = context;
    }

    public List<SmsInfo> getSmsInfo(){
        List<SmsInfo> infos = new ArrayList<SmsInfo>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[] {"_id", "address", "date", "type", "body"}, null, null, " date desc ");
        SmsInfo info;
        while (cursor.moveToNext()){
            info = new SmsInfo();
            String id = cursor.getString(0);
            String address = cursor.getString(1);
            String date = cursor.getString(2);
            int type = cursor.getInt(3);
            String body = cursor.getString(4);
            info.setId(id);
            info.setAddress(address);
            info.setType(type);
            info.setDate(date);
            info.setBody(body);
            infos.add(info);
        }
        cursor.close();
        return infos;
    }
}
