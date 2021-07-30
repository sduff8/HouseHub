package com.example.househub.Notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

public class UpdatedNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.househub";
    private static final String CHANNEL_NAME = "HouseHub";

    private NotificationManager notificationManager;

    public UpdatedNotification(Context base){
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
    }

    public NotificationManager getManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getUpdatedNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon){
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri, audioAttributes)
                .setAutoCancel(true);
    }
}
