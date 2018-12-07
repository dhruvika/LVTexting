package com.example.dhruvikasahni.lvtexting;

//import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TableLayout;

import java.util.List;

public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_URI = "content://sms";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get SMS map from Intent
        Bundle extras = intent.getExtras();

        if ( extras != null ) {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get("pdus");
            ContentResolver contentResolver = context.getContentResolver();

            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
                putSmsToDatabase(contentResolver, sms);

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
