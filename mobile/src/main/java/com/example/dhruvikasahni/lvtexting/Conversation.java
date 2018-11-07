package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
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
        }



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
                String smsNumber = String.format("19175656215"); //hardcoded Abhiti's number for now
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


}
