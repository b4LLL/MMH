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
        Log.d("onReceive"," called in BSS");
        if(Global.isLogged && !Global.bgsReceiverRunning){
            Intent i = new Intent(context, BackgroundService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  //checking for SDK/Build version?
                context.startForegroundService(i);
            else
                context.startService(i);
            Log.d("BroadcastReceiver","BGS started.");
            Global.bgsReceiverRunning = true;
        }
        if (intent.getAction().equals("com.spotify.music.active")) {
            context.sendBroadcast(new Intent("playerStateChange"));
        }
    }

    public void onEnd(Context context, Intent intent)
    {
        Intent i = new Intent(context, BackgroundService.class);
        context.stopService(i);
        Global.bgsReceiverRunning = false;
    }

}
