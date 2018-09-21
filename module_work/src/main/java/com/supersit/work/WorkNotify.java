/************************************************************
 *  * Hyphenate CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of Hyphenate Inc.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from Hyphenate Inc.
 */
package com.supersit.work;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.blankj.utilcode.util.AppUtils;
import com.supersit.common.entity.EventEntity;
import com.supersit.work.entity.NotificationEntity;
import com.supersit.work.ui.NotificationActivity;
import com.supersit.work.ui.NotificationListActivity;
import com.supersit.work.ui.WorkEventListActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * new message notifier class
 * 
 * this class is subject to be inherited and implement the relative APIs
 */
public class WorkNotify {
    private final static String TAG = WorkNotify.class.getSimpleName();
    Ringtone ringtone = null;

    protected static String mCommonChannelID = "CommonNotifier";

    public static  int mWorkEventNotifyID = 0666;
    protected static String mWorkEventChannelID = "WorkEventNotifier"; // start notification id
    private int mWorkEventtNotifynNum=0;

    protected static int foregroundNotifyID = 06666;


    protected NotificationManager notificationManager = null;

    protected Context appContext;
    protected String packageName;
    protected long lastNotifiyTime;
    protected AudioManager audioManager;
    protected Vibrator vibrator;

    public WorkNotify() {
    }
    
    /**
     * this function can be override
     * @param context
     * @return
     */
    public WorkNotify init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        packageName = appContext.getApplicationInfo().packageName;
        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = mCommonChannelID;
            String channelName = "普通通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);

            channelId = mWorkEventChannelID;
            channelName = "工作事件通知";
            importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
        return this;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * this function can be override
     */
    public void resetWorkEventNotify(){
        mWorkEventtNotifynNum = 0;
        if (notificationManager != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                notificationManager.deleteNotificationChannel(mWorkEventChannelID);
            else
                notificationManager.cancel(mWorkEventNotifyID);
        }
    }

    public void resetNotifyById(int id){
        if (notificationManager != null){
            notificationManager.cancel(id);
        }
    }

    public synchronized void sendWorkEventNotification(){
        mWorkEventtNotifynNum ++ ;
        String contentText = String.format(appContext.getString(R.string.urgent_event_notifys),mWorkEventtNotifynNum);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext,mWorkEventChannelID);
        mBuilder.setContentTitle(appContext.getString(R.string.urgent_event_notify))//设置通知栏标题
                .setContentText(contentText) //<span style="font-family: Arial;">/设置通知栏显示内容</span>
                .setContentIntent(getWorkEventIntent()) //设置通知栏点击意图
                .setTicker(appContext.getString(R.string.event_notify)) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setSmallIcon(appContext.getApplicationInfo().icon);//设置通知小ICON                                                        .setAutoCancel(true);
        Notification notification = mBuilder.build();
        notificationManager.notify(mWorkEventNotifyID, notification);
        vibrateAndPlayTone();
    }
    private PendingIntent getWorkEventIntent() {
        Intent intent = new Intent(appContext, WorkEventListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("status", 1);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(appContext, mWorkEventNotifyID, intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * send it to notification bar
     * This can be override by subclass to provide customer implementation
     * @param entity
     */
    public synchronized void sendNotification(NotificationEntity entity) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext,mCommonChannelID);
        mBuilder.setContentTitle(entity.getTitle())//设置通知栏标题
                .setContentText(entity.getRemark()) //<span style="font-family: Arial;">/设置通知栏显示内容</span>
                .setContentIntent(getNotityIntent(entity)) //设置通知栏点击意图
                .setTicker("通知公告") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setSmallIcon(appContext.getApplicationInfo().icon);//设置通知小ICON                                                        .setAutoCancel(true);
        Notification notification = mBuilder.build();
        if (AppUtils.isAppForeground(appContext.getPackageName())) {
            notificationManager.notify(foregroundNotifyID, notification);
            notificationManager.cancel(foregroundNotifyID);
        } else {
            notificationManager.notify(entity.getId(), notification);
        }
        vibrateAndPlayTone();
    }
    private PendingIntent getNotityIntent(NotificationEntity entity) {
   /*     Intent intent = new Intent(appContext, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", appContext.getString(R.string.notification));
        bundle.putInt("id", entity.getId());
        bundle.putString("url", entity.getDetailsUrl());
        intent.putExtras(bundle);
        return PendingIntent.getActivity(appContext, entity.getId(), intent,PendingIntent.FLAG_UPDATE_CURRENT);*/
        Intent intent = new Intent(appContext, NotificationListActivity.class);
        return PendingIntent.getActivity(appContext, entity.getId(), intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * vibrate and  play tone
     */
    public void vibrateAndPlayTone() {
        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
            // received new messages within 2 seconds, skip play ringtone
            return;
        }
        
        try {
            lastNotifiyTime = System.currentTimeMillis();
            
            // check if in silent mode
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                return;
            }
            long[] pattern = new long[] { 0, 180, 80, 120 };
            vibrator.vibrate(pattern, -1);

            if (ringtone == null) {
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                if (ringtone == null) {
                    return;
                }
            }

            if (!ringtone.isPlaying()) {
                String vendor = Build.MANUFACTURER;

                ringtone.play();
                if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                    Thread ctlThread = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                if (ringtone.isPlaying()) {
                                    ringtone.stop();
                                }
                            } catch (Exception e) {
                            }
                        }
                    };
                    ctlThread.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
