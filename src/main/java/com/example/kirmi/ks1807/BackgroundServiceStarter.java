package com.example.kirmi.ks1807;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.protocol.types.PlayerState;

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

        Log.d("intent.getAction() ","" + intent.getAction());
        // access playerState from here.

        if (intent.getAction().equals("com.spotify.music.active")) {
            Log.d("ACTIVE ", "INTENT -> " + intent + "\tINTENT ACTION" + intent.getAction());
            if(Global.mSpotifyAppRemote != null){
                PlayerApi playerApi = Global.mSpotifyAppRemote.getPlayerApi();
                playerApi.getPlayerState()
                        .setResultCallback(playerState -> {
                            // have fun with playerState
                            this.pollPlayerState(playerState);

                        })
                        .setErrorCallback(throwable -> {
                            // =(
                        });

            }
        }

    }

    void pollPlayerState(PlayerState playerState){
        Log.d("BSS\t"," signal received" + "\nTrack.name\t" + playerState.track.name +
                "\nTrack.Artists\t" + playerState.track.artist.name);
    }

    public void onEnd(Context context, Intent intent)
    {
        Intent i = new Intent(context, BackgroundService.class);
        context.stopService(i);
        Global.bgsReceiverRunning = false;
    }

}
