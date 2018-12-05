package com.example.dhruvikasahni.lvtexting;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Check if the current activity is main activity.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> alltasks = am
                .getRunningTasks(1);

        String mainActivityName = "MainActivity";

        String currentActivity = alltasks.get(0).topActivity.getClassName();
        String packageName = "com.example.dhruvikasahni.lvtexting.";

        if (currentActivity.equals(packageName + mainActivityName)) {
            Intent mainActivity = new Intent(context, MainActivity.class);
            context.startActivity(mainActivity);
        }
    }
}
