package com.wewin.live.ui.activity.person;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.ui.adapter.AssetRecordAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;

/**
 * 资金记录界面
 */
public class AssetRecordActivity extends BaseActivity {

    List<Map> mHashMapList = new ArrayList<>();
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private AssetRecordAdapter mAssetRecordAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_asset_record;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.fund_record));
        initRecyclerView();
        initData();
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mHashMapList.add(new HashMap());
        }
        mAssetRecordAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
                mRefreshLayout.finishRefresh();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initData();
                mRefreshLayout.finishLoadMore();
            }
        });
        mAssetRecordAdapter = new AssetRecordAdapter(this, mHashMapList);
        mRecyclerView.setAdapter(mAssetRecordAdapter);
    }

}
