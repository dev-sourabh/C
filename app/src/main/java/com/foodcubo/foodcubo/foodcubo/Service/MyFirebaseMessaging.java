package com.foodcubo.foodcubo.foodcubo.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.Helper.NotificationHelper;
import com.foodcubo.foodcubo.android.OrderStatus;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.ScreenOneActivity;
import com.foodcubo.foodcubo.foodcubo.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificatonAPI(remoteMessage);
        else
        sendNotificaton(remoteMessage);
    }

    private void sendNotificatonAPI(RemoteMessage remoteMessage) {
        System.out.println("message recived......11111");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        assert notification != null;
        String title=notification.getTitle();
        String content=notification.getBody();
        Intent intent;
        if(Common.currentUser!=null) {
            intent = new Intent(MyFirebaseMessaging.this, OrderStatus.class);
            intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else{
            intent = new Intent(MyFirebaseMessaging.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper helper=new NotificationHelper(this);
        Notification.Builder builder=helper.channelNotification(title,content,pendingIntent,defaultSoundUri);
        helper.getManager().notify(new Random().nextInt(),builder.build());

    }

    private void sendNotificaton(RemoteMessage remoteMessage) {
        System.out.println("message recived......22222");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        Intent intent;
        if(Common.currentUser!=null) {
        intent=new Intent(MyFirebaseMessaging.this, ScreenOneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }else{
        intent = new Intent(MyFirebaseMessaging.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        assert notification != null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(notification.getTitle());
        builder.setContentText(notification.getBody()).setAutoCancel(true);
        builder.setSound(defaultSoundUri);
        builder.setContentIntent(pendingIntent);
        int num = (int) System.currentTimeMillis();

        NotificationManager noti=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert noti != null;
        noti.notify(num,builder.build());

    }
}
