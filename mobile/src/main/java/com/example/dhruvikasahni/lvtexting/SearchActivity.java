package com.example.dhruvikasahni.lvtexting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.textmessageslibrary.TextMessageFetcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<List<String>> currentMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsManager.applySettingsToTheme(this);

        setContentView(R.layout.activity_search);
    }

    public List<List<String>> queryMessages(String input) {
        TextMessageFetcher messageFetcher = new TextMessageFetcher(this);
        ArrayList<ArrayList<String>> readConversations = messageFetcher.fetchReadConversations();
        ArrayList<ArrayList<String>> unreadConversations = messageFetcher.fetchUnreadConversations();

        List<List<String>> messageList = new ArrayList<>();

        for (ArrayList<String> conversationInfo : unreadConversations) {
            if(conversationInfo.get(3).contains(input))
                messageList.add(conversationInfo);
        }

        for (ArrayList<String> conversationInfo : readConversations) {
            if(conversationInfo.get(3).contains(input))
                messageList.add(conversationInfo);
        }

        return messageList;
    }

    public void onSearch(View v) {
        String input = ((EditText)findViewById(R.id.searchInput)).getText().toString();
        currentMessages = queryMessages(input);
        updateDashboard();
    }


    // Search dashboard table functions
    public void updateDashboard(){

        TableLayout dashboard = this.findViewById(R.id.search_dashboard);
        TextMessageFetcher messageFetcher = new TextMessageFetcher(this);
        dashboard.removeAllViews();

        HashSet<String> addresses = new HashSet<>();
        for (List<String> conversationInfo : currentMessages){
            TableRow row = addDashboardRow(conversationInfo, messageFetcher);
            addresses.add(conversationInfo.get(1));
            dashboard.addView(row);
        }
    }

    public TableRow addDashboardRow(List<String> conversationInfo, TextMessageFetcher messageFetcher){
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

        // Add formatting to row
        SettingsManager.applyThemeToView(this, row);

        // set row id
        row.setId(conversationInfo.get(1).hashCode());

        // Add a listener for clicks
        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextMessageFetcher messageFetcher2 = new TextMessageFetcher(SearchActivity.this);
                TableRow tableRow = (TableRow) v;
                TextView phoneNumText = (TextView) tableRow.getChildAt(1);
                String address = phoneNumText.getText().toString();
                Intent intent = new Intent(SearchActivity.this, Conversation.class);
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
                v.setBackgroundColor(Color.WHITE);
                v.showContextMenu();
                return true;
            }
        });

        //registerForContextMenu(row);
        return row;
    }
}