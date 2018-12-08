package com.example.dhruvikasahni.lvtexting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Dummy service to make sure this app can be default SMS app.
 */
public class DummySmsSendService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
