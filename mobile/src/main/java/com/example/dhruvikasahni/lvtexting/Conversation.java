package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Conversation extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    1);
        } else { //Permission already given
            sendSms();
            printMessage();

        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (permissions[0].equalsIgnoreCase
                        (Manifest.permission.SEND_SMS)
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    sendSms();
                } else {
                    // Permission denied.
                }
            }
        }

    }

    public void sendSms(){
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String smsNumber = String.format("19175656215"); //hardcoded Abhiti's number for now
                EditText smsEditText = (EditText) findViewById(R.id.smsInput);
                String sms = smsEditText.getText().toString();
                String scAddress = null;
                PendingIntent sentIntent = null, deliveryIntent = null;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage
                        (smsNumber, scAddress, sms,
                                sentIntent, deliveryIntent);
            }
        });
    }

//    public void printMessage(){ //only shows received for no bloody reason
//        TextView msg = findViewById(R.id.textView);
//        String msgContent = "";
//        final Uri SMS_INBOX = Uri.parse("content://sms");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, "address = ?",
//                new String[] {"9172388614"}, "date desc limit 20");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) {
//            try{
//                msgContent = msgContent +cursor.getString(cursor.getColumnIndex("body"))+ '\n';
//            } catch (Exception e ){
//
//            }
//        }
//        msg.setText(msgContent);
//        cursor.close();
//    }

//    public void printMessage(){ //has both sent and received texts in one place (in correct chron order)
//        TextView msg = findViewById(R.id.textView);
//        String msgContent = "";
//        final Uri SMS_INBOX = Uri.parse("content://sms");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date desc");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) {
//            try{
////                if (cursor.getString(cursor.getColumnIndex("address")).equals("9172388614")){
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("body"))+ '\n';
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("address"))+ '\n';
////                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("_id"))+ '\n'; // increasing #
////                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("thread_id"))+ '\n'; //same #
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("person"))+ '\n'; // if null, me; else contact
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("type"))+ '\n'; //1 is them, 2 is me
//
////                }
//            } catch (Exception e ){
//
//            }
//        }
//        msg.setText(msgContent);
//        cursor.close();
//    }

    public void printMessage(){ //has both sent and received texts in one place (in correct chron order)
        //works for all american numbers, AND justifies users
        TextView msg = findViewById(R.id.textView);
        String msgContent = "";
        final Uri SMS_INBOX = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date asc");
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            String no = "3125324190";
            try{
                if (cursor.getString(cursor.getColumnIndex("address")).equals(no)||cursor.getString(cursor.getColumnIndex("address")).equals("+1"+no)){
                    if(cursor.getString(cursor.getColumnIndex("person")) == null){ //user-sent message
                        msgContent = msgContent+"                                        ";
                    }
                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("body"))+ '\n';
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("person"))+ '\n'; // if null, me; else contact

                }
            } catch (Exception e ){

            }
        }
        msg.setText(msgContent);
        cursor.close();
    }

//    public void printMessage(){ //has both sent and received texts in one place (in correct chron order)
//        //works for all american numbers, need to justify users
//        TextView msg = findViewById(R.id.textView);
//        String msgContent = "";
//        final Uri SMS_INBOX = Uri.parse("content://sms");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date asc");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) {
//            String no = "3125324190";
//            try{
//                if (cursor.getString(cursor.getColumnIndex("address")).equals(no)||cursor.getString(cursor.getColumnIndex("address")).equals("+1"+no)){
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("body"))+ '\n';
////                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("_id"))+ '\n'; // increasing #
////                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("thread_id"))+ '\n'; //same #
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("person"))+ '\n'; // if null, me; else contact
////                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("type"))+ '\n'; //1 is them, 2 is me
//
//                }
//            } catch (Exception e ){
//
//            }
//        }
//        msg.setText(msgContent);
//        cursor.close();
//    }


//    public void printMessage(){ // displays sent
//        TextView msg = findViewById(R.id.textView);
//        String msgContent = "";
//        final Uri SMS_INBOX = Uri.parse("content://sms/sent");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date desc limit 10");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) {
//            try{
//                if (cursor.getString(cursor.getColumnIndex("address")).equals("9172388614")){
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("body"))+ '\n';
//                }
//            } catch (Exception e ){
//
//            }
//
//        }
//        msg.setText(msgContent);
//        cursor.close();
//    }

//    public void printMessage(){ //displays received
//        TextView msg = findViewById(R.id.textView);
//        String msgContent = "";
//        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date desc limit 10");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) {
//            try{
//                if (cursor.getString(cursor.getColumnIndex("address")).equals("9172388614")){
//                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("body"))+ '\n';
//                }
//            } catch (Exception e ){
//
//            }
//
//        }
//        msg.setText(msgContent);
//        cursor.close();
//    }

//    public void printMessage(){ //poc
//        TextView msg = findViewById(R.id.textView);
//        String msgContent = "";
//        final Uri SMS_INBOX = Uri.parse("content://sms/");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date desc limit 10");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) { //change the desc limit to go as far back as you want, prints the one it stops on
//            // Do something
//            try{
//                msgContent = msgContent + cursor.getString(cursor.getColumnIndex("body")) + '\n';
//                msgContent = msgContent + cursor.getString(cursor.getColumnIndex("address")) + '\n';
//                msgContent = msgContent + cursor.getString(cursor.getColumnIndex("address")).equals("227898") + '\n';
////                if (!cursor.getString(cursor.getColumnIndex("address")).equals("227898")){
////                    msgContent = msgContent +cursor.getString(cursor.getColumnIndex("body"))+ '\n';
////                }
////                msg.setText(cursor.getString(cursor.getColumnIndex("address")));
//            } catch (Exception e ){
//
//            }
//
//        }
//        msg.setText(msgContent);
//        cursor.close();
//    }


//    public void printMessage(){ //not working discernably
//        TextView msg = findViewById(R.id.textView);
//        final Uri SMS_INBOX = Uri.parse("content://mms-sms/conversations/");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date desc limit 4");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) { // prints final of messages i sent
//            // Do something
//            try{
//                msg.setText(cursor.getString(cursor.getColumnIndex("body")));
//            } catch (Exception e ){
//
//            }
//        }
//        cursor.close();
//    }



//    public void printMessage(){
//        TextView msg = findViewById(R.id.textView);
//        final Uri SMS_INBOX = Uri.parse("content://sms/sent");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date desc limit 3");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) { // prints final of messages i sent
//            // Do something
//            try{
//                msg.setText(cursor.getString(cursor.getColumnIndex("body")));
//            } catch (Exception e ){
//
//            }
//        }
//        cursor.close();
//    }


//    public void printMessage(){
//        TextView msg = findViewById(R.id.textView);
//        final Uri SMS_INBOX = Uri.parse("content://sms/");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date desc limit 3");
//        cursor.moveToFirst();
//        while(cursor.moveToNext()) { //change the desc limit to go as far back as you want, prints the one it stops on
//            // Do something
//            try{
//                msg.setText(cursor.getString(cursor.getColumnIndex("body")));
//            } catch (Exception e ){
//
//            }
//        }
//        cursor.close();
//    }


//    public void printMessage(){
//        TextView msg = findViewById(R.id.textView);
//        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
//        Cursor cursor = getContentResolver().query(SMS_INBOX, null, "address = ?",
//                new String[] {"3125324190"}, "date desc limit 1");
//        if(cursor.moveToFirst()) {
//            // Do something
//            try{
//                msg.setText(cursor.getString(cursor.getColumnIndex("body")));
//            } catch (Exception e ){
//
//            }
//        }
//        cursor.close();
//    }




}
