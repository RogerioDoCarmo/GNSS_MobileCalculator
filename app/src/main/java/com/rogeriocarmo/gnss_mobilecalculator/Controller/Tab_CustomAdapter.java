package com.rogeriocarmo.gnss_mobilecalculator.Controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rogeriocarmo.gnss_mobilecalculator.View.TabbedDialog2;

import java.util.ArrayList;
import java.util.List;

public class Tab_CustomAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentCollection = new ArrayList<>();
    List<String> mTitleCollection = new ArrayList<>();

    public Tab_CustomAdapter(FragmentManager fm) {
        super(fm);
    }

    public static Fragment createInstance(String john) {
        return new TabbedDialog2();
    }

    public void addFragment(String title, Fragment fragment)
    {
        mTitleCollection.add(title);
        mFragmentCollection.add(fragment);
    }
    //Needed for
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleCollection.get(position);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentCollection.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentCollection.size();
    }
}
