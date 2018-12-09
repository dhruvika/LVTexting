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
        SettingsManager.applySettingsToTheme(this);

        setContentView(R.layout.activity_conversation);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    1);
        } else { //Permission already given

            sendSms();
            setGrid();


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SettingsManager.shouldApplyToConvo())
            this.recreate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (permissions[0].equalsIgnoreCase
                        (Manifest.permission.SEND_SMS)
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    // Mark all unread messages from this conversation as read
                    Bundle bundle = getIntent().getExtras();
                    String phoneNumber = bundle.getString("phoneNumber");
                    Uri uri = Uri.parse("content://sms/inbox");
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    try{
                        while (cursor.moveToNext()) {
                            String addressSeen = cursor.getString(cursor.getColumnIndex("address"));
                            if (addressSeen.equals(phoneNumber) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                                String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                                ContentValues values = new ContentValues();
                                values.put("read", true);
                                getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                            }
                        }
                    }catch(Exception e) {}

                    sendSms();
//                    printMessage();
                    setGrid();
                } else {
                    // Permission denied.
                }
            }
        }

    }

//    public void discreteScroll(MessageGridAdapter adapter){
//
//
//    }

    public String parseNumber(){ //@Yasmin, here is the helper fxn to obtain the number 
        Bundle bundle = getIntent().getExtras();
        String noStr = bundle.getString("phoneNumber");
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

    public void setGrid(){

        final GridView messages = findViewById(R.id.messages);


        List<String> messagesList = new ArrayList<String>();
        int previous = 1; //0:other, 1:user

        //works for all American numbers, AND justifies users

        Bundle bundle = getIntent().getExtras();

        final Uri SMS_INBOX = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, null,null, "date asc");
        cursor.moveToFirst();
        TextView tv = findViewById(R.id.textView2);
        while(cursor.moveToNext()) {
            String no = parseNumber();

            try{
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
                    messagesList.add(cursor.getString(cursor.getColumnIndex("body"))+'\n');
                    tv.setText(cursor.getString(cursor.getColumnIndex("body"))+'\n');
                }
            } catch (Exception e){
                messagesList.add("ERROR!");
            }
        }
        messagesList.add("\n\n\n\n\n\n\n\n"); //to make up for scrolling padding (make sure last message is displayed)
        String [] messagesArray = messagesList.toArray(new String[0]);

        cursor.close();


        final MessageGridAdapter  ad = new MessageGridAdapter(this, messagesArray);
        messages.setAdapter(ad);

//        messages.smoothScrollToPosition(); // shifted things slightly down
//        messages.smoothScrollByOffset(150);// doesnt do anything
//        messages.scrollTo(0,1000);//upon click, moves to top; but does scroll (all below y val goes white
//        messages.setTranscriptMode(1);
//        int cellCount = ad.getCount();
//        if (cellCount%2==0){
//            messages.setSelection(ad.getCount()/2);
//        } else{
//            messages.setSelection((int)(ad.getCount()/2+0.5));
//        }
        messages.setSelection(ad.getCount());
        tv.setText(Integer.toString(ad.getCount())+' '+messages.getCount()+' '+messages.getItemAtPosition(messages.getVerticalScrollbarPosition())+' '+messages.getBottom()+' '+messages.getFirstVisiblePosition());



        Button upButton = findViewById(R.id.upButton);
        Button downButton = findViewById(R.id.downButton);

        upButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                messages.setSelection(messages.getFirstVisiblePosition()-5);
            }
        });


        downButton.setOnClickListener(new View.OnClickListener(){
            int currentLine = messages.getFirstVisiblePosition();//ad.getCount();
            @Override
            public void onClick(View v){
                messages.setSelection(messages.getFirstVisiblePosition()+5);
            }
        });

//        upButton.setOnClickListener(new View.OnClickListener(){
//            int currentLine = ad.getCount();
//            @Override
//            public void onClick(View v){
////                messages.setSelection(ad.getCount()/2);
//                messages.setSelection(currentLine-5);
//                currentLine = currentLine -5;
//
//            }
//        });
//
//
//        downButton.setOnClickListener(new View.OnClickListener(){
//            int currentLine = messages.getFirstVisiblePosition();//ad.getCount();
//            @Override
//            public void onClick(View v){
////                messages.setSelection(ad.getCount()+10);
//                messages.setSelection(currentLine+10);
//                currentLine = currentLine+10;
//
//            }
//        });

    }


    public void sendSms(){
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String smsNumber = parseNumber();
                EditText smsEditText = (EditText) findViewById(R.id.smsInput);
                if (!smsEditText.getText().toString().equals("")){
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

                    setGrid(); //TODO: display messages (including the new one!)
                    smsEditText.setText("");

                }

            }
        });
    }







}
