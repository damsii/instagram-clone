package com.iamdamjanmiloshevski.instagramclone.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * --------------------------------------------
 * Author: Damjan Miloshevski
 * E-mail: damjan.milosevski@korvus.mk
 * Date: 15.12.2017
 * Project: instagram-clone
 * © Corvus Info d.o.o Zagreb, Croatia
 * --------------------------------------------
 */

public class InstagramPagesAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    private Context context;

    public InstagramPagesAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }


    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);

    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
