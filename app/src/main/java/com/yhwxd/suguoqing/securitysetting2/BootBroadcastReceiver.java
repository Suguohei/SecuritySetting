package com.yhwxd.suguoqing.securitysetting2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yhwxd.suguoqing.securitysetting2.activity.AppListActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // throw new UnsupportedOperationException("Not yet implemented");
        Intent intent2 = new Intent(context, LockService.class);
        context.startService(intent2);
    }
}
