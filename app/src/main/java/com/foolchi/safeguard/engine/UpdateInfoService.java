package com.foolchi.safeguard.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import com.foolchi.safeguard.domain.UpdateInfo;
/**
 * Created by foolchi on 6/20/14.
 */
public class UpdateInfoService {
    private Context context;

    public UpdateInfoService(Context context){
        this.context = context;
    }
    public UpdateInfo getUpdateInfo(int urlId) throws Exception{
        String path = context.getResources().getString(urlId);
        URL url = new URL(path);
        System.out.println("service path: " + path);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setConnectTimeout(5000); //set timeout 5s
        httpURLConnection.setRequestMethod("GET");

        InputStream is = httpURLConnection.getInputStream();
        System.out.println("service GET");
        return UpdateInfoParser.getUpdateInfo(is);
    }
}
