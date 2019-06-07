package com.wewin.live.ui.fragment;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.live.R;
import com.wewin.live.base.BaseMainFragment;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.activity.SearchActivity;
import com.wewin.live.utils.GlideUtil;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.SignOutUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * @author jsaon
 * @date 2019/2/27
 * 点评
 */
public class ReviewFragment extends BaseMainFragment {
    @InjectView(R.id.tv_search_content)
    TextView tvSearchContent;
    @InjectView(R.id.iv_avatar)
    ImageView ivAvatar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_review;
    }

    @Override
    protected String type() {
        return BaseInfoConstants.REVIEW;
    }


    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        initData();
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        if (SignOutUtil.getIsLogin()) {
            GlideUtil.setCircleImg(getActivity(), UserInfoDao.findAvatar(), ivAvatar);
        } else {
            GlideUtil.setCircleImg(getActivity(), null, ivAvatar);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.LOGIN) {
            initData();
        } else if (msgId == MessageEvent.SIGN_OUT) {
            initData();
        } else if (msgId == MessageEvent.MAIN_MENU_UPDATA && getIsVisible()) {
            updata();
        } else if (msgId == MessageEvent.MAIN_MENU_FAILURE) {
            failure();
        }else if(msgId==MessageEvent.UPTADA_INFO){
            initData();
        }
    }

    @OnClick({R.id.ll_search, R.id.iv_avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_search:
                //搜索
                IntentStart.star(getContext(), SearchActivity.class);
                break;
            case R.id.iv_avatar:
                //跳转侧栏
                EventBus.getDefault().post(new MessageEvent(MessageEvent.CUSTONM_SIDE));
                break;
            default:
                break;
        }
    }

}
