package com.wewin.live.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.fragment.HtmlFragment;
import com.wewin.live.ui.fragment.AbstractLazyFragment;
import com.wewin.live.ui.widget.CustomTabLayout;
import com.wewin.live.ui.widget.CustomTabUtil;
import com.wewin.live.ui.widget.ErrorView;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jsaon
 * @date 2019/3/12
 * 首页5个fragment基类
 */
public abstract class BaseMainFragment extends AbstractLazyFragment {
    @InjectView(R.id.custom_tab)
    CustomTabLayout customTab;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.rl_data)
    LinearLayout rlData;

    private View view;
    private CustomTabUtil customTabUtil;
    private List<Map> mMapList;
    private List<Fragment> mFragmentList = new ArrayList<>();

    private ErrorView mErrorView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), getLayoutId(), null);
        ButterKnife.inject(this, view);
        customTabUtil = new CustomTabUtil(this, viewPager, customTab);
        mErrorView = new ErrorView(getContext(), view);
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initFlexTitle();
        initErrorView();
    }

    protected abstract int getLayoutId();

    protected abstract String type();

    private void initErrorView() {
        mErrorView.setTvError(getString(R.string.error));
        mErrorView.setTvNoData(getString(R.string.no_data));
        mErrorView.setOnContinueListener(new ErrorView.OnContinueListener() {
            @Override
            public void again() {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.MAIN_MENU_AGAIN));
            }
        });
    }

    private void initFlexTitle() {
        mFragmentList.clear();
        List<String> str = new ArrayList<>();

        Map map = JSON.parseObject(MySharedPreferences.getInstance().getString(MySharedConstants.MAIN_MENU));
        if (map == null) {
            failure();
            return;
        }
        mMapList = JSONObject.parseArray(map.get(type()) + "", Map.class);
        //没有数据时
        if (mMapList == null || mMapList.size() <= 0) {
            mErrorView.notDataShow(false);
            rlData.setVisibility(View.GONE);
            return;
        }
        //只有一条时，不展示导航栏
        if(mMapList.size()==1){
            customTab.setVisibility(View.GONE);
        }else{
            customTab.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < mMapList.size(); i++) {
            HtmlFragment htmlFragment = new HtmlFragment();
            Bundle bundle = new Bundle();
            bundle.putString(BaseInfoConstants.URL, mMapList.get(i).get(BaseInfoConstants.URL) + "");
            bundle.putString(BaseInfoConstants.TYPE,type()+"");
            htmlFragment.setArguments(bundle);
            mFragmentList.add(htmlFragment);

            str.add(mMapList.get(i).get(BaseInfoConstants.NAME) + "");
        }
        customTabUtil.setTitleList(str);
        customTabUtil.setFragmentList(mFragmentList);
        customTabUtil.initViewPage();
    }



    /**
     * 更新数据
     */
    protected void updata(){
        mErrorView.hint();
        rlData.setVisibility(View.VISIBLE);
        initFlexTitle();
    }

    /**
     * 失败
     */
    protected void failure(){
        mErrorView.errorShow(true);
        rlData.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.reset(this);
    }
}
