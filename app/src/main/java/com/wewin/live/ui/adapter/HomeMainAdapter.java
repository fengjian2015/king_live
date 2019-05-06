package com.wewin.live.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsaon
 * @date 2019/2/27
 * 主页面导航
 */
public class HomeMainAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentList = new ArrayList<>();

    public HomeMainAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        mFragmentList.addAll(fragmentList);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //如果注释这行，那么不管怎么切换，page都不会被销毁
        //super.destroyItem(container, position, object);
    }
}
