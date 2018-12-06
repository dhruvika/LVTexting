package com.example.dhruvikasahni.lvtexting;

//import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.TableLayout;

import java.util.List;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Check if the current activity is main activity.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> alltasks = am
                .getRunningTasks(1);

        String mainActivityName = "MainActivity";

        String currentActivityName = alltasks.get(0).topActivity.getClassName();
        String packageName = "com.example.dhruvikasahni.lvtexting.";

        if (currentActivityName.equals(packageName + mainActivityName)) {
            intent.setAction("android.provider.Telephony.SMS_RECEIVED");
            context.sendBroadcast(intent);
        }
    }
}
