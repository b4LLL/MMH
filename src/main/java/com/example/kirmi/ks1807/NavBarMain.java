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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class NavBarMain extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener
{
    String UserID = "";
    Boolean mBound;
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
        UserID = Global.UserID;
        nav = findViewById(R.id.bottom_nav);
        nav.setOnNavigationItemSelectedListener(this);
        //Intent intent = new Intent(this, BackgroundService.class);
        Intent i = new Intent(getApplicationContext(), BackgroundService.class);
        if((mService != null) && mService.isRunning){
            if(Build.VERSION.SDK_INT >= 26){
                getApplicationContext().startForegroundService(i);
                Log.i("startService","\tForegroundService\n");
            }else{
                getApplicationContext().startService(i);
                Log.i("startService","\tBackgroundService\n");
            }
        }
        Intent intent = new Intent(getApplication().getApplicationContext(), BackgroundService.class);
        getApplicationContext().bindService(intent ,serviceConnection, Context.BIND_AUTO_CREATE);
        //https://stackoverflow.com/questions/16703162/how-to-prevent-bound-service-from-being-destroyed-while-activitys-runtime-chang
        //bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        this.spotifyListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadFragment(new HomeFragment());
            }
        };
        registerReceiver(this.spotifyListener, new IntentFilter("spotifyConnected"));
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.i("Service"," mService = " + mService);
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

    public BackgroundService getService(){
        return mService;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Log.i("OBP >>> "," isLogged: " + Global.isLogged + " isInstalled: " + Global.isRunning);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Global.isRunning)
            unregisterReceiver(this.spotifyListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!Global.isLogged){
            unbindService(serviceConnection);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }else if (Global.isRunning)
            registerReceiver(this.spotifyListener, new IntentFilter("spotifyConnected"));
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
