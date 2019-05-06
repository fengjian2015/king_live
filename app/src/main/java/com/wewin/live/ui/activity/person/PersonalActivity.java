package com.wewin.live.ui.activity.person;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.example.jasonutil.util.StringUtils;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo2;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.ui.adapter.PersonalAdapter;
import com.wewin.live.ui.adapter.PersonalLevelAdapter;
import com.wewin.live.utils.GlideUtil;
import com.wewin.live.utils.SignOutUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * 个人资料页面
 */
public class PersonalActivity extends BaseActivity {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.recycler_level)
    RecyclerView recyclerLevel;

    List<Map> mHashMapList = new ArrayList<>();
    List<String> mListLevel = new ArrayList<>();
    @InjectView(R.id.iv_avatar)
    ImageView ivAvatar;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.iv_level)
    ImageView ivLevel;
    @InjectView(R.id.rl_top)
    RelativeLayout rlTop;
    @InjectView(R.id.tv_subscription)
    TextView tvSubscription;
    private PersonalAdapter mPersonalAdapter;
    private PersonalLevelAdapter mPersonalLevelAdapter;

    int[] images = new int[]{R.mipmap.icon_online_number, R.mipmap.icon_get_share, R.mipmap.icon_gift, R.mipmap.icon_receive_gift
            , R.mipmap.icon_number_fans, R.mipmap.icon_live_number, R.mipmap.icon_post_video, R.mipmap.icon_send_article};
    int[] titles = new int[]{R.string.online_number, R.string.get_share, R.string.gift, R.string.receive_gift, R.string.number_fans
            , R.string.live_number, R.string.post_video, R.string.send_article};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.personal_information));
        initRecyclerView();
        initData();
        initHttp();
    }

    /**
     * 初始化适配器
     */
    private void initRecyclerView() {
        rlTop.setBackground(getResources().getDrawable(R.drawable.red_gradient));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mPersonalAdapter = new PersonalAdapter(this, mHashMapList);
        mRecyclerView.setAdapter(mPersonalAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        recyclerLevel.setLayoutManager(new GridLayoutManager(this, 5));
        mPersonalLevelAdapter = new PersonalLevelAdapter(this, mListLevel);
        recyclerLevel.setAdapter(mPersonalLevelAdapter);
        recyclerLevel.setNestedScrollingEnabled(false);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        UserInfo userInfo = UserInfoDao.queryUserInfo(SignOutUtil.getUserId());
        GlideUtil.setCircleImg(this, userInfo.getAvatar(), ivAvatar);
        tvName.setText(userInfo.getNickName());
        GlideUtil.setImg(this, userInfo.getLevel_icon(), ivLevel, R.mipmap.icon_qingtong1);
        for (int i = 0; i < images.length; i++) {
            HashMap hashMap = new HashMap();
            hashMap.put(BaseInfoConstants.TITLE, titles[i]);
            hashMap.put(BaseInfoConstants.AVATAR, images[i]);
            hashMap.put(BaseInfoConstants.NUMBER, "0");
            mHashMapList.add(hashMap);
        }
        mPersonalAdapter.notifyDataSetChanged();

    }

    /**
     * 网络请求
     */
    private void initHttp() {
        PersenterPersonal.getInstance().getPersonal(new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                BaseMapInfo2 baseMapInfo2 = (BaseMapInfo2) content;
                setData((Map) baseMapInfo2.getData());
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 设置网络数据
     *
     * @param data
     */
    private void setData(Map data) {
        try {
            List<String> list = JSONArray.parseArray(data.get(BaseInfoConstants.LEVELICONS) + "", String.class);
            mListLevel.clear();
            mListLevel.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvSubscription.setText(getString(R.string.subscription) + data.get(BaseInfoConstants.FANSNUMBER) + "");
        mHashMapList.get(0).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.BONUS_DAY) + "");
        mHashMapList.get(1).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.VOTESTOTAL) + "");
        mHashMapList.get(2).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.TSC) + "");
        mHashMapList.get(3).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.TSD) + "");
        mHashMapList.get(4).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.FANSNUMBER) + "");
        mHashMapList.get(5).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.LIVEDNUMBER) + "");
        mHashMapList.get(6).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.PUBLISHEDVIDEONUMBER) + "");
        mHashMapList.get(7).put(BaseInfoConstants.NUMBER, data.get(BaseInfoConstants.PUBLISHEDARTICLENUMBER) + "");
        mPersonalAdapter.notifyDataSetChanged();
        mPersonalLevelAdapter.notifyDataSetChanged();
    }

}
