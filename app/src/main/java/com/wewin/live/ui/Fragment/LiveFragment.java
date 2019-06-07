package com.wewin.live.ui.fragment;


import android.view.View;
import com.wewin.live.R;
import com.wewin.live.base.BaseMainFragment;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.activity.person.AllRemindersActivity;
import com.wewin.live.ui.activity.SearchActivity;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;


/**
 * @author jsaon
 * @date 2019/2/27
 * 直播
 */
public class LiveFragment extends BaseMainFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live;
    }

    @Override
    protected String type() {
        return BaseInfoConstants.LIVE;
    }


    @OnClick({R.id.iv_search, R.id.iv_alarm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                //搜索
                IntentStart.star(getContext(), SearchActivity.class);
                break;
            case R.id.iv_alarm:
                //消息提醒
                IntentStart.star(getContext(), AllRemindersActivity.class);
                break;
            default:
                break;
        }
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
