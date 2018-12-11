package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
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
    TextView clickedContact = null;

    private BroadcastReceiver smsReceived = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SettingsManager.applySettingsToTheme(this);
        
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
                        if (! t1.isSpeaking()) {
                            TextMessageFetcher messageFetcher = new TextMessageFetcher(MainActivity.this);
                            ArrayList<ArrayList<String>> readConversations = messageFetcher.fetchReadConversations();
                            ArrayList<ArrayList<String>> unreadConversations = messageFetcher.fetchUnreadConversations();
                            String speak = "";
                            readAloud.setBackgroundResource(R.drawable.stop);

                            if (readConversations.isEmpty() && unreadConversations.isEmpty()) {
                                t1.speak("No new messages", TextToSpeech.QUEUE_FLUSH, null);
                                return;
                            }
                            if (unreadConversations.isEmpty()) {
                                speak += "No unread. ";
                            } else {
                                speak += "Unread from: ";
                                for (ArrayList<String> conversationInfo : unreadConversations) {
                                    messageList.add(conversationInfo);
                                }
                            }
                            if (!readConversations.isEmpty()) {
                                for (ArrayList<String> conversationInfo : readConversations) {
                                    messageList.add(conversationInfo);
                                }
                            }
                            Boolean readingUnread = true;
                            int ii = 0;
                            while (ii < messageList.size() && !restart) {
                                currentMessage = messageList.get(ii);
                                if (readConversations.contains(currentMessage) && readingUnread) {
                                    speak += "Red from: ";
                                    readingUnread = false;
                                }
                                String contactName = messageFetcher.getContactName(currentMessage.get(1));
                                if (contactName != null) {
                                    speak += contactName;
                                } else {
                                    for (int i = 0; i < currentMessage.get(1).length(); i++) {
                                        speak += currentMessage.get(1).charAt(i);
                                        speak += ",";
                                    }
                                }
                                SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm aaa dd MMM");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, HH:mm aaa");
                                try {
                                    Date parsed = inputFormat.parse(currentMessage.get(2));
                                    speak += outputFormat.format(parsed);

                                } catch (Exception e) {
                                }
                                ii += 1;
                                t1.speak(speak, TextToSpeech.QUEUE_ADD, null);
                                while (t1.isSpeaking()) {}
                                speak = "";
                            }
                            restart = false;
                            messageList.clear();
                            readAloud.setBackgroundResource(R.drawable.play);
                        } else {
                            restart = true;
                            t1.speak("", TextToSpeech.QUEUE_FLUSH, null);
                            t1.stop();

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

        //Abhiti's added code for new message composition (feel free to change)
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Conversation.class);
                intent.putExtra("phoneNumber","");
                startActivity(intent);
            }});
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        if(SettingsManager.shouldApplyToMain())
            this.recreate();

        //Refresh your stuff here
        clearDashboard();
        loadSMSData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        clickedContact =  findViewById(v.getId());
        String contactName = clickedContact.getText().toString();

        menu.add(0, v.getId(), 0, "Delete " + contactName);

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

    public boolean hasAllPermissions(Context context, String[] permissions){
        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "message_notifs";
            String description = "channel for message notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("message_notifs", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void deleteAddress(String phoneNumber){
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor c = this.getContentResolver().query(
                uriSms,
                new String[] { "_id", "thread_id", "address", "person",
                        "date", "body" }, null, null, null);


        if (c != null && c.moveToFirst()) {
            do {
                String address = c.getString(2);

                if(address.equals(phoneNumber)) {
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
                    clearDashboard();
                    loadSMSData();
                    createNotificationChannel();

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

    public void launchSearchActivity(View v) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
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
            readText.setText("    ");
        }
        else{
            readText.setText("\u2B24  ");
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

        // Add formatting to row
        SettingsManager.applyThemeToView(this, row);

        // set row id
        addressText.setId(conversationInfo.get(1).hashCode());


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

        // Add context menu for deletion
        row.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                TableRow tableRow = (TableRow) v;
                final TextView addressText = (TextView) tableRow.getChildAt(1);

//                // register text view for context menu [on click unregister all context menus]
//                registerForContextMenu(addressText);
//                addressText.showContextMenu();
//                unregisterForContextMenu(addressText);

                // create popup menu
                PopupMenu popup=
                        new PopupMenu(v.getContext(),v);

                popup.inflate(R.menu.dashboard_menu);
                final Activity activity = (Activity) v.getContext();
                popup.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(
                                    MenuItem item) {
                                String contactName = addressText.getText().toString();
                                if (item.getTitle().equals("Delete")) {

                                    String contactNumber = contactName;
                                    TextMessageFetcher messageFetcher = new TextMessageFetcher(activity);

                                    if(messageFetcher.getContactNumber2(contactName) != null){
                                        contactNumber = messageFetcher.getContactNumber2(contactName);
                                    }
                                    deleteAddress(contactNumber);
                                    clearDashboard();
                                    loadSMSData();
                                }
                                else {
                                    return  false;
                                }
                                return true;
                            }
                        });

                popup.show();
                return true;
            }
        });

        return row;
    }
}


