package com.example.kirmi.ks1807;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class NavBarMain extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener
{

    String UserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbarmain);

        UserID = Global.UserID;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnNavigationItemSelectedListener(this);

        //Creating fragments.
        loadFragment(new HomeFragment());
    }

    private boolean loadFragment(Fragment fragment)
    {
        if(fragment != null)
        {

            //Getting the content of the fragment onto the main container.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        Fragment fragment = null;

        /*On selection of the navigation menu, the fragment content is displayed with its
        connected java fragment class*/
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
        //Creating each fragment.
        return loadFragment(fragment);
    }
}
