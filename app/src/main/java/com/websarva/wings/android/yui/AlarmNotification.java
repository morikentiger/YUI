package com.websarva.wings.android.yui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AlarmNotification extends BroadcastReceiver {

  @Override //  データを受信した
  public void onReceive(Context context, Intent intent) {

    Log.d("AlarmBroadcastReceiver", "onReceive() pid=" + android.os.Process.myPid());

    int requestCode = intent.getIntExtra("RequestCode", 0);

    PendingIntent pendingIntent =
            PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    String channdelId = "default";
    //  app name
    String title = context.getString(R.string.app_name);

    long currentTime = System.currentTimeMillis();
    SimpleDateFormat dataFormat =
            new SimpleDateFormat("HH:mm:ss", Locale.JAPAN);
    String cTime = dataFormat.format(currentTime);

    //  メッセージ　＋ 11:22:331
    String message = "時間になりました。　" + cTime;

    NotificationManager notificationManager =
            (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    //  Notification Channel 設定
    NotificationChannel channel = new NotificationChannel(
            channdelId, title, NotificationManager.IMPORTANCE_DEFAULT);
    channel.setDescription(message);
    channel.enableVibration(true);
    channel.canShowBadge();
    channel.enableLights(true);
    channel.setLightColor(Color.BLUE);
    //  the channel appears on the lockscreen
    channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
    channel.setSound(defaultSoundUri, null);
    channel.setShowBadge(true);

    if(notificationManager != null){
      notificationManager.createNotificationChannel(channel);

      Notification notification = new Notification.Builder(context, channdelId)
              .setContentText(title)
              //  android標準アイコンから
              .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
              .setContentText(message)
              .setAutoCancel(true)
              .setContentIntent(pendingIntent)
              .setWhen(System.currentTimeMillis())
              .build();

      //  通知
      notificationManager.notify(R.string.app_name, notification);
    }

  }
}
