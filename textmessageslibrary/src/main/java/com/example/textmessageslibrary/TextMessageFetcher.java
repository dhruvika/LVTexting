package com.example.textmessageslibrary;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

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
                String date = getDate(dateMillis, "hh:mm aaa dd MMM");

                sms.add("Name: " + address + "\n Date: " + date + "\n Message: " + body);
            }
            cursor.close();
        }
        catch(NullPointerException e){
            // do nothing
        }

        return sms;
    }

    // Fetches recent conversations with whether they are read/unread, who its with and the date of the last message.
    public ArrayList<ArrayList<String>> fetchRecentConversations(){

        ArrayList<ArrayList<String>> allConversations = new ArrayList<>();
        Set<String> addresses = new HashSet<>();
        Uri uri = Uri.parse("content://mms-sms/conversations");
        Cursor cursor = this.activity.getContentResolver().query(uri, new String[]{"_id", "address", "date", "read", "body"},
                null, null, "date DESC");

        try {
            cursor.moveToFirst();
            int totalCount = cursor.getCount();
            for (int i = 0; i < totalCount; i++) {
                ArrayList<String> conversation = new ArrayList<>();
                String address = cursor.getString(1);
                Long dateMillis = cursor.getLong(2);
                String date = getDate(dateMillis, "hh:mm aaa dd MMM");
                String body = cursor.getString(4);
                int read = cursor.getInt(3);

                if(!addresses.contains(address)){
                    addresses.add(address);
                    if(read == 1){
                        conversation.add("unread");
                    }
                    else {
                        conversation.add("read");
                    }
                    conversation.add(address);
                    conversation.add(date);
                    allConversations.add(conversation);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch(NullPointerException e){
            // do nothing
        }

        return allConversations;
    }

    /**
     * Return the name of the contact if it exists in the phonebook.
     * @param number phone number assosciated with contact
     * @return String contact name if one exists, null otherwise.
     */
    public String getContactName(String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = activity.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return cur.getString(2);
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return null;
    }

    /**
     * Return the phone number of the contact if it exists in the phonebook.
     * @param contactName contact name assosciated with a phone number
     * @return String phone numeber if one exists in the contact list, null otherwise.
     */
    public String getContactNumber(String contactName) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(contactName));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = activity.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return cur.getString(2);
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return null;
    }

    /**
     * Return the phone number of the contact if it exists in the phonebook.
     * @param contactName contact name assosciated with a phone number
     * @return String phone numeber if one exists in the contact list, null otherwise.
     */
    public String getContactNumber2(String contactName) {
        String number = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + contactName +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            number = c.getString(0);
        }
        c.close();
        return number;
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
        String formattedDate = formatter.format(calendar.getTime());
        return formattedDate;
    }

}
