package com.example.primeapp.scheduling;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.primeapp.R;

public class NotifyWorker extends Worker {
    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        triggerNotification();

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }
    private final static String manasia_notification_channel = "PRIME";

    int eventCounter=1;
    private void triggerNotification(){

//get latest event details
        String notificationTitle = "PRIME Notification";
        String notificationText ="Remember to perfor a test";

        //build the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), manasia_notification_channel)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        //.setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //trigger the notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());

        //we give each notification the ID of the event it's describing,
        //to ensure they all show up and there are no duplicates
        notificationManager.notify(eventCounter, notificationBuilder.build());

        eventCounter=eventCounter+1;
    }
}