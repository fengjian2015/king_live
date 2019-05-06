package com.wewin.live.ui.Fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.ScreenTools;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.base.BaseMainFragment;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.dialog.FeaturesListDialog;
import com.wewin.live.dialog.KingListDialog;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.utils.GlideUtil;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.SignOutUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * @author jsaon
 * @date 2019/2/27
 * 首页
 */
public class HomeFragment extends BaseMainFragment {


    @InjectView(R.id.iv_avatar)
    ImageView ivAvatar;
    @InjectView(R.id.ll_search_out)
    LinearLayout llSearchOut;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected String type() {
        return BaseInfoConstants.HOME;
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        initData();
    }

    /**
     * 显示王者号弹窗
     */
    private void showKing() {
        if (IntentStart.starLogin(getActivity())) return;
        new KingListDialog(getContext()).showAtLocation();
    }

    /**
     * 显示功能盘弹窗
     */
    private void shouFeatures() {
        if (IntentStart.starLogin(getActivity())) return;
        new FeaturesListDialog(getActivity()).showAtLocation();
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


    @OnClick({R.id.iv_avatar, R.id.ll_search, R.id.iv_features, R.id.iv_king})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_avatar:
                //跳转侧栏
                EventBus.getDefault().post(new MessageEvent(MessageEvent.CUSTONM_SIDE));
                break;
            case R.id.ll_search:
                ToastShow.showToast2(getContext(), "尚未开放，敬请期待");
//                IntentStart.star(getActivity(), SearchActivity.class);
                break;
            case R.id.iv_features:
                shouFeatures();
                //功能盘
                break;
            case R.id.iv_king:
                //王者号下拉窗
                showKing();
                break;
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
        } else if (msgId == MessageEvent.UPTADA_INFO) {
            initData();
        } else if (msgId == MessageEvent.WB_SCROLL_HIGHT_CHANGE) {
            hintOrShow(event.getStarY(), event.getScrollY());
        }
    }


    private boolean isInAnim = false;

    private void hintOrShow(float starY, float scrollY) {
        if (scrollY > starY + 30) {
            hintSearch();
        } else if (scrollY < starY - 30) {
            showSearch();
        } else if (scrollY == 0) {
            showSearch();
        }
    }

    private void hintSearch() {
        if (isInAnim || llSearchOut.getHeight() < ScreenTools.dip2px(getActivity(), 60)) return;
        animHeightToView(llSearchOut, ScreenTools.dip2px(getActivity(), 60), 0, 300);
    }

    private void showSearch() {
        if (isInAnim || llSearchOut.getHeight() != 0) return;
        animHeightToView(llSearchOut, 0, ScreenTools.dip2px(getActivity(), 60), 300);
    }

    public void animHeightToView(final View v, final int start, final int end, long duration) {
        isInAnim = true;
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (int) animation.getAnimatedValue();
                layoutParams.height = h;
                v.setLayoutParams(layoutParams);
                v.requestLayout();
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isInAnim = false;
                super.onAnimationEnd(animation);
            }
        });
        va.setDuration(duration);
        va.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
