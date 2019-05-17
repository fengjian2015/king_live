package com.wewin.live.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo2;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterCommon;
import com.wewin.live.ui.Fragment.SearchFragment;
import com.wewin.live.ui.adapter.SearchPopularAdapter;
import com.wewin.live.ui.widget.CustomTabLayout;
import com.wewin.live.ui.widget.CustomTabUtil;
import com.wewin.live.ui.widget.flowlayout.FlowLayout;
import com.wewin.live.ui.widget.flowlayout.TagAdapter;
import com.wewin.live.ui.widget.flowlayout.TagFlowLayout;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 搜索界面，包含搜索结果页
 */
public class SearchActivity extends BaseActivity {

    @InjectView(R.id.et_search)
    EditText etSearch;
    @InjectView(R.id.custom_tab)
    CustomTabLayout customTab;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.tg_flow_layout)
    TagFlowLayout tgFlowLayout;
    @InjectView(R.id.ll_result)
    LinearLayout llResult;
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.scrollView_data)
    NestedScrollView scrollViewData;
    @InjectView(R.id.tv_hot)
    TextView tvHot;

    public String[] str = new String[]{"全部", "主播", "直播", "视频", "资讯"};
    //搜索记录
    private List recordingList ;
    //记录标签
    TagAdapter tagAdapter;

    private CustomTabUtil customTabUtil;
    List<Map> mHashMapList;
    //热门搜索适配器
    private SearchPopularAdapter mSearchPopularAdapter;

    //其余4个类型数据
    public List<Map> mAnchorList=new ArrayList<>();
    public List<Map> mLiveList=new ArrayList<>();
    public List<Map> mVideoList=new ArrayList<>();
    public List<Map> mNewsList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isTitleColor = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void init() {
        setBar();
        initFlow();
        //必须等customTab显示出来才能初始化，
        initFlexTitle();
        setIsSearchView(false);
        initRecyclerView();
        setOnClick();
        initHttpHot();
    }

    /**
     * 初始化历史搜索
     */
    private void initFlow() {
        recordingList=JSONObject.parseArray(MySharedPreferences.getInstance().getString(MySharedConstants.SEARCH_DATA),String.class);
        if (recordingList == null || recordingList.size() <= 0) {
            tvHot.setVisibility(View.GONE);
            tgFlowLayout.setVisibility(View.GONE);
        }
        final LayoutInflater mInflater = LayoutInflater.from(SearchActivity.this);
        tagAdapter=new TagAdapter<String>(recordingList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_flow_tv, tgFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        tgFlowLayout.setAdapter(tagAdapter);
    }

    /**
     * 初始化fragment
     */
    private void initFlexTitle() {
        List<Fragment> mFragmentList = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            SearchFragment searchFragment = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", i);
            searchFragment.setArguments(bundle);
            mFragmentList.add(searchFragment);
        }
        customTab.setTitleArr(str);

        customTabUtil = new CustomTabUtil(this, viewPager, customTab);
        customTabUtil.setTitleArr(str);
        customTabUtil.setFragmentList(mFragmentList);
        customTabUtil.initViewPage();
        viewPager.setOffscreenPageLimit(5);
    }

    /**
     * 初始化热门数据
     */
    private void initRecyclerView() {
        mHashMapList = JSONObject.parseArray(MySharedPreferences.getInstance().getString(MySharedConstants.SEARCH_HOT), Map.class);
        if(mHashMapList==null)mHashMapList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchPopularAdapter = new SearchPopularAdapter(this, mHashMapList);
        recyclerView.setAdapter(mSearchPopularAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        mSearchPopularAdapter.addOnItemListener(new SearchPopularAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                String keyWord= (String) mHashMapList.get(position).get(BaseInfoConstants.KEYWORD);
                etSearch.setText(keyWord);
                addRecordingList(keyWord);
                search(keyWord);
            }
        });
    }


    private void setOnClick() {
        tgFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                search((String) recordingList.get(position));
                return true;
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 隐藏键盘
                    UtilTool.closeKeybord(SearchActivity.this);
                    String content=etSearch.getText().toString();
                    if (!StringUtils.isEmpty(content)) {
                        addRecordingList(content);
                        search(content);
                    } else {
                        ToastShow.showToast2(SearchActivity.this, getString(R.string.search_content_cannot_empty));
                    }
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(StringUtils.isEmpty(charSequence.toString())){
                    setIsSearchView(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    /**
     * 获取热门搜索词接口
     */
    private void initHttpHot(){
        PersenterCommon.getInstance().getHotKeywords(new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                mHashMapList.clear();
                mHashMapList.addAll((List)((BaseMapInfo2) content).getData());
                MySharedPreferences.getInstance().setString(MySharedConstants.SEARCH_HOT,JSONObject.toJSONString(mHashMapList));
                mSearchPopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 切换Fragment
     * @param position
     */
    public void setSelect(int position){
        customTabUtil.setSelect(position);
    }


    /**
     * 搜索
     * @param content
     */
    private void search(String content) {
        MySharedPreferences.getInstance().setStringTemporary(MySharedConstants.SEARCH_CONTENT,content);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.SEARCH_CONTENT));
        setIsSearchView(true);
    }

    /**
     * 插入搜索记录
     * @param content
     */
    private void addRecordingList(String content) {
        if (recordingList == null) {
            recordingList = new ArrayList();
        }
        if(recordingList.contains(content)){
            return;
        }
        if(recordingList.size()>=20){
            recordingList.remove(recordingList.size()-1);
        }
        recordingList.add(0,content);
        MySharedPreferences.getInstance().setString(MySharedConstants.SEARCH_DATA,JSONObject.toJSONString(recordingList));
        tagAdapter.notifyDataChanged();
    }

    /**
     * 是否搜索
     *
     * @param isTrue
     */
    private void setIsSearchView(boolean isTrue) {
        if (isTrue) {
            llResult.setVisibility(View.VISIBLE);
            scrollViewData.setVisibility(View.GONE);
            customTab.reacquireWidth();
        } else {
            llResult.setVisibility(View.GONE);
            scrollViewData.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        UtilTool.closeKeybord(this);
        super.finish();
    }
}
