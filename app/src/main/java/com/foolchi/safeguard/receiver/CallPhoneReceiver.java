package com.foolchi.safeguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.foolchi.safeguard.ui.LostProtectedActivity;
/**
 * Created by foolchi on 6/21/14.
 * 指定拨打指定号码时重定向到启动手机防盗界面
 */
public class CallPhoneReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        String outPhoneNumber = this.getResultData();
        if ("####".equals(outPhoneNumber)){
            Intent i = new Intent(context, LostProtectedActivity.class);
            // 在receiver里面启动activity，需要指定
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            // 不会拨打电话
            setResultData(null);
        }
    }
}
