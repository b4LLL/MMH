package com.example.kirmi.ks1807;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class NavBarMain extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener
{
    String UserID = "";
    Boolean mBound = false;
    BackgroundService mService;
    String fragID;
    BottomNavigationView nav;
    public BroadcastReceiver spotifyListener;
    Integer selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState)  //when NavBarMain loads: it loads the new HomeFragment
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbarmain);
        ProgressBar progressBar;
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        UserID = Global.UserID;
        nav = findViewById(R.id.bottom_nav);
        nav.setOnNavigationItemSelectedListener(this);
        Intent i = new Intent(getApplicationContext(), BackgroundService.class);
        if(!Global.isBGSrunning){
            if (Build.VERSION.SDK_INT >= 26) {
                getApplicationContext().startForegroundService(i);
                Log.i("startService", "\tForegroundService\n");
//            Global.isBound = true;
            } else {
                getApplicationContext().startService(i);
                Log.i("startService", "\tBackgroundService\n");
            }
        }
        //bind it once connected
        Intent intent = new Intent(getApplication().getApplicationContext(), BackgroundService.class);
        getApplicationContext().bindService(intent ,serviceConnection, Context.BIND_AUTO_CREATE);
        //https://stackoverflow.com/questions/16703162/how-to-prevent-bound-service-from-being-destroyed-while-activitys-runtime-chang
        this.spotifyListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(spotifyListener);
                spotifyListener = null;
                loadFragment(new HomeFragment());
                progressBar.setVisibility(View.GONE);
            }
        };
        registerReceiver(this.spotifyListener, new IntentFilter("spotifyConnected"));
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            //Log.i("Service"," {Service = " + mService);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
        }
    };

    boolean loadFragment(Fragment fragment)
    {
        if(Global.isLogged){
            if(fragment != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, fragment, fragID)
                        .attach(fragment)
                        .commit();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("NBM", "onDestroy: ");
        if(spotifyListener != null){
            Log.i("NBM", "unegisteringListener: ");
            unregisterReceiver(spotifyListener);
        }
    }

    public BackgroundService getService(){
        return mService;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Log.i("OBP >>> "," isLogged: " + Global.isLogged + " isInstalled: " + Global.isRunning);
    }

    @Override
    public void onResume(){
        Log.i("NBM","onResume:");
        super.onResume();
        if(!Global.isLogged){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }else if (Global.isRunning){
            ProgressBar progressBar;
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Fragment fragment = null;
        selectedItem = item.getItemId();
        switch(item.getItemId())
        {
            case R.id.nav_home:
                fragment = new HomeFragment();
                fragID = "home";
                break;
            case R.id.nav_diary:
                fragment = new DiaryFragment();
                fragID = "diary";
                break;
            case R.id.nav_resources:
                fragment = new ResourcesFragment();
                fragID = "resources";
                break;
            case R.id.nav_progress:
                fragment = new ProgressFragment();
                fragID = "progress";
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                fragID = "settings";
                break;
        }
        return loadFragment(fragment);
    }
}
