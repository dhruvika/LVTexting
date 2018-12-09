package com.example.dhruvikasahni.lvtexting;

//import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.widget.TableLayout;

import java.util.List;
import java.util.Random;

public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_URI = "content://sms";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get SMS map from Intent
        Bundle extras = intent.getExtras();
        SmsMessage[] msgs = null;

        if (extras != null) {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get("pdus");
            ContentResolver contentResolver = context.getContentResolver();
            Bundle bundle = intent.getExtras();
            String msg_from;

            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                putSmsToDatabase(contentResolver, sms);
            }

            // REMOVE if someone else takes notifications
            String CHANNEL_ID = "message_notifs";

            // Create an explicit intent for an Activity in your app
            Intent notifIntent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            Random rand = new Random();

            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                msg_from = msgs[i].getOriginatingAddress();
                String msgBody = msgs[i].getMessageBody();
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        // CHANGE ICON
                        .setSmallIcon(R.drawable.play)
                        .setContentTitle(msg_from)
                        .setContentText(msgBody)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);


                // notificationId is a unique int for each notification that you must define
                int notificationId = rand.nextInt();
                notificationManager.notify(notificationId, mBuilder.build());

            }
        }


    }


    private void putSmsToDatabase(ContentResolver contentResolver, SmsMessage sms )
    {
        // Create SMS row
        ContentValues values = new ContentValues();
        values.put("address", sms.getOriginatingAddress());
        values.put("date", sms.getTimestampMillis());
        values.put("read", 0);
        values.put("status", sms.getStatus());
        values.put("type", 1); //inbox = 1
        values.put("seen", 0);
        try
        {
            values.put("body", sms.getMessageBody());
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        // Push row into the SMS table
        contentResolver.insert(Uri.parse(SMS_URI), values);
    }
}
