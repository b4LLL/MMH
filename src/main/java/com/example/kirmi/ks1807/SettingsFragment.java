package com.example.kirmi.ks1807;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_settingsfrag, null);

        //Settings the container in which the content of the two tabs will show.
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.tabcontainer);
        setupViewPager(viewPager);

        //Set Tabs inside Toolbar.
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        return view;
    }

    //Add Fragments to Tabs.
    private void setupViewPager(ViewPager viewPager)
    {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new AccountSettings(), "Account");
        adapter.addFragment(new ProfileSettings(), "Profile");
        viewPager.setAdapter(adapter);
    }

    //Fragment tab adapter to add the tabs as fragments.
    static class Adapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}