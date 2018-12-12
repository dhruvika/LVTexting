package com.example.dhruvikasahni.lvtexting;

//import android.app.Activity;
import android.app.Activity;
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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.widget.TableLayout;

import com.example.textmessageslibrary.TextMessageFetcher;

import java.util.List;
import java.util.Random;

public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_URI = "content://sms";

    public String parseNumber(String noStr){ //@Yasmin, here is the helper fxn to obtain the number

        String no = "";
        for (int c=0; c<noStr.length();c++){
            if (Character.isDigit(noStr.charAt(c))){
                no = no + noStr.charAt(c);
            }

        }
        if (no.length()>10&&no.length()!=12){//length 10 for standard US numbers, length 12 for automated numbers
            no = no.substring(1);
        }

        return no;
    }

    public String contactLookup (String name, Context context){
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,from,null,null,null);
        name = parseNumber(name);
        String s = "";
        if(cursor!=null){
            while(cursor.moveToNext()){
                if (parseNumber(cursor.getString(1)).equals(name)){
                    s = cursor.getString(0);
                }
            }
            cursor.close();
        }
        return  s;

    }



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
            notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            Random rand = new Random();

            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                msg_from = msgs[i].getOriginatingAddress();
                String msgBody = msgs[i].getMessageBody();

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);

                // Contact name to be displayed in notification
                String contact = parseNumber(msg_from);

                if(!contactLookup(contact,context).equals("")){
                    contact = contactLookup(contact,context);//getContactNumber(msg_from, context);;
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        // CHANGE ICON
                        .setSmallIcon(R.drawable.sms_logo)//.setSmallIcon(R.drawable.play)
                        .setContentTitle(contact)
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

    /**
     * Return the phone number of the contact if it exists in the phonebook.
     * @param contactName contact name assosciated with a phone number
     * @return String phone numeber if one exists in the contact list, null otherwise.
     */
    private String getContactNumber(String contactName, Context context) {
        String number = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + contactName +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            number = c.getString(0);
        }
        c.close();
        return number;
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
