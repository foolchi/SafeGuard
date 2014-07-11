package com.foolchi.safeguard.receiver;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.foolchi.safeguard.R;
import com.foolchi.safeguard.engine.GPSInfoProvider;
import com.foolchi.safeguard.ui.MainActivity;


/**
 * Created by foolchi on 6/22/14.
 */
public class SmsReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[])intent.getExtras().get("pdus");
        for (Object pdu : pdus){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String content = smsMessage.getMessageBody();
            String sender = smsMessage.getOriginatingAddress();

            if (content.equals("#*location*#")){
                abortBroadcast();

                GPSInfoProvider gpsInfoProvider = GPSInfoProvider.getInstance(context);
                String location = gpsInfoProvider.getLocation();
                System.out.println(location);

                if (!location.equals("")){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sender, null, location, null, null);
                }
                else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sender, null, "cannot get location", null, null);
                }

            }
            else if (content.equals("#*lockscreen*#")){
                DevicePolicyManager manager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                manager.lockNow();
                abortBroadcast();
            }
            else if (content.equals("#*alarm*#")){
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.star);
                mediaPlayer.start();
                abortBroadcast();
            }
        }
    }
}
