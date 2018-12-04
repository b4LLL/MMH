package com.example.kirmi.ks1807;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)  //when NavBarMain loads: it loads the new HomeFragment
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbarmain);
        UserID = Global.UserID;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //checking here if the App has permission to write over other apps
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
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
            Log.d("OSD ","onServiceDisconnected called");
            mBound = false;
            mService = null;
        }
    };

    private boolean loadFragment(Fragment fragment)
    {
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
            if(fragment.getClass() == HomeFragment.class){
                Log.d("NBM", "HomeFragment Loading");
                ((HomeFragment) fragment).setService(mService);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        unbindService(serviceConnection);
        super.onBackPressed();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("onResume"," called in NBM");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Fragment fragment = null;

        switch(item.getItemId())
        {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_diary:
                fragment = new DiaryFragment();
                break;
            case R.id.nav_resources:
                fragment = new ResourcesFragment();
                break;
            case R.id.nav_progress:
                fragment = new ProgressFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
        }
        return loadFragment(fragment);
    }
}
