package com.wewin.live.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.wewin.live.R;

/*
 *   author:jason
 *   date:2019/5/113:34
 */
public class NotificationUtil {
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    final String CHANNEL_ID = "king_live_message";
    final String CHANNEL_NAME = "king_live_name_message";

    private final String CHANNEL = "message";
    private Context context;

    /**
     * 创建通知栏
     */
    public NotificationUtil setNotification(Context c) {
        this.context = c;
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context, CHANNEL);
        return this;
    }

    public NotificationUtil setContent(String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.icon_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo_r));
        } else {
            mBuilder.setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.icon_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo_r))
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis());
        }
        return this;
    }

    /**
     * 添加跳转
     *
     * @param intent
     */
    public NotificationUtil setIntent(Intent intent) {
        if (mBuilder == null) return this;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        return this;
    }

    /**
     * 显示通知
     *
     * @return
     */
    public NotificationUtil notifyNot() {
        if (mNotificationManager == null) return this;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
        return this;
    }
}
