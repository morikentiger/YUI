package com.websarva.wings.android.yui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    //  toastで受け取りを確認
    Toast.makeText(context, "Received ", Toast.LENGTH_SHORT).show();
  }
}
