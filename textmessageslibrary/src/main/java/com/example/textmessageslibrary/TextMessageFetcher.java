package com.example.textmessageslibrary;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TextMessageFetcher {
    private Activity activity;

    public TextMessageFetcher(Activity activity){
        this.activity = activity;
    }

    // Fetches the address, date and content of each individual message in reverse chronological order.
    public ArrayList<String> fetchInbox(){

        ArrayList<String> sms = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = this.activity.getContentResolver().query(uri, new String[]{"_id", "address", "date", "body"},
                null, null, "date DESC");

        try {
            cursor.moveToFirst();
            while(cursor.moveToNext()) {
                String address = cursor.getString(1);
                String body = cursor.getString(3);
                Long dateMillis = cursor.getLong(2);
                String date = getDate(dateMillis, "dd/MM/yyyy hh:mm:ss");

                sms.add("Name: " + address + "\n Date: " + date + "\n Message: " + body);
            }
            cursor.close();
        }
        catch(NullPointerException e){
            // do nothing
        }

        return sms;
    }

    // Fetches the names/addresses of the most recently contacted numbers and the date of most recent message.
    public ArrayList<String> fetchRecentAddresses(){

        ArrayList<String> sortedAddresses = new ArrayList<>();
        Set<String> addresses = new HashSet<>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = this.activity.getContentResolver().query(uri, new String[]{"_id", "address", "date", "body", "read"},
                null, null, "date ASC");

        try {
            cursor.moveToFirst();
            int totalCount = cursor.getCount();
            for (int i = 0; i < totalCount; i++) {
                String address = cursor.getString(1);
                Long dateMillis = cursor.getLong(2);
                String date = getDate(dateMillis, "dd/MM/yyyy hh:mm:ss");
                int read = cursor.getInt(4);

                if(!addresses.contains(address)){
                    addresses.add(address);
                    if(read == 1){
                        sortedAddresses.add("X " + address + "\t\t\t\t" + date);
                    }
                    else {
                        sortedAddresses.add(address + "\t\t\t\t" + date);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch(NullPointerException e){
            // do nothing
        }

        Collections.reverse(sortedAddresses);

        return sortedAddresses;
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
