package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.textmessageslibrary.TextMessageFetcher;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.GET_TASKS};


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
}
