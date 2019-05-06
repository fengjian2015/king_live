package com.wewin.live.ui.activity.person;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.ui.adapter.AllRemindersAdapter;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 提醒页面设置
 */
public class AllRemindersActivity extends BaseActivity {
    @InjectView(R.id.on_off_y)
    Switch onOffY;
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;

    List<Map> mHashMapList = new ArrayList<>();

    private AllRemindersAdapter mAllRemindersAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_all_reminders;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.all_reminders));
        UiUtil.setOnOf(MySharedConstants.ON_OFF_ALL_REMINDERS,onOffY);
        initRecyclerView();
        initData();
    }




    private void initData() {
        for(int i=0;i<10;i++){
            mHashMapList.add(new HashMap());
        }
        mAllRemindersAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

            }
        });
        mAllRemindersAdapter = new AllRemindersAdapter(this, mHashMapList);
        recyclerView.setAdapter(mAllRemindersAdapter);
    }

    @OnClick({R.id.on_off_y})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.on_off_y:
                UiUtil.changeOnOff(MySharedConstants.ON_OFF_ALL_REMINDERS,onOffY);
                break;

        }
    }
}
