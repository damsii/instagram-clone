package com.iamdamjanmiloshevski.instagramclone.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.adapter.InstagramPagesAdapter;
import com.iamdamjanmiloshevski.instagramclone.fragment.HomeFragment;
import com.iamdamjanmiloshevski.instagramclone.fragment.PhotoFragment;
import com.iamdamjanmiloshevski.instagramclone.fragment.ProfileFragment;
import com.iamdamjanmiloshevski.instagramclone.utility.Constants;
import com.iamdamjanmiloshevski.instagramclone.view.InstagramViewPager;
import com.parse.ParseAnalytics;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    public static InstagramViewPager pager;
    public static Toolbar toolbar;
    public static ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        logo = toolbar.findViewById(R.id.iv_logo);
        pager = findViewById(R.id.pages);
        TabLayout tabs = findViewById(R.id.tabs);

        setupPages(pager, tabs);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void setupPages(final ViewPager pager, TabLayout tabs) {
        InstagramPagesAdapter adapter = new InstagramPagesAdapter(getSupportFragmentManager(), getApplicationContext());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new ProfileFragment());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(this);
        pager.setCurrentItem(0);
        tabs.getTabAt(0).setIcon(Constants.tabIconsSelected[0]);
        tabs.getTabAt(1).setIcon(Constants.tabIconsNormal[1]);
        tabs.getTabAt(2).setIcon(Constants.tabIconsNormal[2]);
        tabs.addOnTabSelectedListener(this);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tab.setIcon(Constants.tabIconsSelected[tab.getPosition()]);
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tab.setIcon(Constants.tabIconsNormal[tab.getPosition()]);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            logo.setVisibility(View.GONE);
        } else {
            logo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
