package com.wewin.live.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wewin.live.R;
import com.wewin.live.base.BaseMainFragment;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author jsaon
 * @date 2019/2/27
 * 我的
 */
public class MyFragment extends BaseMainFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected String type() {
        return BaseInfoConstants.ME;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.MAIN_MENU_UPDATA && getIsVisible()) {
            updata();
        } else if (msgId == MessageEvent.MAIN_MENU_FAILURE) {
            failure();
        }
    }
}
