package com.wewin.live.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.jasonutil.util.MySharedPreferences;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo2;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterCommon;
import com.wewin.live.ui.activity.HtmlActivity;
import com.wewin.live.ui.activity.live.VideoDetailsActivity;
import com.wewin.live.ui.activity.SearchActivity;
import com.wewin.live.ui.adapter.SearchAnchorAdapter;
import com.wewin.live.ui.adapter.SearchLiveAdapter;
import com.wewin.live.ui.adapter.SearchNewsAdapter;
import com.wewin.live.ui.adapter.SearchVideoAdapter;
import com.wewin.live.ui.widget.ErrorView;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author jsaon
 * @date 2019/3/2
 * 搜索tab页面
 */
public class SearchFragment extends AbstractLazyFragment implements ErrorView.OnContinueListener {
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @InjectView(R.id.recycler_anchor)
    RecyclerView recyclerAnchor;
    @InjectView(R.id.rl_auchor_more)
    RelativeLayout rlAuchorMore;
    @InjectView(R.id.recycler_live)
    RecyclerView recyclerLive;
    @InjectView(R.id.rl_live_more)
    RelativeLayout rlLiveMore;
    @InjectView(R.id.recycler_video)
    RecyclerView recyclerVideo;
    @InjectView(R.id.rl_video_more)
    RelativeLayout rlVideoMore;
    @InjectView(R.id.recycler_news)
    RecyclerView recyclerNews;
    @InjectView(R.id.rl_news_more)
    RelativeLayout rlNewsMore;
    @InjectView(R.id.sv_all_data)
    ScrollView svAllData;

    private View view;
    //0全部  1主播  2直播   3 视频   4资讯
    private int type = 0;
    //类型代称
    private String typeName;
    //主播适配器
    private SearchAnchorAdapter mSearchAnchorAdapter;
    //直播适配器
    private SearchLiveAdapter mSearchLiveAdapter;
    // 视频适配器
    private SearchVideoAdapter mSearchVideoAdapter;
    //资讯适配器
    private SearchNewsAdapter mSearchNewsAdapter;
    //文本内容
    private String content;
    //分页参数
    private int page=1;
    //错误界面
    private ErrorView mErrorView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_search, null);
        ButterKnife.inject(this, view);
        return view;
    }


    @Override
    protected void lazyLoad() {
        init();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void init() {
        initError();
        getBundle();
        setView();
        initRecyclerView();
        notifyDataSetChanged(true);
        initAllData();
    }

    /**
     * 获取全部搜索数据
     */
    private void initAllData() {
        if(type==0){
            searchAll(true);
        }
    }

    /**
     * 获取上个界面传递数据
     */
    private void getBundle() {
        content=MySharedPreferences.getInstance().getStringTemporary(MySharedConstants.SEARCH_CONTENT);
        Bundle bundle = getArguments();
        type = bundle.getInt("type");
    }

    /**
     * 初始化错误界面
     */
    private void initError() {
        mErrorView = new ErrorView(getContext(),view);
        mErrorView.setTvError(getString(R.string.error));
        mErrorView.setTvNoData(getString(R.string.not_searched));
        mErrorView.setOnContinueListener(this);
    }

    /**
     * 全部和各个分类写在一个界面，通过type区分是全部还是单个
     */
    private void setView(){
        if(type==0){
            svAllData.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }else{
            svAllData.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        if(type==0){
            recyclerAnchor.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerLive.setLayoutManager(new GridLayoutManager(getContext(),2));
            recyclerVideo.setLayoutManager(new GridLayoutManager(getContext(),2));
            recyclerNews.setLayoutManager(new LinearLayoutManager(getContext()));
            initSearchAnchorAdapter();
            initSearchLiveAdapter();
            initSearchVideoAdapter();
            initSearchNewsAdapter();
            recyclerAnchor.setAdapter(mSearchAnchorAdapter);
            recyclerLive.setAdapter(mSearchLiveAdapter);
            recyclerVideo.setAdapter(mSearchVideoAdapter);
            recyclerNews.setAdapter(mSearchNewsAdapter);
            recyclerAnchor.setNestedScrollingEnabled(false);
            recyclerLive.setNestedScrollingEnabled(false);
            recyclerVideo.setNestedScrollingEnabled(false);
            recyclerNews.setNestedScrollingEnabled(false);
        }else if(type==1){
            initSearchAnchorAdapter();
            initAdapter(mSearchAnchorAdapter,new LinearLayoutManager(getContext()));
        }else if(type==2) {
            initSearchLiveAdapter();
            initAdapter(mSearchLiveAdapter,new GridLayoutManager(getContext(),2));
        }else if(type==3){
            initSearchVideoAdapter();
            initAdapter(mSearchVideoAdapter,new GridLayoutManager(getContext(),2));
        }else if(type==4){
            initSearchNewsAdapter();
            initAdapter(mSearchNewsAdapter,new LinearLayoutManager(getContext()));
        }
    }

    /**
     * 初始化数据，加搜索内容，设置是否加载更多和错误界面
     * @param isSetLoadMore  是否设置加载更多
     */
    private void notifyDataSetChanged(boolean isSetLoadMore) {
        if(type==0){
            typeName="";
            mSearchAnchorAdapter.notifyDataSetChanged();
            mSearchLiveAdapter.notifyDataSetChanged();
            mSearchVideoAdapter.notifyDataSetChanged();
            mSearchNewsAdapter.notifyDataSetChanged();
            if(((SearchActivity)getActivity()).mAnchorList.size()<=0
                    &&((SearchActivity)getActivity()).mLiveList.size()<=0
                    &&((SearchActivity)getActivity()).mVideoList.size()<=0
                    &&((SearchActivity)getActivity()).mNewsList.size()<=0){
                mErrorView.notDataShow(false);
            }else {
                mErrorView.hint();
            }
        }else if(type==1){
            typeName="anchor";
            mSearchAnchorAdapter.notifyDataSetChanged();
            if(((SearchActivity)getActivity()).mAnchorList.size()<=0){
                mErrorView.notDataShow(false);
            }else {
                mErrorView.hint();
            }
            if(isSetLoadMore){
                loadMore(((SearchActivity)getActivity()).mAnchorList);
            }
        }else if(type==2){
            typeName="live";
            mSearchLiveAdapter.notifyDataSetChanged();
            if(((SearchActivity)getActivity()).mLiveList.size()<=0){
                mErrorView.notDataShow(false);
            }else {
                mErrorView.hint();
            }
            if(isSetLoadMore){
                loadMore(((SearchActivity)getActivity()).mLiveList);
            }
        }else if(type==3){
            typeName="video";
            mSearchVideoAdapter.notifyDataSetChanged();
            if(((SearchActivity)getActivity()).mVideoList.size()<=0){
                mErrorView.notDataShow(false);
            }else {
                mErrorView.hint();
            }
            if(isSetLoadMore){
                loadMore(((SearchActivity)getActivity()).mVideoList);
            }
        }else if(type==4){
            typeName="article";
            mSearchNewsAdapter.notifyDataSetChanged();
            if(((SearchActivity)getActivity()).mNewsList.size()<=0){
                mErrorView.notDataShow(false);
            }else {
                mErrorView.hint();
            }
            if(isSetLoadMore){
                loadMore(((SearchActivity)getActivity()).mNewsList);
            }
        }

    }

    /**
     * 初始化适配器
     * @param adapter
     * @param layout
     */
    private void initAdapter(RecyclerView.Adapter adapter,RecyclerView.LayoutManager layout){
        recyclerView.setLayoutManager(layout);
        recyclerReOrLoad();
        recyclerView.setAdapter(adapter);
    }

    /**
     * 视频适配器
     */
    private void initSearchVideoAdapter(){
        mSearchVideoAdapter=new SearchVideoAdapter(getContext(),((SearchActivity)getActivity()).mVideoList);
        mSearchVideoAdapter.addOnItemListener(new SearchVideoAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putString(BaseInfoConstants.PULL_URL,((SearchActivity)getActivity()).mVideoList.get(position).get(BaseInfoConstants.FLVURL)+"");
                bundle.putString(BaseInfoConstants.URL,((SearchActivity)getActivity()).mVideoList.get(position).get(BaseInfoConstants.URL)+"");
                IntentStart.starLogin(getActivity(), VideoDetailsActivity.class, bundle);
            }
        });
    }

    /**
     * 资讯适配器
     */
    private void initSearchNewsAdapter(){
        mSearchNewsAdapter=new SearchNewsAdapter(getContext(),((SearchActivity)getActivity()).mNewsList);
        mSearchNewsAdapter.addOnItemListener(new SearchNewsAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle=new Bundle();
                bundle.putString(BaseInfoConstants.URL,((SearchActivity)getActivity()).mNewsList.get(position).get(BaseInfoConstants.URL)+"");
                IntentStart.star(getActivity(),HtmlActivity.class,bundle);
            }
        });
    }

    /**
     * 主播适配器
     */
    private void initSearchAnchorAdapter(){
        mSearchAnchorAdapter=new SearchAnchorAdapter(getContext(),((SearchActivity)getActivity()).mAnchorList);
        mSearchAnchorAdapter.addOnItemListener(new SearchAnchorAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle=new Bundle();
                bundle.putString(BaseInfoConstants.URL,((SearchActivity)getActivity()).mAnchorList.get(position).get(BaseInfoConstants.URL)+"");
                IntentStart.star(getActivity(),HtmlActivity.class,bundle);
            }
        });
    }

    /**
     * 直播适配器
     */
    private void initSearchLiveAdapter(){
        mSearchLiveAdapter=new SearchLiveAdapter(getContext(),((SearchActivity)getActivity()).mLiveList);
        mSearchLiveAdapter.addOnItemListener(new SearchLiveAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putString(BaseInfoConstants.PULL_URL,((SearchActivity)getActivity()).mLiveList.get(position).get(BaseInfoConstants.FLVURL)+"");
                bundle.putString(BaseInfoConstants.URL,((SearchActivity)getActivity()).mLiveList.get(position).get(BaseInfoConstants.URL)+"");
                IntentStart.starLogin(getActivity(), VideoDetailsActivity.class, bundle);
            }
        });
    }


    /**
     * 刷新和加载
     */
    private void recyclerReOrLoad(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if(type==0) {
                    searchAll(true);
                }else {
                    searchType(true);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if(type==0) {
                    searchAll(false);
                }else {
                    searchType(false);
                }
            }
        });
    }

    /**
     * 搜索全站
     */
    private void searchAll(final boolean isRefresh){
        PersenterCommon.getInstance().searchAll(content,new OnSuccess(getContext(), new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                mErrorView.hint();
                finishRefreshLayout(isRefresh);
                Map map= (Map) ((BaseMapInfo2) content).getData();
                List<Map> video= (List<Map>)map.get(BaseInfoConstants.VIDEO);
                List<Map> anchor= (List<Map>) map.get(BaseInfoConstants.ANCHOR);
                List<Map> live1= (List<Map>) map.get(BaseInfoConstants.LIVE1);
                List<Map> article= (List<Map>) map.get(BaseInfoConstants.ARTICLE);
                getVideoData(video,isRefresh);
                getAnchorData(anchor,isRefresh);
                getLiveData(live1,isRefresh);
                getArticleData(article,isRefresh);
                setAllItemHint(((SearchActivity)getActivity()).mVideoList,rlVideoMore);
                setAllItemHint(((SearchActivity)getActivity()).mAnchorList,rlAuchorMore);
                setAllItemHint(((SearchActivity)getActivity()).mLiveList,rlLiveMore);
                setAllItemHint(((SearchActivity)getActivity()).mNewsList,rlNewsMore);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATA_SEARCH_OTHER));
            }

            @Override
            public void onFault(String error) {
                finishRefreshLayout(isRefresh);
                mErrorView.errorShow(true);
            }
        }));
    }

    /**
     * 搜索单个站点
     */
    private void searchType(final boolean isRefresh){
        int p;
        if(isRefresh){
            p=1;
        }else {
            p=page+1;
        }
        PersenterCommon.getInstance().searchType(content,typeName,p,new OnSuccess(getContext(), new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                finishRefreshLayout(isRefresh);
                Map map= (Map) ((BaseMapInfo2) content).getData();
                setOtherData(map,isRefresh);
            }

            @Override
            public void onFault(String error) {
                finishRefreshLayout(isRefresh);
                mErrorView.errorShow(true);
            }
        }));

    }

    /**
     * 设置其余类型数据
     * @param map
     * @param isRefresh
     */
    private void setOtherData(Map map,boolean isRefresh){
        if(type==1){
            List<Map> anchor= (List<Map>) map.get(BaseInfoConstants.ANCHOR);
            getAnchorData(anchor,isRefresh);
            loadMore(anchor);
        }else if(type==2){
            List<Map> live1= (List<Map>)map.get(BaseInfoConstants.LIVE1);
            getLiveData(live1,isRefresh);
            loadMore(live1);
        }else if(type==3){
            List<Map> video= (List<Map>) map.get(BaseInfoConstants.VIDEO);
            getVideoData(video,isRefresh);
            loadMore(video);
        }else if(type==4){
            List<Map> article= (List<Map>) map.get(BaseInfoConstants.ARTICLE);
            getArticleData(article,isRefresh);
            loadMore(article);
        }
        notifyDataSetChanged(false);
    }

    /**
     * 设置网络视频列表
     * @param list
     * @param isRefresh
     */
    private void getVideoData(List<Map> list, boolean isRefresh){
        if (isRefresh){
            page=1;
            ((SearchActivity)getActivity()).mVideoList.clear();
        }else {
            page++;
        }
        ((SearchActivity)getActivity()).mVideoList.addAll(list);
    }

    /**
     * 设置网络资讯列表
     * @param list
     * @param isRefresh
     */
    private void getArticleData(List<Map> list, boolean isRefresh){
        if (isRefresh){
            page=1;
            ((SearchActivity)getActivity()).mNewsList.clear();
        }else {
            page++;
        }
        ((SearchActivity)getActivity()).mNewsList.addAll(list);
    }

    /**
     * 设置网络主播列表
     * @param list
     * @param isRefresh
     */
    private void getAnchorData(List<Map> list, boolean isRefresh){
        if (isRefresh){
            page=1;
            ((SearchActivity)getActivity()).mAnchorList.clear();
        }else {
            page++;
        }
        ((SearchActivity)getActivity()).mAnchorList.addAll(list);
    }

    /**
     * 设置网络直播列表
     * @param list
     * @param isRefresh
     */
    private void getLiveData(List<Map> list, boolean isRefresh){
        if (isRefresh){
            page=1;
            ((SearchActivity)getActivity()).mLiveList.clear();
        }else {
            page++;
        }
        ((SearchActivity)getActivity()).mLiveList.addAll(list);
    }

    /**
     * 设置是否隐藏和显示类型
     * @param list
     * @param view
     */
    private void setAllItemHint(List<Map> list,View view){
        if(list==null||list.size()<=0){
            view.setVisibility(View.GONE);
        }else {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置是否能继续加载更多
     */
    private void loadMore(List<Map> list){
        if (list!=null&&list.size()>=10){
            refreshLayout.setEnableLoadMore(true);
        }else {
            refreshLayout.setEnableLoadMore(false);
        }
    }


    /**
     * 关闭刷新状态和加载状态
     * @param isRefresh
     */
    private void finishRefreshLayout(boolean isRefresh){
        if (isRefresh){
            refreshLayout.finishRefresh();
        }else {
            refreshLayout.finishLoadMore();
        }
    }

    /**
     * 选择某个fragment
     * @param position
     */
    private void setSelectFragment(int position){
        if(getActivity() instanceof SearchActivity){
            ((SearchActivity)getActivity()).setSelect(position);
        }
    }

    @OnClick({R.id.tv_auchor_more,R.id.tv_live_more,R.id.tv_video_more,R.id.tv_news_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_auchor_more:
                //主播更多
                setSelectFragment(1);
                break;
            case R.id.tv_live_more:
                //直播更多
                setSelectFragment(2);
                break;
            case R.id.tv_video_more:
                //视频更多
                setSelectFragment(3);
                break;
            case R.id.tv_news_more:
                //新闻更多
                setSelectFragment(4);
                break;
            default:
                break;
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.SEARCH_CONTENT) {
            content=MySharedPreferences.getInstance().getStringTemporary(MySharedConstants.SEARCH_CONTENT);
            initAllData();
        }else if(msgId==MessageEvent.UPDATA_SEARCH_OTHER){
            notifyDataSetChanged(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.reset(this);
    }

    @Override
    public void again() {
        if(type==0) {
            searchAll(true);
        }else {
            searchType(true);
        }
    }
}
