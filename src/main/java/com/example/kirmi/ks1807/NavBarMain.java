package com.example.kirmi.ks1807;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)  //when NavBarMain loads: it loads the new HomeFragment
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbarmain);
        UserID = Global.UserID;
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnNavigationItemSelectedListener(this);
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d("Service"," mService = " + mService);
            loadFragment(new HomeFragment());
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
        }
    };

    private boolean loadFragment(Fragment fragment)
    {
        if(Global.isLogged){
            if(fragment != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, fragment, fragID)
                        .commit();
                if(fragment.getClass() == HomeFragment.class){  //bind service to the homefragment
                    ((HomeFragment) fragment).setService(mService);
                }
                return true;
            }
        }
        return false;
    }

    /*@Override
    protected void onPause() {
        if(!Global.isLogged && !Global.isInstalled){
            unbindService(serviceConnection);
        }
        super.onPause();
    }*/

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Log.d("OBP >>> "," isLogged and " + Global.isLogged + " isInstalled " + Global.isInstalled);
    }

    @Override
    public void onStop(){
        unbindService(serviceConnection);
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("onResume ","called");
        if(!Global.isLogged){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            unbindService(serviceConnection);
            this.finish();
        } else {
            Intent intent = new Intent(this, BackgroundService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Fragment fragment = null;
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
