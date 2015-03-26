package com.easemob.chatuidemo.adapter;

import itstudio.instructor.jazzyViewPager.JazzyViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter{
	private Fragment[] fragments;
	private JazzyViewPager mJazzyViewPager;
	public MainPagerAdapter(FragmentManager fm,JazzyViewPager mJazzyViewPager,Fragment[] fragments) {
		super(fm);
		this.fragments = fragments;
		this.mJazzyViewPager =mJazzyViewPager;
		
	}
	
	@Override
	public int getCount() {
		return fragments.length;
	}
	
	@Override
	public Fragment getItem(int position) {
        
        mJazzyViewPager.setObjectForPosition(fragments[position], position);
        return fragments[position];
	}
	
	
}
