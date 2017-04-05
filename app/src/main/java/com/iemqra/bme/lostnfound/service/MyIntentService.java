package com.iemqra.bme.lostnfound.service;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.iemqra.bme.lostnfound.R;

public class MyIntentService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Notification")
                .setContentText(message);
        notificationManager.notify(1, mBuilder.build());
    }
}