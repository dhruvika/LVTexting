package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.textmessageslibrary.TextMessageFetcher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS}, 1);
        }
        else if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        else if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        else {
            // do nothing - you already have permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS}, 1);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1: {
                // permission granted
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    loadSMSData();
                else {
                    // no permission granted
                }
            }
        }
    }

    public void loadSMSData(){
        TableLayout dashboard = findViewById(R.id.Dashboard);
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
            String phoneNum = messageFetcher.getContactNumber(contactName);
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

            // Add the row to the dashboard
            dashboard.addView(row);
        }
    }

//    /** Called when user presses the New Message Button */
//    public void newMessage(View view) {
//        // Do something in response to button click
//        TableRow row = new TableRow(this);
//        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//        row.setLayoutParams(lp);
//
//        // Create the required fields
//        TextView readText = new TextView(this);
//        if(conversationInfo.get(0).equals("unread")){
//            readText.setText("");
//        }
//        else{
//            readText.setText("\u25CF  ");
//        }
//        TextView addressText = new TextView(this);
//        String contactName = messageFetcher.getContact(conversationInfo.get(1));
//        if(contactName != null){
//            addressText.setText(contactName);
//        }
//        else{
//            addressText.setText(conversationInfo.get(1));
//        }
//        TextView dateText = new TextView(this);
//        dateText.setText(conversationInfo.get(2));
//
//        // Add the fields to the row
//        row.addView(readText);
//        row.addView(addressText);
//        row.addView(dateText);
//
//        // Add the row to the dashboard
//        dashboard.addView(row);
//
//    }
}
