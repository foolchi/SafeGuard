package com.foolchi.safeguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * Created by foolchi on 6/22/14.
 * 手机重启完成接收器
 */
public class BootCompleteReceiver extends BroadcastReceiver{
    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean isProtected = sp.getBoolean("isProtected", false);

        if (isProtected){
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = telephonyManager.getSimSerialNumber();
            String protectedSim = sp.getString("simSerial", "");
            if (!currentSim.equals(protectedSim)){
                SmsManager smsManager = SmsManager.getDefault();
                String number = sp.getString("number", "");
                smsManager.sendTextMessage(number, null, "SIM card changed", null, null);
            }
        }
    }
}
