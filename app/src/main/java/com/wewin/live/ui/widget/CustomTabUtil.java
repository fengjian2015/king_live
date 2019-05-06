package com.wewin.live.ui.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.wewin.live.ui.adapter.HomeMainAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsaon
 * @date 2019/2/28
 * 设置tab管理工具
 */
public class CustomTabUtil {
    private CustomTabLayout customTab;
    private ViewPager viewPager;
    private Fragment fragment;
    private FragmentActivity activity;
    private List<Fragment> mFragmentList=new ArrayList<>();
    private  HomeMainAdapter adapter;

    public CustomTabUtil(Fragment fragment, ViewPager viewPager, CustomTabLayout customTab){
        this.customTab=customTab;
        this.viewPager=viewPager;
        this.fragment=fragment;
    }


    public CustomTabUtil(FragmentActivity activity, ViewPager viewPager, CustomTabLayout customTab){
        this.customTab=customTab;
        this.viewPager=viewPager;
        this.activity=activity;
    }

    /**
     * 设置选择某个Fragment
     * @param position
     */
    public void setSelect(int position){
        viewPager.setCurrentItem(position,false);
        customTab.moveToPosition(position);
    }

    /**
     * 设置内容
     * @param str
     */
    public void setTitleArr(String[] str){
        customTab.setTitleArr(str);
    }

    /**
     * 设置内容
     * @param titleList
     */
    public void setTitleList(List<String> titleList){
        customTab.setTitleList(titleList);
    }

    public void setFragmentList(List<Fragment> fragmentList){
        mFragmentList.clear();
        mFragmentList.addAll(fragmentList);
    }

    public void initViewPage(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();
            return;
        }
        if(activity==null) {
            adapter = new HomeMainAdapter(fragment.getChildFragmentManager(), mFragmentList);
        }else {
            adapter = new HomeMainAdapter(activity.getSupportFragmentManager(), mFragmentList);
        }
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                customTab.moveToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        customTab.setOnTabClickListener(new CustomTabLayout.OnTabClickListener() {
            @Override
            public void tabClick(int position, String str) {
                viewPager.setCurrentItem(position,false);
                customTab.moveToPosition(position);
            }
        });
    }




}
