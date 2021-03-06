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
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class NavBarMain extends FragmentActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener
{
    String UserID = "";
    Boolean mBound = false;
    BackgroundService mService;
    String fragID;
    BottomNavigationView nav;
    public BroadcastReceiver spotifyListener;
    Integer selectedItem;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.i("NBM", "onServiceConnected: \t\t" + mService);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
        }
    };

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
                loadFragment(new HomeFragment());
                progressBar.setVisibility(View.GONE);
                unregisterReceiver(spotifyListener);
            }
        };
        registerReceiver(this.spotifyListener, new IntentFilter("spotifyConnected"));
    }

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
            try{
                unregisterReceiver(spotifyListener);
            }catch (Exception e){
                Log.i("NBM", "unegisteringListener: " + e.toString());
            }
        }
    }

    public BackgroundService getService(){
        return mService;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onResume(){
        Log.i("NBM","onResume:");
        if(mService != null)
            Log.i("MSERVICE", "onResume: \t\t" + mService);
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
