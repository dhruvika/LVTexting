package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TableLayout;
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
            handleNewMessages();

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

    public void handleNewMessages(){
        // Register a new broadcast receiver
        BroadcastReceiver smsReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String currentChatNumber = parseNumber("");

                SmsMessage[] msgs;
                Bundle bundle = intent.getExtras();
                String msg_from;
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    msg_from = msgs[i].getOriginatingAddress();
                    if(currentChatNumber.equals(msg_from)){
                        setGrid();
                    }
                }

//                setGrid();
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceived, intentFilter);
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
        final Activity activityContext = this;
        String no = parseNumber("");
        if (!no.equals("")){
            final GridView messages = findViewById(R.id.messages);

            List<String> messagesList = new ArrayList<String>();
            List<String> messagesDtList = new ArrayList<String>();
            List<String> messagesClickedList = new ArrayList<String>();
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
                                messagesDtList.add("");
                                messagesClickedList.add("");
                            } else{
                                messagesList.add("");
                                messagesList.add("");
                                messagesDtList.add("");
                                messagesDtList.add("");
                                messagesClickedList.add("");
                                messagesClickedList.add("");
                            }
                            previous = 1;
                        } else { //other-sent message
                            if(previous == 0){//previous message also sent by other
                                messagesList.add("");
                                messagesDtList.add("");
                                messagesClickedList.add("");

                            }
                            previous = 0;
                        }
                        messagesList.add(cursor.getString(cursor.getColumnIndex("body"))+'\n');
                        messagesDtList.add(cursor.getString(cursor.getColumnIndex("date"))+'\n');
                        messagesClickedList.add("0");
//                        tv.setText(cursor.getString(cursor.getColumnIndex("body"))+'\n');
                    }
                } catch (Exception e){
                    messagesList.add("ERROR!");
                    messagesDtList.add("ERROR!");
                }
            }
            messagesList.add("\n\n\n\n\n\n\n\n"); //to make up for scrolling padding (make sure last message is displayed)
            messagesDtList.add("\n\n\n\n\n\n\n\n");
            final String [] messagesArray = messagesList.toArray(new String[0]);
            final String [] pristinemessagesArray = messagesList.toArray(new String[0]);
            final String [] messagesDtArray = messagesDtList.toArray(new String[0]);
            final String [] messagesClickedArray = messagesClickedList.toArray(new String[0]);

            cursor.close();

            final MessageGridAdapter  ad = new MessageGridAdapter(activityContext, messagesArray);
            messages.setAdapter(ad);

            messages.setSelection(ad.getCount());

            Button upButton = findViewById(R.id.upButton);
            Button downButton = findViewById(R.id.downButton);

            final int shiftAmount = 5;

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
//                                readAloud.setBackgroundResource(R.drawable.stop);
                                String speak = "";
                                int previous = -1;
                                for(int i = messages.getFirstVisiblePosition(); i <= messages.getLastVisiblePosition(); i++) {
                                    /*
                                    View view = messages.getChildAt(i);
                                    if (view != null) {
                                    */
                                    if (!messagesArray[i].equals("")) {
                                        /*
                                        TextView textView = (TextView) view;
                                        String message = textView.getText().toString();
                                        */
                                        String message = messagesArray[i];
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
//                                readAloud.setBackgroundResource(R.drawable.play);
                            } else{
                                t1.speak("", TextToSpeech.QUEUE_FLUSH, null);
                                t1.stop();
                            }
                        }
                    }).start();
                }
            });

            messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int pos = messages.getFirstVisiblePosition();
                    if (messagesClickedArray[i].equals("0")){
                        messagesClickedArray[i] = "1";

                        String msgMillis = messagesDtArray[i];
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(msgMillis.substring(0,msgMillis.length()-1)));

                        String mMonth = Integer.toString(calendar.get(Calendar.MONTH));
                        if(mMonth.equals("1")){
                            mMonth = "Jan";
                        }else if (mMonth.equals("2")){
                            mMonth = "Feb";
                        }else if (mMonth.equals("3")){
                            mMonth = "Mar";
                        }else if (mMonth.equals("4")){
                            mMonth = "Apr";
                        }else if (mMonth.equals("5")){
                            mMonth = "May";
                        }else if (mMonth.equals("6")){
                            mMonth = "Jun";
                        }else if (mMonth.equals("7")){
                            mMonth = "Jul";
                        }else if (mMonth.equals("8")){
                            mMonth = "Aug";
                        }else if (mMonth.equals("9")){
                            mMonth = "Sep";
                        }else if (mMonth.equals("10")){
                            mMonth = "Oct";
                        }else if (mMonth.equals("11")){
                            mMonth = "Nov";
                        }else if (mMonth.equals("12")){
                            mMonth = "Dec";
                        } else{
                            mMonth = "";
                        }

                        String mDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
                        if(mDay.length()==1){
                            mDay = '0'+mDay;
                        }
                        String mhour = Integer.toString(calendar.get(Calendar.HOUR));
                        if(mhour.length()==1){
                            mhour = '0'+mhour;
                        }
                        String mmin = Integer.toString(calendar.get(Calendar.MINUTE));
                        if(mmin.length()==1){
                            mmin = '0'+mmin;
                        }
                        String mampm = Integer.toString(calendar.get(Calendar.AM_PM));
                        if(mampm.equals("0")){
                            mampm = "AM";
                        } else{
                            mampm = "PM";
                        }

                        String time = mhour+':'+mmin+' '+mampm + ' ' + mDay+' '+mMonth;

                        messagesArray[i] = pristinemessagesArray[i]+time+'\n';//messagesDtArray[i];//"HERE!";
                    } else if (messagesClickedArray[i].equals("1")){
                        messagesClickedArray[i] = "0";
                        messagesArray[i] = pristinemessagesArray[i];
                    }

                    final MessageGridAdapter ad = new MessageGridAdapter(activityContext, messagesArray);
                    messages.setAdapter(ad);
                    messages.setSelection(pos);//i
//                    EditText smsInput = findViewById(R.id.smsInput);
//                    smsInput.setText(Integer.toString(messagesDtArray[i]));

                }
            });

            messages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    String clipboardContent = pristinemessagesArray[i];
                    if(clipboardContent.charAt(clipboardContent.length()-1)=='\n'){
                        clipboardContent = clipboardContent.substring(0,clipboardContent.length()-1);
                    }
                    ClipData clip = ClipData.newPlainText("simple text",clipboardContent);
                    clipboard.setPrimaryClip(clip);
                    return false; //need to return a boolean for some reason
                }
            });
        }



    }

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
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Boolean nameNotNumber = false;
                String smsNumber = parseNumber("");
                EditText smsEditText = findViewById(R.id.smsInput);
                EditText contactHeader = findViewById(R.id.contactHeader);

                if (smsNumber.equals("")){ //new message
                    for(int c =0;  c<contactHeader.getText().toString().length();c++){
                        if (!Character.isDigit(contactHeader.getText().toString().charAt(c))){
                            nameNotNumber = true; //contact name instead of number has been inputted
                        }
                    }
                }

                if(nameNotNumber){ //need to lookup corresponding number for contact
                    smsNumber = contactLookup(contactHeader.getText().toString());//getApplicationContext()
                    smsNumber = parseNumber(smsNumber); //parse the contact number
                }

                Boolean newMessage = false; //so will be able to open up conversation containing this message, if new (or previous messages, if old)
                if (smsNumber.equals("")){ //new message, where inputted contact number
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
                    Long formattedDate = date.getTime();

                    values.put("date", formattedDate);
                    getContentResolver().insert(Uri.parse("content://sms/sent"), values);

                    setGrid(); //display messages (including the new one!) [make sure SMS is set to default text app]
                    smsEditText.setText("");

                    if (newMessage||nameNotNumber){ //if number inputted/contact name inputted for a new message composed
                        Intent intent = new Intent(Conversation.this, Conversation.class);
                        intent.putExtra("phoneNumber", smsNumber);
                        startActivity(intent);
                    }

                }

            }
        });
    }
}
