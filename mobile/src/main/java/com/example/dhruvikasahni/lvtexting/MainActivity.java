package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech t1;
    Button readAloud;
    Button goTo;
    List<List<String>> messageList = new ArrayList<>();
    List<String> currentMessage;
    Boolean restart = false;
    Boolean paused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS};


        if(!hasAllPermissions(MainActivity.this, permissions)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions, 1);
        }
        else {
            // do nothing - you already have permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions, 1);
        }

        Button convo = findViewById(R.id.convo); //FOR CONVERSATION DEBUGGING (Abhiti will remove)
        convo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), Conversation.class));
            }
        });

        setFontFromSettings();

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
                            if (messageFetcher.fetchRecentConversations().isEmpty()) {
                                t1.speak("No new messages", TextToSpeech.QUEUE_FLUSH, null);
                                return;
                            }
                            for (ArrayList<String> conversationInfo : messageFetcher.fetchRecentConversations()) {
                                if (conversationInfo.get(0).equals("read")) {
                                    messageList.add(conversationInfo);
                                }
                            }
                            while (messageList.size() != 0 && !restart) {
                                currentMessage = messageList.remove(0);
                                String speak = "";
                                String contactName = messageFetcher.getContactName(currentMessage.get(1));
                                if(contactName != null){
                                    speak += contactName;
                                }
                                else{
                                    speak += currentMessage.get(1);
                                }
                                speak += currentMessage.get(2);
                                t1.speak(speak, TextToSpeech.QUEUE_ADD, null);
                                while (t1.isSpeaking()) {};
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
        setFontFromSettings();
    }

    public boolean hasAllPermissions(Context context, String[] permissions){
        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1: {
                // permission granted
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TableLayout dashboard = findViewById(R.id.Dashboard);
                    loadSMSData(dashboard);
                }

                else {
                    // no permission granted
                }
            }
        }
    }

    public void launchSettingsActivity(View v) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void loadSMSData(TableLayout dashboard){

        TextMessageFetcher messageFetcher = new TextMessageFetcher(MainActivity.this);

        if (messageFetcher.fetchRecentConversations().isEmpty()) {
            return;
        }

        for (ArrayList<String> conversationInfo : messageFetcher.fetchRecentConversations()){
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            // Create the required fields
            TextView readText = new TextView(this);
            if(conversationInfo.get(0).equals("unread")){
                readText.setText("");
            }
            else{
                readText.setText("\u25CF  ");
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

            // Add the row to the dashboard
            dashboard.addView(row);
        }
    }


    public void setFontFromSettings() {
        SettingsManager.onFontChange(this, (ViewGroup) findViewById(R.id.Dashboard_Container));
    }
}
