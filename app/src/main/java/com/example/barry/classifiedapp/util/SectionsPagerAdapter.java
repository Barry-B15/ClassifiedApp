package com.example.barry.classifiedapp.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barry on 11/4/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "SectionsPagerAdapter";

    // list of fragments
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    // adding the fragments in SearActivity was redlining but
    // alt+enter > creating the ff fixed it. I'm not sure if this is ok
    /*public void addFragment(SearchFragment searchFragment) {
    }

    public void addFragment(WatchListFragment watchListFragment) {
    }

    public void addFragment(PostFragment postFragment) {
    }

    public void addFragment(AccountFragment accountFragment) {
    }*/
}

