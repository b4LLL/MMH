package com.example.kirmi.ks1807;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BackgroundServiceStarter extends BroadcastReceiver
{

    public void onReceive(Context context, Intent intent)
    {
        Log.i("onReceive"," called in BSS");
        if(Global.isLogged && !Global.bgsReceiverRunning){
            Intent i = new Intent(context, BackgroundService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  //checking for SDK/Build version?
                context.startForegroundService(i);
            else
                context.startService(i);
            Log.i("BroadcastReceiver","BGS started.");
            Global.bgsReceiverRunning = true;
        }
    }

    public void onEnd(Context context, Intent intent)
    {
        Intent i = new Intent(context, BackgroundService.class);
        context.stopService(i);
        Global.bgsReceiverRunning = false;
    }

}
