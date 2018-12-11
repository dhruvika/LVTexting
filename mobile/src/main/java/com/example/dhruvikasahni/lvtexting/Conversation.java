package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
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

import com.example.textmessageslibrary.TextMessageFetcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Conversation extends AppCompatActivity {
    Button call;
    TextToSpeech t1;
    Button readAloud;

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

            // Mark all unread messages from this conversation as read
            String phoneNumber = parseNumber("");
            Uri uri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                while (cursor.moveToNext()) {
                    if (sameNumber(cursor, phoneNumber) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                        String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("read", true);
                        getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                    }
                }
            }
            catch (Exception e) {}

            sendSms();
            setGrid();
            setHeader();

        }

        call = (Button)findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + parseNumber("");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SettingsManager.shouldApplyToConvo())
            this.recreate();
    }

    public boolean sameNumber(Cursor cursor, String no){
        return cursor.getString(cursor.getColumnIndex("address")).equals(no)||cursor.getString(cursor.getColumnIndex("address")).equals("+1"+no)||cursor.getString(cursor.getColumnIndex("address")).equals("1"+no)||cursor.getString(cursor.getColumnIndex("address")).equals(no.substring(1));
    }

    public String parseNumber(String noStr){ //@Yasmin, here is the helper fxn to obtain the number

        if (noStr.equals("")){
            Bundle bundle = getIntent().getExtras();
            noStr = bundle.getString("phoneNumber");
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (permissions[0].equalsIgnoreCase
                        (Manifest.permission.SEND_SMS)
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    // Mark all unread messages from this conversation as read
                    String phoneNumber = parseNumber("");
                    Uri uri = Uri.parse("content://sms/inbox");
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    try {
                        while (cursor.moveToNext()) {
                            if (sameNumber(cursor, phoneNumber) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                                String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                                ContentValues values = new ContentValues();
                                values.put("read", true);
                                getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                            }
                        }
                    }
                    catch (Exception e) {}

                    sendSms();
                    setGrid();
                } else {
                    // Permission denied.
                }
            }
        }

    }

    public void setHeader(){
        String no = parseNumber("");
        EditText contactHeader = findViewById(R.id.contactHeader);
        if (!no.equals("")){
//            View.VISIBLE;
//            TextView tv = findViewById(R.id.textView);
//            tv.setVisibility(View.INVISIBLE);
            contactHeader.setVisibility(View.INVISIBLE);
        }
    }



    public void setGrid(){
        String no = parseNumber("");
        if (!no.equals("")){
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
//                        tv.setText(cursor.getString(cursor.getColumnIndex("body"))+'\n');
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

            messages.setSelection(ad.getCount());

            Button upButton = findViewById(R.id.upButton);
            Button downButton = findViewById(R.id.downButton);

            final int shiftAmount = 5;


            // Add formatting to row
            SettingsManager.applyThemeToView(this, messages);

            // Add onClick listeners
            upButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    messages.setSelection(messages.getFirstVisiblePosition()-shiftAmount);
                }
            });


            downButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    messages.setSelection(messages.getFirstVisiblePosition()+shiftAmount+1);
                }
            });

            final float speechRate = SettingsManager.getSpeakerSpeed();
            t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);
                        t1.setSpeechRate(speechRate);
                    }
                }
            });

            readAloud = (Button)findViewById(R.id.readAloud);
            readAloud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        public void run() {
                            if (!t1.isSpeaking()) {
                                readAloud.setBackgroundResource(R.drawable.stop);
                                String speak = "";
                                int previous = messages.getFirstVisiblePosition() % 2;
                                if (previous == 0) {
                                    speak += "They said: ";
                                } else {
                                    speak += "You said: ";
                                }
                                for(int i = messages.getFirstVisiblePosition(); i <= messages.getLastVisiblePosition(); i++) {
                                    View view = messages.getChildAt(i);
                                    if (view != null) {
                                        TextView textView = (TextView) view;
                                        String message = textView.getText().toString();
                                        if (message!= "" && message!="\n\n\n\n\n\n\n\n") {
                                            int sent = i % 2;
                                            if (sent != previous) {
                                                if (sent == 0) {
                                                    speak += "They said: ";
                                                } else {
                                                    speak += "You said: ";
                                                }
                                                previous = sent;
                                            }
                                            speak += message;
                                        }
                                    }
                                }
                                t1.speak(speak, TextToSpeech.QUEUE_ADD, null);
                                while (t1.isSpeaking()) {}
                                readAloud.setBackgroundResource(R.drawable.play);
                            } else{
                                t1.speak("", TextToSpeech.QUEUE_FLUSH, null);
                                t1.stop();
                            }
                        }
                    }).start();
                }
            });

        }

    }

//    public String contactLookup (String name, Context context){
//        Uri uri=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        //        Uri uri=Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,Uri.encode(name));
//
//        String[] projection = new String[]{ContactsContract.PhoneLookup.NUMBER};
//
//        String number="";
//        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
//
//        if (cursor != null) {
//            if(cursor.moveToFirst()) {
//                number=cursor.getString(0);
//            }
//            cursor.close();
//        }
//
//        return number;
//    }

//    public String contactLookup (String name, Context context){
//        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(name));
//
//        String[] projection = new String[]{ContactsContract.Contacts.HAS_PHONE_NUMBER};
//
//        String contactName="";
//        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
//
//        if (cursor != null) {
//            if(cursor.moveToFirst()) {
//                contactName=cursor.getString(0);
//            }
//            cursor.close();
//        }
//
//        return contactName;
//    }


//    public String contactLookup (String name){
//        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
//        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,from,null,null,null);
////        startManagingCursor(cursor);
//
//        String s = "";
//        int counter = 0;
//        if(cursor!=null){
//            while(cursor.moveToNext()&&counter<2){
//                counter++;
//                s=s+cursor.getString(0)+' '+cursor.getString(1)+'\n';
//                for (int c =0; c<cursor.getString(0).length();c++){
//                    s = s+Boolean.toString(cursor.getString(0).charAt(c)=='R');
//                }
//                s = s+'\n';
//                s = s+ Boolean.toString(cursor.getString(0).equals("Richa"));
//                if (cursor.getString(0).equals("Caroline Ayala")){
//                    s = s+ cursor.getString(1);
//                }
////                if(cursor.getString(0).equals(name)){
////                    s = cursor.getString(1);
//////                    break;
////                }
//
//            }
//            cursor.close();
//        }
//        return  s;
//
//    }

        public String contactLookup (String name){
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,from,null,null,null);

        String s = "";
        if(cursor!=null){
            while(cursor.moveToNext()){
                if (cursor.getString(0).equals(name)){
                    s = cursor.getString(1);
                }
            }
            cursor.close();
        }
        return  s;

    }

    public void sendSms(){
        Button sendButton = findViewById(R.id.sendButton);
        final TextMessageFetcher messageFetcher = new TextMessageFetcher(this);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Boolean nameNotNumber = false;
                String smsNumber = parseNumber("");
                EditText smsEditText = (EditText) findViewById(R.id.smsInput);
                EditText contactHeader = (EditText) findViewById(R.id.contactHeader);

                if (smsNumber.equals("")){ //new message
                    for(int c =0;  c<contactHeader.getText().toString().length();c++){
                        if (!Character.isDigit(contactHeader.getText().toString().charAt(c))){
                            nameNotNumber = true; //contact name instead of number has been inputted
                        }
                    }
                }

//                smsEditText.setText(Boolean.toString(nameNotNumber));

                if(nameNotNumber){ //need to lookup corresponding number for contact
                    smsNumber = contactLookup(contactHeader.getText().toString());//getApplicationContext()
//                    smsNumber = messageFetcher.getContactNumber2(smsNumber);
//                    if (smsNumber == null){
//                        smsNumber = "";
//                    }
                    smsNumber = parseNumber(smsNumber); //parse the contact number

//                    smsEditText.setText(smsNumber);
                }

                Boolean newMessage = false; //so will be able to open up conversation containing this message, if new (or previous messages, if old)
                if (smsNumber.equals("")){
                    newMessage = true;
                    smsNumber = contactHeader.getText().toString();

                }
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

                    setGrid(); //display messages (including the new one!) [make sure SMS is set to default text app]
                    smsEditText.setText("");

                    if (newMessage){
                        Intent intent = new Intent(Conversation.this, Conversation.class);
                        intent.putExtra("phoneNumber", smsNumber);
                        startActivity(intent);
                    }

                }

            }
        });
    }
}
