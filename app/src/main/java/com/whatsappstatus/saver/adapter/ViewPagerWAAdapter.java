package com.whatsappstatus.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by umer on 19-Apr-18.
 */

public class ViewPagerWAAdapter extends FragmentPagerAdapter {
    ArrayList<String> arrayListText = new ArrayList<>();
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    public ViewPagerWAAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arrayListText.get(position);
    }


    @Override
    public int getCount() {
        return arrayListText.size();
    }

    public void addTabs(String text, Fragment fragment) {
        arrayListText.add(text);
        fragmentArrayList.add(fragment);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }
}