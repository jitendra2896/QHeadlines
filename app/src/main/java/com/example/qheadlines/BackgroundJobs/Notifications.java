package com.example.qheadlines.BackgroundJobs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.qheadlines.ActivitiesAndFragments.MainActivity;
import com.example.qheadlines.R;

public class Notifications {

    private static final int PENDING_INTENT_REQUEST_CODE = 1234;
    private static final int NOTIFICATION_ID = 2;
    private static NotificationTarget notificationTarget;

    public static void notifyUser(Context context,String headline,String summary){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(headline)
                .setContentText(summary);

        Intent intent = new Intent(context,MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,PENDING_INTENT_REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    public static void notifyUserCustom(Context context,String headline,String summary,String imageUrl){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.id_custom_notification_headline_text_view,headline);
        remoteViews.setTextViewText(R.id.id_custom_notification_description_text_view,summary);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews);

        Intent intent = new Intent(context,MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,PENDING_INTENT_REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = remoteViews;
        }
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.notify(3,notification);

        notificationTarget = new NotificationTarget(context,remoteViews,R.id.id_custom_notification_image_view,notification,NOTIFICATION_ID);
        Glide
                .with(context)
                .load(imageUrl)
                .asBitmap()
                .into(notificationTarget);
    }
}
