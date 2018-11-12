package com.example.dhruvikasahni.lvtexting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mainActivity = new Intent(context, MainActivity.class);
        context.startActivity(mainActivity);
    }
}
