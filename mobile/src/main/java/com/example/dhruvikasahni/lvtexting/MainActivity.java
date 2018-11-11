package com.example.dhruvikasahni.lvtexting;

import android.Manifest;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.textmessageslibrary.TextMessageFetcher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS}, 1);
        }
        else {
            // do nothing - you already have permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS}, 1);
        }
        Button convo = (Button) findViewById(R.id.convo); //FOR CONVERSATION DEBUGGING (Abhiti will remove)
        convo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), Conversation.class));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission granted
                    final ListView lv = findViewById(R.id.SmsList);
                    TextMessageFetcher messageFetcher = new TextMessageFetcher(MainActivity.this);

                    if (messageFetcher.fetchRecentAddresses().isEmpty()) {
                        return;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1, messageFetcher.fetchRecentAddresses());
                    lv.setAdapter(adapter);


                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String line = lv.getItemAtPosition(i).toString();
                            int space = 0;
                            String phoneNumber = "";
                            for (int c=0;c<line.length();c++){
                                if (line.charAt(c) == ' ' ||line.charAt(c)=='\t'){space++;}
                                if (space == 2){break;}
                                if (space ==1 && line.charAt(c)!=' '){phoneNumber = phoneNumber + line.charAt(c);}
                            }
                            Intent intent = new Intent(MainActivity.this, Conversation.class);
                            intent.putExtra("phoneNumber",phoneNumber);//lv.getItemAtPosition(i).toString());//
                            startActivity(intent);
                        }
                    });

                } else {
                    // no permission granted
                }
            }
        }
    }
}
