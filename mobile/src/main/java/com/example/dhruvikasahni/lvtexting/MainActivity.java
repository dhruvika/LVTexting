package com.example.dhruvikasahni.lvtexting;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = findViewById(R.id.SmsList);

        if(fetchInbox() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fetchInbox());
            lv.setAdapter(adapter);
        }
    }
    public ArrayList<String> fetchInbox(){
        ArrayList<String> sms = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, new String[]{"id", "address", "date", "body"}, null, null, null);

        cursor.moveToFirst();
        while(cursor.moveToNext()){
            String address = cursor.getString(1);
            String body = cursor.getString(3);
            sms.add("Name: " + address + "\n Message: "+body);
        }
        return sms;
    }
}
