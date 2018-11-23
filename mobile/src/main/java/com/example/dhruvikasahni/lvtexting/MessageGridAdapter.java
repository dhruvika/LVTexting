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
    private final String [] messages;
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
        tv.setText(messages[i]);
        //TextView item = (TextView) getItem(i);
        int lineCount;
        tv.post(new Runnable() {

            @Override
            public void run() {

                int lineCount2 = tv.getLineCount();
//                tv.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,tv.getLineCount()*tv.getLineHeight()*3));


            }
        });
        tv.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,tv.getLineHeight()*4));//TODO: dynamically adjust height
        if(i%2==1){ //if user-sent message
            tv.setGravity(RIGHT);
        }
        return tv;


    }
}
