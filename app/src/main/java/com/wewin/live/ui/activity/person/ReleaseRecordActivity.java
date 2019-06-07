package com.wewin.live.ui.activity.person;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.example.jasonutil.util.ScreenTools;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.ui.adapter.ReleaseArticleAdapter;
import com.wewin.live.ui.adapter.ReleaseVideoAdapter;
import com.wewin.live.ui.widget.recyclerive.VegaLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 发布记录界面
 */
public class ReleaseRecordActivity extends BaseActivity {

    @InjectView(R.id.recycler_video)
    RecyclerView recyclerVideo;
    @InjectView(R.id.recycler_article)
    RecyclerView recyclerArticle;
    @InjectView(R.id.refresh_layout_video)
    SmartRefreshLayout refreshLayoutVideo;
    @InjectView(R.id.refresh_layout_article)
    SmartRefreshLayout refreshLayoutArticle;


    List<Map> mVideoMapList = new ArrayList<>();
    List<Map> mArticleMapList = new ArrayList<>();


    private ReleaseVideoAdapter mReleaseVideoAdapter;
    private ReleaseArticleAdapter mReleaseArticleAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release_record;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.release_record));
        changeHight(true);
        initRecyclerView();
        initData();

    }

    private void initRecyclerView() {
        recyclerVideo.setLayoutManager(new VegaLayoutManager());
        mReleaseVideoAdapter = new ReleaseVideoAdapter(this, mVideoMapList);
        recyclerVideo.setAdapter(mReleaseVideoAdapter);
        refreshLayoutVideo.setEnableLoadMore(true);
        refreshLayoutVideo.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLayoutVideo.finishRefresh(2000);
            }
        });
        refreshLayoutVideo.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayoutVideo.finishLoadMore(2000);
            }
        });


        recyclerArticle.setLayoutManager(new VegaLayoutManager());
        mReleaseArticleAdapter = new ReleaseArticleAdapter(this, mArticleMapList);
        recyclerArticle.setAdapter(mReleaseArticleAdapter);
        refreshLayoutVideo.setEnableLoadMore(true);
        refreshLayoutArticle.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayoutArticle.finishRefresh(2000);
            }
        });
        refreshLayoutArticle.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayoutArticle.finishLoadMore(2000);
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mVideoMapList.add(new HashMap());
            mArticleMapList.add(new HashMap());
        }
        mReleaseVideoAdapter.notifyDataSetChanged();
        mReleaseArticleAdapter.notifyDataSetChanged();
    }


    /**
     * 修改recycler高度
     */
    private void changeHight(boolean isVideo) {
        int height = ScreenTools.getScreenHeight(this) - ScreenTools.getStateBar3(this);
        if (isVideo) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) refreshLayoutVideo.getLayoutParams();
            params.height = height - ScreenTools.dip2px(this, 130);
            refreshLayoutVideo.setLayoutParams(params);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) refreshLayoutArticle.getLayoutParams();
            params1.height = 0;
            refreshLayoutArticle.setLayoutParams(params1);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) refreshLayoutVideo.getLayoutParams();
            params.height = 0;
            refreshLayoutVideo.setLayoutParams(params);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) refreshLayoutArticle.getLayoutParams();
            params1.height = height - ScreenTools.dip2px(this, 130);
            refreshLayoutArticle.setLayoutParams(params1);
        }
    }

    @OnClick({R.id.tv_video, R.id.tv_article})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_video:
                changeHight(true);
                break;
            case R.id.tv_article:
                changeHight(false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
