package com.example.kirmi.ks1807;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BackgroundServiceStarter extends BroadcastReceiver
{

    public void onReceive(Context context, Intent intent)
    {
        Intent i = new Intent(context, BackgroundService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(i);
        else
            context.startService(i);
    }

    public void onEnd(Context context, Intent intent)
    {
        Intent i = new Intent(context, BackgroundService.class);
        context.stopService(i);
    }
}
