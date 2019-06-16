package com.whatsappstatus.saver.fragments.wa;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whatsappstatus.saver.R;
import com.whatsappstatus.saver.adapter.ViewPagerWAAdapter;


public class WAFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;

    public WAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_wa, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewPager_wa);
        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout_wa);
        viewPager.setOffscreenPageLimit(2);
        ViewPagerWAAdapter adapter = new ViewPagerWAAdapter(getChildFragmentManager());
        adapter.addTabs("Images",new WAImageFragment());
        adapter.addTabs("Videos",new WAVideoFragment());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

}
