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
import android.view.ViewGroup;
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


        setFontFromSettings();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setFontFromSettings();
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
                Bundle bundle = getIntent().getExtras();
                String noStr = bundle.getString("phoneNumber");
                String no = "";
                for (int c=0; c<noStr.length();c++){
                    if (noStr.charAt(c)!='+'&&noStr.charAt(c)!='-'&&noStr.charAt(c)!=' '&&noStr.charAt(c)!='('&&noStr.charAt(c)!=')'){
                        no = no + noStr.charAt(c);
                    }
                }
                String smsNumber = no; //String.format("19175656215"); //hardcoded Abhiti's number for now
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

    public void printMessage(){ //has both sent and received texts in one place (in correct chron order)
        //works for all american numbers, AND justifies users

        Bundle bundle = getIntent().getExtras();

        TextView msg = findViewById(R.id.textView);
        String msgContent = "";
        final Uri SMS_INBOX = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date asc");
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            String noStr = bundle.getString("phoneNumber");//"3125324190";
            String no = "";
            for (int c=0; c<noStr.length();c++){
                if (noStr.charAt(c)!='+'&&noStr.charAt(c)!='-'&&noStr.charAt(c)!=' '&&noStr.charAt(c)!='('&&noStr.charAt(c)!=')'){
                    no = no + noStr.charAt(c);
                }
            }
//            TextView textView2 = findViewById(R.id.textView2);
//            textView2.setText(no+"   "+Integer.toString(no.length()));
            try{
                if (cursor.getString(cursor.getColumnIndex("address")).equals(no)||cursor.getString(cursor.getColumnIndex("address")).equals("+1"+no)||cursor.getString(cursor.getColumnIndex("address")).equals("1"+no)||cursor.getString(cursor.getColumnIndex("address")).equals(no.substring(1))){
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








    public void setFontFromSettings() {
        SettingsManager.onFontChange(this, (ViewGroup) findViewById(R.id.Conversation_Container));
    }
}
