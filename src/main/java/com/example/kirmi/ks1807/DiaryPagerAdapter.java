package com.example.kirmi.ks1807;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DiaryPagerAdapter extends FragmentPagerAdapter {
    DiaryPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch (position) {
            case 0:
                return new DiaryInputFragment();
            case 1:
                return new DiaryCalendarFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return 2;
    }

    @Override
    public String getPageTitle(int position){
        switch(position){
            case 0:
                return "Diary Input";
            case 1:
                return "Recent Entries";
        }
        return  null;
    }

}
