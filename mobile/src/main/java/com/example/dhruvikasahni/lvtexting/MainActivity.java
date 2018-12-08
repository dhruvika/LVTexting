package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.textmessageslibrary.TextMessageFetcher;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextToSpeech t1;
    Button readAloud;
    Button goTo;
    List<List<String>> messageList = new ArrayList<>();
    List<String> currentMessage;
    Boolean restart = false;
    Boolean paused = false;

    private BroadcastReceiver smsReceived = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        applySettingsToTheme();
        
        setContentView(R.layout.activity_main);
        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS};


        if(!hasAllPermissions(MainActivity.this, permissions)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions, 1);
        }
        else {
            // do nothing - you already have permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions, 1);
        }


        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        readAloud = (Button)findViewById(R.id.readAloud);
        readAloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        if (! t1.isSpeaking()) {
                            readAloud.setBackgroundResource(R.drawable.stop);
                            TextMessageFetcher messageFetcher = new TextMessageFetcher(MainActivity.this);
                            if (messageFetcher.fetchReadConversations().isEmpty() && messageFetcher.fetchUnreadConversations().isEmpty()) {
                                t1.speak("No new messages", TextToSpeech.QUEUE_FLUSH, null);
                                return;
                            }
                            for (ArrayList<String> conversationInfo : messageFetcher.fetchUnreadConversations()) {
                                messageList.add(conversationInfo);
                            }
                            while (messageList.size() != 0 && !restart) {
                                currentMessage = messageList.remove(0);
                                String speak = "";
                                String contactName = messageFetcher.getContactName(currentMessage.get(1));
                                if(contactName != null){
                                    speak += contactName;
                                }
                                else{
                                    for (int i = 0; i < currentMessage.get(1).length(); i ++) {
                                        speak += currentMessage.get(1).charAt(i);
                                        speak += ",";
                                    }
                                }
                                SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm aaa dd MMM");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d yyyy, HH:mm aaa");
                                try {
                                    Date parsed = inputFormat.parse(currentMessage.get(2));
                                    speak += outputFormat.format(parsed);

                                } catch (Exception e) {

                                }
                                t1.speak(speak, TextToSpeech.QUEUE_ADD, null);
                                while (t1.isSpeaking()) {}
                            }
                            restart = false;
                            messageList.clear();
                            readAloud.setBackgroundResource(R.drawable.play);
                        } else {
                            restart = true;
                            t1.stop();
                            t1.speak("", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }).start();
            }
        });
        goTo = (Button)findViewById(R.id.goTo);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t1.isSpeaking()) {
                    TextMessageFetcher messageFetcher = new TextMessageFetcher(MainActivity.this);
                    t1.stop();
                    restart = true;
                    String address = currentMessage.get(1);
                    Intent intent = new Intent(MainActivity.this, Conversation.class);
                    String phoneNumber = messageFetcher.getContactNumber2(address);
                    if(phoneNumber != null){
                        address = phoneNumber;
                    }
                    intent.putExtra("phoneNumber", address);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        applySettingsToTheme();
    }

    public boolean hasAllPermissions(Context context, String[] permissions){
        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public void deleteAddress(String addressName){
        String phoneNumber = addressName;
        TextMessageFetcher messageFetcher = new TextMessageFetcher(this);

        if(messageFetcher.getContactNumber2(addressName) != null)
            phoneNumber = messageFetcher.getContactNumber2(addressName);

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor c = this.getContentResolver().query(
                uriSms,
                new String[] { "_id", "thread_id", "address", "person",
                        "date", "body" }, null, null, null);


        if (c != null && c.moveToFirst()) {
            do {
//                long id = c.getLong(0);
                String address = c.getString(2);
//                String body = c.getString(5);

                if(address.equals(phoneNumber)) {
//                    this.getContentResolver().delete(
//                            Uri.parse("content://sms/" + id), "date=?",
//                            new String[]{c.getString(4)});
                    int thread_id = c.getInt(1); //get the thread_id
                    this.getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id),null,null);
                    break;
                }

            } while (c.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1: {
                // permission granted
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    loadSMSData();
//                    deleteAddress("6505551355");
                    loadSMSData();

                    // Register a new broadcast receiver
                        BroadcastReceiver smsReceived = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                TableLayout dashboard = MainActivity.this.findViewById(R.id.Dashboard);

                                final long changeTime = 400L;
                                dashboard.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        clearDashboard();
                                        loadSMSData();
                                    }
                                }, changeTime);
                                }
                        };

                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                    registerReceiver(smsReceived, intentFilter);
                }

                else {
                    // no permission granted
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // If the broadcast receiver is not null then unregister it.
        // This action is better placed in activity onDestroy() method.
        if(this.smsReceived!=null) {
            unregisterReceiver(this.smsReceived);
        }
    }


    public void launchSettingsActivity(View v) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void clearDashboard(){
        TableLayout dashboard = MainActivity.this.findViewById(R.id.Dashboard);
        dashboard.removeAllViews();
    }

    public void loadSMSData(){
        TableLayout dashboard = MainActivity.this.findViewById(R.id.Dashboard);
        TextMessageFetcher messageFetcher = new TextMessageFetcher(MainActivity.this);
        HashSet<String> addresses = new HashSet<>();
        for (ArrayList<String> conversationInfo : messageFetcher.fetchUnreadConversations()){
            TableRow row = addDashboardRow(conversationInfo, messageFetcher);
            addresses.add(conversationInfo.get(1));
            dashboard.addView(row);
        }

        for (ArrayList<String> conversationInfo : messageFetcher.fetchReadConversations()){
            TableRow row = addDashboardRow(conversationInfo, messageFetcher);
            if (!addresses.contains(conversationInfo.get(1))) dashboard.addView(row);
        }
    }

    public TableRow addDashboardRow(ArrayList<String> conversationInfo, TextMessageFetcher messageFetcher){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        // Create the required fields
        TextView readText = new TextView(this);
        if(conversationInfo.get(0).equals("read")){
            readText.setText("");
        }
        else{
            readText.setText("\u2B24    ");
        }
        TextView addressText = new TextView(this);
        String contactName = messageFetcher.getContactName(conversationInfo.get(1));
        if(contactName != null){
            addressText.setText(contactName);
        }
        else{
            addressText.setText(conversationInfo.get(1));
        }
        TextView dateText = new TextView(this);
        dateText.setText(conversationInfo.get(2));

        // Add the fields to the row
        row.addView(readText);
        row.addView(addressText);
        row.addView(dateText);

        // Add a listener for clicks
        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextMessageFetcher messageFetcher2 = new TextMessageFetcher(MainActivity.this);
                TableRow tableRow = (TableRow) v;
                TextView phoneNumText = (TextView) tableRow.getChildAt(1);
                String address = phoneNumText.getText().toString();
                Intent intent = new Intent(MainActivity.this, Conversation.class);
                String phoneNumber = messageFetcher2.getContactNumber2(address);
                if(phoneNumber != null){
                    address = phoneNumber;
                }
                intent.putExtra("phoneNumber",address);
                startActivity(intent);
            }});
        return row;
    }

    public void applySettingsToTheme() {
        SettingsManager.applySettingsToTheme(this);
    }
}


