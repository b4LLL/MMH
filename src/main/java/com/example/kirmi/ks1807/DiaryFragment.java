package com.example.kirmi.ks1807;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiaryFragment extends Fragment {
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_diary_mainfrag, null);
        viewPager = view.findViewById(R.id.view_pager);
        pagerAdapter = new DiaryPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        return view;
    }
}