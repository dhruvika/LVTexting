package com.example.dhruvikasahni.lvtexting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.RIGHT;

public class MessageGridAdapter extends BaseAdapter {

    Context context;
    final String [] messages;
    View view;

    public MessageGridAdapter(Context context, String[] messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final TextView tv = new TextView(context);
        final String msg = messages[i];
        tv.setText(messages[i]);



        if (i%2==1){ //if user-sent then it's right-side message (therefore, will set height for row)
            String msgPrev = messages[i-1];
            if (msgPrev.length()>msg.length()){ // if left is longer than right
                tv.setText(msgPrev); // set tv to left
            }
        }

        tv.post(new Runnable() {

            @Override
            public void run() {

                int lineCount = tv.getLineCount(); // get line count for tv
                tv.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,(lineCount+1)*tv.getLineHeight()));//set height for self(if left) or longer (if right)
                tv.setText(msg); //reset tv text in case it was set to left to obtain line count


            }
        });
        if(i%2==1){ //if user-sent message
            tv.setGravity(RIGHT);
        }

        // Add formatting to row
        SettingsManager.applyThemeToView(this.context, tv);

        return tv;


    }
}
