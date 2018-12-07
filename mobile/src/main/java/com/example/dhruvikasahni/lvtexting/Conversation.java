package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_conversation);
            sendSms();
//            printMessage();
            setGrid();

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
//                    printMessage();
                    setGrid();
                } else {
                    // Permission denied.
                }
            }
        }

    }

    public void setGrid(){

        GridView messages = findViewById(R.id.messages);


        List<String> messagesList = new ArrayList<String>();
        int previous = 1; //0:other, 1:user

        //works for all american numbers, AND justifies users

        Bundle bundle = getIntent().getExtras();

//        TextView msg = findViewById(R.id.textView);
        final Uri SMS_INBOX = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date asc");
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            String noStr = bundle.getString("phoneNumber");
            String no = "";
            for (int c=0; c<noStr.length();c++){
                if (Character.isDigit(noStr.charAt(c))){//(noStr.charAt(c)!='+'&&noStr.charAt(c)!='-'&&noStr.charAt(c)!=' '&&noStr.charAt(c)!='('&&noStr.charAt(c)!=')'){
                    no = no + noStr.charAt(c);
                }
                if (no.length()>10){//TODO: fix longer automated number parsing
                    no = no.substring(1);
                }
            }
//            TextView textView2 = findViewById(R.id.textView2);
//            String allnos = "";
//            textView2.setText(no+"   "+Integer.toString(no.length()));
            try{
//                allnos = allnos + no + '\n';
                if (cursor.getString(cursor.getColumnIndex("address")).equals(no)||cursor.getString(cursor.getColumnIndex("address")).equals("+1"+no)||cursor.getString(cursor.getColumnIndex("address")).equals("1"+no)||cursor.getString(cursor.getColumnIndex("address")).equals(no.substring(1))){
                    if(cursor.getString(cursor.getColumnIndex("type")).equals("2")){ //user-sent message
                        if(previous==1){//previous message also sent by me
                            messagesList.add("");
                        } else{
                            messagesList.add("");
                            messagesList.add("");
                        }
                        previous = 1;
                    } else { //other-sent message
                        if(previous == 0){//previous message also sent by other
                            messagesList.add("");
                        }
                        previous = 0;
                    }
//                    messagesList.add(Boolean.toString(cursor.getString(cursor.getColumnIndex("type")).equals("1")));
                    messagesList.add(cursor.getString(cursor.getColumnIndex("body"))+'\n');
                }
            } catch (Exception e){
                messagesList.add("ERROR!");
            }
//            textView2.setText(allnos);
        }
        String [] messagesArray = messagesList.toArray(new String[0]);
//        msg.setText(msgContent);

        cursor.close();

        messages.setAdapter(new MessageGridAdapter(this, messagesArray));

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
                String smsNumber = no;
                EditText smsEditText = (EditText) findViewById(R.id.smsInput);
                String sms = smsEditText.getText().toString();
                String scAddress = null;
                PendingIntent sentIntent = null, deliveryIntent = null;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage
                        (smsNumber, scAddress, sms,
                                sentIntent, deliveryIntent);

                // Put sent SMS in db : Code added by dhruvika - feel free to change!
                ContentValues values = new ContentValues();
                values.put("address", smsNumber);
                values.put("body", sms);
                values.put("read", 1);

                // Get current date time
                Date date = new Date();
                String strDateFormat = "hh:mm aaa dd MMM";
                DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                String formattedDate = dateFormat. format(date);

                values.put("date", formattedDate);
                getContentResolver().insert(Uri.parse("content://sms/sent"), values);

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
            String noStr = bundle.getString("phoneNumber");
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
                }
            } catch (Exception e){
                msgContent = "Error loading messages";
            }
        }
        msg.setText(msgContent);
        cursor.close();
    }








    public void setFontFromSettings() {
        SettingsManager.onFontChange(this, (ViewGroup) findViewById(R.id.Conversation_Container));
    }
}
